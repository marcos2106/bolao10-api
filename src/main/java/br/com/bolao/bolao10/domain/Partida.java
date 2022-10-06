
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.bolao.bolao10.model.ApostaPartida;

@Entity
@Table(name = "partida")
public class Partida implements Serializable {

	private static final long serialVersionUID = -3424324;

	@Id
	@Column(name = "idpartida", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idselecaoA", nullable = false)
	private Selecao selecaoA;

	@Column(name = "placarA", nullable = true, columnDefinition = "INT")
	private Integer placarA;

	@Column(name = "placarB", nullable = true, columnDefinition = "INT")
	private Integer placarB;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idselecaoB", nullable = false)
	private Selecao selecaoB;

	@Column(name = "iniciada", nullable = false, columnDefinition = "CHAR(1) DEFAULT 0")
	private Boolean iniciada;
	
	@Column(name = "finalizada", nullable = false, columnDefinition = "CHAR(1) DEFAULT 0")
	private Boolean finalizada;

	@DateTimeFormat(pattern = "dd/MM HH:mm")
	@Column(name = "datahora", nullable = true, columnDefinition = "DATETIME")
	private LocalDateTime dataHora;

	@Column(name = "fase", nullable = false, columnDefinition = "INT")
	private int fase;

	@Column(name = "rodada", nullable = true, columnDefinition = "INT")
	private Integer rodada;

	@Column(name = "local", nullable = true, columnDefinition = "VARCHAR(50)")
	private String local;
	
	@Transient
	private ApostaPartida aposta;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Selecao getSelecaoA() {
		return selecaoA;
	}

	public void setSelecaoA(Selecao selecaoA) {
		this.selecaoA = selecaoA;
	}

	public Integer getPlacarA() {
		return placarA;
	}

	public void setPlacarA(Integer placarA) {
		this.placarA = placarA;
	}

	public Integer getPlacarB() {
		return placarB;
	}

	public void setPlacarB(Integer placarB) {
		this.placarB = placarB;
	}

	public Selecao getSelecaoB() {
		return selecaoB;
	}

	public void setSelecaoB(Selecao selecaoB) {
		this.selecaoB = selecaoB;
	}

	public Boolean getIniciada() {
		return iniciada;
	}

	public void setIniciada(Boolean iniciada) {
		this.iniciada = iniciada;
	}

	public Boolean getFinalizada() {
		return finalizada;
	}

	public void setFinalizada(Boolean finalizada) {
		this.finalizada = finalizada;
	}

	public String getDataHoraFmt() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
		return dataHora.format(fmt);
	}
	
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM HH:mm")
	@JsonIgnore
	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public int getFase() {
		return fase;
	}

	public void setFase(int fase) {
		this.fase = fase;
	}

	public Integer getRodada() {
		return rodada;
	}

	public void setRodada(Integer rodada) {
		this.rodada = rodada;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getFaseDsc() {
		switch (this.fase) {
		case 1:
			return "Fase de Grupos";
		case 2:
			return "Oitavas de Final";
		case 3:
			return "Quartas de Final";
		case 4:
			return "Semi-Final";
		case 5:
			return "Terceiro Lugar";			
		case 6:
			return "Final";			
		default:
			return " - ";
		}
	}

	public ApostaPartida getAposta() {
		return aposta;
	}

	public void setAposta(ApostaPartida aposta) {
		this.aposta = aposta;
	}
	
}
