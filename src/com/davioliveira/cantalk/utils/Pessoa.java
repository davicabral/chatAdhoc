package com.davioliveira.cantalk.utils;

public class Pessoa {
	
	private String nome;
	private String email;
	private String celular;
	private String foto;
	
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
}
