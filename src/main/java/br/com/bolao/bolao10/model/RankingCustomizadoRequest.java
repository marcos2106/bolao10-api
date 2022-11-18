package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

public class RankingCustomizadoRequest implements Serializable {

	private static final long serialVersionUID = 81112719L;
	
	private Long id;
	private List<RankingUsuario> listaRankingUsuario;
	private String nome;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<RankingUsuario> getListaRankingUsuario() {
		return listaRankingUsuario;
	}
	
	public void setListaRankingUsuario(List<RankingUsuario> listaRankingUsuario) {
		this.listaRankingUsuario = listaRankingUsuario;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
