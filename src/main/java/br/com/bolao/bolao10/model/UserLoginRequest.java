package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.support.Strings;

public class UserLoginRequest implements Serializable {

	private static final long serialVersionUID = 8211297891377069719L;
	
	private String login;
	private String password;
	
	public String getLogin() {
		return Strings.removeNoNumericChars(login);
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
