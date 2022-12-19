
package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Usuario;

public class HomeDepoisCuriosidade implements Serializable {

	private static final long serialVersionUID = -2440650223L;

	private List<HomeDepoisPlacarExato> listaPlacarExato;
	private List<Usuario> listaColocado;
	private List<HomeDepoisPlacarExato> listaNenhumPlacar;
	private List<Usuario> listaNenhumColocado;
	
	
	public List<HomeDepoisPlacarExato> getListaPlacarExato() {
		return listaPlacarExato;
	}

	public void setListaPlacarExato(List<HomeDepoisPlacarExato> listaPlacarExato) {
		this.listaPlacarExato = listaPlacarExato;
	}

	public List<Usuario> getListaColocado() {
		return listaColocado;
	}
	
	public void setListaColocado(List<Usuario> listaColocado) {
		this.listaColocado = listaColocado;
	}
	
	public List<HomeDepoisPlacarExato> getListaNenhumPlacar() {
		return listaNenhumPlacar;
	}
	
	public void setListaNenhumPlacar(List<HomeDepoisPlacarExato> listaNenhumPlacar) {
		this.listaNenhumPlacar = listaNenhumPlacar;
	}
	
	public List<Usuario> getListaNenhumColocado() {
		return listaNenhumColocado;
	}
	
	public void setListaNenhumColocado(List<Usuario> listaNenhumColocado) {
		this.listaNenhumColocado = listaNenhumColocado;
	}
	
}
