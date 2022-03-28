package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import br.com.segmedic.clubflex.excel.ExcelField;

public class MotionReportExcel implements Serializable{

	private static final String DD_MM_YYYY = "dd/MM/yyyy";

	private static final long serialVersionUID = 8333257083672222794L;
	
	private Long invoiceId;
	private String invoiceType;
	private String competenceBegin;   
	private String competenceEnd;
	private String amountPaid;
	private String dueDate;
	private String paymentDate;
	private String holderName;
	private Integer delay;
	private String paymentType;
	private Long subscriptionId;
	private String nfeNumber;
	private String authorizationCode;
	private String userReponsable;
	private String nsu;
	
	public MotionReportExcel(MotionDetail detail) {
		super();
		this.invoiceId = detail.getInvoiceId();
		this.invoiceType = detail.getInvoiceType().getDescribe();
		this.competenceBegin = detail.getCompetenceBegin().format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
		this.competenceEnd = detail.getCompetenceEnd().format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
		this.amountPaid = detail.getAmountPaidFmt();
		this.dueDate = detail.getDueDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
		this.paymentDate =  detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY));;
		this.holderName = detail.getHolderName();
		this.delay = detail.getDelay();
		this.paymentType = detail.getPaymentType().getDescribe();
		this.subscriptionId = detail.getSubscriptionId();
		this.nfeNumber = detail.getNfeNumber();
		this.authorizationCode = detail.getAuthorizationCode();
		this.userReponsable = detail.getUserResponsable();
		this.nsu = detail.getNsu();
	}
	
	@ExcelField(nome="Fatura", posicao=0)
	public Long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	@ExcelField(nome="Tipo", posicao=1)
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	@ExcelField(nome="Comp.Inicial", posicao=2)
	public String getCompetenceBegin() {
		return competenceBegin;
	}
	public void setCompetenceBegin(String competenceBegin) {
		this.competenceBegin = competenceBegin;
	}
	
	@ExcelField(nome="Comp.Final", posicao=3)
	public String getCompetenceEnd() {
		return competenceEnd;
	}
	public void setCompetenceEnd(String competenceEnd) {
		this.competenceEnd = competenceEnd;
	}
	
	@ExcelField(nome="Valor Pago", posicao=4)
	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	@ExcelField(nome="Dt. Vencimento", posicao=5)
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	@ExcelField(nome="Dt. Pagamento", posicao=6)
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	@ExcelField(nome="Titular", posicao=7)
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	
	@ExcelField(nome="Dias Atraso", posicao=8)
	public Integer getDelay() {
		return delay;
	}
	public void setDelay(Integer delay) {
		this.delay = delay;
	}
	
	@ExcelField(nome="Tp. Pagamento", posicao=9)
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	@ExcelField(nome="Assinatura", posicao=10)
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	
	@ExcelField(nome="Nfe", posicao=11)
	public String getNfeNumber() {
		return nfeNumber;
	}
	public void setNfeNumber(String nfeNumber) {
		this.nfeNumber = nfeNumber;
	}
	
	@ExcelField(nome="Autorização", posicao=12)
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	@ExcelField(nome="Usuário", posicao=13)
	public String getUserReponsable() {
		return userReponsable;
	}

	public void setUserReponsable(String userReponsable) {
		this.userReponsable = userReponsable;
	}

	@ExcelField(nome="NSU", posicao=14)
	public String getNsu() {
		return nsu;
	}

	public void setNsu(String nsu) {
		this.nsu = nsu;
	}
}
