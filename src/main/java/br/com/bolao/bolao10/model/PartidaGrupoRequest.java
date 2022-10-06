package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Partida;

public class PartidaGrupoRequest implements Serializable {

	private static final long serialVersionUID = 8211297891377069719L;
	
	private String grupo;
	private List<Partida> listaPartidas;

	
	public String getGrupo() {
		return grupo;
	}
	
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	public List<Partida> getListaPartidas() {
		return listaPartidas;
	}
	
	public void setListaPartidas(List<Partida> listaPartidas) {
		this.listaPartidas = listaPartidas;
	}
	
}
