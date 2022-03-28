package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.support.NumberUtils;

public class PreviewInvoice implements Serializable{

	private static final long serialVersionUID = -7185903287774108400L;
	
	private Long subscriptionId;
	private String holderName;
	private Integer month;
	private Integer year;
	private Integer totalHolders;
	private Integer totalDependents;
	private BigDecimal amountPreview;
	private LocalDate dueDate;
	private List<LifeGroup> lifes;
	private LocalDate maxDateAjusts;
	
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public BigDecimal getAmountPreview() {
		return amountPreview;
	}
	public String getAmountPreviewFmt() {
		return NumberUtils.formatMoney(amountPreview);
	}
	public void setAmountPreview(BigDecimal amountPreview) {
		this.amountPreview = amountPreview;
	}
	public List<LifeGroup> getLifes() {
		return lifes;
	}
	public void setLifes(List<LifeGroup> lifes) {
		this.lifes = lifes;
	}
	public Integer getTotalHolders() {
		return totalHolders;
	}
	public void setTotalHolders(Integer totalHolders) {
		this.totalHolders = totalHolders;
	}
	public Integer getTotalDependents() {
		return totalDependents;
	}
	public void setTotalDependents(Integer totalDependents) {
		this.totalDependents = totalDependents;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getMaxDateAjusts() {
		return maxDateAjusts;
	}
	public void setMaxDateAjusts(LocalDate maxDateAjusts) {
		this.maxDateAjusts = maxDateAjusts;
	}
}
