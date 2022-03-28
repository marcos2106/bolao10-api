package br.com.segmedic.clubflex.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Company implements Serializable{
	
	private static final long serialVersionUID = 3751702650134739366L;

	@Id
    @Column(name = "idcompany", nullable = false, columnDefinition="INT(10)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, columnDefinition="VARCHAR(80)")
	private String name;
	
	@Column(name = "cnpj", nullable = true, columnDefinition="VARCHAR(14)")
	private String cnpj;
	
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
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
}
