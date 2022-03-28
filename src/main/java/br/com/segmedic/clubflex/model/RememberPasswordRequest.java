package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.support.Strings;

public class RememberPasswordRequest implements Serializable {

	private static final long serialVersionUID = 4447494143891201697L;
	
	private String login;

	public String getLogin() {
		return Strings.removeNoNumericChars(login);
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
}
