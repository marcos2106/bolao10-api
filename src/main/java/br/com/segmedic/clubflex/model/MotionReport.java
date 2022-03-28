package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;

public class MotionReport implements Serializable{
	
	private static final long serialVersionUID = 90924628378854116L;
	
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateBegin;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateEnd;
	private List<MotionDetail> details;
	private MotionReportTotal totalDefault;
	private MotionReportTotal totalExtras;
	private MotionReportTotal totalAgreement;
	private MotionReportTotal totalGeneral;
	
	public MotionReport(List<MotionDetail> details) {
		super();
		this.details = details;
		this.totalDefault = new MotionReportTotal();
		this.totalExtras = new MotionReportTotal();
		this.totalAgreement = new MotionReportTotal();
		this.totalGeneral = new MotionReportTotal();
		
		calculateTotalDefault();
		calculateTotalExtra();
		calculateTotalAgreement();
		calculateTotalGeneral();
	}
	
	private void calculateTotalGeneral() {
		this.details.forEach(detail->{
			if (detail.getAmountPaid()!=null) {
				this.totalGeneral.addTotal(detail.getAmountPaid());
				if(PaymentType.CREDIT_CARD.equals(detail.getPaymentType())) {
					this.totalGeneral.addCreditCard(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD.equals(detail.getPaymentType())) {
					this.totalGeneral.addDebitCard(detail.getAmountPaid());
				}
				if(PaymentType.TICKET.equals(detail.getPaymentType())) {
					this.totalGeneral.addTicket(detail.getAmountPaid());
				}
				if(PaymentType.TICKETS.equals(detail.getPaymentType())) {
					this.totalGeneral.addTickets(detail.getAmountPaid());
				}
				if(PaymentType.CREDIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalGeneral.addCreditCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalGeneral.addDebitCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.MONEY.equals(detail.getPaymentType())) {
					this.totalGeneral.addMoney(detail.getAmountPaid());
				}
			}
		});
	}

	private void calculateTotalAgreement() {
		this.details.forEach(detail->{
			if(InvoiceType.AGREEMENT.equals(detail.getInvoiceType())) {
				this.totalAgreement.addTotal(detail.getAmountPaid());
				if(PaymentType.CREDIT_CARD.equals(detail.getPaymentType())) {
					this.totalAgreement.addCreditCard(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD.equals(detail.getPaymentType())) {
					this.totalAgreement.addDebitCard(detail.getAmountPaid());
				}
				if(PaymentType.TICKET.equals(detail.getPaymentType())) {
					this.totalAgreement.addTicket(detail.getAmountPaid());
				}
				if(PaymentType.TICKETS.equals(detail.getPaymentType())) {
					this.totalAgreement.addTickets(detail.getAmountPaid());
				}
				if(PaymentType.CREDIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalAgreement.addCreditCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalAgreement.addDebitCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.MONEY.equals(detail.getPaymentType())) {
					this.totalAgreement.addMoney(detail.getAmountPaid());
				}
			}
		});
	}

	private void calculateTotalExtra() {
		this.details.forEach(detail->{
			if(InvoiceType.EXTRA.equals(detail.getInvoiceType())) {
				this.totalExtras.addTotal(detail.getAmountPaid());
				if(PaymentType.CREDIT_CARD.equals(detail.getPaymentType())) {
					this.totalExtras.addCreditCard(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD.equals(detail.getPaymentType())) {
					this.totalExtras.addDebitCard(detail.getAmountPaid());
				}
				if(PaymentType.TICKET.equals(detail.getPaymentType())) {
					this.totalExtras.addTicket(detail.getAmountPaid());
				}
				if(PaymentType.TICKETS.equals(detail.getPaymentType())) {
					this.totalExtras.addTickets(detail.getAmountPaid());
				}
				if(PaymentType.CREDIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalExtras.addCreditCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalExtras.addDebitCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.MONEY.equals(detail.getPaymentType())) {
					this.totalExtras.addMoney(detail.getAmountPaid());
				}
			}
		});
	}

	private void calculateTotalDefault() {
		this.details.forEach(detail->{
			if(InvoiceType.DEFAULT.equals(detail.getInvoiceType())) {
				this.totalDefault.addTotal(detail.getAmountPaid());
				if(PaymentType.CREDIT_CARD.equals(detail.getPaymentType())) {
					this.totalDefault.addCreditCard(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD.equals(detail.getPaymentType())) {
					this.totalDefault.addDebitCard(detail.getAmountPaid());
				}
				if(PaymentType.TICKET.equals(detail.getPaymentType())) {
					this.totalDefault.addTicket(detail.getAmountPaid());
				}
				if(PaymentType.TICKETS.equals(detail.getPaymentType())) {
					this.totalDefault.addTickets(detail.getAmountPaid());
				}
				if(PaymentType.CREDIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalDefault.addCreditCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.DEBIT_CARD_LOCAL.equals(detail.getPaymentType())) {
					this.totalDefault.addDebitCardLocal(detail.getAmountPaid());
				}
				if(PaymentType.MONEY.equals(detail.getPaymentType())) {
					this.totalDefault.addMoney(detail.getAmountPaid());
				}
			}
		});
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
	public List<MotionDetail> getDetails() {
		return details;
	}
	public void setDetails(List<MotionDetail> details) {
		this.details = details;
	}
	public MotionReportTotal getTotalDefault() {
		return totalDefault;
	}
	public void setTotalDefault(MotionReportTotal totalDefault) {
		this.totalDefault = totalDefault;
	}
	public MotionReportTotal getTotalExtras() {
		return totalExtras;
	}
	public void setTotalExtras(MotionReportTotal totalExtras) {
		this.totalExtras = totalExtras;
	}
	public MotionReportTotal getTotalAgreement() {
		return totalAgreement;
	}
	public void setTotalAgreement(MotionReportTotal totalAgreement) {
		this.totalAgreement = totalAgreement;
	}
	public MotionReportTotal getTotalGeneral() {
		return totalGeneral;
	}
	public void setTotalGeneral(MotionReportTotal totalGeneral) {
		this.totalGeneral = totalGeneral;
	}
}
