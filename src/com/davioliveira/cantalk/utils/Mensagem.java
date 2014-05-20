package com.davioliveira.cantalk.utils;

public class Mensagem {
	
	private String msgEnviada,msgOk,remVc,remContato,email;
	
	public Mensagem(String rEnviada ,String mEnviada,String rContato,String mOk) {
		// TODO Auto-generated constructor stub
		msgEnviada = mEnviada;
		remVc = rEnviada;
		msgOk = mOk;
		remContato = rContato;
	}
	
	public Mensagem(String rContato,String mOk) {
		// TODO Auto-generated constructor stub
		msgEnviada = mOk;
		remContato = rContato;
	}
	
	public Mensagem() {
		// TODO Auto-generated constructor stub
		
	}

	public String getMsgEnviada() {
		return msgEnviada;
	}

	public void setMsgEnviada(String msg1) {
		this.msgEnviada = msg1;
	}

	public String getMsgOk() {
		return msgOk;
	}

	public void setMsgOk(String msg2) {
		this.msgOk = msg2;
	}

	public String getRemVc() {
		return remVc;
	}

	public void setRemVc(String rem1) {
		this.remVc = rem1;
	}

	public String getRemContato() {
		return remContato;
	}

	public void setRemContato(String rem2) {
		this.remContato = rem2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
