
package br.com.bolao.bolao10.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Invoice;
import br.com.bolao.bolao10.domain.enums.InvoiceStatus;
import br.com.bolao.bolao10.repository.InvoiceRepository;
import br.com.bolao.bolao10.repository.SubscriptionRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class InvoiceService {

   private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private JmsTemplate queueMonthlyInvoice;

   @Autowired
   private JmsTemplate queueSubscriptionOperation;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private UserService userService;

   @Autowired
   private JmsTemplate queueSubscriptionLog;


   @Value("${enotas.emitir}")
   private Boolean deveEmitirNfe;

   private static final int TWELVE_MONTHS = 12;

   public List<Invoice> listInvoiceByStatus(InvoiceStatus status) {
      return invoiceRepository.listInvoiceByStatus(status);
   }

}
