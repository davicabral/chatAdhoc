package com.davioliveira.cantalk.utils;

import android.net.wifi.p2p.WifiP2pDevice;

public class Pessoa {
	
	private String nome;
	private String email;
	private String celular;
	private String foto;
	private WifiP2pDevice peer;
	
	public Pessoa()
	{
		
	}
	public Pessoa(String n,String e,String cel, byte[] photo)
	{
		nome = n;
		email = e;
		celular = cel;
		foto = foto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public WifiP2pDevice getPeer() {
		return peer;
	}
	public void setPeer(WifiP2pDevice peer) {
		this.peer = peer;
	}
	
}
