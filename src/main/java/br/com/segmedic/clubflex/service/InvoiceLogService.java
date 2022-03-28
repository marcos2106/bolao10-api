package br.com.segmedic.clubflex.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.InvoiceLog;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.Functionality;
import br.com.segmedic.clubflex.repository.InvoiceLogRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class InvoiceLogService {

	@Autowired
	private InvoiceLogRepository invoiceLogRepository;
	
	@Transactional
	public void generateLog(User user, Invoice invoice, BigDecimal amount, Functionality action) {
		InvoiceLog log = new InvoiceLog();
		log.setUser(user);
		log.setAmount(amount);
		log.setInvoice(invoice);
		log.setDescription(action.getDescribe());
		invoiceLogRepository.save(log);
	}
	
	@Transactional
	public void generateLog(User user, Invoice invoice, Functionality action) {
		InvoiceLog log = new InvoiceLog();
		log.setUser(user);
		log.setInvoice(invoice);
		log.setDescription(action.getDescribe());
		log.setAmount(invoice.getAmount());
		invoiceLogRepository.save(log);
	}
	
}
