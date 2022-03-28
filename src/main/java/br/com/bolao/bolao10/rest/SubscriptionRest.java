
package br.com.segmedic.clubflex.rest;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.AddDependentRequest;
import br.com.segmedic.clubflex.model.CancelDependentRequest;
import br.com.segmedic.clubflex.model.CancelSubscriptionRequest;
import br.com.segmedic.clubflex.model.ChangeDependentRequest;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.ExtraPay;
import br.com.segmedic.clubflex.model.NotificationRequest;
import br.com.segmedic.clubflex.model.PayInvoiceInfo;
import br.com.segmedic.clubflex.model.PetRequest;
import br.com.segmedic.clubflex.model.SimpleSubscriptionRequest;
import br.com.segmedic.clubflex.model.SubscriptionFilter;
import br.com.segmedic.clubflex.model.UpdatePayDayRequest;
import br.com.segmedic.clubflex.model.UpdatePaymentTypeRequest;
import br.com.segmedic.clubflex.model.UpdatePlanRequest;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.ClubFlexCardService;
import br.com.segmedic.clubflex.service.CreditCardService;
import br.com.segmedic.clubflex.service.HolderService;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.service.UserService;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.Cryptography;
import br.com.segmedic.clubflex.support.NumberUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RestController
public class SubscriptionRest extends BaseRest {

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private UserService userService;

   @Autowired
   private ClubFlexCardService clubFlexCardService;

   @Autowired
   private HolderService holderService;

   @PostMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> newSimpleSubscription(@RequestBody SimpleSubscriptionRequest subscription) {
      Long holderCreatedId = subscriptionService.preSubscription(subscription);
      return createObjectReturn(userService.loginByHolder(holderCreatedId));
   }

   @PostMapping(value = "/subscription/token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> getTokenByHolder(@RequestBody SimpleSubscriptionRequest subscription) {
      Long holderCreatedId = subscriptionService.getIdHolderByCpf(subscription);
      return createObjectReturn(userService.loginByHolder(holderCreatedId));
   }

   @PutMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> completeSubscription(HttpServletRequest request,
      @RequestBody CreateSubscriptionRequest subscription) {
      User user = userService.findUserById(getUserToken(request).getId());
      if (UserProfile.HOLDER.equals(user.getProfile())) {
         subscription.getHolder().setName(user.getHolder().getName());
         subscription.getHolder().setCpfCnpj(user.getHolder().getCpfCnpj());
         subscription.getHolder().setEmail(user.getHolder().getEmail());
      }
      Subscription createdSubscription = subscriptionService.createSubscription(subscription, user);
      super.generateLog(user.getId(), createdSubscription.getId(), SubscriptionLogAction.CADASTRO_ASSINATURA);

      if (subscription.getCreditCard() != null && subscription.getCreditCard().isChoice()
         && (PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
            || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType()))) {
         User usuario = userService.findUserByHolderId(createdSubscription.getHolder().getId());
         if (subscription.getHolder().getCellPhone() != null && !subscription.getHolder().getCellPhone().equals("")) {
            holderService.sendWelcomeSMS(subscription.getHolder().getName(), subscription.getHolder().getCellPhone(),
               Cryptography.decrypt(usuario.getPassword()));
         }
      }
      return createObjectReturn(createdSubscription);
   }

   @PostMapping(value = "/subscription/filter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> filter(@RequestBody SubscriptionFilter filter) {
      return createObjectReturn(subscriptionService.filter(filter));
   }

   @PostMapping(value = "/subscription/invoice/generate", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> generateInvoiceExtraPay(HttpServletRequest request, @RequestBody ExtraPay extraPay) {
      extraPay.setUserResponsibleId(getUserToken(request).getId());
      subscriptionService.generateInvoiceExtraPay(extraPay, getUserToken(request).getId());
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/invoice/first-generate/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> generateFirstInvoice(HttpServletRequest request, @PathVariable Long subscriptionId) {

      Subscription sub = subscriptionService.findById(subscriptionId);
      CreditCard card = creditCardService.findByHolderId(sub.getHolder().getId());
      subscriptionService.generateFirstInvoice(sub, card);

      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/invoice/simulate", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getInvoiceSimulateFirstInvoice(HttpServletRequest request,
      @RequestBody CreateSubscriptionRequest form) {
      return createObjectReturn(invoiceService.simulateFirstInvoice(form));
   }

   @GetMapping(value = "/subscription/generate/contract-file/{subscriptionId}", produces = MediaType.TEXT_HTML_VALUE)
   public @ResponseBody ResponseEntity<?> generateContractFile(@PathVariable Long subscriptionId) {
      try {
         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "inline; filename=contrato.html");
         headers.add("Content-Type", "text/html; charset=utf-8");
         return ResponseEntity
                  .ok()
                  .headers(headers)
                  .contentType(MediaType.TEXT_HTML)
                  .body(subscriptionService.generateContract(subscriptionId));
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar contrato. CONTRACT_ERROR", e);
      }
   }

   @PostMapping(value = "/subscription/generate/contract-file/new", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> generateNewContractFile(@RequestBody CreateSubscriptionRequest subscription) {
      try {
         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "inline; filename=contrato.html");
         headers.add("Content-Type", "text/html; charset=utf-8");
         return ResponseEntity
                  .ok()
                  .headers(headers)
                  .contentType(MediaType.TEXT_HTML)
                  .body(subscriptionService.generateNewContract(subscription));
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar contrato. CONTRACT_ERROR", e);
      }
   }

   @PostMapping(value = "/subscription/generate/contract-file/load/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> generateNewContractFile(@PathVariable Long subscriptionId) {
      try {
         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "inline; filename=contrato.html");
         headers.add("Content-Type", "text/html; charset=utf-8");

         Subscription subscription = subscriptionService.findById(subscriptionId);

         CreateSubscriptionRequest create = new CreateSubscriptionRequest();
         create.setPlanId(subscription.getPlan().getId());

         Holder holder = new Holder();
         holder.setName(subscription.getHolder().getName());
         holder.setCpfCnpj(subscription.getHolder().getCpfCnpj());
         holder.setAddress(subscription.getHolder().getAddress());
         holder.setComplementAddress(subscription.getHolder().getComplementAddress());
         holder.setCity(subscription.getHolder().getCity());
         holder.setUf(subscription.getHolder().getUf());
         create.setHolder(holder);

         return ResponseEntity
                  .ok()
                  .headers(headers)
                  .contentType(MediaType.TEXT_HTML)
                  .body(subscriptionService.generateNewContract(create));
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar contrato. CONTRACT_ERROR", e);
      }
   }

   @GetMapping(value = "/subscription/validate/clubcard/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> validateClubCard(@PathVariable String token) {
      return createObjectReturn(subscriptionService.validateClubCardByToken(token));
   }

   @PostMapping(value = "/subscription/update/payday", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updatePayDay(HttpServletRequest request, @RequestBody UpdatePayDayRequest form) {
      subscriptionService.updatePayDay(getUserToken(request), form.getSubscriptionId(), form.getNewDay());
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(), "Dia: ".concat(form.getNewDay().toString()),
         SubscriptionLogAction.ATUALIZACAO_DATA_VENCIMENTO);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/update/paymenttype", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updatePaymentType(HttpServletRequest request, @RequestBody UpdatePaymentTypeRequest form) {
      String formaAnterior = subscriptionService.updatePaymentType(getUserToken(request), form);
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(),
         "De: " + formaAnterior + " Para: ".concat(form.getNewType().getDescribe()), SubscriptionLogAction.ATUALIZACAO_FORMA_PAGAMENTO);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/{subscriptionId}/update/holder", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updateHolderType(HttpServletRequest request, @PathVariable Long subscriptionId) {

      subscriptionService.updateHolderType(subscriptionId);

      super.generateLog(getUserToken(request).getId(), subscriptionId,
         "De: Responsável Financeiro Para: Titular", SubscriptionLogAction.CADASTRO_ASSINATURA);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/generate/tickets", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> generateTickets(HttpServletRequest request, @RequestBody UpdatePaymentTypeRequest form) {
      subscriptionService.generateTickets(getUserToken(request), form);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/tickets/lastcompetence", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> lastCompetence(HttpServletRequest request, @RequestBody UpdatePaymentTypeRequest form) {
      Invoice lastInvoicePay = invoiceService.getLastCompetence(form.getSubscriptionId());
      String competencia = "";
      if (lastInvoicePay != null) {
         DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
         competencia = df.format(lastInvoicePay.getCompetenceBegin()) + " a " + df.format(lastInvoicePay.getCompetenceEnd());
      }
      return createObjectReturn(competencia);
   }

   @PostMapping(value = "/subscription/update/plan", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updatePlan(HttpServletRequest request, @RequestBody UpdatePlanRequest form) {
      subscriptionService.updatePlan(form, getUserToken(request));
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(), "Plano: ".concat(form.getPlanId().toString()),
         SubscriptionLogAction.ATUALIZACAO_PLANO);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/add/dependent", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> addDependent(HttpServletRequest request, @RequestBody AddDependentRequest form) {
      subscriptionService.addDependent(getUserToken(request), form);
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(), SubscriptionLogAction.ADICIONADO_DEPENDENTES);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/add/notification", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> addNotification(HttpServletRequest request, @RequestBody NotificationRequest form) {
      subscriptionService.addNotification(getUserToken(request), form);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/change/dependent", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> changeDependent(HttpServletRequest request, @RequestBody ChangeDependentRequest form) {
      subscriptionService.changeDependent(getUserToken(request), form);
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(), SubscriptionLogAction.ALTERANDO_DEPENDENTES);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/change/holder", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> changeHolder(HttpServletRequest request, @RequestBody ChangeDependentRequest form) {
      subscriptionService.changeHolder(getUserToken(request), form);
      super.generateLog(getUserToken(request).getId(), form.getSubscriptionId(), SubscriptionLogAction.ALTERANDO_DEPENDENTES);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/list/dependent/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER})
   public @ResponseBody ResponseEntity<?> listDependentBySubs(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listDependentBySubscriptionId(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/dependent", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listDependent(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listDependentBySubscriptionId(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/group/lifes", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.HOLDER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listGroupLifes(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listGroupLifes(subscriptionId, getUserToken(request)));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/log", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listLog(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listLog(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listNotifications(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listNotifications(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/has/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> hasNotifications(HttpServletRequest request, @PathVariable Long subscriptionId) {
      List<NotificationRequest> listNotifications = subscriptionService.listNotifications(subscriptionId);
      Boolean retorno = false;
      if (listNotifications != null) {
         retorno = !listNotifications.isEmpty();
      }
      return createObjectReturn(retorno);
   }

   @PostMapping(value = "/subscription/remove/dependent", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> removeDependent(HttpServletRequest request, @RequestBody CancelDependentRequest form) {
      subscriptionService.removeDependent(getUserToken(request), form);
      super.generateLog(getUserToken(request).getId(), form.getIdsubscription(), form.getReasonCancel(),
         SubscriptionLogAction.CANCELAMENTO_DEPENDENTE, form.getReason());
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/reactive/dependent/{subscriptionId}/{dependentId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.BROKER, UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> reactiveDependent(HttpServletRequest request,
      @PathVariable Long subscriptionId, @PathVariable Long dependentId) {
      subscriptionService.reactiveDependent(getUserToken(request), dependentId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.REATIVAR_DEPENDENTE);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/cancel/card/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> cancelCard(HttpServletRequest request, @PathVariable Long cardId) {
      subscriptionService.cancelClubcard(cardId);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/cancel/card/holder/{holderId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> cancelCardHolder(HttpServletRequest request, @PathVariable Long holderId) {
      subscriptionService.cancelClubcardHolder(holderId);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> listLastByUser(HttpServletRequest request) {
      return createObjectReturn(subscriptionService.getSubscriptionByUser(getUserToken(request)));
   }

   @GetMapping(value = "/subscription/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listById(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.findById(subscriptionId));
   }

   @GetMapping(value = "/subscription/load/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> loadSubsById(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.findById(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/resend/welcomekit", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> resendWelcomeKit(HttpServletRequest request, @PathVariable Long subscriptionId) {
      subscriptionService.resendWelcomeKit(subscriptionId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.REENVIO_KIT_BOASVINDAS);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/{subscriptionId}/resend/card", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> resendCardRequest(HttpServletRequest request,
      @PathVariable Long subscriptionId) {

      Subscription sub = subscriptionService.findById(subscriptionId);
      User usuario = userService.findUserByHolderId(sub.getHolder().getId());
      if (sub.getHolder().getCellPhone() != null && !sub.getHolder().getCellPhone().equals("")) {
         holderService.sendWelcomeSMS(sub.getHolder().getName(), sub.getHolder().getCellPhone(),
            Cryptography.decrypt(usuario.getPassword()));
         holderService.sendRequestCardMail(sub.getHolder().getName(), sub.getHolder().getEmail());
      }

      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/{subscriptionId}/invoice/total/nopaidamount", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> listNoPaidAmount(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.getNoPaidAmount(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listLastInvoices(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(invoiceService.listInvoiceBySubscriptionId(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/load/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER})
   public @ResponseBody ResponseEntity<?> loadInvoicesBySubs(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(invoiceService.loadInvoiceBySubs(getUserToken(request), subscriptionId));
   }

   @GetMapping(value = "/subscription/list/cards/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> loadClubCards(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(clubFlexCardService.listLastClubCardsBySubscriptionId(subscriptionId));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/list/cards", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.BROKER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listClubCards(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(clubFlexCardService.listLastClubCardsBySubscriptionId(subscriptionId));
   }

   @PostMapping(value = "/subscription/cancel/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> cancelSubscription(HttpServletRequest request, @PathVariable Long subscriptionId,
      @RequestBody CancelSubscriptionRequest form) {
      subscriptionService.cancel(subscriptionId, form, getUserToken(request), false);
      super.generateLog(getUserToken(request).getId(), subscriptionId, form.getReasonCancel(),
         SubscriptionLogAction.CANCELAMENTO_ASSINATURA, form.getIdreason());
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/cancel/immediate/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> cancelSubscriptionImmediate(HttpServletRequest request, @PathVariable Long subscriptionId,
      @RequestBody CancelSubscriptionRequest form) {
      subscriptionService.cancel(subscriptionId, form, getUserToken(request), true);
      super.generateLog(getUserToken(request).getId(), subscriptionId, form.getReasonCancel(),
         SubscriptionLogAction.CANCELAMENTO_ASSINATURA, form.getIdreason());
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/cancel/invoice/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> cancelInvoice(HttpServletRequest request, @PathVariable Long invoiceId) {
      invoiceService.cancelInvoice(invoiceId, getUserToken(request));
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/valid/pay/invoice/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> validPayInvoice(HttpServletRequest request, @PathVariable Long invoiceId) {
      return createObjectReturn(invoiceService.validPayInvoice(invoiceId, getUserToken(request)));
   }

   @PostMapping(value = "/subscription/pay/invoice", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> payInvoice(HttpServletRequest request, @RequestBody PayInvoiceInfo info) {
      return createObjectReturn(invoiceService.payInvoice(info, getUserToken(request)));
   }

   @PostMapping(value = "/subscription/changePaymentType/invoice", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> changePaymentTypeInvoice(HttpServletRequest request, @RequestBody PayInvoiceInfo info) {
      invoiceService.changePaymentTypeInvoice(info, getUserToken(request));
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/refound/invoice", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> refoundInvoice(HttpServletRequest request, @RequestBody PayInvoiceInfo info) {
      invoiceService.refoundInvoice(info, getUserToken(request));
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/block/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> blockSubscription(HttpServletRequest request, @PathVariable Long subscriptionId) {
      subscriptionService.block(subscriptionId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.BLOQUEIO_DE_ASSINATURA);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/unblock/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> unblockSubscription(HttpServletRequest request, @PathVariable Long subscriptionId) {
      subscriptionService.unBlock(subscriptionId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.DESBLOQUEIO_DE_ASSINATURA);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/previewCancellationPenaltyAmount/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> previewCancellationPenaltyAmount(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(NumberUtils.formatMoney(subscriptionService.calculateCancellationPenaltyAmount(subscriptionId)));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/preview/invoice/{month}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> previewInvoice(HttpServletRequest request, @PathVariable Long subscriptionId,
      @PathVariable Integer month, @PathVariable Integer year) {
      return createObjectReturn(subscriptionService.previewInvoice(subscriptionId, month, year));
   }

   @GetMapping(value = "/subscription/preview/invoice", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> previewInvoice(HttpServletRequest request, String token) {
      Claims claims = Jwts.parser()
               .setSigningKey(DatatypeConverter.parseBase64Binary(Constants.TOKEN_PREVIEW_INVOICE))
               .parseClaimsJws(token).getBody();
      return createObjectReturn(subscriptionService.previewInvoice(claims.get("sub", Long.class), claims.get("month", Integer.class),
         claims.get("year", Integer.class)));
   }

   @GetMapping(value = "/subscription/{subscriptionId}/preview-token/invoice/{month}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> previewInvoiceToken(HttpServletRequest request, @PathVariable Long subscriptionId,
      @PathVariable Integer month, @PathVariable Integer year) {
      return createObjectReturn(subscriptionService.generateUrlInvoicePreview(subscriptionId, month, year));
   }

   @GetMapping(value = "/subscription/load", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> listSubscriptions(HttpServletRequest request) {
      return createObjectReturn(subscriptionService.findSubscriptionByUser(getUserToken(request)));
   }

   @GetMapping(value = "/subscription/contract/accepted/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> verifyContract(HttpServletRequest request, @PathVariable Long subscriptionId) {

      Subscription sub = subscriptionService.findById(subscriptionId);
      return createObjectReturn(sub.getDateTimeAcceptedContract() != null);
   }

   @PostMapping(value = "/subscription/contract/accepted", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.HOLDER, UserProfile.DEPENDENT})
   public @ResponseBody ResponseEntity<?> updateContract(HttpServletRequest request) {

      subscriptionService.updateContract(getUserToken(request).getIdSubscription());
      super.generateLog(getUserToken(request).getId(), getUserToken(request).getIdSubscription(),
         "Dia: ".concat(new Date().toString()),
         SubscriptionLogAction.ASSINATURA_EFETUADA_CLIENTE);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/filter/invoice", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> filterInvoice(@RequestBody SubscriptionFilter filter) {
      return createObjectReturn(subscriptionService.filterInvoice(filter));
   }

   @PostMapping(value = "/subscription/transform/holder/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> transformToHolder(HttpServletRequest request,
      @PathVariable Long subscriptionId) {
      subscriptionService.transformToHolder(subscriptionId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.TRANSFORMAR_PARA_TITULAR);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/subscription/transform/respfinanc/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> transformToResponsibleFinance(HttpServletRequest request,
      @PathVariable Long subscriptionId) {
      subscriptionService.transformToResponsibleFinance(subscriptionId);
      super.generateLog(getUserToken(request).getId(), subscriptionId, SubscriptionLogAction.TRANSFORMAR_PARA_RESP_FINANCEIRO);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription/pet/list", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ALL})
   public @ResponseBody ResponseEntity<?> listPets(HttpServletRequest request) {
      return createObjectReturn(subscriptionService.listaPet());
   }

   @GetMapping(value = "/subscription/pet/list/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listPets(HttpServletRequest request, @PathVariable Long subscriptionId) {
      return createObjectReturn(subscriptionService.listaPet(subscriptionId));
   }

   @DeleteMapping(value = "/subscription/pet/{subscriptionId}/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> deletePet(HttpServletRequest request, @PathVariable Long subscriptionId,
      @PathVariable Long petId) {
      subscriptionService.deletePet(petId);
      return createObjectReturn(subscriptionService.listaPet(subscriptionId));
   }

   @PutMapping(value = "/subscription/pet", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> addPet(HttpServletRequest request, @RequestBody PetRequest petRequest) {
      subscriptionService.addPet(petRequest);
      return createObjectReturn(subscriptionService.listaPet(petRequest.getSubscriptionId()));
   }
}
