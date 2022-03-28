package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.com.segmedic.clubflex.support.NumberUtils;

public class ReceiptReport implements Serializable{
	
	private static final long serialVersionUID = 9092456754116L;
	
	private List<ReceiptDetail> detailsReceived;
	private List<ReceiptDetail> detailsReceivable;
	private List<ReceiptDetail> detailsDefaulters;
	private BigDecimal totalValueReceived;
	private BigDecimal totalValueReceivable;
	private BigDecimal totalValueDefaulters;
	

	public List<ReceiptDetail> getDetailsReceived() {
		return detailsReceived;
	}

	public void setDetailsReceived(List<ReceiptDetail> detailsReceived) {
		this.detailsReceived = detailsReceived;
	}

	public List<ReceiptDetail> getDetailsReceivable() {
		return detailsReceivable;
	}

	public void setDetailsReceivable(List<ReceiptDetail> detailsReceivable) {
		this.detailsReceivable = detailsReceivable;
	}

	public List<ReceiptDetail> getDetailsDefaulters() {
		return detailsDefaulters;
	}

	public void setDetailsDefaulters(List<ReceiptDetail> detailsDefaulters) {
		this.detailsDefaulters = detailsDefaulters;
	}

	public BigDecimal getTotalValueReceived() {
		return totalValueReceived;
	}

	public void setTotalValueReceived(BigDecimal totalValueReceived) {
		this.totalValueReceived = totalValueReceived;
	}

	public BigDecimal getTotalValueReceivable() {
		return totalValueReceivable;
	}

	public void setTotalValueReceivable(BigDecimal totalValueReceivable) {
		this.totalValueReceivable = totalValueReceivable;
	}

	public BigDecimal getTotalValueDefaulters() {
		return totalValueDefaulters;
	}

	public void setTotalValueDefaulters(BigDecimal totalValueDefaulters) {
		this.totalValueDefaulters = totalValueDefaulters;
	}

	public String getReceivedFmt() {
		return NumberUtils.formatMoney(this.getTotalValueReceived());
	}
	
	public String getReceivableFmt() {
		return NumberUtils.formatMoney(this.getTotalValueReceivable());
	}
	
	public String getDefaultersFmt() {
		return NumberUtils.formatMoney(this.getTotalValueDefaulters());
	}
}
