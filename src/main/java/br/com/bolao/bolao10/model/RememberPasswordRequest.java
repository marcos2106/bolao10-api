package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.support.Strings;

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
