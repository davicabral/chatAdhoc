package com.davioliveira.cantalk.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.davioliveira.cantalk.utils.Mensagem;

public class MensagemDAO {
	private SQLiteDatabase database;
	private OpenHelper dbHelper;

    public static final String TABLE_NAME = "mensagem";
    public static final String MSG = "msg";
    public static final String REM = "rem";
    public static final String EMAIL = "email";

    
	private String[] allColumns = {MSG, REM, EMAIL };

	public MensagemDAO(Context context) {
		dbHelper = new OpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		database.close();
		database = null;
	}

	public long insert(Mensagem msg) {
		ContentValues values = new ContentValues();
		values.put(REM, msg.getRemContato());
		values.put(MSG, msg.getMsgEnviada());
		values.put(EMAIL, msg.getEmail());

		return database.insert(TABLE_NAME, null, values);
	}

	public boolean deleteHistorico(String email) {
		long wReturn = database.delete(TABLE_NAME, EMAIL + " = '" + email + "'", null);
		if(wReturn == 0) {
			return false;
		} else {
			return true;
		}
	}

	public Mensagem read(String email) {
		Mensagem msg;
		
		Cursor c = database.query(TABLE_NAME, allColumns, EMAIL + " = '" + email + "'", null,null, null, null); 
		if(c.moveToFirst()) {
			
			int indexRemetente = c.getColumnIndex(REM);
			int indexMensagem = c.getColumnIndex(MSG);
			
			
			
			msg = new Mensagem();
			msg.setRemContato(c.getString(indexRemetente));
			msg.setMsgEnviada(c.getString(indexMensagem));
			return msg;
		} else {
			return null;
		}
	}
	
	public ArrayList<Mensagem> readAll() {
		Mensagem msg;
		ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
		
		Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);
		if(c.moveToFirst()) {
			mensagens = new ArrayList<Mensagem>();
			
			int indexRemetente = c.getColumnIndex(REM);
			int indexMensagem = c.getColumnIndex(MSG);
			
			do {
				msg = new Mensagem();
				msg.setRemContato(c.getString(indexRemetente));
				msg.setMsgEnviada(c.getString(indexMensagem));
				msg.setRemVc("Você");
				msg.setMsgOk("Ok");
				
				
				
				mensagens.add(msg);
			} while(c.moveToNext());
		}
		
		c.close();
		
		return mensagens;
	}
	
	public ArrayList<Mensagem> readChat(String email) {
		Mensagem msg;
		ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
		
		Cursor c = database.query(TABLE_NAME, allColumns, EMAIL + " = '" + email + "'", null, null, null, null);
		if(c.moveToFirst()) {
			
			
			int indexRemetente = c.getColumnIndex(REM);
			int indexMensagem = c.getColumnIndex(MSG);
			
			do {
				msg = new Mensagem();
				msg.setRemContato(c.getString(indexRemetente));
				msg.setMsgEnviada(c.getString(indexMensagem));
				msg.setRemVc("Você");
				msg.setMsgOk("Ok");
				
				mensagens.add(msg);
			} while(c.moveToNext());
		}
		
		c.close();
		
		return mensagens;
	}
}
