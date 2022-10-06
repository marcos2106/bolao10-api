
package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.support.Constants;

public class HomeUsuario implements Serializable {

	private static final long serialVersionUID = -4812121226L;

	private Long pontuacao;
	private Long posicao;
	private Long melhorPosicao;
	private String melhorPosicaoData;
	private Long placarExato;
	private Long totalPartida;

	private Usuario usuario;
	private String dataPagamento;
	private String dataAposta;
	

	public Long getPosicao() {
		return posicao;
	}
	public void setPosicao(Long posicao) {
		this.posicao = posicao;
	}
	public Long getMelhorPosicao() {
		return melhorPosicao;
	}
	public void setMelhorPosicao(Long melhorPosicao) {
		this.melhorPosicao = melhorPosicao;
	}
	public String getMelhorPosicaoData() {
		return melhorPosicaoData;
	}
	public void setMelhorPosicaoData(String melhorPosicaoData) {
		this.melhorPosicaoData = melhorPosicaoData;
	}
	public Long getPlacarExato() {
		return placarExato;
	}
	public void setPlacarExato(Long placarExato) {
		this.placarExato = placarExato;
	}
	public Long getAproveitamento() {
		
		Long maxPontos = getTotalPartida() * Constants.APOSTA_CORRETA;
		Long aproveitamento = 0L;
		if (maxPontos>0) {
			aproveitamento = getPontuacao() * 100 / maxPontos;
		}
		return aproveitamento;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(String dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public String getDataAposta() {
		return dataAposta;
	}
	public void setDataAposta(String dataAposta) {
		this.dataAposta = dataAposta;
	}
	public Long getPontuacao() {
		return pontuacao;
	}
	public void setPontuacao(Long pontuacao) {
		this.pontuacao = pontuacao;
	}
	public Long getTotalPartida() {
		return totalPartida;
	}
	public void setTotalPartida(Long totalPartida) {
		this.totalPartida = totalPartida;
	}

}
