package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Classificacao;

public class ClassificacaoGrupo implements Serializable {

	private static final long serialVersionUID = 82112911069719L;
	
	private String grupo;
	private List<Classificacao> listaClassificacao;
	
	
	public String getGrupo() {
		return grupo;
	}
	
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	public List<Classificacao> getListaClassificacao() {
		return listaClassificacao;
	}
	
	public void setListaClassificacao(List<Classificacao> listaClassificacao) {
		this.listaClassificacao = listaClassificacao;
	}
	
}
