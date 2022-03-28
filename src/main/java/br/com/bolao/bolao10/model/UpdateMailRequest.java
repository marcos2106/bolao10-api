package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UpdateMailRequest implements Serializable {

	private static final long serialVersionUID = 6738309570561602157L;
	
	private String mail;

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
