package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.domain.Selecao;

public class ColocacaoRequest implements Serializable {

	private static final long serialVersionUID = 8211297891377069719L;
	
	private Selecao campeao;
	private Selecao vice;
	private Selecao terceiro;
	private Selecao quarto;
	private Selecao artilharia;
	
	
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
