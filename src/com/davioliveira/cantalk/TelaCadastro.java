package com.davioliveira.cantalk;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TelaCadastro extends Activity {

	ImageButton btnFoto;
	EditText editNome;
	TextView textTel;
	Button btnOkay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tela_login);
		
		btnFoto = (ImageButton) findViewById(R.id.imageBtnCadastro);
		editNome = (EditText) findViewById(R.id.editTextCadastroNome);
		btnOkay = (Button) findViewById(R.id.btnOkayCadastro);
		textTel = (TextView) findViewById(R.id.textViewTelefoneCadastro);
		
		TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		String number = tm.getLine1Number();
		if(number!= null && !number.isEmpty())
		{
			String numFormatado = "Tel: (" + number.charAt(0)+number.charAt(1)+ ") ";
			
			for(int i = 2; i < number.length(); i++)
			{
				numFormatado += number.charAt(i);
			}
			textTel.setText(numFormatado);
		}
		else 
		{
			textTel.setText("");
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tela_login, menu);
		return true;
	}

}
