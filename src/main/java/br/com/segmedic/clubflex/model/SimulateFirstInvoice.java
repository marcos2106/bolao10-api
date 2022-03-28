package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.support.NumberUtils;

public class SimulateFirstInvoice implements Serializable{
	
	private static final long serialVersionUID = -2375569269897676730L;
	
	private BigDecimal proRataAmount;
	private BigDecimal accessionFee;
	private BigDecimal totalAmount;
	private LocalDate dataBegin;
	private LocalDate dataEnd;
	private Long quantityDays;
	
	public BigDecimal getProRataAmount() {
		return proRataAmount;
	}
	public void setProRataAmount(BigDecimal proRataAmount) {
		this.proRataAmount = proRataAmount;
	}
	public BigDecimal getAccessionFee() {
		return accessionFee;
	}
	public void setAccessionFee(BigDecimal accessionFee) {
		this.accessionFee = accessionFee;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDataBegin() {
		return dataBegin;
	}
	public void setDataBegin(LocalDate dataBegin) {
		this.dataBegin = dataBegin;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(LocalDate dataEnd) {
		this.dataEnd = dataEnd;
	}
	public Long getQuantityDays() {
		return quantityDays;
	}
	public void setQuantityDays(Long quantityDays) {
		this.quantityDays = quantityDays;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getProRataAmountFmt() {
		return NumberUtils.formatMoney(this.proRataAmount);
	}
	public String getAccessionFeeFmt() {
		return NumberUtils.formatMoney(this.accessionFee);
	}
	public String getTotalAmountFmt() {
		return NumberUtils.formatMoney(this.totalAmount);
	}
	
}
