package br.com.bolao.bolao10.support;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.bolao.bolao10.domain.Invoice;
import br.com.bolao.bolao10.domain.Subscription;
import br.com.bolao.bolao10.domain.enums.InvoiceStatus;

public class InvoiceBuilder {
	
	private Invoice invoice;
	
	public InvoiceBuilder(Subscription sub) {
		super();
		this.invoice = new Invoice();
		this.invoice.setSubscription(sub);
		this.invoice.setAmount(BigDecimal.ZERO);
		this.invoice.setStatus(InvoiceStatus.OPENED);
		this.invoice.setCompetenceBegin(LocalDate.now());
		this.invoice.setCompetenceEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
		this.invoice.setDueDate(LocalDate.now().plusDays(2));
	}
	
	public InvoiceBuilder withBaseDate(LocalDate baseDate) {
		this.invoice.setCompetenceBegin(baseDate);
		this.invoice.setCompetenceEnd(baseDate.withDayOfMonth(baseDate.lengthOfMonth()));
		return this;
	}
	
	public InvoiceBuilder withAmount(BigDecimal amount) {
		this.invoice.setAmount(amount);
		return this;
	}
	
	public InvoiceBuilder withDueDate(LocalDate date) {
		this.invoice.setDueDate(date);
		return this;
	}
	
	public InvoiceBuilder withInstallmentNumber(Integer installmentNumber) {
		this.invoice.setInstallmentNumber(installmentNumber);
		return this;
	}
	
	public InvoiceBuilder withCompetence(LocalDate begin, LocalDate end) {
		this.invoice.setCompetenceBegin(begin);
		this.invoice.setCompetenceEnd(end);
		return this;
	}
	
	public InvoiceBuilder withStatus(InvoiceStatus status) {
		this.invoice.setStatus(status);
		return this;
	}
	
	public InvoiceBuilder withPaymentDate(LocalDate paymentDate) {
		this.invoice.setPaymentDate(paymentDate);
		return this;
	}
	
	public InvoiceBuilder withAmountPaid(BigDecimal amount) {
		this.invoice.setPayAmount(amount);
		return this;
	}
	
	public Invoice buildWithProRateValue(BigDecimal amountBase) {
		this.invoice.setAmount(NumberUtils.proRateCalculate(amountBase, invoice.getCompetenceBegin(), invoice.getCompetenceEnd()));
		return this.invoice;
	}
	
	public Invoice buildWithProRateValue(BigDecimal amountBase, BigDecimal extraAmountNotIncludedInProRate) {
		this.invoice.setAmount(NumberUtils.proRateCalculate(amountBase, invoice.getCompetenceBegin(), invoice.getCompetenceEnd()).add(extraAmountNotIncludedInProRate));
		return this.invoice;
	}
	
	public Invoice build() {
		return this.invoice;
	}
	
}
