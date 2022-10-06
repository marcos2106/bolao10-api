
package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.domain.Usuario;

public class HomeDepoisPlacarExato implements Serializable {

	private static final long serialVersionUID = -2440650223L;

	private Usuario usuario;
	private Integer quantidade;
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
}
