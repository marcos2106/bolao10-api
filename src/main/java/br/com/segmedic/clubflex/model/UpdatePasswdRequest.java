package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UpdatePasswdRequest implements Serializable{

	private static final long serialVersionUID = -3782594950814052352L;
	
	private String passwd;

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
