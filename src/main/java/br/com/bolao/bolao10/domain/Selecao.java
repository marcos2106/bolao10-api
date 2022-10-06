
package br.com.bolao.bolao10.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "selecao")
public class Selecao implements Serializable {

	private static final long serialVersionUID = -2304582L;

	@Id
	@Column(name = "idselecao", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome", nullable = false, columnDefinition = "VARCHAR(100)")
	private String nome;

	@Column(name = "imagem", nullable = false, columnDefinition = "VARCHAR(100)")
	private String imagem;

	@Column(name = "ativo", nullable = false, columnDefinition = "CHAR(1) DEFAULT 1")
	private Boolean ativo;

	@Column(name = "grupo", nullable = false, columnDefinition = "VARCHAR(1)")
	private String grupo;
	
	@Column(name = "cor", nullable = false, columnDefinition = "VARCHAR(7)")
	private String cor;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

}
