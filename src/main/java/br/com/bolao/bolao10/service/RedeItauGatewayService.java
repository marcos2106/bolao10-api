
package br.com.segmedic.clubflex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.Card;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.rest.BaseRest;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RedeItauGatewayService extends BaseRest {

   private static final Logger LOGGER = LoggerFactory.getLogger(RedeItauGatewayService.class);

   private static final String CONTACT_REDE_ERROR = "124";
   private static final String ALREADY_OPERATION = "122";
   private static final String SECURITY_CODE_INVALID = "119";
   private static final String NO_AVALIABLE_ONLINE_STORE = "114";
   private static final String EXPIRED_CARD = "112";
   private static final String NO_FUNDS_AVALIABLE = "111";
   private static final String RESTRICT_CARD = "105";
   private static final String VALUE_NOT_ALLOWED = "108";
   private static final String TIME_OUT = "150";
   private static final String PROBLEMS_ON_CARD = "101";
   private static final String NO_AVALIABLE_CANCEL = "371";
   private static final String FAIL_CANCEL = "370";
   private static final String NO_SUCCESS_CANCEL = "368";
   private static final String ALREADY_CANCELED = "355";
   private static final String PERIOD_EXPIRED_CANCEL = "354";
   private static final String AUTHORIZED_CANCEL_CODE2 = "360";
   private static final String AUTHORIZED_CANCEL_CODE1 = "359";
   private static final String AUTHORIZED = "00";
   private static final String TRY_AGAIN_103 = "103";
   private static final String TRY_AGAIN_104 = "104";
   private static final String TRY_AGAIN_107 = "107";

   private static final String SANDBOX = "sandbox";

   @Value("${erede.gateway.api.pv}")
   private String pv;

   @Value("${erede.gateway.api.token}")
   private String token;

   @Value("${erede.gateway.api.environment}")
   private String environment;

   @Autowired
   private JWTService jwtService;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private MailService mailService;

   @Autowired
   private SubscriptionLogService subscriptionLogService;

   @Autowired
   private CreditCardService creditCardService;

   @Transactional
   public Invoice pay(Long invoiceId) {
      Invoice invoice = invoiceRepository.findById(invoiceId);

      // Se a forma de pagamento nao for cartao pegar o cartao do usuario
      if (!PaymentType.CREDIT_CARD.equals(invoice.getPaymentType())) {
         invoice.setCreditCard(creditCardService.findByHolderId(invoice.getSubscription().getHolder().getId()));
      }

      return pay(invoice, true);
   }

   @Transactional
   public Invoice payRetry(Long invoiceId) {
      Invoice invoice = invoiceRepository.findById(invoiceId);

      // Se a forma de pagamento nao for cartao pegar o cartao do usuario
      if (!PaymentType.CREDIT_CARD.equals(invoice.getPaymentType())) {
         invoice.setCreditCard(creditCardService.findByHolderId(invoice.getSubscription().getHolder().getId()));
      }

      invoice.getCreditCard().setRecurrency(invoice.getCreditCard().getRecurrency() + 1);
      invoice.getCreditCard().setDateLastRecurrency(LocalDate.now());
      return pay(invoice, true);
   }

   @Transactional
   public Boolean payRecurrency(Invoice invoice) {

      Calendar cal = Calendar.getInstance();
      int diaDoMes = cal.get(Calendar.DAY_OF_MONTH);

      try {
         System.out.println("passou");
         // Se a forma de pagamento nao for cartao pegar o cartao do usuario
         if (!PaymentType.CREDIT_CARD.equals(invoice.getPaymentType())) {
            invoice.setCreditCard(creditCardService.findByHolderId(invoice.getSubscription().getHolder().getId()));
         }

         if (invoice.getCreditCard() == null) {
            throw new Exception();
         }

         if (diaDoMes == 1) {
            if (!invoice.getCreditCard().getDateLastRecurrency().equals(LocalDate.now())) {
               invoice.getCreditCard().setDateLastRecurrency(LocalDate.now());
               invoice.getCreditCard().setRecurrency(0l);
            }
         }

         if (invoice.getCreditCard().getRecurrency() <= (invoice.getSubscription().getPlan().getAttempts() == null ? 60
            : invoice.getSubscription().getPlan().getAttempts())) {
            if (invoice.getCreditCard().getLastReturnCode().equals(AUTHORIZED)) {

               pay(invoice, true);
            }
            else if (invoice.getCreditCard().getLastReturnCode().equals(NO_FUNDS_AVALIABLE)
               || invoice.getCreditCard().getLastReturnCode().equals(TRY_AGAIN_103)
               || invoice.getCreditCard().getLastReturnCode().equals(TRY_AGAIN_104)
               || invoice.getCreditCard().getLastReturnCode().equals(TRY_AGAIN_107)
               || invoice.getCreditCard().getLastReturnCode().equals(RESTRICT_CARD)
               || invoice.getCreditCard().getLastReturnCode().equals(VALUE_NOT_ALLOWED)
               || invoice.getCreditCard().getLastReturnCode().equals(FAIL_CANCEL)
               || invoice.getCreditCard().getLastReturnCode().equals(TIME_OUT)
               || invoice.getCreditCard().getLastReturnCode().equals(NO_SUCCESS_CANCEL)) {
               // if (invoice.getCreditCard().getDateLastRecurrency() == null ||
               // invoice.getCreditCard().getDateLastRecurrency().plusDays(5).isEqual(LocalDate.now().minusDays(1)) ||
               // invoice.getCreditCard().getDateLastRecurrency().plusDays(5).isBefore(LocalDate.now().minusDays(1))) {
               invoice.getCreditCard().setRecurrency(invoice.getCreditCard().getRecurrency() + 1);
               invoice.getCreditCard().setDateLastRecurrency(LocalDate.now());

               boolean enviaEmail;

               if (invoice.getSubscription().getPlan().getAttempts() == null && invoice.getCreditCard().getRecurrency() <= 3) {
                  enviaEmail = false;
               }
               else if (invoice.getSubscription().getPlan().getAttempts() == null
                  && (invoice.getCreditCard().getRecurrency() > 3 && invoice.getCreditCard().getRecurrency() <= 8)) {
                  enviaEmail = true;
               }
               else if (invoice.getSubscription().getPlan().getAttempts() == null && invoice.getCreditCard().getRecurrency() > 8) {
                  enviaEmail = false;
               }
               else {
                  enviaEmail = true;
               }

               pay(invoice, enviaEmail);
               // }
            }
            else {
               invoice.getCreditCard().setRecurrency(invoice.getCreditCard().getRecurrency() + 1);
               creditCardService.update(invoice.getCreditCard());
            }
         }
      }
      catch (Exception exc) {
         return true;
      }

      if (!invoice.getCreditCard().getLastReturnCode().equals(AUTHORIZED)) {
         return true;
      }
      return false;
   }

   @Transactional
   public Boolean test(CreditCard creditCard, PaymentType type) {
      Card card = jwtService.readJwtTokenCreditCard(creditCard.getCardHash());

      return false;
   }

   @Transactional
   public Invoice pay(Invoice invoice, Boolean sendaMail) {

      if (invoice == null) {
         throw new ClubFlexException("Fatura inválida");
      }
      return invoice;
   }

   @Transactional
   public Invoice refund(Long invoiceId) {
      return refund(invoiceRepository.findById(invoiceId));
   }

   @Transactional
   public Invoice refund(Invoice invoice) {
      if (invoice == null) {
         throw new ClubFlexException("Fatura inválida");
      }
      if (invoice.getTransactId() == null) {
         throw new ClubFlexException("Transação do cartão não informada");
      }

      return invoice;
   }

   @Transactional
   public Invoice refundPartial(Invoice invoice, BigDecimal refundAmount) {
      if (invoice == null) {
         throw new ClubFlexException("Fatura inválida");
      }
      if (invoice.getTransactId() == null) {
         throw new ClubFlexException("Transação do cartão não informada");
      }


      return invoice;
   }


}
