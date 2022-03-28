package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PlanReportFilter implements Serializable {

	private static final long serialVersionUID = -1371550282927504400L;

	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateBegin;
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateEnd;
	
	private Long idPlan;
	private Long idBroker;
	private Long idUser;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(LocalDate dateBegin) {
		this.dateBegin = dateBegin;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(LocalDate dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Long getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(Long idPlan) {
		this.idPlan = idPlan;
	}

	public Long getIdBroker() {
		return idBroker;
	}

	public void setIdBroker(Long idBroker) {
		this.idBroker = idBroker;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
}
