
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
@Table(name = "aposta")
public class Aposta implements Serializable {

	private static final long serialVersionUID = -12244L;

	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idpartida", nullable = false)	
	private Partida partida;

	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;

	@Column(name = "placarA", nullable = true, columnDefinition = "INT")
	private Integer placarA;
	
	@Column(name = "placarB", nullable = true, columnDefinition = "INT")
	private Integer placarB;
	
	@Column(name = "pontuacao", nullable = true, columnDefinition = "INT")
	private Integer pontuacao;
	
	@Column(name = "pontuacao_provisoria", nullable = true, columnDefinition = "INT")
	private Integer pontuacaoProvisoria;
	
	
	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getPlacarA() {
		return placarA;
	}

	public void setPlacarA(Integer placarA) {
		this.placarA = placarA;
	}

	public Integer getPlacarB() {
		return placarB;
	}

	public void setPlacarB(Integer placarB) {
		this.placarB = placarB;
	}

	public Integer getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Integer pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Integer getPontuacaoProvisoria() {
		return pontuacaoProvisoria;
	}

	public void setPontuacaoProvisoria(Integer pontuacaoProvisoria) {
		this.pontuacaoProvisoria = pontuacaoProvisoria;
	}

}
