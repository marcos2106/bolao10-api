
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

	private int valorSeguro(Integer valor) {
		return valor == null ? 0 : valor;
	}
	
	public Integer getSomaTotal() {
		return valorSeguro(numSelecaoA) + valorSeguro(numEmpate) + valorSeguro(numSelecaoB);
	}

	private int indiceAjuste(double[] exatos, int[] arredondados, boolean aumentar) {
		int indice = 0;
		double melhorScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < arredondados.length; i++) {
			double score = aumentar ? (exatos[i] - arredondados[i]) : (arredondados[i] - exatos[i]);
			if (score > melhorScore) {
				melhorScore = score;
				indice = i;
			}
		}
		return indice;
	}

	private int[] calcularPercentuaisAjustados() {
		int total = getSomaTotal();
		if (total <= 0) {
			return new int[] {0, 0, 0};
		}

		double[] exatos = new double[] {
			(100.0 * valorSeguro(this.numSelecaoA)) / total,
			(100.0 * valorSeguro(this.numEmpate)) / total,
			(100.0 * valorSeguro(this.numSelecaoB)) / total
		};

		int[] arredondados = new int[] {
			(int) Math.round(exatos[0]),
			(int) Math.round(exatos[1]),
			(int) Math.round(exatos[2])
		};

		int diferenca = 100 - (arredondados[0] + arredondados[1] + arredondados[2]);
		while (diferenca != 0) {
			boolean aumentar = diferenca > 0;
			int indice = indiceAjuste(exatos, arredondados, aumentar);
			arredondados[indice] += aumentar ? 1 : -1;
			diferenca += aumentar ? -1 : 1;
		}

		return arredondados;
	}

	public Integer getPorcSelecaoA() {
		return calcularPercentuaisAjustados()[0];
	}
	public Integer getPorcEmpate() {
		return calcularPercentuaisAjustados()[1];
	}
	public Integer getPorcSelecaoB() {
		return calcularPercentuaisAjustados()[2];
	}
	
}
