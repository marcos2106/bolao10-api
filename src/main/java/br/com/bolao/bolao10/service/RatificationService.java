
package br.com.segmedic.clubflex.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Ratification;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.Functionality;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.CreateRatification;
import br.com.segmedic.clubflex.repository.RatificationRepository;
import br.com.segmedic.clubflex.repository.SubscriptionLogRepository;
import br.com.segmedic.clubflex.repository.UserRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RatificationService {

   @Autowired
   private RatificationRepository ratificationRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private SubscriptionLogRepository subscriptionLogRepository;

   @Autowired
   private InvoiceService invoiceService;

   public List<Ratification> listRatification() {
      return ratificationRepository.listRatification();
   }

   public List<Ratification> listRatification(User user) {
      return ratificationRepository.listRatificationByUser(user);
   }

   @Transactional
   public void approval(CreateRatification createRatification, User usuario) {

      Ratification ratification = ratificationRepository.findById(createRatification.getId());

      if (ratification == null) {
         throw new ClubFlexException("Ratificação não encontrada.");
      }

      validateManager(usuario);

      if (ratification.getFunctionality() == Functionality.QUITAR_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();

         // Método que quita uma fatura.
         invoiceService.payInvoiceApproved(invoice);

      }
      else if (ratification.getFunctionality() == Functionality.CANCELAR_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();

         // Método que cancela uma fatura.
         invoiceService.cancelInvoiceApproved(invoice);

      }
      else if (ratification.getFunctionality() == Functionality.ALT_FORMA_PGTO_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();

         String descricao = "APROVADA. Justificativa: " + ratification.getJustification() + " "
            + "De: " + invoice.getPaymentType().getDescribe() + " Para: " + invoice.getNewPaymentType().getDescribe();
         this.saveLog(usuario.getId(), invoice.getSubscription().getId(),
            descricao, SubscriptionLogAction.ALT_FORMA_PAGAMENTO_FATURA);

         // Método que altera a forma de pagamento de uma fatura.
         invoiceService.changePaymentTypeInvoiceApproved(invoice, ratification);
      }
      ratification.setIsPending(Boolean.FALSE);
      ratification.setIsApproved(Boolean.TRUE);
      ratification.setRatificationAt(LocalDateTime.now());

      User user = userRepository.findById(usuario.getId());
      ratification.setManager(user);

      ratificationRepository.save(ratification);
   }

   @Transactional
   public void deny(CreateRatification createRatification, User usuario) {

      Ratification ratification = ratificationRepository.findById(createRatification.getId());

      validateManager(usuario);

      if (ratification.getFunctionality() == Functionality.QUITAR_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();

         invoice.setStatus(InvoiceStatus.DENIED);

         if (invoice.getUserResponsiblePayment() != null) {
            invoiceService.generateLog(invoice.getUserResponsiblePayment(), invoice.getSubscription().getId(),
               "Fatura: ".concat(invoice.getId().toString()), SubscriptionLogAction.QUITACAO_FATURA_NEGADA);
         }

      }
      else if (ratification.getFunctionality() == Functionality.CANCELAR_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();

         invoice.setStatus(InvoiceStatus.DENIED);

         if (invoice.getUserResponsiblePayment() != null) {
            invoiceService.generateLog(invoice.getUserResponsiblePayment(), invoice.getSubscription().getId(),
               "Fatura: ".concat(invoice.getId().toString()), SubscriptionLogAction.CANCELAMENTO_FATURA_NEGADA);
         }
      }
      else if (ratification.getFunctionality() == Functionality.ALT_FORMA_PGTO_FATURA) {

         if (ratification.getInvoice() == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         Invoice invoice = ratification.getInvoice();
         invoice.setStatus(InvoiceStatus.PAID);

         String descricao = "NEGADA. Justificativa: " + createRatification.getJustification() + " "
            + "De: " + invoice.getPaymentType().getDescribe() + " Para: " + invoice.getNewPaymentType().getDescribe();
         this.saveLog(usuario.getId(), invoice.getSubscription().getId(),
            descricao, SubscriptionLogAction.ALT_FORMA_PAGAMENTO_FATURA);

      }
      ratification.setJustification(createRatification.getJustification());
      ratification.setIsPending(Boolean.FALSE);
      ratification.setIsApproved(Boolean.FALSE);
      ratification.setRatificationAt(LocalDateTime.now());

      User user = userRepository.findById(usuario.getId());
      ratification.setManager(user);

      ratificationRepository.save(ratification);
   }

   private void validateManager(User userResponsable) {
      if (!UserProfile.MANAGER.equals(userResponsable.getProfile())) {
         throw new ClubFlexException("Ratificação não permitida. Solicite ao gerente.");
      }
   }

   public void saveLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      log.setObs(obs);
      subscriptionLogRepository.saveLog(log);
   }
}
