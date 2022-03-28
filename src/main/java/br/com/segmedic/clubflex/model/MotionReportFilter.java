package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MotionReportFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateBegin;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateEnd;
	private Long subscriptionId;
	private Long userResponsiblePayment;
	
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
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Long getUserResponsiblePayment() {
		return userResponsiblePayment;
	}
	public void setUserResponsiblePayment(Long userResponsiblePayment) {
		this.userResponsiblePayment = userResponsiblePayment;
	}
}
