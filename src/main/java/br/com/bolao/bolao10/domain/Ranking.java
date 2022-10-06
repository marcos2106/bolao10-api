
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
@Table(name = "ranking")
public class Ranking implements Serializable {

	private static final long serialVersionUID = -121114L;

	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario", nullable = false)	
	private Usuario usuario;

	@Column(name = "pontuacao", nullable = true, columnDefinition = "INT")
	private Integer pontuacao;
	
	@Column(name = "pontuacao_provisoria", nullable = true, columnDefinition = "INT")
	private Integer pontuacaoProvisoria;
	
	@Column(name = "posicaoanterior", nullable = true, columnDefinition = "INT")
	private Integer posicaoAnterior;

	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Integer pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Integer getPosicaoAnterior() {
		return posicaoAnterior;
	}

	public void setPosicaoAnterior(Integer posicaoAnterior) {
		this.posicaoAnterior = posicaoAnterior;
	}

	public Integer getPontuacaoProvisoria() {
		return pontuacaoProvisoria;
	}

	public void setPontuacaoProvisoria(Integer pontuacaoProvisoria) {
		this.pontuacaoProvisoria = pontuacaoProvisoria;
	}
	
}
