package com.davioliveira.cantalk;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import android.widget.TextView;
import android.widget.Toast;

import com.davioliveira.cantalk.adapters.AdapterContatos;
import com.davioliveira.cantalk.db.PessoaDAO;
import com.davioliveira.cantalk.dialogs.ConfimationDialog;
import com.davioliveira.cantalk.dialogs.DialogAdd;
import com.davioliveira.cantalk.utils.Pessoa;


@SuppressLint("NewApi")
public class Lista_Contatos extends Activity {

	
	protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
	public static ArrayList<Pessoa> listaContatos = new ArrayList<Pessoa>();
	public static AdapterContatos adapter;
	private static byte[] photoByte;
	public PessoaDAO pessoaDAO;
	private Pessoa pessoa;
	private boolean hasPhoto;
	private boolean isLongClicked = false;
	
	public static int selectedItemId = -1;
	
	
	ImageView imagem;
	private int count = 0;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista__contatos);
		pessoaDAO = new PessoaDAO(this);
		pessoaDAO.open();
		pessoa = new Pessoa();
		
		ActionBar actionbar = getActionBar();
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6196B2")));
		
		listaContatos = pessoaDAO.readAll(); 

		adapter = new AdapterContatos(this, listaContatos);
		
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listview.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			//Implementar nova ACTIONBAR DINAMICA
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				selectedItemId = -1;
				adapter.notifyDataSetChanged();
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.lista__contatos_selecionados, menu);
		        return true;

			}
			
			@Override
			public boolean onActionItemClicked(final ActionMode mode,  final MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
	            case R.id.delete: 
	            	AlertDialog.Builder builderDelete = new Builder(Lista_Contatos.this); 
    				builderDelete.setTitle("Confimação").setMessage("Deseja realmente apagar este contato?");
    				builderDelete.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Pessoa p = listaContatos.get((Integer) mode.getTag());
							if(pessoaDAO.delete(p))
							{
								
								listaContatos.remove((Integer) mode.getTag());
								adapter.notifyDataSetChanged();
								onCreate(new Bundle()); 
								Toast.makeText(Lista_Contatos.this, "Contato apagado com sucesso", Toast.LENGTH_SHORT).show();
							}
						}
					});
    				
    				builderDelete.setNegativeButton("Não", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
    				
    				AlertDialog dialogDelete = builderDelete.create();
    				dialogDelete.show();
	            	return false;
	            	
                case R.id.edit:
                	
                	AlertDialog.Builder builder = new Builder(Lista_Contatos.this); 
    				View view = LayoutInflater.from(Lista_Contatos.this).inflate(R.layout.dialog_edit_layout, null);
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
    						
    						AlertDialog dialogConfirmacao = ConfimationDialog.getConfirmationDialog(Lista_Contatos.this, "Confirmação", "Quer mesmo editar este contato?", new OnClickListener() {
    							
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								// TODO Auto-generated method stub
    								Pessoa p = new Pessoa();
    								p.setCelular(txtTelefone.getText().toString());
    								p.setNome(txtNome.getText().toString());
    								p.setEmail(txtEmail.getText().toString());
    								if(pessoaDAO.update(p) > -1)
    								{
    									Lista_Contatos.listaContatos.get((Integer) mode.getTag()).setNome(txtNome.getText().toString());
        								Lista_Contatos.listaContatos.get((Integer) mode.getTag()).setEmail(txtEmail.getText().toString());
        								Lista_Contatos.listaContatos.get((Integer) mode.getTag()).setCelular(txtTelefone.getText().toString());
        								Toast.makeText(Lista_Contatos.this, "Contato editado com sucesso", Toast.LENGTH_SHORT).show();
        								adapter.notifyDataSetChanged();	
    								}
    							}
    						}, new OnClickListener() {
    							
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								// TODO Auto-generated method stub
    								isLongClicked = false;
    							}
    						});
    						dialogConfirmacao.show();
    					}
    				});
    				
    				builder.setNegativeButton("Cancelar", new OnClickListener() {
    					
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						// TODO Auto-generated method stub
    						
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
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
				if(checked)
				{
					selectedItemId = position;
				}
				else
				{
					selectedItemId = -1;
				}
				mode.setTitle(listaContatos.get(position).getNome());
				mode.setSubtitle("Selecionado");
				mode.setTag(position);
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				if(!isLongClicked)
				{
					Intent intent = new Intent(Lista_Contatos.this, ConversasActivity.class);
					intent.putExtra("Nome", adapter.getItem(position).getNome());
					intent.putExtra("Email", adapter.getItem(position).getEmail());
					startActivity(intent);	
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lista__contatos, menu);
	
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case R.id.create_new:
				
				DialogAdd.getDialog(this,new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				}, new OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if(!hasPhoto)
						{
							pessoa.setNome(DialogAdd.txtNome.getText().toString());
							pessoa.setEmail(DialogAdd.txtEmail.getText().toString());
							pessoa.setCelular(DialogAdd.txtTelefone.getText().toString());
							pessoa.setFoto(null);
							if(pessoaDAO.insert(pessoa) > -1)
							{
								Lista_Contatos.listaContatos.add(pessoa);
								onCreate(new Bundle());
								Lista_Contatos.adapter.notifyDataSetChanged();
								Toast.makeText(Lista_Contatos.this, "Contato adicionado", Toast.LENGTH_SHORT).show();
							}
							else
							{
								DialogAdd.txtEmail.setText("");
								DialogAdd.txtNome.setText("");
								DialogAdd.txtTelefone.setText("");
								Toast.makeText(Lista_Contatos.this, "Contato Inválido", Toast.LENGTH_SHORT).show();
							}
						}
						else if(hasPhoto)
						{
							pessoa.setNome(DialogAdd.txtNome.getText().toString());
							pessoa.setEmail(DialogAdd.txtEmail.getText().toString());
							pessoa.setCelular(DialogAdd.txtTelefone.getText().toString());
							if(pessoaDAO.insert(pessoa) > -1)
							{
								Lista_Contatos.listaContatos.add(pessoa);
								onCreate(new Bundle());
								Lista_Contatos.adapter.notifyDataSetChanged();
								Toast.makeText(Lista_Contatos.this, "Contato adicionado", Toast.LENGTH_SHORT).show();
							}
							else
							{
								DialogAdd.txtEmail.setText("");
								DialogAdd.txtNome.setText("");
								DialogAdd.txtTelefone.setText("");
								
								Toast.makeText(Lista_Contatos.this, "Contato Inválido", Toast.LENGTH_SHORT).show();
							}
							hasPhoto = false;
						}	
					}
				}).show();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            int bytes = photo.getByteCount();
//            ByteBuffer buffer = ByteBuffer.allocate(bytes);
//            photo.copyPixelsToBuffer(buffer);
//            photoByte = buffer.array();
            
            //System.out.println(photoByte);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            pessoa.setFoto(encoded);
            hasPhoto = true;
            
        }
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED)
		{
			if(count % 2 == 0)
			{
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			count++;
			
		}
    
	}

}
