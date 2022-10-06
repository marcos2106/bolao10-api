package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Partida;

public class PartidasOutras implements Serializable {

	private static final long serialVersionUID = 82112L;
	
	private List<Partida> outrasPartidasA;
	private List<Partida> outrasPartidasB;
	
	
	public List<Partida> getOutrasPartidasA() {
		return outrasPartidasA;
	}
	
	public void setOutrasPartidasA(List<Partida> outrasPartidasA) {
		this.outrasPartidasA = outrasPartidasA;
	}
	
	public List<Partida> getOutrasPartidasB() {
		return outrasPartidasB;
	}
	
	public void setOutrasPartidasB(List<Partida> outrasPartidasB) {
		this.outrasPartidasB = outrasPartidasB;
	}

}
