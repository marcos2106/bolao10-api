
package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeUsuarioGrafico implements Serializable {

	private static final long serialVersionUID = -4812121226L;

	private List<String> listaDatas;
	private List<Integer> listaPontuacao;
	private List<Integer> listaPontuacaoLider;
	private List<Integer> listaPosicao;
	
	
	public List<String> getListaDatas() {
		if (listaDatas == null) {
			listaDatas = new ArrayList<String>();
		}
		return listaDatas;
	}
	
	public void setListaDatas(List<String> listaDatas) {
		this.listaDatas = listaDatas;
	}
	
	public List<Integer> getListaPontuacao() {
		if (listaPontuacao == null) {
			listaPontuacao = new ArrayList<Integer>();
		}
		return listaPontuacao;
	}
	
	public void setListaPontuacao(List<Integer> listaPontuacao) {
		this.listaPontuacao = listaPontuacao;
	}
	
	public List<Integer> getListaPontuacaoLider() {
		if (listaPontuacaoLider == null) {
			listaPontuacaoLider = new ArrayList<Integer>();
		}
		return listaPontuacaoLider;
	}
	
	public void setListaPontuacaoLider(List<Integer> listaPontuacaoLider) {
		this.listaPontuacaoLider = listaPontuacaoLider;
	}
	
	public List<Integer> getListaPosicao() {
		if (listaPosicao == null) {
			listaPosicao = new ArrayList<Integer>();
		}
		return listaPosicao;
	}
	
	public void setListaPosicao(List<Integer> listaPosicao) {
		this.listaPosicao = listaPosicao;
	}
	
}
