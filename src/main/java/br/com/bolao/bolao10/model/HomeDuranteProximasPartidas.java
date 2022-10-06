
package br.com.bolao.bolao10.model;

import java.io.Serializable;

import br.com.bolao.bolao10.domain.Partida;

public class HomeDuranteProximasPartidas implements Serializable {

	private static final long serialVersionUID = -24122223L;

	private Partida partida1;
	private Partida partida2;
	private Partida partida3;
	
	
	public Partida getPartida1() {
		return partida1;
	}
	public void setPartida1(Partida partida1) {
		this.partida1 = partida1;
	}
	public Partida getPartida2() {
		return partida2;
	}
	public void setPartida2(Partida partida2) {
		this.partida2 = partida2;
	}
	public Partida getPartida3() {
		return partida3;
	}
	public void setPartida3(Partida partida3) {
		this.partida3 = partida3;
	}
	
}
