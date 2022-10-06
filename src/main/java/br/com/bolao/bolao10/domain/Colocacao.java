
package br.com.bolao.bolao10.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "colocacao")
public class Colocacao implements Serializable {

	private static final long serialVersionUID = -230423232L;

	@Id
	@Column(name = "idcolocacao", nullable = false, columnDefinition = "INT")
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "campeao", nullable = false)
	private Selecao campeao;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "vice", nullable = false)
	private Selecao vice;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "terceiro", nullable = false)
	private Selecao terceiro;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "quarto", nullable = false)
	private Selecao quarto;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "artilharia", nullable = false)
	private Selecao artilharia;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Selecao getCampeao() {
		return campeao;
	}

	public void setCampeao(Selecao campeao) {
		this.campeao = campeao;
	}

	public Selecao getVice() {
		return vice;
	}

	public void setVice(Selecao vice) {
		this.vice = vice;
	}

	public Selecao getTerceiro() {
		return terceiro;
	}

	public void setTerceiro(Selecao terceiro) {
		this.terceiro = terceiro;
	}

	public Selecao getQuarto() {
		return quarto;
	}

	public void setQuarto(Selecao quarto) {
		this.quarto = quarto;
	}

	public Selecao getArtilharia() {
		return artilharia;
	}

	public void setArtilharia(Selecao artilharia) {
		this.artilharia = artilharia;
	}
	
}
