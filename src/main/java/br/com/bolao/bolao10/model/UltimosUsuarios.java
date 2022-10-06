package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Usuario;

public class UltimosUsuarios implements Serializable {

	private static final long serialVersionUID = 821117069719L;
	
	private List<Usuario> listaApostadores;
	private List<Usuario> listaFaltam;
	private List<Usuario> listaParticipantes;
	
	
	public List<Usuario> getListaApostadores() {
		return listaApostadores;
	}
	
	public void setListaApostadores(List<Usuario> listaApostadores) {
		this.listaApostadores = listaApostadores;
	}
	
	public List<Usuario> getListaFaltam() {
		return listaFaltam;
	}
	
	public void setListaFaltam(List<Usuario> listaFaltam) {
		this.listaFaltam = listaFaltam;
	}
	
	public List<Usuario> getListaParticipantes() {
		return listaParticipantes;
	}
	
	public void setListaParticipantes(List<Usuario> listaParticipantes) {
		this.listaParticipantes = listaParticipantes;
	}
	
}
