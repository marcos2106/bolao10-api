
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
@Table(name = "aposta_colocacao")
public class ApostaColocacao implements Serializable {

	private static final long serialVersionUID = -230423232L;

	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario", nullable = false)	
	private Usuario usuario;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "campeao", nullable = false)
	private Selecao campeao;
	
	@Column(name = "pontoscampeao", nullable = true, columnDefinition = "INT")
	private Integer pontosCampeao;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "vice", nullable = false)
	private Selecao vice;
	
	@Column(name = "pontosvice", nullable = true, columnDefinition = "INT")
	private Integer pontosVice;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "terceiro", nullable = false)
	private Selecao terceiro;
	
	@Column(name = "pontosterceiro", nullable = true, columnDefinition = "INT")
	private Integer pontosTerceiro;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "quarto", nullable = false)
	private Selecao quarto;
	
	@Column(name = "pontosquarto", nullable = true, columnDefinition = "INT")
	private Integer pontosQuarto;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "artilharia", nullable = false)
	private Selecao artilharia;
	
	@Column(name = "pontosartilharia", nullable = true, columnDefinition = "INT")
	private Integer pontosArtilharia;
	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public Integer getPontosCampeao() {
		return pontosCampeao;
	}

	public void setPontosCampeao(Integer pontosCampeao) {
		this.pontosCampeao = pontosCampeao;
	}

	public Integer getPontosVice() {
		return pontosVice;
	}

	public void setPontosVice(Integer pontosVice) {
		this.pontosVice = pontosVice;
	}

	public Integer getPontosTerceiro() {
		return pontosTerceiro;
	}

	public void setPontosTerceiro(Integer pontosTerceiro) {
		this.pontosTerceiro = pontosTerceiro;
	}

	public Integer getPontosQuarto() {
		return pontosQuarto;
	}

	public void setPontosQuarto(Integer pontosQuarto) {
		this.pontosQuarto = pontosQuarto;
	}

	public Integer getPontosArtilharia() {
		return pontosArtilharia;
	}

	public void setPontosArtilharia(Integer pontosArtilharia) {
		this.pontosArtilharia = pontosArtilharia;
	}

}
