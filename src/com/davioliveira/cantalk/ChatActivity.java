package com.davioliveira.cantalk;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.davioliveira.cantalk.adapters.AdapterChat;
import com.davioliveira.cantalk.db.MensagemDAO;
import com.davioliveira.cantalk.utils.Mensagem;

@SuppressLint("NewApi")
public class ChatActivity extends Activity {

	public static ArrayList<Mensagem> mensagens;
	public static AdapterChat adapter;
	public MensagemDAO mensagemDAO;
	ListView listView;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversas);
		
		
		mensagemDAO = new MensagemDAO(this);
		mensagemDAO.open();
		final Intent intent = getIntent();
		mensagens = mensagemDAO.readChat(intent.getStringExtra("Email"));
		ActionBar actionbar = getActionBar();
		actionbar.setTitle(intent.getStringExtra("Nome"));
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		adapter = new AdapterChat(this, mensagens);
		
		listView = (ListView) findViewById(R.id.listaChat);
		listView.setAdapter(adapter);
		listView.setDividerHeight(0);
		
		
		final EditText text = (EditText) findViewById(R.id.textoDigitado);
		final ImageButton btnEnviar = (ImageButton) findViewById(R.id.btnEnviar);
		
		btnEnviar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Mensagem m = new Mensagem();
				m.setMsgEnviada(text.getText().toString());
				m.setRemContato(intent.getStringExtra("Nome"));
				m.setEmail(intent.getStringExtra("Email"));
				if(mensagemDAO.insert(m) > -1)
				{
					Mensagem msg = new Mensagem();
					msg.setMsgEnviada(text.getText().toString());
					msg.setMsgOk("Ok");
					msg.setEmail(intent.getStringExtra("Email"));
					msg.setRemContato(intent.getStringExtra("Nome"));
					msg.setRemVc("Você");
					adapter.addView(msg);					
					text.setText(null);
					
					
				}
				
			}
		});
		
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.lista__contatos_selecionados, menu);
				return false;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversas, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case R.id.deleteMensagem:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Confirmação").setMessage("Tem certeza que deseja deletar TODA a conversa?");
				builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = getIntent();
						String email = intent.getStringExtra("Email");
						if(mensagemDAO.deleteHistorico(email))
						{
							mensagens.clear();
							adapter.notifyDataSetChanged();
							Toast.makeText(ChatActivity.this, "Histórico da conversa apagada com sucesso.", Toast.LENGTH_SHORT).show();
						}
						
						
						
					}

				});
				
				builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}

					
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
