package com.davioliveira.cantalk.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.davioliveira.cantalk.utils.Pessoa;

public class PessoaDAO {
	private SQLiteDatabase database;
	private OpenHelper dbHelper;

    public static final String TABLE_NAME = "pessoa";
    public static final String NOME = "nome";
    public static final String EMAIL = "email";
    public static final String CELULAR = "celular";
    public static final String FOTO = "foto";
    
	private String[] allColumns = { NOME, EMAIL, CELULAR, FOTO };

	public PessoaDAO(Context context) {
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

	public long insert(Pessoa pessoa) {
		ContentValues values = new ContentValues();
		values.put(NOME, pessoa.getNome());
		values.put(EMAIL, pessoa.getEmail());
		values.put(CELULAR, pessoa.getCelular());
		values.put(FOTO, pessoa.getFoto());

		return database.insert(TABLE_NAME, null, values);
	}
	
	public long update(Pessoa pessoa) {
		ContentValues values = new ContentValues();
		values.put(NOME, pessoa.getNome());
		values.put(EMAIL, pessoa.getEmail());
		values.put(CELULAR, pessoa.getCelular());
		values.put(FOTO, pessoa.getFoto());

		return database.update(TABLE_NAME, values, EMAIL + " = '" + pessoa.getEmail() + "'", null);
	}

	public boolean delete(Pessoa pessoa) {
		long wReturn = database.delete(TABLE_NAME, EMAIL + " = '" + pessoa.getEmail() + "'", null);
		if(wReturn == 0) {
			return false;
		} else {
			return true;
		}
	}

	public Pessoa read(String email) {
		Pessoa pessoa;
		
		Cursor c = database.query(TABLE_NAME, allColumns, EMAIL + " = '" + email + "'", null,null, null, null); 
		if(c.moveToFirst()) {
			
			int indexNome = c.getColumnIndex(NOME);
			int indexEmail = c.getColumnIndex(EMAIL);
			int indexCelular = c.getColumnIndex(CELULAR);
			int indexFoto = c.getColumnIndex(FOTO);
			
			pessoa = new Pessoa();
			pessoa.setNome(c.getString(indexNome));
			pessoa.setEmail(c.getString(indexEmail));
			pessoa.setCelular(c.getString(indexCelular));
			pessoa.setFoto(c.getString(indexFoto));
			
			return pessoa;
		} else {
			return null;
		}
	}
	
	public ArrayList<Pessoa> readAll() {
		Pessoa pessoa;
		ArrayList<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);
		if(c.moveToFirst()) {
			pessoas = new ArrayList<Pessoa>();
			
			int indexNome = c.getColumnIndex(NOME);
			int indexEmail = c.getColumnIndex(EMAIL);
			int indexCelular = c.getColumnIndex(CELULAR);
			int indexFoto = c.getColumnIndex(FOTO);
			
			do {
				pessoa = new Pessoa();
				pessoa.setNome(c.getString(indexNome));
				pessoa.setEmail(c.getString(indexEmail));
				pessoa.setCelular(c.getString(indexCelular));
				pessoa.setFoto(c.getString(indexFoto));
				
				pessoas.add(pessoa);
			} while(c.moveToNext());
		}
		
		c.close();
		
		return pessoas;
	}
}
