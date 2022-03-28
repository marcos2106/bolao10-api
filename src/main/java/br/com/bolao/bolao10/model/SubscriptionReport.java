package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SubscriptionReport implements Serializable{
	
	private static final long serialVersionUID = 90924628378854116L;
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateBegin;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateEnd;
	private List<SubscriptionDetail> details;
	
	public SubscriptionReport(List<SubscriptionDetail> details) {
		super();
		this.details = details;
	}
	
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
	public List<SubscriptionDetail> getDetails() {
		return details;
	}
	public void setDetails(List<SubscriptionDetail> details) {
		this.details = details;
	}
}
