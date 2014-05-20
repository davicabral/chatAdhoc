package com.davioliveira.cantalk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String PESSOA_TABLE_CREATE =
			"CREATE TABLE " + PessoaDAO.TABLE_NAME + " (" +
					PessoaDAO.NOME + " TEXT, " +
					PessoaDAO.FOTO + " TEXT, " +
					PessoaDAO.EMAIL + " TEXT PRIMARY KEY, " +
					PessoaDAO.CELULAR + " TEXT);";
	
	private static final String CONVERSAS_TABLE_CREATE =
			"CREATE TABLE " + MensagemDAO.TABLE_NAME + " (" +
					MensagemDAO.REM + " TEXT, " +
					MensagemDAO.EMAIL + " TEXT, " +
					MensagemDAO.MSG + " TEXT);";

	public OpenHelper(Context context) {
		super(context, PessoaDAO.TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PESSOA_TABLE_CREATE);
		db.execSQL(CONVERSAS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PessoaDAO.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MensagemDAO.TABLE_NAME);
		onCreate(db);
	}
}
