
package br.com.bolao.bolao10.model;

import java.io.Serializable;

public class PontuacaoPadrao implements Serializable {

	private static final long serialVersionUID = -24406502L;

	private int pontosCampeao;
	private int pontosVice;
	private int pontosTerceiro;
	private int pontosQuarto;
	private int pontosArtilharia;
	private int pontosPosicaoIncorreta;
	
	
	public int getPontosCampeao() {
		return pontosCampeao;
	}
	public void setPontosCampeao(int pontosCampeao) {
		this.pontosCampeao = pontosCampeao;
	}
	public int getPontosVice() {
		return pontosVice;
	}
	public void setPontosVice(int pontosVice) {
		this.pontosVice = pontosVice;
	}
	public int getPontosTerceiro() {
		return pontosTerceiro;
	}
	public void setPontosTerceiro(int pontosTerceiro) {
		this.pontosTerceiro = pontosTerceiro;
	}
	public int getPontosQuarto() {
		return pontosQuarto;
	}
	public void setPontosQuarto(int pontosQuarto) {
		this.pontosQuarto = pontosQuarto;
	}
	public int getPontosArtilharia() {
		return pontosArtilharia;
	}
	public void setPontosArtilharia(int pontosArtilharia) {
		this.pontosArtilharia = pontosArtilharia;
	}
	public int getPontosPosicaoIncorreta() {
		return pontosPosicaoIncorreta;
	}
	public void setPontosPosicaoIncorreta(int pontosPosicaoIncorreta) {
		this.pontosPosicaoIncorreta = pontosPosicaoIncorreta;
	}

}
