
package br.com.bolao.bolao10.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.repository.HolderRepository;
import br.com.bolao.bolao10.repository.InvoiceRepository;
import br.com.bolao.bolao10.repository.SubscriptionRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SubscriptionService {

   private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

   private static final int MAX_DAYS_BEFORE_AJUSTS = 2;
   private static final String MSG_REF_INC_DEPENDENT_PRO_RATA = "Ref. Inclusão do dependente %s (pro-rata)";
   private static final String MSG_REF_INC_DEPENDENT_FEE = "Taxa Adesão de dependente";
   private static final String RESOURCES_MAIL_TEMPLATES = "templates/mail";

   @Autowired
   private JmsTemplate queuePaySingleInvoice;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private HolderRepository holderRepository;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private InvoiceService invoiceService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

}
