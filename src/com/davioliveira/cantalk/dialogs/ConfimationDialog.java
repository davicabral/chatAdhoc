package com.davioliveira.cantalk.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfimationDialog extends AlertDialog {

	protected ConfimationDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static AlertDialog.Builder builder;
	
	public static AlertDialog getConfirmationDialog(Context context,String title,String text,DialogInterface.OnClickListener listenerPositivo
			,DialogInterface.OnClickListener listenerNegativo)
	{
		builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(text);
		
		builder.setPositiveButton("Sim",listenerPositivo);
		
		builder.setNegativeButton("Não",listenerNegativo);
		
		return builder.create();
	}
	
	
	
	

}
