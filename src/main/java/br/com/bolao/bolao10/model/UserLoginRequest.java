package br.com.bolao.bolao10.model;

import java.io.Serializable;

public class UserLoginRequest implements Serializable {

	private static final long serialVersionUID = 8211297891377069719L;
	
	private String login;
	private String password;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
