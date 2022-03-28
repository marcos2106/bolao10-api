
package br.com.segmedic.clubflex.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import br.com.segmedic.clubflex.domain.ClubCard;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.InvoiceDetail;
import br.com.segmedic.clubflex.domain.ParamsSubscription;
import br.com.segmedic.clubflex.domain.Pet;
import br.com.segmedic.clubflex.domain.Plan;
import br.com.segmedic.clubflex.domain.Reason;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.PetEnum;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.AddDependentRequest;
import br.com.segmedic.clubflex.model.Amount;
import br.com.segmedic.clubflex.model.CancelDependentRequest;
import br.com.segmedic.clubflex.model.CancelSubscriptionRequest;
import br.com.segmedic.clubflex.model.ChangeDependentRequest;
import br.com.segmedic.clubflex.model.ClubCardValidateResponse;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.DependentType;
import br.com.segmedic.clubflex.model.ExtraPay;
import br.com.segmedic.clubflex.model.LifeGroup;
import br.com.segmedic.clubflex.model.NotificationRequest;
import br.com.segmedic.clubflex.model.PetRequest;
import br.com.segmedic.clubflex.model.PreviewInvoice;
import br.com.segmedic.clubflex.model.SimpleSubscriptionRequest;
import br.com.segmedic.clubflex.model.SubLog;
import br.com.segmedic.clubflex.model.SubscriptionFilter;
import br.com.segmedic.clubflex.model.SubscriptionLoad;
import br.com.segmedic.clubflex.model.UpdatePaymentTypeRequest;
import br.com.segmedic.clubflex.model.UpdatePlanRequest;
import br.com.segmedic.clubflex.repository.DependentRepository;
import br.com.segmedic.clubflex.repository.HolderRepository;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.repository.NotificationRepository;
import br.com.segmedic.clubflex.repository.ReasonRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.repository.UserRepository;
import br.com.segmedic.clubflex.support.CNPJValidator;
import br.com.segmedic.clubflex.support.CPFValidator;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.Cryptography;
import br.com.segmedic.clubflex.support.CurrencyWriter;
import br.com.segmedic.clubflex.support.EmailValidator;
import br.com.segmedic.clubflex.support.HolidayUtils;
import br.com.segmedic.clubflex.support.InvoiceBuilder;
import br.com.segmedic.clubflex.support.NumberUtils;
import br.com.segmedic.clubflex.support.Strings;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.exception.RestClientException;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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
   private UserRepository userRepository;

   @Autowired
   private HolderService holderService;

   @Autowired
   private BrokerService brokerService;

   @Autowired
   private CompanyService companyService;

   @Autowired
   private SystemParamsService systemParamsService;

   @Autowired
   private UserService userService;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private DependentRepository dependentRepository;

   @Autowired
   private HolderRepository holderRepository;

   @Autowired
   private ReasonRepository reasonRepository;

   @Autowired
   private NotificationRepository notificationRepository;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private PlanService planService;

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private ClubFlexCardService clubFlexCardService;

   @Autowired
   private DependentService dependentService;

   @Autowired
   private PetService petService;

   @Autowired
   private SystemParamsService paramService;

   @Autowired
   private TicketGatewayService ticketGatewayService;

   @Autowired
   private SubscriptionLogService subscriptionLogService;

   @Autowired
   private MailService mailService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   public void resendWelcomeKit(Long subscriptionId) {
      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }

      // Obtendo usuario do titular
      User user = userService.findUserByHolderId(sub.getHolder().getId());

      if (user == null) {
         user = userRepository.findByLogin(sub.getHolder().getCpfCnpj());
      }

      // E-mail de Boas Vindas
      holderService.sendWelcomeMail(sub.getHolder().getName(), sub.getHolder().getEmail(), Cryptography.decrypt(user.getPassword()));

      // E-mail de contrato
      this.sendContract(sub);
   }

   @Transactional
   public Long preSubscription(SimpleSubscriptionRequest subscription) {
      if (StringUtils.isBlank(subscription.getCpf())) {
         throw new ClubFlexException("CPF é obrigatório para assinatura.");
      }
      if (!CPFValidator.isValido(subscription.getCpf())) {
         throw new ClubFlexException("CPF inválido.");
      }

      // Verificar se ja possui uma assinatura como titular
      List<Subscription> subsHolder = findSubscriptionByHolderCpfCnpjNotResponsibleFinancial(subscription.getCpf());
      if (subsHolder != null && !subsHolder.isEmpty()) {
         throw new ClubFlexException("Você já é titular em uma assinatura. Acesse sua conta!");
      }

      // Verificar se ja possui uma assinatura como dependente (ok ou bloqueada)
      /**
       * Trocar por outra forma melhor List<Subscription> subsDependent = findSubscriptionByHolderCpfCnpj(subscription.getCpf());
       * AtomicBoolean isDepedent = new AtomicBoolean(false); subsDependent.forEach(s->{ if(SubscriptionStatus.OK.equals(s.getStatus()) ||
       * SubscriptionStatus.BLOCKED.equals(s.getStatus())) { isDepedent.set(true); } }); if(isDepedent.get()) { throw new
       * ClubFlexException("Você já é dependente em uma assinatura. Acesse sua conta, ou solicite ao titular a sua retirada."); }
       */
      List<Dependent> listaDependente = dependentRepository.findByCPFAndAtivo(subscription.getCpf());
      if (listaDependente != null && !listaDependente.isEmpty()) {
         throw new ClubFlexException("Você já é dependente em uma assinatura. Acesse sua conta, ou solicite ao titular a sua retirada.");
      }

      Holder holder = holderService.findByCpfCnpj(subscription.getCpf());
      if (holder == null) {
         holder = new Holder();
         holder.setName(subscription.getName());
         holder.setCpfCnpj(Strings.removeNoNumericChars(subscription.getCpf()));
         holder.setEmail(subscription.getEmail());
         holder.setCellPhone(subscription.getCellphone());
         holder.setIsHolder(Boolean.TRUE);
         holderService.save(holder);
      }
      else {
         User user = userService.findUserByHolderId(holder.getId());

         if (user == null) {
            user = userRepository.findByLogin(holder.getCpfCnpj());
            user.setHolder(holder);
         }

         user.setName(subscription.getName());
         user.setEmail(subscription.getEmail());
         user.setPassword(Cryptography.decrypt(user.getPassword()));

         holder.setName(subscription.getName());
         holder.setEmail(subscription.getEmail());
         holder.setCellPhone(subscription.getCellphone());
         userService.save(user);
         holderService.save(holder);
         // throw new ClubFlexException("Você tem um cadastro pendente de conclusão!");
         // return holder;
      }
      return holder.getId();
   }

   public Long getIdHolderByCpf(SimpleSubscriptionRequest subscription) {
      Holder holder = holderService.findByCpfCnpj(subscription.getCpf());
      return holder.getId();
   }

   @Transactional
   public void updatePayDay(User userRequester, Long subscriptionId, Integer newDay) {
      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      if (!SubscriptionStatus.OK.equals(sub.getStatus())) {
         throw new ClubFlexException("Assinatura cancelada ou bloqueada. Operação não permitida.");
      }

      validaHolderRole(userRequester, sub);

      Long days = ChronoUnit.DAYS.between(sub.getUpdateAtPayDay(), LocalDateTime.now());
      if (days <= 30) {
         throw new ClubFlexException(
            String.format("Existe um alteração de vencimento efetuada a %s dias. O vencimento só pode ser alterado a cada 30 dias.", days));
      }

      // atualizando datas de vencimento
      invoiceService.updateDueDateInvoices(sub, newDay);

      // atualizar assinatura
      sub.setDayForPayment(newDay);
      sub.setUpdateAtPayDay(LocalDateTime.now());
      subscriptionRepository.save(sub);
   }

   /**
    * Método responsável por alterar o plano, após a aprovação do gerente
    * 
    * @param invoice - Invoice
    * @return alteração de plano
    */
   @Transactional
   public void updatePlanAprroved(Subscription sub) {

   }

   /**
    * Método responsável por deixar uma alteração de plano pendente de aprovação pelo gerente.
    * 
    * @param form - UpdatePlanRequest
    * @param userResponsable - User
    * @return alteração de plano
    */
   @Transactional
   public void updatePlan(UpdatePlanRequest form, User userRequester) {
      if (form.getSubscriptionId() == null) {
         throw new ClubFlexException("Assinatura não informada.");
      }
      if (form.getPlanId() == null) {
         throw new ClubFlexException("Plano não informado.");
      }
      Subscription sub = findById(form.getSubscriptionId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }

      if (sub.getPlan().getId().equals(form.getPlanId())) {
         throw new ClubFlexException("Assinatura já possui plano informado.");
      }

      Plan plan = planService.findById(form.getPlanId());
      sub.setPlan(plan);

      // valida se o plano, para qual a assinatura vai ser alterado,
      // permite dependentes e se a assinatura já tem dependentes
      if (validaPlanoPermiteDependentes(sub, plan)) {
         throw new ClubFlexException("Plano selecionado não permite dependentes.");
      }

      // pagamentos
      LocalDate baseDate = LocalDate.now();
      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {
         invoiceService.cancelInvoiceNotOverDue(sub, userRequester); // cancela todos os boletos em aberto
         invoiceService.generateInvoicesTypeTickets(sub, baseDate, null, true);
      }

      subscriptionRepository.save(sub);
   }

   private boolean validaPlanoPermiteDependentes(Subscription sub, Plan plan) {
      if (plan.getTypeSub() == TypeSub.PF && !plan.getHasdependent()) {
         for (Dependent dep : sub.getDependents()) {
            if (dep.getStatus() == DependentStatus.OK) {
               return true;
            }
         }
      }
      return false;
   }

   @Transactional
   public void generateTickets(User userRequester, UpdatePaymentTypeRequest form) {
      Subscription sub = findById(form.getSubscriptionId());
      Invoice lastInvoicePay = invoiceService.getLastCompetence(form.getSubscriptionId());
      LocalDate baseDate = LocalDate.now();
      if (lastInvoicePay != null)
         baseDate = lastInvoicePay.getCompetenceEnd().plusDays(1);
      invoiceService.generateTickets12MonthsService(sub, baseDate, baseDate);
   }

   @Transactional
   public void updateHolderType(long idSub) {
      Subscription sub = new Subscription();
      sub = findById(idSub);

      List<Subscription> subscriptions = findSubscriptionByHolderCpfCnpjNotResponsibleFinancial(sub.getHolder().getCpfCnpj());
      if (subscriptions != null && !subscriptions.isEmpty() && !sub.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException(
            "O usuário é titular de outra assinatura. Operação não realizada.");
      }

      sub.setHolderOnlyResponsibleFinance(false);
      subscriptionRepository.merge(sub);
   }

   @Transactional
   public String updatePaymentType(User userRequester, UpdatePaymentTypeRequest form) {
      if (form.getNewType() == null) {
         throw new ClubFlexException("Nova forma de pagamento não informada.");
      }
      if (form.getSubscriptionId() == null) {
         throw new ClubFlexException("Assinatura não informada.");
      }
      if ((PaymentType.CREDIT_CARD.equals(form.getNewType()) || PaymentType.DEBIT_CARD.equals(form.getNewType()))
         && form.getCard() == null) {
         throw new ClubFlexException("Informe os dados do cartão.");
      }

      Subscription sub = findById(form.getSubscriptionId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      String formaAnterior = sub.getPaymentType().getDescribe();

      validaHolderRole(userRequester, sub);

      if (sub.getPaymentType().equals(form.getNewType())) {
         throw new ClubFlexException("A forma de pagamento informada já consta na assinatura.");
      }

      if (PaymentType.CREDIT_CARD.equals(form.getNewType()) || PaymentType.DEBIT_CARD.equals(form.getNewType())) {
         // Preparando para atualizar o cartao
         form.getCard().setHolderId(sub.getHolder().getId());
         // Atualizando cartao de cred/deb
         creditCardService.update(form.getCard(), form.getNewType(), true, userRequester.getId());
      }
      // ESSE É O PONTO DE GERACAO DAS FATURAS RETROATIVAS
      Invoice lastInvoicePay = invoiceService.getLastInvoicePay(form.getSubscriptionId());
      if (form.getNewType().equals(PaymentType.TICKETS)) {
         sub.setPaymentType(form.getNewType());

         // Se titular nunca pagou, usar data atual
         LocalDate baseDate = LocalDate.now();

         if (lastInvoicePay != null) {
            baseDate = lastInvoicePay.getCompetenceEnd();
            // gerar faturas com isenção de taxa de adesão
            invoiceService.generateInvoicesTypeTickets(sub, baseDate, baseDate, true);
         }
         else {
            // gerar faturas com de taxa de adesão, pois o titular nunca pagou nenhuma fatura
            invoiceService.generateInvoicesTypeTickets(sub, baseDate, baseDate, false);
         }
      }
      else {
         // Se o tipo atual é ticket e está migrando para cartao ou boleto cancelar todos os boletos não vencidos do carne
         if (sub.getPaymentType().equals(PaymentType.TICKETS)) {
            // De acordo com o Renan (planilha), não precisa esperar o cliente pagar o primeiro carnê para alterar a forma de pgto.
            /*
             * if(lastInvoicePay == null) { throw new
             * ClubFlexException("Para mudar de CARNÊ para outra forma de pagamento, o cliente deve ter ao menos a PRIMEIRA fatura PAGA.");
             * }
             */
            // cancelar todos os pagamentos não vencidos
            invoiceService.cancelInvoiceNotOverDue(sub, userRequester);
         }
         sub.setPaymentType(form.getNewType());
      }

      if (sub.getStatus().equals(SubscriptionStatus.REQUESTED_CARD)
         && (!PaymentType.CREDIT_CARD.equals(form.getNewType()) && !PaymentType.DEBIT_CARD.equals(form.getNewType()))) {
         sub.setStatus(SubscriptionStatus.BLOCKED);
      }

      if (sub.getStatus().equals(SubscriptionStatus.BLOCKED)
         && (PaymentType.CREDIT_CARD.equals(form.getNewType()) && PaymentType.DEBIT_CARD.equals(form.getNewType()))) {

         if (creditCardService.findByHolderId(sub.getHolder().getId()) == null) {
            sub.setStatus(SubscriptionStatus.REQUESTED_CARD);
         }

      }

      subscriptionRepository.save(sub);

      return formaAnterior;
   }

   @Transactional
   public void removeDependent(User userRequester, CancelDependentRequest form) {

      Long idreason = form.getReason();
      String reasonCancel = form.getReasonCancel();

      if (form.getIddependent() == null) {
         throw new ClubFlexException("Dependente não informado.");
      }

      if (idreason == null) {
         throw new ClubFlexException("Selecione o Motivo do cancelamento.");
      }
      Reason reason = reasonRepository.findById(idreason);
      if (reason == null) {
         throw new ClubFlexException("Motivo do cancelamento não encontrado.");
      }
      if (StringUtils.isBlank(reasonCancel)) {
         throw new ClubFlexException("Informe a observação do cancelamento.");
      }

      Dependent dependent = dependentService.getById(form.getIddependent());
      if (dependent == null) {
         throw new ClubFlexException("Dependente inválido.");
      }
      if (DependentStatus.REMOVED.equals(dependent.getStatus())) {
         throw new ClubFlexException("Dependente já cancelado.");
      }

      Subscription sub = findById(dependent.getSubscription().getId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      if (SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
         throw new ClubFlexException("Assinatura cancelada. Operação não permitida.");
      }

      if (sub.getHolderOnlyResponsibleFinance()) {
         boolean validarDep = false;
         for (Dependent dep : sub.getDependents()) {
            if (dep.getStatus() == DependentStatus.OK && dep.getDateOfRemoval() == null && !dep.equals(dependent)) {
               validarDep = true;
               break;
            }
         }
         if (!validarDep) {
            throw new ClubFlexException("Assinatura não pode ter Responsável Financeiro sem dependentes.");
         }
      }

      validaHolderRole(userRequester, sub);

      // definir data de cancelamento do dependente.
      LocalDateTime dateToCancel = LocalDateTime.now();

      Invoice invoiceDependent = null;
      // Procurar fatura para que foi responsavel pela entrada o dependente no sistema
      List<Invoice> invoices = invoiceService.listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.PAID);
      for (Invoice invoice : invoices) {
         for (InvoiceDetail detail : invoice.getDetails()) {
            if (detail.getDescribe().contains(dependent.getName())) {
               invoiceDependent = invoice;
            }
         }
      }

      Invoice lastInvoice = invoiceService.getLastInvoicePay(sub.getId(), InvoiceType.DEFAULT);
      if (lastInvoice != null) {

         if (form.isImmediateDependent()) {
            dateToCancel = LocalDateTime.now();
         }
         else if (invoiceDependent == null) {
            dateToCancel = lastInvoice.getCompetenceEnd().atTime(23, 59);
         }
         else if (invoiceDependent != null && invoiceDependent.getId() < lastInvoice.getId()) {
            dateToCancel = lastInvoice.getCompetenceEnd().atTime(23, 59);
         }
         else {
            dateToCancel = invoiceDependent.getCompetenceEnd().atTime(23, 59);
         }
      }

      // cancelar dependent
      for (Dependent dep : sub.getDependents()) {
         if (dep.getId().equals(dependent.getId())) {
            dep.setDateOfRemoval(dateToCancel);
            dep.setReason(reason);
            dep.setReasonCancellation(reasonCancel);
            if (dateToCancel.isBefore(LocalDateTime.now()) || dateToCancel.isEqual(LocalDateTime.now())) {
               dep.setStatus(DependentStatus.REMOVED);
            }
         }
      }

      // cancelar cartao do dependent
      if (dateToCancel.isBefore(LocalDateTime.now()) || dateToCancel.isEqual(LocalDateTime.now())) {
         sub.getCards().forEach(card -> {
            if (card.getDependent() != null && card.getDependent().equals(dependent)) {
               card.setStatus(ClubCardStatus.CANCELED);
               card.setDateCancel(LocalDateTime.now());
            }
         });
      }

      subscriptionRepository.save(sub);

      // pagamentos
      LocalDate baseDate = LocalDate.now();
      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {
         invoiceService.cancelInvoiceNotOverDue(sub, userRequester); // cancela todos os boletos em aberto
         Invoice lastInvoicePay = invoiceService.getLastInvoicePay(sub.getId());
         // gera novos boletos com o dependente removido
         if (lastInvoicePay != null) {
            baseDate = lastInvoicePay.getCompetenceEnd();
            invoiceService.generateInvoicesTypeTickets(sub, baseDate, null, true);
         }
         else {
            invoiceService.generateInvoicesTypeTickets(sub, baseDate, null, false);
         }
      }
   }

   @Transactional
   public void reactiveDependent(User userRequester, Long dependentId) {
      if (dependentId == null) {
         throw new ClubFlexException("Dependente não informado.");
      }

      Dependent dependent = dependentService.getById(dependentId);
      if (dependent == null) {
         throw new ClubFlexException("Dependente inválido.");
      }
      if (DependentStatus.OK.equals(dependent.getStatus())
         && dependent.getDateOfRemoval() == null) {
         throw new ClubFlexException("Dependente já está ativo.");
      }

      Subscription sub = findById(dependent.getSubscription().getId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      if (SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
         throw new ClubFlexException("Assinatura cancelada. Operação não permitida.");
      }

      if (sub.getPlan().getTypeSub() == TypeSub.PF && !sub.getPlan().getHasdependent()) {
         throw new ClubFlexException("O plano dessa assinatura não permite dependentes.");
      }

      validaHolderRole(userRequester, sub);

      validarAssinaturaInadiplencia(sub);

      if (TypeSub.PJ.equals(sub.getTypeSub())) {
         reactiveDependentPJ(sub, dependent);
      }
      else {
         // Se o dependente está Ok com data de remoção preenchida, é apenas a situação de "desistir do cancelamento do dependente"
         if (DependentStatus.OK.equals(dependent.getStatus())
            && dependent.getDateOfRemoval() != null) {
            reactiveDependentOKPF(sub, dependent);

         }
         // Por outro lado, é uma reativação de um dependente que já estava realmente cancelado.
         else {

            if (!dependent.getCpf().isEmpty() && !dependent.getCpf().equals("00000000000")) {
               List<Dependent> listaDependente = dependentRepository.findByCPFAndAtivo(dependent.getCpf());
               if (listaDependente.size() > 0) {
                  throw new ClubFlexException("Esse CPF já está ativo em uma assinatura.");
               }
            }
            reactiveDependentPF(sub, dependent);
         }
      }
   }

   private void validarAssinaturaInadiplencia(Subscription sub) {
      if (invoiceService.hasInvoiceOverDue(sub.getId())) {
         throw new ClubFlexException("Assinatura com inadimplência. Regularize os pagamentos para adicionar dependente.");
      }
   }

   @Transactional
   public void changeDependent(User userRequester, ChangeDependentRequest form) {
      if (form == null) {
         throw new ClubFlexException("Dados do dependente não informado.");
      }

      Dependent dependent = dependentRepository.findByDependentId(form.getId());

      String cpfPrevious = dependent.getCpf();

      dependent.setCpf(Strings.removeNoNumericChars(form.getCpf()));
      dependent.setName(form.getName());
      dependent.setEmail(form.getEmail());
      dependent.setPhone(form.getPhone());
      dependent.setDateOfBirth(form.getDateOfBirth());
      dependent.setSex(form.getSex());

      validateDependentData(dependent, cpfPrevious);

      // salvar alteração do dependente
      dependentRepository.persist(dependent);
   }

   @Transactional
   public void changeHolder(User userRequester, ChangeDependentRequest form) {
      if (form == null) {
         throw new ClubFlexException("Dados do titular não informado.");
      }

      Holder holder = holderService.findById(form.getId());

      String cpfPrevious = holder.getCpfCnpj();

      holder.setCpfCnpj(Strings.removeNoNumericChars(form.getCpf()));
      holder.setName(form.getName());
      holder.setEmail(form.getEmail());
      holder.setCellPhone(form.getPhone());
      holder.setDateOfBirth(form.getDateOfBirth());
      holder.setSex(form.getSex());

      validateChangeHolderData(holder, cpfPrevious);

      // salvar alteração do dependente
      holderRepository.save(holder);
   }

   private void validateDependentData(Dependent d, String cpfPrevious) {

      if (StringUtils.isBlank(d.getName())) {
         throw new ClubFlexException("Nome do(s) dependente(s) não foi informado");
      }
      if (d.getDateOfBirth() == null) {
         throw new ClubFlexException(String.format("Data de nascimento do dependente %s não informada.", d.getName()));
      }

      Boolean isChild = ChronoUnit.YEARS.between(d.getDateOfBirth(), LocalDateTime.now()) < 18;

      if (StringUtils.isBlank(d.getCpf()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do dependente %s não informado.", d.getName()));
      }
      if (!CPFValidator.isValido(d.getCpf()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do dependente %s não está correto.", d.getName()));
      }

      if (!isChild && d.getCpf().equals("00000000000")) {
         throw new ClubFlexException(String.format("CPF do dependente %s não está correto.", d.getName()));
      }

      if (!cpfPrevious.equalsIgnoreCase(d.getCpf()) && !d.getCpf().equals("00000000000")) {

         // Verifica se o cpf do dependente é dependente ou holder em outra assinatura
         List<Subscription> subsDependent = Lists.newArrayList();
         subsDependent.addAll(subscriptionRepository.findByDependentOkByCpf(d.getCpf()));
         subsDependent.addAll(subscriptionRepository.findByHolderCpfCnpj(d.getCpf()));
         AtomicBoolean isDepedent = new AtomicBoolean(false);
         subsDependent.forEach(s -> {
            if (SubscriptionStatus.OK.equals(s.getStatus()) || SubscriptionStatus.BLOCKED.equals(s.getStatus())) {
               isDepedent.set(true);
            }
         });
         if (isDepedent.get()) {
            throw new ClubFlexException(
               String.format("Não pode ser alterado para o CPF %s, pois já pertence a uma assinatura.", d.getCpf()));
         }
      }

      if (isChild) {
         if (StringUtils.isBlank(d.getCpf())) {
            d.setCpf("00000000000");
         }
      }
   }

   private void validateChangeHolderData(Holder h, String cpfPrevious) {

      if (StringUtils.isBlank(h.getName())) {
         throw new ClubFlexException("Nome do(s) Titular(s) não foi informado");
      }
      if (h.getDateOfBirth() == null) {
         throw new ClubFlexException(String.format("Data de nascimento do titular %s não informada.", h.getName()));
      }

      Boolean isChild = ChronoUnit.YEARS.between(h.getDateOfBirth(), LocalDateTime.now()) < 18;

      if (StringUtils.isBlank(h.getCpfCnpj()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do titular %s não informado.", h.getName()));
      }
      if (!CPFValidator.isValido(h.getCpfCnpj()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do titular %s não está correto.", h.getName()));
      }

      if (!cpfPrevious.equalsIgnoreCase(h.getCpfCnpj())) {

         // Verifica se o cpf do dependente é dependente ou holder em outra assinatura
         List<Subscription> subsDependent = Lists.newArrayList();
         subsDependent.addAll(subscriptionRepository.findByDependentOkByCpf(h.getCpfCnpj()));
         subsDependent.addAll(subscriptionRepository.findByHolderCpfCnpj(h.getCpfCnpj()));
         AtomicBoolean isDepedent = new AtomicBoolean(false);
         subsDependent.forEach(s -> {
            if (SubscriptionStatus.OK.equals(s.getStatus()) || SubscriptionStatus.BLOCKED.equals(s.getStatus())) {
               isDepedent.set(true);
            }
         });
         if (isDepedent.get()) {
            throw new ClubFlexException(
               String.format("Não pode ser alterado para o CPF %s, pois já pertence a uma assinatura.", h.getCpfCnpj()));
         }
      }

      if (isChild) {
         if (StringUtils.isBlank(h.getCpfCnpj())) {
            h.setCpfCnpj("00000000000");
         }
      }
   }

   @Transactional
   public void addDependent(User userRequester, AddDependentRequest form) {

      if (form == null) {
         throw new ClubFlexException("Dados do dependente não informado.");
      }
      Subscription sub = findById(form.getSubscriptionId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }

      if (sub.getPlan().getTypeSub() == TypeSub.PF && !sub.getPlan().getHasdependent()) {
         throw new ClubFlexException("Plano dessa assinatura não permite dependentes.");
      }

      if (SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
         throw new ClubFlexException("Assinatura cancelada. Operação não permitida.");
      }
      if (form.getDependents() == null || form.getDependents().isEmpty()) {
         throw new ClubFlexException("Nenhum dependente informado para inclusão.");
      }

      validaHolderRole(userRequester, sub);

      validarAssinaturaInadiplencia(sub);

      if (TypeSub.PJ.equals(sub.getTypeSub())) {
         addDependentPJ(form, sub);
      }
      else {
         addDependentPF(form, sub);
      }
   }

   @Transactional
   public void addNotification(User user, NotificationRequest form) {
      if (form == null || form.getDescription() == null || form.getDescription().isEmpty()) {
         throw new ClubFlexException("Descrição da notificação não informada.");
      }
      Subscription sub = findById(form.getSubscriptionId());
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      notificationRepository.save(sub, user, form);
   }

   private void addDependentPJ(AddDependentRequest form, Subscription sub) {
      form.getDependents().forEach(dependent -> {
         boolean isHolder = DependentType.HOLDER.equals(dependent.getType());

         // verificar se esse titular ja está cadastrado como um titular na empresa
         if (isHolder && dependentService.existsHolder(sub.getId(), dependent.getCpf())) {
            throw new ClubFlexException("O TITULAR informado já encontra-se cadastrado e ativo!");
         }
         if (!isHolder && StringUtils.isBlank(dependent.getCpfHolder())) {
            throw new ClubFlexException(String.format("CPF do titular não informado para o dependente: %s", dependent.getName()));
         }

         // validar os dados do dependente com as regras básicas
         dependentService.validateDependentData(dependent);

         // cadastrar titular na base do do sistema
         Holder holder = null;
         if (isHolder) {
            holder = holderService.findByCpfCnpj(dependent.getCpf());
            if (holder == null) {
               holder = new Holder();
               holder.setName(dependent.getName());
               holder.setCpfCnpj(Strings.removeNoNumericChars(dependent.getCpf()));
               holder.setEmail(dependent.getEmail());
               holder.setCellPhone(dependent.getPhone() == null ? "(00)00000-0000" : dependent.getPhone());
               holder.setDateOfBirth(dependent.getDateOfBirth());
               holder.setSex(dependent.getSex());
               holder.setIsHolder(Boolean.TRUE);
               holderService.save(holder);
            }
            dependent.setCpfHolder(null); // zera cpf por precaução
         }
         else {
            holder = holderService.findByCpfCnpj(dependent.getCpfHolder());
            if (holder == null) {
               throw new ClubFlexException(
                  "O titular informado para o dependente não foi encontrado. Cadastre o TITULAR antes de cadastrar o DEPENDENTE.");
            }
         }

         // setando dados e formatando dependnte
         dependent.setCpf(Strings.removeNoNumericChars((dependent.getCpf())));
         dependent.setDateOfInsert(LocalDateTime.now());
         dependent.setSubscription(sub);
         dependent.setStatus(DependentStatus.OK);

         if (!isHolder) {
            dependent.setCpfHolder(Strings.removeNoNumericChars((dependent.getCpfHolder())));
         }

         // criando cartao para o dependente
         ClubCard clubCard = null;
         if (isHolder) {
            clubCard = clubFlexCardService.createCard(holder, sub, null, true);
         }
         else {
            clubCard = clubFlexCardService.createCard(holder, sub, dependent, false);
         }

         // salva o cartao e o dependente
         clubFlexCardService.save(clubCard);
      });
   }

   private void addDependentPF(AddDependentRequest form, Subscription sub) {
      // adicionar dependentes na assinatura
      dependentService.updateDependents(sub, form.getDependents());

      // gerar cartoes
      clubFlexCardService.generateClubCardFlexDependents(sub);

      // salvar assinatura com novos dependentes
      subscriptionRepository.save(sub);

      // pagamentos
      LocalDate baseDate = LocalDate.now();
      LocalDate payDayNextMonth = baseDate.plusMonths(1).withDayOfMonth(sub.getDayForPayment());

      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {

         // atualizar os boletos incluindo valor dos novo(s) dependente(s)
         BigDecimal dependentAmount =
            sub.getPlan().getPriceDependent().multiply(new BigDecimal(form.getDependents().size())).setScale(2, RoundingMode.DOWN);
         List<Invoice> invoicesToUpdate = invoiceService.listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
         LocalDate baseDateNextCompetence = LocalDate.now().plusMonths(1).withDayOfMonth(1);
         invoicesToUpdate.forEach(invoice -> {
            if (invoice.getCompetenceBegin().isEqual(baseDateNextCompetence)
               || invoice.getCompetenceBegin().isAfter(baseDateNextCompetence)) {
               BigDecimal amountTotalDependent =
                  NumberUtils.proRateCalculate(dependentAmount, invoice.getCompetenceBegin(), invoice.getCompetenceEnd(), true);
               BigDecimal newAmountInvoice = invoice.getAmount().add(amountTotalDependent);

               // add descricao nos boletos
               BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), invoice.getCompetenceBegin(),
                  invoice.getCompetenceEnd(), true);
               form.getDependents().forEach(dependent -> {
                  invoice.getDetails().add(
                     new InvoiceDetail(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent, invoice));
               });

               invoiceService.updateAmountInvoice(invoice, newAmountInvoice);
            }
         });

         // gerar um boleto para ESSA competencia referente a inclusão do dependente.
         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent());
         List<Amount> amounts = Lists.newArrayList();
         form.getDependents().forEach(dependent -> {
            amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
            amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));
         });

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withType(InvoiceType.EXTRA)
                  .withStatus(InvoiceStatus.GENERATING)
                  .build();
         invoiceService.generateSingleInvoiceTypeGeneric(invoice, true);

      }
      else if (PaymentType.TICKET.equals(sub.getPaymentType())) {
         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), baseDate, payDayNextMonth);
         List<Amount> amounts = Lists.newArrayList();
         form.getDependents().forEach(dependent -> {
            amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
            amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));
         });

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withCompetence(baseDate, payDayNextMonth)
                  .withType(InvoiceType.EXTRA)
                  .withStatus(InvoiceStatus.GENERATING)
                  .build();
         // Primeira fatura do dependente sempre sera em boleto ou carne
         invoiceService.generateSingleInvoiceTypeGenericDependent(invoice, true);

      }
      else {
         // gera pagamento avulso com os dependentes para o mes atual
         CreditCard card = creditCardService.findByHolderId(sub.getHolder().getId());

         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), baseDate, payDayNextMonth);
         List<Amount> amounts = Lists.newArrayList();
         form.getDependents().forEach(dependent -> {
            amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
            amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));
         });

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withCompetence(baseDate, payDayNextMonth)
                  .withType(InvoiceType.EXTRA)
                  .withDueDate(LocalDate.now().plusDays(2))
                  .withCreditCard(card)
                  .build();

         invoiceRepository.save(invoice);
         queuePaySingleInvoice.convertAndSend(invoice.getId());

      }
   }

   private void reactiveDependentPJ(Subscription sub, Dependent dependent) {

      // reativa o dependente
      dependent.setStatus(DependentStatus.OK);
      dependent.setDateOfRemoval(null);
      dependent.setReason(null);
      dependent.setReasonCancellation(null);
      dependentRepository.merge(dependent);

      // reativa o cartão do dependente
      sub.getCards().forEach(card -> {
         if (card.getDependent() != null && card.getDependent().equals(dependent)) {
            card.setStatus(ClubCardStatus.OK);
         }
      });
      subscriptionRepository.merge(sub);
   }

   @Transactional
   private void reactiveDependentPF(Subscription sub, Dependent dependent) {

      // reativa o dependente
      dependent.setStatus(DependentStatus.OK);
      dependent.setDateOfRemoval(null);
      dependent.setReason(null);
      dependent.setReasonCancellation(null);
      dependentRepository.merge(dependent);

      // reativa o cartão do dependente
      sub.getCards().forEach(card -> {
         if (card.getDependent() != null && card.getDependent().equals(dependent)) {
            card.setStatus(ClubCardStatus.OK);
         }
      });
      subscriptionRepository.update(sub);

      // pagamentos
      LocalDate baseDate = LocalDate.now();
      LocalDate payDayNextMonth = baseDate.plusMonths(1).withDayOfMonth(sub.getDayForPayment());

      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {

         // atualizar os boletos incluindo valor do dependente reativado
         BigDecimal dependentAmount = sub.getPlan().getPriceDependent().multiply(new BigDecimal(1)).setScale(2, RoundingMode.DOWN);
         List<Invoice> invoicesToUpdate = invoiceService.listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
         LocalDate baseDateNextCompetence = LocalDate.now().plusMonths(1).withDayOfMonth(1);
         invoicesToUpdate.forEach(invoice -> {
            if (invoice.getCompetenceBegin().isEqual(baseDateNextCompetence)
               || invoice.getCompetenceBegin().isAfter(baseDateNextCompetence)) {

               invoice.setSubscription(sub);

               try {
                  BigDecimal amountTotalDependent =
                     NumberUtils.proRateCalculate(dependentAmount, invoice.getCompetenceBegin(), invoice.getCompetenceEnd(), true);
                  BigDecimal newAmountInvoice = invoice.getAmount().add(amountTotalDependent);

                  // add descricao nos boletos
                  BigDecimal amountByDependent =
                     NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), invoice.getCompetenceBegin(),
                        invoice.getCompetenceEnd(), true);
                  invoice.getDetails().add(
                     new InvoiceDetail(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent, invoice));
                  invoiceService.updateAmountInvoice(invoice, newAmountInvoice);
               }
               catch (Exception exc) {
                  System.out.println(exc);
               }

            }
         });

         // gerar um boleto para ESSA competencia referente a reativação do dependente.
         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent());
         List<Amount> amounts = Lists.newArrayList();

         amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
         amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withType(InvoiceType.EXTRA)
                  .withStatus(InvoiceStatus.GENERATING)
                  .build();
         invoiceService.generateSingleInvoiceTypeGeneric(invoice, true);

      }
      else if (PaymentType.TICKET.equals(sub.getPaymentType())) {

         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), baseDate, payDayNextMonth);
         List<Amount> amounts = Lists.newArrayList();

         amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
         amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withCompetence(baseDate, payDayNextMonth)
                  .withType(InvoiceType.EXTRA)
                  .withStatus(InvoiceStatus.GENERATING)
                  .build();
         invoiceService.generateSingleInvoiceTypeGeneric(invoice, true);

      }
      else {
         // gera pagamento avulso com o dependente para o mes atual
         CreditCard card = creditCardService.findByHolderId(sub.getHolder().getId());

         BigDecimal amountByDependent = NumberUtils.proRateCalculate(sub.getPlan().getPriceDependent(), baseDate, payDayNextMonth);
         List<Amount> amounts = Lists.newArrayList();

         amounts.add(new Amount(String.format(MSG_REF_INC_DEPENDENT_PRO_RATA, dependent.getName()), amountByDependent));
         amounts.add(new Amount(MSG_REF_INC_DEPENDENT_FEE, sub.getPlan().getAccessionFee()));

         Invoice invoice = new InvoiceBuilder(sub)
                  .addAmounts(amounts)
                  .withCompetence(baseDate, payDayNextMonth)
                  .withType(InvoiceType.EXTRA)
                  .withDueDate(LocalDate.now().plusDays(2))
                  .withCreditCard(card)
                  .build();

         invoiceService.generateSingleInvoiceTypeGeneric(invoice, false);
      }
   }

   @Transactional
   private void reactiveDependentOKPF(Subscription sub, Dependent dependent) {

      // reativa o dependente
      dependent.setDateOfRemoval(null);
      dependent.setReason(null);
      dependent.setReasonCancellation(null);
      dependentRepository.merge(dependent);
   }

   private void validaHolderRole(User userRequester, Subscription sub) {
      if (UserProfile.HOLDER.equals(userRequester.getProfile())) {
         User user = userService.findUserByHolderId(sub.getHolder().getId());
         if (user == null) {
            throw new ClubFlexException("Alteração não permitida. Usuário não tem perfil de titular dessa assinatura.");
         }
         if (!user.equals(userRequester)) {
            throw new ClubFlexException("Alteração não permitida. Titularidade não confere.");
         }
      }
   }

   public Subscription findById(Long subscriptionId) {
      return subscriptionRepository.findById(subscriptionId);
   }

   public List<Subscription> findSubscriptionByHolderId(Long id) {
      if (id == null) {
         throw new ClubFlexException("ID do titular não informado.");
      }
      return subscriptionRepository.findByHolderId(id);
   }

   public List<Subscription> findSubscriptionByHolderCpfCnpjNotResponsibleFinancial(String cpfCnpj) {
      if (StringUtils.isBlank(cpfCnpj)) {
         throw new ClubFlexException("CPF/CNPJ do titular não informado.");
      }
      return subscriptionRepository.findByHolderCpfCnpjNotResponsibleFinancial(cpfCnpj);
   }

   public List<Subscription> findSubscriptionByDependentCpf(String cpfCnpj) {
      if (StringUtils.isBlank(cpfCnpj)) {
         throw new ClubFlexException("CPF/CNPJ do titular não informado.");
      }
      return subscriptionRepository.findByDependentCpf(cpfCnpj);
   }

   public Subscription getSubscriptionByUser(User user) {

      validateNewHolder(user);

      return getLastSubscriptionByUser(user);
   }

   private void validateNewHolder(User userRequester) {
      if (UserProfile.HOLDER.equals(userRequester.getProfile())) {
         List<Subscription> listSub = findSubscriptionByHolderId(userRequester.getHolder().getId());
         if (listSub.isEmpty()) {
            throw new ClubFlexException("NewUser");
         }
      }
   }

   public Subscription getLastSubscriptionByUser(User user) {
      user = userService.findUserById(user.getId());
      if (UserProfile.HOLDER.equals(user.getProfile()) || UserProfile.DEPENDENT.equals(user.getProfile())) {
         return getLastSubscriptionByHolderId(user.getHolder().getId());
      }
      else {
         throw new ClubFlexException("Usuário não suportado pelo método.");
      }
   }

   private Subscription getLastSubscriptionByHolderId(Long id) {
      return subscriptionRepository.getLastByHolderId(id);
   }

   public List<Invoice> listLastInvoicesByUser(User user) {
      if (user == null) {
         throw new ClubFlexException("Usuário não informado.");
      }
      Subscription subscription = getLastSubscriptionByUser(user);
      return invoiceService.listLastBySubscriptionId(subscription.getId());
   }

   @Transactional
   public Subscription createSubscription(CreateSubscriptionRequest subscription, User loggedUser) {
      if (subscription == null) {
         throw new ClubFlexException("Dados de assinatura não informados.");
      }

      if (subscription.getHolder().getNumber() == null) {
         throw new ClubFlexException("O número do endereço é numérico e obrigatório.");
      }

      if (subscription.getDependents() != null && subscription.getDependents().size() == 0
         && subscription.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException("É necessário informar pelo menos um dependente.");
      }

      // se nao informado tipo de assinatura, conclui-se como Pessoa Fisica - PF como default.
      if (subscription.getType() == null) {
         subscription.setType(TypeSub.PF);
      }

      // se PJ
      if (TypeSub.PJ.equals(subscription.getType())) {
         subscription.setHolderOnlyResponsibleFinance(Boolean.TRUE);
         subscription.setPaymentType(PaymentType.TICKET);
      }

      // validar titular
      validateHolderData(subscription);

      // validar forma de pagamento
      validatePaymentData(subscription);

      // validar informacoes de endereco
      validateAddressData(subscription);

      // verificar se o titular possui alguma assinatura vigente (OK ou Bloqueada)
      // Onde NÃO seja apenas um responsável financeiro
      // Ou que ele esteja criando uma nova assinatura onde o titular seja um responsável financeiro
      List<Subscription> subscriptions = findSubscriptionByHolderCpfCnpjNotResponsibleFinancial(subscription.getHolder().getCpfCnpj());
      if (subscriptions != null && !subscriptions.isEmpty() && !subscription.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException(
            "Já existe uma assinatura com o CPF/CNPJ do titular! Uma nova assinatura não pode ser realizada no momento.");
      }

      // verificar se o titular é dependente em alguma assinatura (ok ou bloqueada)
      // Ou que ele esteja criando uma nova assinatura onde o titular seja um responsável financeiro
      // (pode ser dependente em uma assinatura e resp financeiro em outra)
      /**
       * List<Subscription> subsDependent = findSubscriptionByDependentCpf(subscription.getHolder().getCpfCnpj()); AtomicBoolean isDepedent
       * = new AtomicBoolean(false); subsDependent.forEach(s->{ if(SubscriptionStatus.OK.equals(s.getStatus()) ||
       * SubscriptionStatus.BLOCKED.equals(s.getStatus())) { isDepedent.set(true); } }); if(isDepedent.get() &&
       * !subscription.getHolderOnlyResponsibleFinance()) { throw new ClubFlexException("Você já é dependente em uma assinatura. Acesse sua
       * conta, ou solicite ao titular a sua retirada."); }
       */
      if (!subscription.getHolderOnlyResponsibleFinance()) {
         List<Dependent> listaDependente = dependentRepository.findByCPFAndAtivo(subscription.getHolder().getCpfCnpj());
         if (listaDependente != null && !listaDependente.isEmpty()) {
            throw new ClubFlexException("Você já é dependente em uma assinatura. Acesse sua conta, ou solicite ao titular a sua retirada.");
         }
      }

      // validação do plano que permita dependentes ou não
      Plan plan = planService.findById(subscription.getPlanId());
      if (subscription.getDependents() != null && subscription.getDependents().size() > 0
         && plan.getTypeSub() == TypeSub.PF && !plan.getHasdependent()) {
         throw new ClubFlexException("Plano dessa assinatura não permite dependentes.");
      }

      // verificar se o titular precisa ser cadastrado, ou se já está e atualizar os dados
      Holder holder = null;
      if (subscription.getHolder().getId() == null) {
         holder = holderService.findByCpfCnpj(subscription.getHolder().getCpfCnpj());
         if (holder == null) {
            holder = new Holder();
         }
         else if (dependentService.existsCpfHolder(subscription.getHolder().getCpfCnpj())) {
            User user = userRepository.findByLogin(holder.getCpfCnpj());
            if (user != null) {
               user.setIsActive(false);
               userRepository.merge(user);
            }
            holder = new Holder();
         }
      }
      else {
         holder = subscription.getHolder();
      }
      holderUpdateData(subscription, holder);

      // salvar dados do cartão de crédito, caso haja necessidade

      CreditCard creditCard = new CreditCard();

      if ((PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
         || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) && !subscription.getCreditCard().isChoice()) {
         creditCard = creditCardUpdateData(subscription, holder, loggedUser.getId());
      }

      // gerando assinatura
      Subscription sub = new Subscription();
      sub.setHolder(holder);
      sub.setHolderOnlyResponsibleFinance(subscription.getHolderOnlyResponsibleFinance());
      sub.setDateBegin(subscription.getDateBegin());
      sub.setDateOfRegistry(LocalDate.now());

      // sub.setDayForPayment(subscription.getDayForPayment()); Data do vencimento = data do cadastro
      sub.setDayForPayment(LocalDate.now().getDayOfMonth());

      sub.setPaymentType(subscription.getPaymentType());
      sub.setUpdateAtPayDay(LocalDateTime.now());
      sub.setPlan(plan);
      sub.setTypeSub(subscription.getType());

      // Verificar se a assinatura foi criada pelo próprio usuário que já aceitou o contrato.
      if (subscription.getAcceptedContract()) {
         sub.setDateTimeAcceptedContract(LocalDateTime.now());
      }

      if (((PaymentType.CREDIT_CARD.equals(subscription.getPaymentType()) || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType()))
         && !subscription.getCreditCard().isChoice())) {
         if (sub.getDateBegin().isAfter(LocalDate.now())) {
            sub.setStatus(SubscriptionStatus.BLOCKED);
            sub.setWaitingFirstPay(true);
         }
         else {
            sub.setStatus(SubscriptionStatus.OK);
            sub.setWaitingFirstPay(false);
         }
      }
      else if ((PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
         || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) && subscription.getCreditCard().isChoice()) {
         sub.setStatus(SubscriptionStatus.REQUESTED_CARD);
         sub.setWaitingFirstPay(true);
      }
      else {
         sub.setStatus(SubscriptionStatus.BLOCKED);
         sub.setWaitingFirstPay(true);
      }

      // corretor
      if (subscription.getBrokerId() != null && subscription.getBrokerId() != 0L) {
         sub.setBroker(brokerService.findById(subscription.getBrokerId()));
      }

      // filial
      if (subscription.getCompanyId() != null && subscription.getCompanyId() != 0L) {
         sub.setCompany(companyService.findById(subscription.getCompanyId()));
      }

      // setar usuario logado, responsavel pela assinatura
      if (loggedUser != null) {
         sub.setUser(loggedUser);
      }

      // Quantidade de dias para bloqueio de assinatura apos vencimento.
      ParamsSubscription param = paramService.getParamsSubscription();
      if (param.getDaysDueBlock() != null && param.getDaysDueBlock() > 0l)
         sub.setDaysToBlock(param.getDaysDueBlock());

      // insere assinatura
      subscriptionRepository.save(sub);

      // gerar dependentes
      dependentService.generateDependents(sub, subscription);

      // gerar Pets
      petService.generatePets(sub, subscription);

      // gerar cartoes clubflex
      clubFlexCardService.generateAllClubFlexCards(sub);

      // salvar assinatura com novos dados
      subscriptionRepository.save(sub);

      // gerar faturas
      if (TypeSub.PF.equals(subscription.getType())) {

         if ((PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
            || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) && !subscription.getCreditCard().isChoice()) {
            invoiceService.generateInvoicesTypeGeneric(sub, creditCard, true);
         }
         else if (!PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
            && !PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) {
            invoiceService.generateInvoicesTypeGeneric(sub, creditCard, true);
         }
      }

      // enviar contrato por e-mail
      sendContract(sub);

      return sub;
   }

   @Transactional
   public void generateFirstInvoice(Subscription subscription, CreditCard creditCard) {

      if (PaymentType.CREDIT_CARD.equals(subscription.getPaymentType()) || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) {
         if (subscription.getDateBegin().isAfter(LocalDate.now())) {
            subscription.setStatus(SubscriptionStatus.BLOCKED);
            subscription.setWaitingFirstPay(true);
         }
         else {
            subscription.setStatus(SubscriptionStatus.OK);
            subscription.setWaitingFirstPay(false);
            subscription.getCards().forEach(card -> {
               if (ClubCardStatus.BLOCKED.equals(card.getStatus())) {
                  card.setStatus(ClubCardStatus.OK);
               }
            });
            subscription.setDateBlocked(null);
         }
      }

      if (TypeSub.PF.equals(subscription.getTypeSub())) {
         invoiceService.generateInvoicesTypeGeneric(subscription, creditCard, true);
      }

      subscriptionRepository.update(subscription);
   }

   public String generateContract(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura não encontrada.");
      }

      String fileTemplate = "contrato-fidelidade.html";
      if (!sub.getPlan().getHasfidelity()) {
         fileTemplate = "contrato-sem-fidelidade.html";
      }

      CurrencyWriter cw = CurrencyWriter.getInstance();
      Map<String, String> params = Maps.newConcurrentMap();
      params.put("nomeTitular", sub.getHolder().getName());
      params.put("cpfTitular", sub.getHolder().getCpfCnpjFmt());
      params.put("enderecoTitular", sub.getHolder().getCompleteAddress());
      params.put("valorContrato", sub.getPlan().getPriceHolderFmt());
      params.put("valorExtenso", cw.write(sub.getPlan().getPriceHolder()));
      params.put("valorDependenteContrato", sub.getPlan().getPriceDependentFmt());
      params.put("valorDependenteExtenso", cw.write(sub.getPlan().getPriceDependent()));
      params.put("valorTaxaAdesao", sub.getPlan().getAccessionFeeFmt());
      params.put("valorTaxaExtenso", cw.write(sub.getPlan().getAccessionFee()));
      params.put("diasBloqueio", (sub.getDaysToBlock() != null ? sub.getDaysToBlock().toString() : "30"));

      try {
         // @formatter:off
         @SuppressWarnings("resource")
         String html = new Scanner(Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream(RESOURCES_MAIL_TEMPLATES.concat(File.separator).concat(fileTemplate)))
                           .useDelimiter("°")
                           .next();
         for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
            String prop = iterator.next();
            if (params.get(prop) == null)
               params.put(prop, "");
            html = html.replace("{{" + prop + "}}", params.get(prop));
         }
         html.concat("<script>window.print();</script>");
         return html;
         // @formatter:on
      }
      catch (Exception e) {
         return null;
      }
   }

   public String generateNewContract(CreateSubscriptionRequest subscription) {

      Plan plano = planService.findById(subscription.getPlanId());

      String fileTemplate = "contrato-fidelidade.html";
      if (!plano.getHasfidelity()) {
         fileTemplate = "contrato-sem-fidelidade.html";
      }

      ParamsSubscription param = systemParamsService.getParamsSubscription();
      Long daysDueBlock = 30l;
      if (param != null) {
         daysDueBlock = param.getDaysDueBlock();
      }

      CurrencyWriter cw = CurrencyWriter.getInstance();
      Map<String, String> params = Maps.newConcurrentMap();
      params.put("nomeTitular", subscription.getHolder().getName());
      params.put("cpfTitular", subscription.getHolder().getCpfCnpjFmt());
      params.put("enderecoTitular", subscription.getHolder().getCompleteAddress());
      params.put("valorContrato", plano.getPriceHolderFmt());
      params.put("valorExtenso", cw.write(plano.getPriceHolder()));
      params.put("valorDependenteContrato", plano.getPriceDependentFmt());
      params.put("valorDependenteExtenso", cw.write(plano.getPriceDependent()));
      params.put("valorTaxaAdesao", plano.getAccessionFeeFmt());
      params.put("valorTaxaExtenso", cw.write(plano.getAccessionFee()));
      params.put("diasBloqueio", daysDueBlock.toString());

      try {
         // @formatter:off
         @SuppressWarnings("resource")
         String html = new Scanner(Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream(RESOURCES_MAIL_TEMPLATES.concat(File.separator).concat(fileTemplate)))
                           .useDelimiter("°")
                           .next();
         for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
            String prop = iterator.next();
            if (params.get(prop) == null)
               params.put(prop, "");
            html = html.replace("{{" + prop + "}}", params.get(prop));
         }
         html.concat("<script>window.print();</script>");
         return html;
         // @formatter:on
      }
      catch (Exception e) {
         return null;
      }
   }

   private void sendContract(Subscription sub) {
      try {
         String fileTemplate = "contrato-fidelidade.html";
         if (!sub.getPlan().getHasfidelity()) {
            fileTemplate = "contrato-sem-fidelidade.html";
         }
         if (StringUtils.isNotBlank(sub.getHolder().getEmail())) {
            CurrencyWriter cw = CurrencyWriter.getInstance();
            MailTemplate mail = new MailTemplateBuilder()
                     .subject("Seu contrato clubFlex")
                     .template(fileTemplate)
                     .addParam("valorContrato", sub.getPlan().getPriceHolderFmt())
                     .addParam("valorExtenso", cw.write(sub.getPlan().getPriceHolder()))
                     .addParam("valorDependenteContrato", sub.getPlan().getPriceDependentFmt())
                     .addParam("valorDependenteExtenso", cw.write(sub.getPlan().getPriceDependent()))
                     .addParam("valorTaxaAdesao", sub.getPlan().getAccessionFeeFmt())
                     .addParam("valorTaxaExtenso", cw.write(sub.getPlan().getAccessionFee()))
                     .addParam("diasBloqueio", (sub.getDaysToBlock() != null ? sub.getDaysToBlock().toString() : "30"))
                     .to(sub.getHolder().getEmail())
                     .build();
            mailService.send(mail);
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro ao enviar e-mail do contrato");
      }
   }

   @Transactional
   public CreditCard creditCardUpdateData(CreateSubscriptionRequest subscription, Holder holder, Long usuario) {
      if (PaymentType.CREDIT_CARD.equals(subscription.getPaymentType()) || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) {
         return creditCardService.save(subscription.getCreditCard(), null, holder, true, usuario);
      }
      return null;
   }

   private void holderUpdateData(CreateSubscriptionRequest subscription, Holder holder) {
      holder.setName(subscription.getHolder().getName());
      holder.setCpfCnpj(Strings.removeNoNumericChars(subscription.getHolder().getCpfCnpj()));
      holder.setEmail(subscription.getHolder().getEmail());
      holder.setIsHolder(Boolean.TRUE);
      holder.setCellPhone(subscription.getHolder().getCellPhone());
      holder.setHomePhone(subscription.getHolder().getHomePhone());
      holder.setSex(subscription.getHolder().getSex());
      holder.setDateOfBirth(subscription.getHolder().getDateOfBirth());
      holder.setResponsibleCpf(subscription.getHolder().getResponsibleCpf());
      holder.setResponsibleName(subscription.getHolder().getResponsibleName());
      holder.setZipcode(subscription.getHolder().getZipcode());
      holder.setAddress(subscription.getHolder().getAddress());
      holder.setUf(subscription.getHolder().getUf());
      holder.setCity(subscription.getHolder().getCity());
      holder.setNeighborhood(subscription.getHolder().getNeighborhood());
      holder.setComplementAddress(subscription.getHolder().getComplementAddress());
      holder.setNumber(subscription.getHolder().getNumber());
      holderService.save(holder); // atualizar dados do titular
   }

   private void validateAddressData(CreateSubscriptionRequest subscription) {
      if (StringUtils.isBlank(subscription.getHolder().getZipcode())) {
         throw new ClubFlexException("CEP não informado.");
      }
      if (StringUtils.isBlank(subscription.getHolder().getAddress())) {
         throw new ClubFlexException("Endereço não informado.");
      }
      if (StringUtils.isBlank(subscription.getHolder().getUf())) {
         throw new ClubFlexException("UF do titular não informada.");
      }
      if (StringUtils.isBlank(subscription.getHolder().getCity())) {
         throw new ClubFlexException("Cidade do titular não informada.");
      }
      if (StringUtils.isBlank(subscription.getHolder().getNeighborhood())) {
         throw new ClubFlexException("Bairro do titular não informado.");
      }
   }

   private void validateHolderData(CreateSubscriptionRequest subscription) {
      if (subscription.getHolder() == null) {
         throw new ClubFlexException("Dados do titular não informados.");
      }
      if (StringUtils.isBlank(subscription.getHolder().getName())) {
         throw new ClubFlexException("Nome ou Razão social não informado(a).");
      }
      if (StringUtils.isBlank(subscription.getHolder().getCpfCnpj())) {
         throw new ClubFlexException("CPF/CNPJ não informado.");
      }
      // if(StringUtils.isBlank(subscription.getHolder().getEmail())) {
      // throw new ClubFlexException("E-mail não informado.");
      // }
      if (TypeSub.PJ.equals(subscription.getType()) && !CNPJValidator.isValido(subscription.getHolder().getCpfCnpj())) {
         throw new ClubFlexException("CNPJ inválido.");
      }
      if (TypeSub.PF.equals(subscription.getType()) && !CPFValidator.isValido(subscription.getHolder().getCpfCnpj())) {
         throw new ClubFlexException("CPF inválido.");
      }
      if (StringUtils.isNotBlank(subscription.getHolder().getEmail())) {
         if (!EmailValidator.isValido(subscription.getHolder().getEmail())) {
            throw new ClubFlexException("E-mail inválido.");
         }
      }

      // Campos importantes devido a emissao de NFE
      if (StringUtils.isBlank(subscription.getHolder().getZipcode())) {
         throw new ClubFlexException("CEP não informado");
      }
      if (StringUtils.isBlank(subscription.getHolder().getAddress())) {
         throw new ClubFlexException("Endereço não informado");
      }
      if (StringUtils.isBlank(subscription.getHolder().getUf())) {
         throw new ClubFlexException("UF não informada");
      }
      if (StringUtils.isBlank(subscription.getHolder().getCity())) {
         throw new ClubFlexException("Cidade não informada");
      }
      if (StringUtils.isBlank(subscription.getHolder().getNeighborhood())) {
         throw new ClubFlexException("Bairro não informado");
      }
      if (StringUtils.isBlank(subscription.getHolder().getCellPhone())) {
         throw new ClubFlexException("Telefone celular não informado");
      }
      if (TypeSub.PF.equals(subscription.getType()) && subscription.getHolder().getDateOfBirth() == null) {
         throw new ClubFlexException("Data de Nascimento não informada");
      }
      if (TypeSub.PF.equals(subscription.getType())) {
         // Ano < 1881 não pode
         LocalDate ano1881 = LocalDate.of(1881, 1, 1);
         if (subscription.getHolder().getDateOfBirth().isBefore(ano1881)) {
            throw new ClubFlexException("Data de nascimento não pode ser menor do que o ano de 1881");
         }
         // Depois de hoje não pode
         if (subscription.getHolder().getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ClubFlexException("Data de nascimento não pode ser maior do que a data de hoje");
         }
      }
   }

   private void validatePaymentData(CreateSubscriptionRequest subscription) {

      if (subscription.getCreditCard() == null || !subscription.getCreditCard().isChoice()) {
         if (subscription.getDateBegin() == null) {
            subscription.setDateBegin(LocalDate.now());
         }
         else if (subscription.getDateBegin().isBefore(LocalDate.now())) {
            throw new ClubFlexException("Data de cadastro não pode ser no passado.");
         }

         if (subscription.getPlanId() == null) {
            throw new ClubFlexException("Plano não informado.");
         }
         if (subscription.getPaymentType() == null) {
            throw new ClubFlexException("Tipo de pagamento não informado.");
         }
         /**
          * Alterado - Data de vencimento = data do cadastro if (subscription.getDayForPayment() == null) { throw new ClubFlexException("Dia
          * de pagamento/vencimento não informado."); }
          */
         if (PaymentType.CREDIT_CARD.equals(subscription.getPaymentType())
            || PaymentType.DEBIT_CARD.equals(subscription.getPaymentType())) {
            if (subscription.getCreditCard() == null) {
               throw new ClubFlexException("Para pagamento em Débito/Crédito informe os dados do cartão.");
            }
            if (StringUtils.isBlank(subscription.getCreditCard().getBrand())) {
               throw new ClubFlexException("Informe a bandeira do cartão.");
            }
            if (StringUtils.isBlank(subscription.getCreditCard().getValidate())) {
               throw new ClubFlexException("Validade do cartão não informada.");
            }
            if (StringUtils.isBlank(subscription.getCreditCard().getNumber())) {
               throw new ClubFlexException("Número do cartão não informado.");
            }
            if (StringUtils.isBlank(subscription.getCreditCard().getName())) {
               throw new ClubFlexException("Informe o nome como está no cartão.");
            }
            if (StringUtils.isBlank(subscription.getCreditCard().getSecurityCode())) {
               throw new ClubFlexException(
                  "Código se segurança do cartão não informado. Informe os 3 números do verso do cartão.");
            }
         }
      }
   }

   public List<Dependent> listDependentLastSubscription(User user) {
      if (user == null) {
         throw new ClubFlexException("Usuário não informado.");
      }
      Subscription subscription = getLastSubscriptionByUser(user);
      return Lists.newArrayList(subscription.getDependents());
   }

   public Object listLastWithoutCancelledBySubscriptionId(User user) {
      if (user == null) {
         throw new ClubFlexException("Usuário não informado.");
      }
      Subscription subscription = getLastSubscriptionByUser(user);
      return invoiceService.listLastWithoutCancelledBySubscriptionId(subscription.getId());
   }

   public ClubCardValidateResponse validateClubCardByToken(String token) {
      ClubCard card = clubFlexCardService.getByToken(token);
      if (card == null) {
         throw new ClubFlexException("Clubcard inválido ou não encontrado.");
      }

      ClubCardValidateResponse response = new ClubCardValidateResponse();
      response.setName(card.getNameOnCard());
      response.setStatus(card.getStatus());
      response.setNumber(card.getNumberOnCard());
      response.setCpf(card.getFirst6DigitsFromCPFOnCard());

      return response;
   }

   public List<BigInteger> listNoShouldBeBlocked() {
      return subscriptionRepository.listNoShouldBeBlocked();
   }

   public List<BigInteger> listRenewTickets() {
      return subscriptionRepository.listRenewTickets();
   }

   public List<Invoice> listAdviseTickets() {
      return subscriptionRepository.listAdviseTickets();
   }

   public List<Invoice> listAdviseTickets2DaysToDue() {
      return subscriptionRepository.listAdviseTickets2DaysToDue();
   }

   public List<BigInteger> listShouldBeBlocked() {
      return subscriptionRepository.listShouldBeBlocked();
   }

   public List<BigInteger> listShouldBeBlockedByParam(LocalDate dataComparacao) {
      return subscriptionRepository.listShouldBeBlockedByParam(dataComparacao);
   }

   @Transactional
   public void unBlock(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      sub.setStatus(SubscriptionStatus.OK);
      sub.setWaitingFirstPay(false);
      // desbloqueando cartoes
      sub.getCards().forEach(card -> {
         if (ClubCardStatus.BLOCKED.equals(card.getStatus())) {
            card.setStatus(ClubCardStatus.OK);
         }
      });
      sub.setDateBlocked(null);
      subscriptionRepository.merge(sub);
   }

   @Transactional
   public void block(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      sub.setStatus(SubscriptionStatus.BLOCKED);
      // bloqueando cartoes
      sub.getCards().forEach(card -> {
         if (ClubCardStatus.OK.equals(card.getStatus())) {
            card.setStatus(ClubCardStatus.BLOCKED);
         }
      });
      sub.setDateBlocked(LocalDateTime.now());

      subscriptionRepository.merge(sub);
   }

   @Transactional
   public Subscription blockWithParam(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      sub.setStatus(SubscriptionStatus.BLOCKED);
      // bloqueando cartoes
      sub.getCards().forEach(card -> {
         if (ClubCardStatus.OK.equals(card.getStatus())) {
            card.setStatus(ClubCardStatus.BLOCKED);
         }
      });
      sub.setDateBlocked(LocalDateTime.now());

      subscriptionRepository.merge(sub);

      // subscriptionLogService.generateLog(null, sub.getId(), "Bloqueio gerado pelo parâmetro do Plano dessa assinatura",
      // SubscriptionLogAction.BLOQUEIO_DE_ASSINATURA);
      subscriptionLogService.generateLog(null, sub.getId(), "Bloqueio gerado pelo sistema",
         SubscriptionLogAction.BLOQUEIO_DE_ASSINATURA);

      return sub;
   }

   public List<BigInteger> listActiveSubscriptions(Integer actualCompetence, TypeSub typesub) {
      return subscriptionRepository.listActiveSubscriptions(actualCompetence, typesub);
   }

   public List<BigInteger> listActiveAndBlocksSubscriptions(Integer actualCompetence, TypeSub typesub) {
      return subscriptionRepository.listActiveBlockedSubscriptions(actualCompetence, typesub);
   }

   public List<BigInteger> listActiveSubscriptions(Integer actualCompetence, Integer dayOfPayment, TypeSub typesub) {
      return subscriptionRepository.listActiveSubscriptionsInvoices(actualCompetence, dayOfPayment, typesub);
   }

   public List<Dependent> listDependentBySubscriptionId(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      return Lists.newArrayList(sub.getDependents());
   }

   @Transactional
   public void cancel(Long subscriptionId, CancelSubscriptionRequest form, User userResponsable, boolean immediate) {

      Long idreason = form.getIdreason();
      String reasonCancel = form.getReasonCancel();
      Boolean exemptFine = form.getExemptFine();

      if (idreason == null) {
         throw new ClubFlexException("Selecione o Motivo do cancelamento.");
      }
      Reason reason = reasonRepository.findById(form.getIdreason());
      if (reason == null) {
         throw new ClubFlexException("Motivo do cancelamento não encontrado.");
      }
      if (StringUtils.isBlank(reasonCancel)) {
         throw new ClubFlexException("Informe a observação do cancelamento.");
      }
      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }
      if (SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
         throw new ClubFlexException("Assinatura já cancelada.");
      }
      if (sub.getDateOfCancellation() != null && !immediate) {
         throw new ClubFlexException("Assinatura com cancelamento previsto para: "
                  .concat(sub.getDateOfCancellation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
      }

      // tratamento
      if (exemptFine == null) {
         exemptFine = false;
      }

      // Ultima invoice paga
      Invoice lastInvoicePaid = invoiceService.getLastInvoicePay(subscriptionId);

      // Dias passados
      long pastDays = ChronoUnit.DAYS.between(sub.getDateBegin(), LocalDateTime.now());

      // Data de cancelamento
      LocalDate dataToCancel = LocalDate.now();

      if (sub.getPlan().getHasfidelity() && pastDays < 180 && !exemptFine) {
         dataToCancel = dataToCancel.plusDays(180 - pastDays);
      }
      else {
         if (lastInvoicePaid != null && lastInvoicePaid.getCompetenceEnd().isAfter(LocalDate.now()) && !immediate) {
            dataToCancel = lastInvoicePaid.getCompetenceEnd();
         }
         else {
            dataToCancel = LocalDate.now();
         }
      }

      // Multa
      BigDecimal amountToPay = calculateCancellationPenaltyAmount(subscriptionId);
      if (amountToPay.doubleValue() > 0 && !exemptFine) {
         // data incio para fins de calculo de multa, se houver pagamento considerar o final de vigencia da multa paga
         LocalDate dateBegin = sub.getDateBegin();
         if (lastInvoicePaid != null) {
            dateBegin = lastInvoicePaid.getCompetenceEnd().plusDays(1);
         }

         // gerar fatura com os dias restantes, devido a fidelidade para assinaturas com pagamento via cartao
         if (PaymentType.DEBIT_CARD.equals(sub.getPaymentType()) || PaymentType.CREDIT_CARD.equals(sub.getPaymentType())) {
            // gerando invoice
            CreditCard card = creditCardService.findByHolderId(sub.getHolder().getId());
            Invoice invoice = new InvoiceBuilder(sub)
                     .addAmount(amountToPay, "Multa por cancelamento com fidelidade.")
                     .withDueDate(LocalDate.now())
                     .withCompetence(dateBegin, dataToCancel)
                     .withType(InvoiceType.EXTRA)
                     .withCreditCard(card)
                     .build();
            invoiceService.generateSingleInvoiceTypeGeneric(invoice, false);
         }

         // gerar fatura com os dias restantes, devido a fidelidade para assinaturas com pagamento via boleto
         if (PaymentType.TICKET.equals(sub.getPaymentType()) || PaymentType.TICKETS.equals(sub.getPaymentType())) {
            // gerando invoice
            Invoice invoice = new InvoiceBuilder(sub)
                     .addAmount(amountToPay, "Multa por cancelamento com fidelidade.")
                     .withDueDate(HolidayUtils.proximoDiaUtil(LocalDate.now().plusDays(2)))
                     .withCompetence(dateBegin, dataToCancel)
                     .withStatus(InvoiceStatus.GENERATING)
                     .withType(InvoiceType.EXTRA)
                     .build();
            invoiceService.generateSingleInvoiceTypeGeneric(invoice, true);
         }
      }

      if (dataToCancel.isEqual(LocalDate.now())) {
         // cancelar dependents
         sub.getDependents().forEach(dep -> {
            dep.setStatus(DependentStatus.REMOVED);
            dep.setDateOfRemoval(LocalDateTime.now());
         });

         // cancelar cartoes
         sub.getCards().forEach(card -> {
            card.setStatus(ClubCardStatus.CANCELED);
            card.setDateCancel(LocalDateTime.now());
         });

         // salvar assinatura cancelada
         sub.setStatus(SubscriptionStatus.CANCELED);
      }

      sub.setDateRequestOfCancellation(LocalDateTime.now());
      sub.setDateOfCancellation(dataToCancel);
      sub.setReasonCancellation(reasonCancel);
      sub.setReason(reason);
      subscriptionRepository.save(sub);

      // cancelar pagamentos
      invoiceService.cancelInvoiceNotOverDue(sub, userResponsable);
   }

   public BigDecimal calculateCancellationPenaltyAmount(Long subscriptionId) {
      BigDecimal amountPenalty = BigDecimal.ZERO;

      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }

      // Ultima fatura paga
      Invoice lastInvoicePaid = invoiceService.getLastInvoicePay(subscriptionId);

      // Dias passados
      long pastDays = ChronoUnit.DAYS.between(sub.getDateBegin(), LocalDateTime.now());

      // Data de cancelamento
      LocalDate dataToCancel = LocalDate.now();

      if (sub.getPlan().getHasfidelity() && pastDays < 180) {
         // data de cancelamento
         dataToCancel = dataToCancel.plusDays(180 - pastDays);

         // data incio para fins de calculo de multa, se houver pagamento considerar o final de vigencia da multa paga
         LocalDate dateBegin = sub.getDateBegin();
         if (lastInvoicePaid != null) {
            dateBegin = lastInvoicePaid.getCompetenceEnd().plusDays(1);
         }

         // dias entre o ultimo pagamento e a data de cancelamento
         long daysBetweenBeginAndCancelDate = ChronoUnit.DAYS.between(dateBegin, dataToCancel);

         if (daysBetweenBeginAndCancelDate > 0) {
            amountPenalty =
               sub.getTotalPrice().divide(new BigDecimal("30"), RoundingMode.DOWN).multiply(new BigDecimal(daysBetweenBeginAndCancelDate));
         }
      }

      return amountPenalty;
   }

   public List<Subscription> filter(SubscriptionFilter filter) {
      boolean notDefinedSearch = StringUtils.isBlank(filter.getCpfCnpjHolder()) &&
         StringUtils.isBlank(filter.getNameHolder()) &&
         filter.getDateBegin() == null &&
         filter.getDateEnd() == null &&
         StringUtils.isBlank(filter.getPhone()) &&
         StringUtils.isBlank(filter.getEmail()) &&
         filter.getCardNumber() == null &&
         StringUtils.isBlank(filter.getDependentName()) &&
         filter.getIdSubscription() == null &&
         StringUtils.isBlank(filter.getNsu());
      if (notDefinedSearch) {
         throw new ClubFlexException("Informe a informação da busca.");
      }

      if (filter.getDateBegin() != null && filter.getDateEnd() != null) {
         long daysRange = filter.getDateBegin().until(filter.getDateEnd(), ChronoUnit.DAYS);
         if (daysRange > 60) {
            throw new ClubFlexException("O período máximo de busca são 60 dias.");
         }
      }

      if (StringUtils.isNotBlank(filter.getNsu())) {
         return invoiceService.findByNsu(filter.getNsu()).stream().map(Invoice::getSubscription).collect(Collectors.toList());
      }
      else {
         return subscriptionRepository.filter(filter);
      }

   }

   public List<Invoice> filterInvoice(SubscriptionFilter filter) {

      if (filter.getDateBegin() == null || filter.getDateEnd() == null) {
         throw new ClubFlexException("Informe o período da pesquisa.");
      }

      if (filter.getIdSubscription() == null) {
         throw new ClubFlexException("Assiantura não selecionada.");
      }

      long daysRange = filter.getDateBegin().until(filter.getDateEnd(), ChronoUnit.DAYS);
      if (daysRange < 0) {
         throw new ClubFlexException("A Data Início tem que ser menor que a Data Fim.");
      }
      if (daysRange >= 60) {
         throw new ClubFlexException("O período máximo de busca são 60 dias.");
      }

      return invoiceRepository.filterInvoice(filter);
   }

   public String getNoPaidAmount(Long subscriptionId) {
      List<Invoice> invoices = invoiceService.listBySubscriptionId(subscriptionId);
      BigDecimal amount = BigDecimal.ZERO;
      for (Invoice invoice : invoices) {
         if (invoice.isOutDate()) {
            amount = amount.add(invoice.getAmount());
         }
      }
      return NumberUtils.formatMoney(amount).replace("R$", "").trim();
   }

   public void generateInvoiceExtraPay(ExtraPay extraPay, Long usuario) {
      if (extraPay.getInvoiceType() == null) {
         throw new ClubFlexException("Tipo de fatura não informado.");
      }
      if (extraPay.getPaymentType() == null) {
         throw new ClubFlexException("Forma de pagamento não informada.");
      }
      if (extraPay.getAmount() == null) {
         throw new ClubFlexException("Valor não informado.");
      }
      // if((PaymentType.DEBIT_CARD.equals(extraPay.getPaymentType()) || PaymentType.CREDIT_CARD.equals(extraPay.getPaymentType())) &&
      // extraPay.getCreditcard() == null){
      // throw new ClubFlexException("Cartão de crédito/débito não informado.");
      // }
      if (StringUtils.isBlank(extraPay.getAmount()) || extraPay.getAmount().equals("0.0")) {
         throw new ClubFlexException("Valor inválido.");
      }
      if (StringUtils.isBlank(extraPay.getDescribe())) {
         throw new ClubFlexException("Descrição da fatura não informada.");
      }
      if (extraPay.getDueDate() == null) {
         throw new ClubFlexException("Data de vencimento da fatura não informado.");
      }

      if (PaymentType.TICKET.equals(extraPay.getPaymentType()) && extraPay.getDueDate().compareTo(LocalDate.now()) < 0) {
         throw new ClubFlexException("Data para geração de boleto não pode ser menor que hoje.");
      }

      // validar as parcelas do valor da fatura extra
      validateParcelaPagamento(extraPay);

      Subscription sub = findById(extraPay.getSubscriptionId());

      List<Amount> amounts = Lists.newArrayList();
      amounts.add(new Amount(extraPay.getDescribe(), new BigDecimal(extraPay.getAmount().replace(".", ",").replace(",", "."))));

      if (PaymentType.DEBIT_CARD.equals(extraPay.getPaymentType()) || PaymentType.CREDIT_CARD.equals(extraPay.getPaymentType())) {

         CreditCard creditCard = null;
         if (extraPay.getCreditcard() == null) {
            creditCard = creditCardService.findByHolderId(sub.getHolder().getId());
         }
         else {
            creditCard = creditCardService.update(extraPay.getCreditcard(), false, usuario);
         }

         if (creditCard == null) {
            throw new ClubFlexException("Cartão de crédito/débito não informado. Titular não possui cartão disponível para pagamento.");
         }

         Invoice invoice = null;
         if (extraPay.getInvoiceRelation() != null) {
            Invoice invoiceRelation = invoiceService.findById(extraPay.getInvoiceRelation());
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withDueDate(extraPay.getDueDate())
                     // .withInstallmentNumber(extraPay.getInstallmentNumber())
                     .withStatus(InvoiceStatus.OPENED)
                     .withCreditCard(creditCard)
                     .withCompetence(invoiceRelation.getCompetenceBegin(), invoiceRelation.getCompetenceEnd())
                     .build();
         }
         else {
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withDueDate(extraPay.getDueDate())
                     .withInstallmentNumber(extraPay.getInstallmentNumber())
                     .withStatus(InvoiceStatus.OPENED)
                     .withCreditCard(creditCard)
                     .build();
         }
         invoice.setUserResponsiblePayment(extraPay.getUserResponsibleId());
         invoiceService.generateSingleInvoiceTypeGeneric(invoice, false);
      }
      else if (PaymentType.TICKET.equals(extraPay.getPaymentType())) {

         Invoice invoice = null;
         if (extraPay.getInvoiceRelation() != null) {
            Invoice invoiceRelation = invoiceService.findById(extraPay.getInvoiceRelation());
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withStatus(InvoiceStatus.GENERATING)
                     .withDueDate(HolidayUtils.proximoDiaUtil(extraPay.getDueDate()))
                     // .withDueDate(extraPay.getDueDate())
                     .withCompetence(invoiceRelation.getCompetenceBegin(), invoiceRelation.getCompetenceEnd())
                     .build();
         }
         else {
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withStatus(InvoiceStatus.GENERATING)
                     .withDueDate(HolidayUtils.proximoDiaUtil(extraPay.getDueDate()))
                     // .withDueDate(extraPay.getDueDate())
                     .build();
         }

         invoice.setUserResponsiblePayment(extraPay.getUserResponsibleId());
         invoiceService.generateSingleInvoiceTypeGeneric(invoice, true);

         // Cartões maquina e dinheiro (pagamento local)
      }
      else {
         Invoice invoice = null;
         if (extraPay.getInvoiceRelation() != null) {
            Invoice invoiceRelation = invoiceService.findById(extraPay.getInvoiceRelation());
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withStatus(InvoiceStatus.OPENED)
                     .withDueDate(extraPay.getDueDate())
                     // .withPaymentDate(LocalDate.now())
                     // .withAmountPaid(new BigDecimal(extraPay.getAmount().replace(".", ",").replace(",", ".")))
                     .withCompetence(invoiceRelation.getCompetenceBegin(), invoiceRelation.getCompetenceEnd())
                     .build();
         }
         else {
            invoice = new InvoiceBuilder(sub)
                     .addAmounts(amounts)
                     .withType(extraPay.getInvoiceType())
                     .withPaymentType(extraPay.getPaymentType())
                     .withStatus(InvoiceStatus.OPENED)
                     .withDueDate(extraPay.getDueDate())
                     // .withPaymentDate(LocalDate.now())
                     // .withAmountPaid(new BigDecimal(extraPay.getAmount().replace(".", ",").replace(",", ".")))
                     .build();
         }
         invoice.setUserResponsiblePayment(extraPay.getUserResponsibleId());
         invoiceService.generateWithLocalPayment(invoice);
      }

      // gerar o log
      BigDecimal bigDecimal = new BigDecimal(extraPay.getAmount().replace(".", ",").replace(",", "."));
      String valorFormatado = NumberFormat.getCurrencyInstance().format(bigDecimal);
      subscriptionLogService.generateLog(usuario, sub.getId(), "Valor: " + valorFormatado + " Descrição: " + extraPay.getDescribe(),
         SubscriptionLogAction.NOVA_FATURA_EXTRA_GERADA);
   }

   private void validateParcelaPagamento(ExtraPay extraPay) {
      if (PaymentType.CREDIT_CARD.equals(extraPay.getPaymentType())) {

         // validar número da parcela preenchida
         if (extraPay.getInstallmentNumber() == null) {
            throw new ClubFlexException("Número de parcelas não informado.");
         }

         if (extraPay.getInstallmentNumber() > 1) {

            if (extraPay.getInstallmentNumber() > 12) {
               throw new ClubFlexException("Número de parcelas não pode ser maior que 12.");
            }

            // validar valor da parcela menor que R$ 50,00
            BigDecimal valorTotal = new BigDecimal(extraPay.getAmount().replace(".", ",").replace(",", "."));
            BigDecimal numParcelas = new BigDecimal(extraPay.getInstallmentNumber());
            BigDecimal valorParcela = valorTotal.divide(numParcelas, 2);
            if (valorParcela.compareTo(new BigDecimal(50)) < 0) {
               throw new ClubFlexException("O valor da parcela não pode ser menor do que R$ 50,00.");
            }
         }
      }
   }

   public List<BigInteger> listPreCancelled() {
      return subscriptionRepository.listPreCancelled();
   }

   @Transactional
   public void cancelPreCancelled(Long subscriptionId) {
      Subscription sub = findById(subscriptionId);

      // cancelar dependents
      sub.getDependents().forEach(dep -> {
         dep.setStatus(DependentStatus.REMOVED);
         dep.setDateOfRemoval(LocalDateTime.now());
      });

      // cancelar cartoes
      sub.getCards().forEach(card -> {
         card.setStatus(ClubCardStatus.CANCELED);
         card.setDateCancel(LocalDateTime.now());
      });

      // salvar assinatura cancelada
      sub.setStatus(SubscriptionStatus.CANCELED);
      subscriptionRepository.save(sub);
   }

   public Boolean verifyEligibility(String cpf) {
      if (StringUtils.isBlank(cpf)) {
         throw new ClubFlexException("CPF não informado");
      }

      AtomicBoolean isEligibility = new AtomicBoolean(false);

      List<Subscription> subs = Lists.newArrayList();
      subs.addAll(subscriptionRepository.findByHolderCpfCnpj(cpf));
      subs.addAll(subscriptionRepository.findByDependentCpf(cpf));

      if (subs != null) {
         // Titular
         subs.forEach(s -> {
            if (SubscriptionStatus.OK.equals(s.getStatus())) {
               s.getCards().forEach(c -> {
                  if (c.getHolder() != null && c.getHolder().getCpfCnpj().equals(cpf) && ClubCardStatus.OK.equals(c.getStatus())) {
                     isEligibility.set(true);
                  }
               });
            }
         });

         // Dependente
         subs.forEach(s -> {
            if (SubscriptionStatus.OK.equals(s.getStatus())) {
               s.getCards().forEach(c -> {
                  if (c.getDependent() != null && c.getDependent().getCpf().equals(cpf) && ClubCardStatus.OK.equals(c.getStatus())) {
                     isEligibility.set(true);
                  }
               });
            }
         });
      }

      return isEligibility.get();
   }

   public Boolean verifyEligibility(Long cardNumber) {
      if (cardNumber == null) {
         throw new ClubFlexException("Número do cartão não informado");
      }

      ClubCard clubCard = clubFlexCardService.findById(cardNumber);
      if (clubCard != null && ClubCardStatus.OK.equals(clubCard.getStatus())) {
         return true;
      }

      return false;
   }

   @Transactional
   public void saveLog(SubscriptionLog log) {
      if (log.getSubscriptionId() == null) {
         throw new ClubFlexException("Subscription LOG erro. Id da assinatura não informado.");
      }

      if (log.getAction() == null) {
         log.setAction(SubscriptionLogAction.OPERACAO_DESCONHECIDA);
      }

      log.setDateTimeLog(LocalDateTime.now());
      subscriptionRepository.saveLog(log);
   }

   public List<SubLog> listLog(Long subscriptionId) {
      return subscriptionRepository.listLog(subscriptionId);
   }

   public List<NotificationRequest> listNotifications(Long subscriptionId) {
      return notificationRepository.listNotifications(subscriptionId);
   }

   public List<LifeGroup> listGroupLifes(Long subscriptionId, User loggedUser) {
      try {
         Subscription sub = subscriptionRepository.findById(subscriptionId);
         if (sub == null) {
            throw new ClubFlexException("Assinatura inválida.");
         }
         if (UserProfile.HOLDER.equals(loggedUser.getProfile()) && !sub.getHolder().equals(loggedUser.getHolder())) {
            throw new ClubFlexException("Acesso a assinatura não autorizado.");
         }

         Map<Holder, List<ClubCard>> group = Maps.newConcurrentMap();
         Set<ClubCard> cards = sub.getCards();
         cards.forEach(card -> {
            List<ClubCard> obj = group.get(card.getHolder());
            if (obj == null) {
               group.put(card.getHolder(), Lists.newArrayList(card));
            }
            else {
               group.get(card.getHolder()).add(card);
            }
         });

         List<LifeGroup> lifes = Lists.newArrayList();
         group.entrySet().forEach(entry -> {
            lifes.add(new LifeGroup(entry.getKey(), entry.getValue()));
         });

         for (int i = 0; i < lifes.size(); i++) {
            List<ClubCard> cardsOrder = new ArrayList<ClubCard>();

            cardsOrder = lifes.get(i).getCards();

            if (!cardsOrder.isEmpty()) {
               cardsOrder = cardsOrder.stream()
                        .sorted(Comparator.comparing(ClubCard::getId))
                        .collect(Collectors.toList());

               lifes.get(i).setCards(cardsOrder);
            }
         }

         return lifes;
      }
      catch (Exception e) {
         return null;
      }
   }

   @Transactional
   public void cancelClubcard(Long cardId) {
      ClubCard card = clubFlexCardService.findById(cardId);
      card.setStatus(ClubCardStatus.CANCELED);
      card.setDateCancel(LocalDateTime.now());

      if (!card.getIsCardOfHolder() && card.getDependent() != null) {
         card.getDependent().setStatus(DependentStatus.REMOVED);
         card.getDependent().setDateOfRemoval(LocalDateTime.now());
         dependentService.save(card.getDependent());
      }

      clubFlexCardService.save(card);
   }

   @Transactional
   public void cancelClubcardHolder(Long holderId) {

      List<ClubCard> listcard = clubFlexCardService.findByHolderId(holderId);
      for (ClubCard clubCard : listcard) {

         clubFlexCardService.deleteClubCard(clubCard.getId());

         if (!clubCard.getIsCardOfHolder() && clubCard.getDependent() != null) {
            userService.deleteUserByDependente(clubCard.getDependent().getId());
            dependentService.deleteDependent(clubCard.getDependent().getId());
         }
         else {
            userService.deleteUserByHolder(holderId);
         }
      }
      holderService.deleteHolder(holderId);
   }

   public PreviewInvoice previewInvoice(Long subscriptionId, Integer month, Integer year) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);

      LocalDateTime baseDateIni = YearMonth.of(year, month).atDay(1).atStartOfDay().withHour(00).withMinute(00);
      LocalDateTime baseDateEnd = YearMonth.of(year, month).atEndOfMonth().atStartOfDay().withHour(23).withMinute(59);

      Integer totalHolders = 0;
      Integer totalDependents = 0;
      BigDecimal amountCalculated = BigDecimal.ZERO;

      Map<Holder, List<ClubCard>> group = Maps.newConcurrentMap();
      for (ClubCard card : sub.getCards()) {
         if (ClubCardStatus.OK.equals(card.getStatus()) && card.getDateGenerated().isBefore(baseDateEnd) ||
            ClubCardStatus.CANCELED.equals(card.getStatus()) && card.getDateCancel().isAfter(baseDateIni)) {
            if (card.getIsCardOfHolder()) {
               totalHolders += 1;
               amountCalculated = amountCalculated.add(sub.getPlan().getPriceHolder());
            }
            else {
               totalDependents += 1;
               amountCalculated = amountCalculated.add(sub.getPlan().getPriceDependent());
            }
            List<ClubCard> obj = group.get(card.getHolder());
            if (obj == null) {
               group.put(card.getHolder(), Lists.newArrayList(card));
            }
            else {
               group.get(card.getHolder()).add(card);
            }
         }
      }

      // Criando grupo de vida
      List<LifeGroup> lifes = Lists.newArrayList();
      group.entrySet().forEach(entry -> {
         lifes.add(new LifeGroup(entry.getKey(), entry.getValue()));
      });

      PreviewInvoice preview = new PreviewInvoice();
      preview.setSubscriptionId(subscriptionId);
      preview.setMonth(month);
      preview.setYear(year);
      preview.setHolderName(sub.getHolder().getName());
      preview.setLifes(lifes);
      preview.setAmountPreview(amountCalculated);
      preview.setTotalHolders(totalHolders);
      preview.setTotalDependents(totalDependents);
      preview.setDueDate(LocalDate.now().withDayOfMonth(sub.getDayForPayment()).withMonth(month).withYear(year));
      preview.setMaxDateAjusts(
         LocalDate.now().withDayOfMonth(sub.getDayForPayment()).withMonth(month).withYear(year).minusDays(MAX_DAYS_BEFORE_AJUSTS));

      return preview;
   }

   public String generateUrlInvoicePreview(Long subscriptionId, Integer month, Integer year) {
      Map<String, Object> params = Maps.newConcurrentMap();
      params.put("sub", subscriptionId);
      params.put("month", month);
      params.put("year", year);

      Date expiresAt = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
      JwtBuilder builder = Jwts.builder()
               .compressWith(CompressionCodecs.DEFLATE)
               .setClaims(params)
               .setExpiration(expiresAt)
               .signWith(SignatureAlgorithm.HS256, Constants.TOKEN_PREVIEW_INVOICE);
      return builder.compact();
   }

   public void sendPreviewInvoiceWithoutCC(BigInteger subId, Integer monthCompetence, Integer yearCompetence) {
      Subscription sub = subscriptionRepository.findById(subId.longValue());
      String url = generateUrlInvoicePreview(sub.getId(), monthCompetence, yearCompetence);

      MailTemplate mail = new MailTemplateBuilder()
               .subject("Seu contrato clubFlex")
               .template("preview-invoice.html")
               .addParam("dateAjusts",
                  LocalDate.now().withDayOfMonth(sub.getDayForPayment()).withMonth(monthCompetence).withYear(yearCompetence)
                           .minusDays(MAX_DAYS_BEFORE_AJUSTS).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
               .addParam("link", url)
               .addParam("holder", sub.getHolder().getName())
               .addParam("dayOfpayment", sub.getDayForPayment().toString())
               .to(sub.getHolder().getEmail())
               .build();
      mailService.send(mail);
   }

   public void sendPreviewInvoiceWithCC(BigInteger subId, Integer monthCompetence, Integer yearCompetence) {
      Subscription sub = subscriptionRepository.findById(subId.longValue());
      String url = generateUrlInvoicePreview(sub.getId(), monthCompetence, yearCompetence);

      MailTemplate mail = new MailTemplateBuilder()
               .subject("Seu contrato clubFlex")
               .template("preview-invoice.html")
               .addParam("dateAjusts",
                  LocalDate.now().withDayOfMonth(sub.getDayForPayment()).withMonth(monthCompetence).withYear(yearCompetence)
                           .minusDays(MAX_DAYS_BEFORE_AJUSTS).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
               .addParam("link", url)
               .addParam("holder", sub.getHolder().getName())
               .addParam("dayOfpayment", sub.getDayForPayment().toString())
               .to(sub.getHolder().getEmail())
               .cc(Constants.SEGMEDIC_FINANC_MAIL)
               .build();
      mailService.send(mail);
   }

   public List<SubscriptionLoad> findSubscriptionByUser(User user) {
      final User userObject = userService.findUserById(user.getId());

      List<SubscriptionLoad> listaSubsLoad = new ArrayList<SubscriptionLoad>();

      List<Subscription> listSubsHolder = subscriptionRepository.findByHolderCpfCnpj(userObject.getLogin());

      if (listSubsHolder == null || listSubsHolder.isEmpty()) {

         String cpfCnpjFormatado = "";

         if (userObject.getLogin() != null && userObject.getLogin().length() == 11) {
            cpfCnpjFormatado = Strings.formatCPF(userObject.getLogin());
         }
         else if (userObject.getLogin() != null && userObject.getLogin().length() == 14) {
            cpfCnpjFormatado = Strings.formatCNPJ(userObject.getLogin());

         }
         else {
            cpfCnpjFormatado = null;
         }

         if (cpfCnpjFormatado != null)
            listSubsHolder = subscriptionRepository.findByHolderCpfCnpj(cpfCnpjFormatado);
      }

      for (Subscription s : listSubsHolder) {
         if (s.getHolderOnlyResponsibleFinance()) {
            listaSubsLoad.add(new SubscriptionLoad(s.getId(), "Resp. Financeiro"));
         }
         else {
            listaSubsLoad.add(new SubscriptionLoad(s.getId(), "Titular"));
         }
      }

      List<Subscription> listSubsDep = subscriptionRepository.findByDependentOkByCpf(userObject.getLogin());
      for (Subscription s : listSubsDep) {
         listaSubsLoad.add(new SubscriptionLoad(s.getId(), "Dependente"));
      }
      return listaSubsLoad;
   }

   public List<BigInteger> ListSubscriptionIdWithoutCard() {
      return subscriptionRepository.ListSubscriptionIdWithoutCard();
   }

   public BigInteger SubscriptionIdWithoutCard(Long subId) {
      return subscriptionRepository.SubscriptionIdWithoutCard(subId);
   }

   @Transactional
   public void updateContract(Long idSubscription) {
      try {
         Subscription sub = subscriptionRepository.findById(idSubscription);
         sub.setDateTimeAcceptedContract(LocalDateTime.now());
         subscriptionRepository.save(sub);

         // Enviar SMS.
         sendSMSContractOK(sub.getHolder().getName(), sub.getHolder().getCellPhone());

         // Enviar E-mail.
         sendMailContractOk(sub);

      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao assinar o contrato da assinatura.");
      }
   }

   public void sendSMSContractOK(String name, String telefone) {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      String mensagem = "Contrato assinado! Seja bem-vindo(a) ao ClubFlex, " + name.split(" ")[0]
         + "! Acesse sua assinatura em https://meu.clubflex.com.br";
      MessageSmsElement messageSms = new MessageSmsElement();
      messageSms.setFrom(remetente);
      messageSms.setMsg(mensagem);
      messageSms.setTo("55".concat(telefone.replaceAll("[^0-9]", "")));
      multipleMessageSms.addMessageSms(messageSms);
      try {
         smsBuilder.sendMultipleSms(multipleMessageSms);
      }
      catch (RestClientException e) {
         e.printStackTrace();
      }
   }

   private void sendMailContractOk(Subscription sub) {

      MailTemplate mail = new MailTemplateBuilder()
               .subject("Seu contrato ClubFlex")
               .template("contrato-assinado.html")
               .addParam("nome", sub.getHolder().getName())
               .addParam("linkclubflex", Constants.URL_LOGIN_CLUBFLEX_B2C)
               .to(sub.getHolder().getEmail())
               .build();
      mailService.send(mail);
   }

   public List<BigInteger> listShouldBeBlockedByDateDue() {
      return subscriptionRepository.listShouldBeBlockedByDateDue();
   }

   @Transactional
   public void transformToHolder(Long subscriptionId) {

      if (subscriptionId == null) {
         throw new ClubFlexException("Nenhuma assinatura selecionada.");
      }

      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura não encontrada.");
      }
      if (sub.getStatus() != SubscriptionStatus.OK) {
         throw new ClubFlexException("Assinatura não está ativa.");
      }
      if (sub.getTypeSub() != TypeSub.PF) {
         throw new ClubFlexException("Assinatura não é de pessoa física.");
      }
      if (!sub.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException("Assinatura não tem Responsável Financeiro.");
      }

      ClubCard clubCardVerificacao = clubFlexCardService.findByHolderSubIsHolder(sub.getHolder(), sub);
      if (clubCardVerificacao != null) {
         throw new ClubFlexException("Já existe um cartão para o titular informado.");
      }

      // obter o valor da assinatura anterior a alteração
      BigDecimal valorAnterior = sub.getTotalPrice();

      sub.setHolderOnlyResponsibleFinance(false);

      // obter o valor da assinatura anterior a alteração
      BigDecimal valorAdicionar = sub.getTotalPrice().subtract(valorAnterior);

      // atualizar faturas do tipo Carne
      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {

         // Obtém as faturas abertas e não vencidas e adicionar um valor da diferença anterior pra atual
         List<Invoice> invoicesToUpdate = invoiceService.listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
         invoicesToUpdate.forEach(invoice -> {

            if (!invoice.isOutDate() && invoice.getType() == InvoiceType.DEFAULT) {

               BigDecimal novoValor = invoice.getAmount().add(valorAdicionar);

               // Se o valor estiver dentro da competencia, é necessario calcular pro-rata
               novoValor = NumberUtils.proRateCalculate(novoValor, invoice.getCompetenceBegin(), invoice.getCompetenceEnd(), true);

               ticketGatewayService.updateAmountTicket(invoice, novoValor);
               invoice.setSubscription(sub);
               invoice.setAmount(novoValor);
               invoiceRepository.save(invoice);
            }
         });
      }

      // Criar o clubecard para o novo titular
      Holder holder = sub.getHolder();
      ClubCard clubCard = clubFlexCardService.createCard(holder, sub, null, true);
      clubFlexCardService.save(clubCard);

      // salvar a alteração na assinatura
      subscriptionRepository.save(sub);

      // Enviar o cartão de boas vindas ao "novo" titular
      User usuario = userService.findUserByHolderId(sub.getHolder().getId());
      if (sub.getHolder().getCellPhone() != null && !sub.getHolder().getCellPhone().equals("")) {
         holderService.sendWelcomeSMS(sub.getHolder().getName(), sub.getHolder().getCellPhone(),
            Cryptography.decrypt(usuario.getPassword()));
         holderService.sendRequestCardMail(sub.getHolder().getName(),
            sub.getHolder().getEmail());
      }
   }

   @Transactional
   public void transformToResponsibleFinance(Long subscriptionId) {

      if (subscriptionId == null) {
         throw new ClubFlexException("Nenhuma assinatura selecionada.");
      }

      Subscription sub = findById(subscriptionId);
      if (sub == null) {
         throw new ClubFlexException("Assinatura não encontrada.");
      }
      if (sub.getStatus() != SubscriptionStatus.OK) {
         throw new ClubFlexException("Assinatura não está ativa.");
      }
      if (sub.getTypeSub() != TypeSub.PF) {
         throw new ClubFlexException("Assinatura não é de pessoa física.");
      }
      if (sub.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException("Assinatura já tem um Responsável Financeiro.");
      }

      boolean validarDep = false;
      for (Dependent dep : sub.getDependents()) {
         if (dep.getStatus() == DependentStatus.OK && dep.getDateOfRemoval() == null) {
            validarDep = true;
            break;
         }
      }
      if (!validarDep) {
         throw new ClubFlexException("Assinatura não pode ter Responsável Financeiro sem dependentes.");
      }

      sub.setHolderOnlyResponsibleFinance(true);
      BigDecimal novoValorSub = sub.getTotalPrice();

      // atualizar faturas do tipo Carne
      if (PaymentType.TICKETS.equals(sub.getPaymentType())) {

         // Obtém as faturas abertas e não vencidas e adicionar um valor da diferença anterior pra atual
         List<Invoice> invoicesToUpdate = invoiceService.listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
         invoicesToUpdate.forEach(invoice -> {

            if (!invoice.isOutDate() && invoice.getType() == InvoiceType.DEFAULT) {

               BigDecimal novoValor = novoValorSub;

               // Se o valor estiver dentro da competencia, é necessario calcular pro-rata
               novoValor = NumberUtils.proRateCalculate(novoValor, invoice.getCompetenceBegin(), invoice.getCompetenceEnd(), true);

               // ticketGatewayService.updateAmountTicket(invoice, novoValor);
               invoice.setSubscription(sub);
               invoice.setAmount(novoValor);
               invoiceRepository.save(invoice);
            }
         });
      }

      // salvar a alteração na assinatura
      subscriptionRepository.save(sub);

      // Criar o clubecard para o novo titular
      ClubCard clubCard = clubFlexCardService.findByHolderSubIsHolder(sub.getHolder(), sub);
      if (clubCard == null) {
         throw new ClubFlexException("Cartão do titular não encontrado.");
      }
      clubFlexCardService.deleteCardById(clubCard.getId());
   }

   public List<PetRequest> listaPet() {

      ArrayList<PetRequest> listaPet = new ArrayList<PetRequest>();

      for (PetEnum pet : PetEnum.values()) {
         PetRequest p = new PetRequest();
         p.setDescription(pet.getDescribe());
         // p.setPet(pet);
         listaPet.add(p);
      }
      return listaPet;
   }

   public List<PetRequest> listaPet(Long subscriptionId) {

      if (subscriptionId == null) {
         return null;
      }

      List<Pet> listPet = petService.findPetsBySub(subscriptionId);

      ArrayList<PetRequest> listaPets = new ArrayList<PetRequest>();
      for (Pet pet : listPet) {
         PetRequest petReq = new PetRequest();
         petReq.setId(pet.getId());
         petReq.setDescription(pet.getPet().getDescribe());
         petReq.setQuantity(pet.getQuantity());
         listaPets.add(petReq);
      }
      return listaPets;
   }

   @Transactional
   public void deletePet(Long petId) {

      petService.deletePet(petId);
   }

   public void addPet(PetRequest petRequest) {
      petService.addPet(petRequest);
   }

}
