
package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Partida;

public class ApostaFilter implements Serializable {

	private static final long serialVersionUID = -48388221334601826L;

	private List<Partida> listaPartidas;
	private List<String> posicao;
	
	
	public List<Partida> getListaPartidas() {
		return listaPartidas;
	}
	public void setListaPartidas(List<Partida> listaPartidas) {
		this.listaPartidas = listaPartidas;
	}
	public List<String> getPosicao() {
		return posicao;
	}
	public void setPosicao(List<String> posicao) {
		this.posicao = posicao;
	}
	
}
