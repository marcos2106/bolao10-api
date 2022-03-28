package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;

public class ClubCardValidateResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String cpf;
	private String number;
	private ClubCardStatus status;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public ClubCardStatus getStatus() {
		return status;
	}
	public void setStatus(ClubCardStatus status) {
		this.status = status;
	}
}

