package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class LeadReport implements Serializable{

	private static final long serialVersionUID = -1436233856346228266L;
	
	private Long holderId;
	private String name;
	private String cpfCnpj;
	private String email;
	private String cellphone;
	
	public Long getHolderId() {
		return holderId;
	}
	public void setHolderId(Long holderId) {
		this.holderId = holderId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
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
