
package br.com.segmedic.clubflex.rest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.model.Card;
import br.com.segmedic.clubflex.model.RememberPasswordRequest;
import br.com.segmedic.clubflex.model.UpdateMailRequest;
import br.com.segmedic.clubflex.model.UpdatePasswdRequest;
import br.com.segmedic.clubflex.model.UserLoginRequest;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.CreditCardService;
import br.com.segmedic.clubflex.service.RedeItauGatewayService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.service.UserService;

@RestController
public class UserRest extends BaseRest {

   @Autowired
   private UserService userService;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private JmsTemplate queueChangeCreditCard;

   @Autowired
   private RedeItauGatewayService redeGatewayService;

   private static final Logger LOGGER = LoggerFactory.getLogger(UserRest.class);

   @PostMapping(value = "/user/remember/passwd", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> externalLogin(@RequestBody RememberPasswordRequest request) {
      String email = userService.rememberPassword(request);
      return createObjectReturn("Senha enviada para o  " + email);
   }

   @PostMapping(value = "/user/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> externalLogin(@RequestBody UserLoginRequest request) {
      return createObjectReturn(userService.externalLogin(request.getLogin(), request.getPassword()));
   }

   @PostMapping(value = "/user/backoffice/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> backofficeLogin(@RequestBody UserLoginRequest request) {
      return createObjectReturn(userService.backofficeLogin(request.getLogin(), request.getPassword()));
   }

   @GetMapping(value = "/user/data", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ALL})
   public @ResponseBody ResponseEntity<?> getData(HttpServletRequest request) {
      return createObjectReturn(getUserToken(request));
   }

   @GetMapping(value = "/user/data/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getData(HttpServletRequest request, @PathVariable Long id) {
      return createObjectReturn(userService.findUserById(id));
   }

   @GetMapping(value = "/user/data/complete", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ALL})
   public @ResponseBody ResponseEntity<?> getDataComplete(HttpServletRequest request) {
      return createObjectReturn(userService.findUserById(getUserToken(request).getId()));
   }

   @PostMapping(value = "/user/update/mail", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> updateMail(HttpServletRequest request, @RequestBody UpdateMailRequest form) {
      userService.updateMail(getUserToken(request).getId(), form.getMail());
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/update/passwd", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ALL})
   public @ResponseBody ResponseEntity<?> updatePasswd(HttpServletRequest request, @RequestBody UpdatePasswdRequest form) {
      userService.updatePasswd(getUserToken(request).getId(), form.getPasswd());
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/user/list/creditcard", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER})
   public @ResponseBody ResponseEntity<?> listCreditCards(HttpServletRequest request) {
      return createObjectReturn(creditCardService.findByUserId(getUserToken(request).getId()));
   }

   @GetMapping(value = "/user/find/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> find(HttpServletRequest request, @PathVariable String name) {
      return createObjectReturn(userService.findByName(name));
   }

   @GetMapping(value = "/user/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listaAll() {
      return createObjectReturn(userService.listAll());
   }

   @PostMapping(value = "/user/update/creditcard", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updatePaymentType(HttpServletRequest request, @RequestBody Card card) {

      User user = userService.findUserById(getUserToken(request).getId());
      if (UserProfile.HOLDER.equals(user.getProfile())) {
         card.setHolderId(user.getHolder().getId());
      }
      creditCardService.updateRequest(card, true, getUserToken(request).getId());

      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/update/creditcard/pay/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> updatePaymentTypePay(HttpServletRequest request, @PathVariable Long subscriptionId) {

      Subscription sub = subscriptionService.findById(subscriptionId);

      // Efetua o pagamento de faturas pendentes e vencidas, após troca do cartão.
      List<Invoice> invoices = invoiceRepository.listInvoiceBySubscriptionIdAndStatus(subscriptionId,
         InvoiceStatus.OPENED);
      AtomicLong lastInvoice = new AtomicLong(0L);
      if (invoices != null && !invoices.isEmpty()) {
         for (Invoice invoice : invoices) {
            LOGGER.info(String.format("LOG CHANGE_CREDCARD / Fatura %s / SizeArray %s / Payload %s",
               invoice.getId(), invoices.size(), subscriptionId));
            if (invoice.isOutDate() && lastInvoice.get() != invoice.getId()) {
               try {
                  CreditCard creditCard = creditCardService.findByHolderId(sub.getHolder().getId());
                  creditCard.setHolder(sub.getHolder());
                  invoice.setCreditCard(creditCard);
                  invoice.setSubscription(sub);
                  invoice.setPaymentType(sub.getPaymentType());
                  redeGatewayService.pay(invoice, true);
                  TimeUnit.MILLISECONDS.sleep(200);
                  lastInvoice.set(invoice.getId());
               }
               catch (Exception e) {
                  LOGGER.error(e.getMessage());
               }
            }
         }
      }
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/update/creditcardqueue/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> updatePaymentTypeRequest(HttpServletRequest request, @PathVariable Long subscriptionId) {

      try {
         queueChangeCreditCard.convertAndSend(subscriptionId);
      }
      catch (Exception exc) {
      }

      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> update(@RequestBody User user) {
      userService.update(user);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> save(@RequestBody User user) {
      userService.save(user);
      return createObjectReturn(Boolean.TRUE);
   }
}
