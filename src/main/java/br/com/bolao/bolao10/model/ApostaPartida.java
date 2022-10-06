
package br.com.bolao.bolao10.model;

import java.io.Serializable;

public class ApostaPartida implements Serializable {

	private static final long serialVersionUID = -24122223L;

	private Integer numSelecaoA;
	private Integer numEmpate;
	private Integer numSelecaoB;
	
	
	public Integer getNumSelecaoA() {
		return numSelecaoA;
	}
	public void setNumSelecaoA(Integer numSelecaoA) {
		this.numSelecaoA = numSelecaoA;
	}
	public Integer getNumEmpate() {
		return numEmpate;
	}
	public void setNumEmpate(Integer numEmpate) {
		this.numEmpate = numEmpate;
	}
	public Integer getNumSelecaoB() {
		return numSelecaoB;
	}
	public void setNumSelecaoB(Integer numSelecaoB) {
		this.numSelecaoB = numSelecaoB;
	}
	
	public Integer getSomaTotal() {
		return numSelecaoA + numEmpate + numSelecaoB;
	}
	public Integer getPorcSelecaoA() {
		if (getSomaTotal() > 0) {
			return (100 * this.numSelecaoA) / getSomaTotal();
		} 
		return 0;
	}
	public Integer getPorcEmpate() {
		if (getSomaTotal() > 0) {
			return (100 * this.numEmpate) / getSomaTotal();
		} 
		return 0;
	}
	public Integer getPorcSelecaoB() {
		if (getSomaTotal() > 0) {
			return (100 * this.numSelecaoB) / getSomaTotal();
		} 
		return 0;
	}
	
}
