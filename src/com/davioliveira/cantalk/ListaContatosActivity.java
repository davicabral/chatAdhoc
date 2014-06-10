package com.davioliveira.cantalk;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davioliveira.cantalk.adapters.AdapterContatos;
import com.davioliveira.cantalk.db.PessoaDAO;
import com.davioliveira.cantalk.dialogs.ConfimationDialog;
import com.davioliveira.cantalk.dialogs.DialogAdd;
import com.davioliveira.cantalk.utils.Pessoa;
import com.ess.wifi_direct.WifiDirectListener;
import com.ess.wifi_direct.WifiDirectMgr;

@SuppressLint("NewApi")
public class ListaContatosActivity extends Activity implements WifiDirectListener{
	
	protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
	public static AdapterContatos adapter;
	public PessoaDAO pessoaDAO;
	private String foto;
	
	public static int selectedItemId = -1;	
	private int count = 0;

	//WifiDirect variables

	private Activity thisActivity;
	private CanTalkApp canTalkApp;
	private ImageView ivWifiDirect;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista__contatos);
		ivWifiDirect = (ImageView) findViewById(R.id.ivWifiDirect);

		thisActivity = this;
		ActionBar actionbar = getActionBar();
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6196B2")));
		
		pessoaDAO = new PessoaDAO(this);
		pessoaDAO.open();
		adapter = new AdapterContatos(this);
		adapter.addContacts(pessoaDAO.readAll());
		
		canTalkApp = (CanTalkApp)getApplication();
		canTalkApp.wifiDirectMgr = new WifiDirectMgr(canTalkApp, this);
		
		configListView();
	}

	private void configListView() {
		ListView listview = (ListView) findViewById(R.id.lvMsgs);
		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listview.setMultiChoiceModeListener(new MultiChoiceMode());		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Pessoa pessoa = adapter.getItem(position);
				canTalkApp.wifiDirectMgr.connect(pessoa.getPeer());
				
				Builder builder = new AlertDialog.Builder(thisActivity);
				builder.setTitle("Conectando...").
				setMessage("Aguardando usuário " + pessoa.getNome() + " confirmar...").
				setView(new ProgressBar(thisActivity)).
				setCancelable(true).
				setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
						canTalkApp.wifiDirectMgr.cancelConnect();
					}
				});
				canTalkApp.alertDlgConnecting = builder.create();
				canTalkApp.alertDlgConnecting.show();
			}
		});
	}
	
	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
	    super.onResume();
	    canTalkApp.wifiDirectMgr.onResume();
	}

	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
	    super.onPause();
	    canTalkApp.wifiDirectMgr.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lista__contatos, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.create_new:
				DialogAdd.getDialog(this,new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				}, clickPosButton).show();
				break;
			case R.id.action_settings:
				canTalkApp.wifiDirectMgr.searchPeers();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private OnClickListener clickPosButton = new OnClickListener(){
		
		@Override
		public void onClick(DialogInterface dialog, int which){
			Pessoa pessoa = new Pessoa();
			pessoa.setNome(DialogAdd.txtNome.getText().toString());
			pessoa.setEmail(DialogAdd.txtEmail.getText().toString());
			pessoa.setCelular(DialogAdd.txtTelefone.getText().toString());
			pessoa.setFoto(foto);
			if(pessoaDAO.insert(pessoa) > -1){
				adapter.addContact(pessoa);
//					onCreate(new Bundle());
				Toast.makeText(ListaContatosActivity.this, "Contato adicionado", Toast.LENGTH_SHORT).show();
			}
			else{
				DialogAdd.txtEmail.setText("");
				DialogAdd.txtNome.setText("");
				DialogAdd.txtTelefone.setText("");
				Toast.makeText(ListaContatosActivity.this, "Contato Inválido", Toast.LENGTH_SHORT).show();
			}
			foto = null;
		}
	};
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            foto = Base64.encodeToString(byteArray, Base64.DEFAULT);
            
        }
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED){
			if(count % 2 == 0){
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			count++;
		}
	}
	
	@Override
	public void onWifiDirectStateChange(boolean state) {
		canTalkApp.wifiDirectState = state;
		if(state){
			ivWifiDirect.setImageResource(android.R.drawable.presence_online);
		}else{
			ivWifiDirect.setImageResource(android.R.drawable.presence_offline);
		}
	}

	@Override
	public void onPeersListAvailable(Collection<WifiP2pDevice> deviceList) {
		adapter.removeAll();
		for (WifiP2pDevice peer : deviceList) {
			Pessoa pessoa = new Pessoa();
			Log.i("PEER", peer.deviceName);
			pessoa.setNome(peer.deviceName);
			pessoa.setPeer(peer);
			adapter.addContact(pessoa);
		}
	}

	@Override
	public void execOnUiThread(Runnable runnable) {
		runOnUiThread(runnable);
	}

	class MultiChoiceMode implements MultiChoiceModeListener {
		
		//Implementar nova ACTIONBAR DINAMICA
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			selectedItemId = -1;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.lista__contatos_selecionados, menu);
	        return true;

		}
		
		@Override
		public boolean onActionItemClicked(final ActionMode mode,  final MenuItem item) {
			switch (item.getItemId()) {
	            case R.id.delete: 
	            	AlertDialog.Builder builderDelete = new Builder(ListaContatosActivity.this); 
					builderDelete.setTitle("Confimação").setMessage("Deseja realmente apagar este contato?");
					builderDelete.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Pessoa p = adapter.getItem((Integer) mode.getTag());
							if(pessoaDAO.delete(p)){
								adapter.remContact((Integer) mode.getTag());
								onCreate(new Bundle()); 
								Toast.makeText(ListaContatosActivity.this, "Contato apagado com sucesso", Toast.LENGTH_SHORT).show();
							}
							mode.finish();
						}
					});
					
					builderDelete.setNegativeButton("Não", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
					AlertDialog dialogDelete = builderDelete.create();
					dialogDelete.show();
	            	return false;
	            	
	            case R.id.edit:
	            	AlertDialog.Builder builder = new Builder(ListaContatosActivity.this); 
					View view = LayoutInflater.from(ListaContatosActivity.this).inflate(R.layout.dialog_edit_layout, null);
					builder.setView(view);
					builder.setTitle(R.string.edit);
					
					final EditText txtNome  = (EditText) view.findViewById(R.id.editNomeEdit);
					final TextView txtEmail  = (TextView) view.findViewById(R.id.textEmailEdit);
					final EditText txtTelefone = (EditText) view.findViewById(R.id.editTelefoneEdit);
					txtNome.setText(adapter.getItem((Integer) mode.getTag()).getNome());
					txtEmail.setText(adapter.getItem((Integer) mode.getTag()).getEmail());
					txtTelefone.setText(adapter.getItem((Integer) mode.getTag()).getCelular());
					
					builder.setPositiveButton("Editar", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AlertDialog dialogConfirmacao = ConfimationDialog.getConfirmationDialog(ListaContatosActivity.this, "Confirmação", "Quer mesmo editar este contato?", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Pessoa p = new Pessoa();
									p.setCelular(txtTelefone.getText().toString());
									p.setNome(txtNome.getText().toString());
									p.setEmail(txtEmail.getText().toString());
									if(pessoaDAO.update(p) > -1){
										adapter.getItem((Integer) mode.getTag()).setNome(txtNome.getText().toString());
										adapter.getItem((Integer) mode.getTag()).setEmail(txtEmail.getText().toString());
										adapter.getItem((Integer) mode.getTag()).setCelular(txtTelefone.getText().toString());
	    								Toast.makeText(ListaContatosActivity.this, "Contato editado com sucesso", Toast.LENGTH_SHORT).show();
									}
								}
							}, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							});
							dialogConfirmacao.show();
						}
					});
					
					builder.setNegativeButton("Cancelar", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
					AlertDialog dialogEdit = builder.create();
					dialogEdit.show();
	                return true;
	            default:
	                return false;
			}
		}
		
		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
			if(checked){
				selectedItemId = position;
			}else{
				selectedItemId = -1;
			}
			mode.setTitle(adapter.getItem(position).getNome());
			mode.setSubtitle("Selecionado");
			mode.setTag(position);
		}
	}

}
