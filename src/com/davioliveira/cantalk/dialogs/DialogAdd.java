package com.davioliveira.cantalk.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.davioliveira.cantalk.R;

public class DialogAdd extends AlertDialog{

	protected static final String CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = null;
	public AlertDialog.Builder builder;
	public AlertDialog dialog;
	
	public static EditText txtNome,txtTelefone,txtEmail;
	
	public DialogAdd(Context context) {
		super(context);
	}
	
	public static AlertDialog getDialog(final Context context,View.OnClickListener clickBotaoFoto, OnClickListener clickPosButton)
	{
		AlertDialog.Builder builder = new Builder(context); 
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_layout, null);
		builder.setView(view);
		builder.setTitle(R.string.add);
		
		txtNome  = (EditText) view.findViewById(R.id.editNome);
		txtEmail  = (EditText) view.findViewById(R.id.editEmail);
		txtTelefone = (EditText) view.findViewById(R.id.editTelefone);
		
		ImageButton btnFoto  = (ImageButton) view.findViewById(R.id.imageButton1);
		
		btnFoto.setOnClickListener(clickBotaoFoto);
		
		
			
			
		
		builder.setPositiveButton("Adicionar", clickPosButton); 
			
		
		builder.setNegativeButton("Cancelar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder.create();
	}

	
	
	
	

}
