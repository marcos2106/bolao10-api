package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.support.Strings;

public class SubscriptionFilter implements Serializable{

	private static final long serialVersionUID = 3752434383604576815L;
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateBegin;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateEnd;
	private String cpfCnpjHolder;
	private String nameHolder;
	private String dependentName;
	private Long cardNumber;
	private Long idSubscription;
	private Long planId;
	private String phone;
	private String email;
	private SubscriptionStatus status;
	private String nsu;
	
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
	public String getCpfCnpjHolder() {
		return Strings.removeNoNumericChars(cpfCnpjHolder);
	}
	public void setCpfCnpjHolder(String cpfCnpjHolder) {
		this.cpfCnpjHolder = cpfCnpjHolder;
	}
	public String getNameHolder() {
		return nameHolder;
	}
	public void setNameHolder(String nameHolder) {
		this.nameHolder = nameHolder;
	}
	public String getDependentName() {
		return dependentName;
	}
	public void setDependentName(String dependentName) {
		this.dependentName = dependentName;
	}
	public Long getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}
	public Long getIdSubscription() {
		return idSubscription;
	}
	public void setIdSubscription(Long idSubscription) {
		this.idSubscription = idSubscription;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public SubscriptionStatus getStatus() {
		return status;
	}
	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}
	public String getNsu() {
		return nsu;
	}
	public void setNsu(String nsu) {
		this.nsu = nsu;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
