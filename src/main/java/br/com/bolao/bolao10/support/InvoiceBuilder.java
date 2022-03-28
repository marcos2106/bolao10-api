package br.com.segmedic.clubflex.support;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.google.common.collect.Sets;

import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.InvoiceDetail;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.model.Amount;

public class InvoiceBuilder {
	
	private Invoice invoice;
	
	public InvoiceBuilder(Subscription sub) {
		super();
		this.invoice = new Invoice();
		this.invoice.setDetails(Sets.newConcurrentHashSet());
		this.invoice.setSubscription(sub);
		this.invoice.setAmount(BigDecimal.ZERO);
		this.invoice.setType(InvoiceType.DEFAULT);
		this.invoice.setStatus(InvoiceStatus.OPENED);
		this.invoice.setPaymentType(sub.getPaymentType());
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
	
	public InvoiceBuilder withPaymentType(PaymentType paymentType) {
		this.invoice.setPaymentType(paymentType);
		return this;
	}
	
	public InvoiceBuilder addAmount(BigDecimal amount, String describe) {
		this.invoice.setAmount(this.invoice.getAmount().add(amount));
		this.invoice.getDetails().add(new InvoiceDetail(describe, amount, invoice));
		return this;
	}
	
	public InvoiceBuilder addAmounts(List<Amount> amounts) {
		amounts.forEach(a->{
			addAmount(a.getAmount(), a.getDescribe());
		});
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
	
	public InvoiceBuilder withType(InvoiceType type) {
		this.invoice.setType(type);
		return this;
	}
	
	public InvoiceBuilder withStatus(InvoiceStatus status) {
		this.invoice.setStatus(status);
		return this;
	}
	
	public InvoiceBuilder addDetail(String detail, BigDecimal price) {
		this.invoice.getDetails().add(new InvoiceDetail(detail, price, this.invoice));
		return this;
	}
	
	public InvoiceBuilder addDetails(List<InvoiceDetail> details) {
		this.invoice.getDetails().addAll(details);
		return this;
	}
	
	public InvoiceBuilder withPaymentDate(LocalDate paymentDate) {
		this.invoice.setPaymentDate(paymentDate);
		return this;
	}
	
	public InvoiceBuilder withCreditCard(CreditCard card) {
		this.invoice.setCreditCard(card);
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
