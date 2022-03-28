package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.support.Strings;

public class SimpleSubscriptionRequest implements Serializable{

	private static final long serialVersionUID = 8108970576938542397L;
	
	private String cpf;
	private String name;
	private String email;
	private String cellphone;
	
	public String getCpf() {
		return Strings.unnormalizeCPF(cpf);
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCellphone() {
		return cellphone;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
}
