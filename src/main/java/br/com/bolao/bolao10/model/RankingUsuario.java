package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.domain.Ranking;

public class RankingUsuario implements Serializable {

	private static final long serialVersionUID = 8211212719L;
	
	private Boolean selecionado = false;
	private Ranking ranking;
	
	
	public Boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}
	
}
