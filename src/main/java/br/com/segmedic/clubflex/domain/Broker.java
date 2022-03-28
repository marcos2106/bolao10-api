package br.com.segmedic.clubflex.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "broker")
public class Broker implements Serializable{
	
	private static final long serialVersionUID = 8233101493187094578L;

	@Id
    @Column(name = "idbroker", nullable = false, columnDefinition="INT(10)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, columnDefinition="VARCHAR(80)")
	private String name;
	
	@Column(name = "cpf", nullable = false, columnDefinition="VARCHAR(11)")
	private String cpf;
	
	@Column(name = "email", nullable = false, columnDefinition="VARCHAR(80)")
	private String email;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
