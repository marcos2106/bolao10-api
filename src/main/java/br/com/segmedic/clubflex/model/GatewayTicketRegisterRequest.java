package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import br.com.segmedic.clubflex.domain.Invoice;

@JsonRootName(value="bank_billet")
public class GatewayTicketRegisterRequest implements Serializable {

	private static final long serialVersionUID = -704711843415820152L;
	
	@JsonProperty("amount")
	private BigDecimal amount;
	
	@JsonProperty("expire_at")
	private String expireAt;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("customer_id")
	private Long customerId;
	
	@JsonProperty("fine_type")
	private Integer fineType = 1;
	
	@JsonProperty("fine_percentage")
	private Double finePercentage = 2.0;
	
	@JsonProperty("days_for_fine")
	private Integer dayForFine = 1;
	
	@JsonProperty("interest_type")
	private Integer interestType = 3;
	
	@JsonProperty("interest_daily_percentage")
	private Double interestDailyPercentage = 0.33;
	
	@JsonProperty("days_for_interest")
	private Integer daysForInterest = 2;
	
	@JsonProperty("acceptance")
	private String acceptance = "N";
	
	@JsonProperty("bank_billet_layout_id")
	private Integer bankBilletLayoutId;
	
	@JsonProperty("document_number")
	private Long documentNumber;
	
	@JsonProperty("our_number")
	private Long ourNumber;

	public GatewayTicketRegisterRequest() {
		super();
	}

	public GatewayTicketRegisterRequest(Invoice invoice, Long customerId, Integer modelBilletLayoutId) {
		super();
		this.amount = invoice.getAmount();
		this.expireAt = invoice.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.description = "Fatura Clubflex";
		this.customerId = customerId;
		this.bankBilletLayoutId = modelBilletLayoutId;
		this.documentNumber = invoice.getId();
		this.ourNumber = invoice.getId();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(String expireAt) {
		this.expireAt = expireAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Integer getFineType() {
		return fineType;
	}

	public void setFineType(Integer fineType) {
		this.fineType = fineType;
	}

	public Double getFinePercentage() {
		return finePercentage;
	}

	public void setFinePercentage(Double finePercentage) {
		this.finePercentage = finePercentage;
	}

	public Integer getDayForFine() {
		return dayForFine;
	}

	public void setDayForFine(Integer dayForFine) {
		this.dayForFine = dayForFine;
	}

	public Integer getInterestType() {
		return interestType;
	}

	public void setInterestType(Integer interestType) {
		this.interestType = interestType;
	}

	public Double getInterestDailyPercentage() {
		return interestDailyPercentage;
	}

	public void setInterestDailyPercentage(Double interestDailyPercentage) {
		this.interestDailyPercentage = interestDailyPercentage;
	}

	public String getAcceptance() {
		return acceptance;
	}

	public void setAcceptance(String acceptance) {
		this.acceptance = acceptance;
	}

	public Integer getBankBilletLayoutId() {
		return bankBilletLayoutId;
	}

	public void setBankBilletLayoutId(Integer bankBilletLayoutId) {
		this.bankBilletLayoutId = bankBilletLayoutId;
	}

	public Long getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(Long documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Long getOurNumber() {
		return ourNumber;
	}

	public void setOurNumber(Long ourNumber) {
		this.ourNumber = ourNumber;
	}

	public Integer getDaysForInterest() {
		return daysForInterest;
	}

	public void setDaysForInterest(Integer daysForInterest) {
		this.daysForInterest = daysForInterest;
	}
	
}
