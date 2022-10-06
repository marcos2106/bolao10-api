
package br.com.bolao.bolao10.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "gol")
public class Gol implements Serializable {

	private static final long serialVersionUID = -2311212L;

	@Id
	@Column(name = "idgol", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idjogador", nullable = false)
	private Jogador jogador;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idpartida", nullable = false)
	private Partida partida;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idselecao", nullable = false)
	private Selecao selecao;

	@Column(name = "minuto", nullable = false, columnDefinition = "INT")
	private int minuto;
	
	@Column(name = "golcontra", nullable = false, columnDefinition = "CHAR(1) DEFAULT '0'")
	private Boolean golcontra;

	@Transient
	private Long idJogador;
	
	@Transient
	private Long contraIdJogador;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Jogador getJogador() {
		return jogador;
	}

	public void setJogador(Jogador jogador) {
		this.jogador = jogador;
	}

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public Selecao getSelecao() {
		return selecao;
	}

	public void setSelecao(Selecao selecao) {
		this.selecao = selecao;
	}

	public int getMinuto() {
		return minuto;
	}

	public void setMinuto(int minuto) {
		this.minuto = minuto;
	}

	public Boolean getGolcontra() {
		return golcontra;
	}

	public void setGolcontra(Boolean golcontra) {
		this.golcontra = golcontra;
	}

	public Long getIdJogador() {
		return idJogador;
	}

	public void setIdJogador(Long idJogador) {
		this.idJogador = idJogador;
	}

	public Long getContraIdJogador() {
		return contraIdJogador;
	}

	public void setContraIdJogador(Long contraIdJogador) {
		this.contraIdJogador = contraIdJogador;
	}

}
