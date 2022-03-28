
package br.com.segmedic.clubflex.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Plan;
import br.com.segmedic.clubflex.domain.Ratification;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.domain.enums.Functionality;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.TypeFunctionality;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.Amount;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.Operation;
import br.com.segmedic.clubflex.model.PayInvoiceInfo;
import br.com.segmedic.clubflex.model.SimulateFirstInvoice;
import br.com.segmedic.clubflex.model.SubscriptionOperation;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.repository.RatificationRepository;
import br.com.segmedic.clubflex.repository.SubscriptionLogRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.HolidayUtils;
import br.com.segmedic.clubflex.support.InvoiceBuilder;
import br.com.segmedic.clubflex.support.NumberUtils;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class InvoiceService {

   private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private SubscriptionLogRepository subscriptionLogRepository;

   @Autowired
   private RatificationRepository ratificationRepository;

   @Autowired
   private TicketGatewayService ticketGatewayService;

   @Autowired
   private RedeItauGatewayService redeItauGatewayService;

   @Autowired
   private JmsTemplate queueMonthlyInvoice;

   @Autowired
   private JmsTemplate queueSubscriptionOperation;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private MailService mailService;

   @Autowired
   private UserService userService;

   @Autowired
   private PlanService planService;

   @Autowired
   private JmsTemplate queueSubscriptionLog;

   @Autowired
   private InvoiceLogService invoiceLogService;

   @Value("${enotas.emitir}")
   private Boolean deveEmitirNfe;

   private static final int TWELVE_MONTHS = 12;

   public List<Invoice> listInvoiceByStatus(InvoiceStatus status) {
      return invoiceRepository.listInvoiceByStatus(status);
   }

   /**
    * Pagamento por Carnê (Tickets)
    * 
    * @param sub
    * @param baseDate
    */
   @Transactional
   public void generateInvoicesTypeTickets(Subscription sub, LocalDate baseDate, LocalDate dataBegin) {
      generateTicketsInvoices(sub, baseDate, dataBegin, false);
   }

   /**
    * Pagamento por Carnê (Tickets)
    * 
    * @param sub
    * @param baseDate
    * @param exemptAccessionFee
    */
   @Transactional
   public void generateInvoicesTypeTickets(Subscription sub, LocalDate baseDate, LocalDate dataBegin, Boolean exemptAccessionFee) {
      generateTicketsInvoices(sub, baseDate, dataBegin, exemptAccessionFee);
   }

   @Transactional
   public void generateTickets12MonthsService(Subscription sub, LocalDate baseDate, LocalDate dataBegin) {
      generateTickets12Months(sub, baseDate, dataBegin);
   }

   /**
    * * Pagamento por Carnê (Tickets)
    * 
    * @param sub
    * @param baseDate
    * @param card
    * @param onlyDependentsPrice
    * @param exemptAccessionFee
    */
   @Transactional
   public void generateInvoicesTypeCreditCard(Subscription sub, CreditCard card,
      boolean onlyDependentsPrice, Boolean exemptAccessionFee, Boolean firstInvoice) {
      generateCreditCardInvoice(sub, LocalDate.now(), sub.getDateBegin(), card, onlyDependentsPrice, exemptAccessionFee, firstInvoice);
   }

   /**
    * Pagamento com Cartão de Crédito / Débito
    * 
    * @param sub
    * @param card
    */
   @Transactional
   public void generateInvoicesTypeCreditCard(Subscription sub, CreditCard card) {
      generateCreditCardInvoice(sub, LocalDate.now(), sub.getDateBegin(), card, false, false, false);
   }

   /**
    * Pagamento com Cartao/Carne ou boleto
    * 
    * @param sub
    * @param creditCard
    */
   @Transactional
   public void generateInvoicesTypeGeneric(Subscription sub, CreditCard creditCard, Boolean firstInvoice) {
      if (PaymentType.CREDIT_CARD.equals(sub.getPaymentType())
         || PaymentType.DEBIT_CARD.equals(sub.getPaymentType())) {
         generateInvoicesTypeCreditCard(sub, creditCard, false, false, firstInvoice);
      }
      else if (PaymentType.TICKET.equals(sub.getPaymentType())) {
         generateTicketInvoice(sub, LocalDate.now(), sub.getDateBegin(), false, false, firstInvoice);
      }
      else {
         generateInvoicesTypeTickets(sub, LocalDate.now(), sub.getDateBegin());
      }
   }

   /**
    * Gera uma fatura unica para Carne, Cartao ou Boleto
    * 
    * @param invoice
    */
   @Transactional
   public void generateSingleInvoiceTypeGeneric(Invoice invoice, boolean saveInvoiceBeforePayment) {
      if (invoice.getAmount().doubleValue() > 0) {
         if (saveInvoiceBeforePayment) {
            invoiceRepository.save(invoice);
         }
         if (PaymentType.TICKETS.equals(invoice.getPaymentType())
            || PaymentType.TICKET.equals(invoice.getPaymentType())) {
            ticketGatewayService.generateTicket(invoice);
         }
         else {
            if (!invoice.getDueDate().isAfter(LocalDate.now())) {
               redeItauGatewayService.pay(invoice, true);
            }
            else {
               invoiceRepository.save(invoice);
            }
         }
      }
   }

   /**
    * Gera uma fatura unica para Carne, Cartao ou Boleto
    * 
    * @param invoice
    */
   @Transactional
   public void paySingleInvoiceCCard(Invoice invoice) {
      if (invoice.getAmount().doubleValue() > 0) {
         redeItauGatewayService.pay(invoice, true);
      }
   }

   /**
    * Gera uma fatura unica para Carne, Cartao ou Boleto
    * 
    * @param invoice
    */
   @Transactional
   public void generateSingleInvoiceTypeGenericDependent(Invoice invoice, boolean saveInvoiceBeforePayment) {
      if (invoice.getAmount().doubleValue() > 0) {
         if (saveInvoiceBeforePayment) {
            invoiceRepository.save(invoice);
         }
         ticketGatewayService.generateTicket(invoice);
      }
   }

   /**
    * Pagamento com Cartão de Crédito / Débito
    * 
    * @param sub
    * @param card
    * @param onlyDependentsPrice
    * @param exemptAccessionFee
    */
   private void generateCreditCardInvoice(Subscription sub, LocalDate baseDataParam, LocalDate dateBeginSub, CreditCard card,
      Boolean onlyDependentsPrice, Boolean exemptAccessionFee, Boolean firstInvoice) {

      LocalDate dateBegin = (dateBeginSub == null) ? LocalDate.now() : dateBeginSub;
      LocalDate baseData = baseDataParam;
      if (dateBegin.isAfter(baseData)) { // Data do contrato futura, baseData é a dateBegin
         baseData = dateBegin;
      }
      LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());

      // a conta é feita com base na data de cadastro (passado ou futuro)
      BigDecimal amountProRatePlan = NumberUtils.proRateCalculate(sub.getTotalPrice(), dateBegin, payDayNextMonth);

      // BigDecimal amountProRatePlan = NumberUtils.proRateCalculate(sub.getTotalPrice(), baseData);
      BigDecimal amountProRateDependents = NumberUtils.proRateCalculate(sub.getTotalDependentsPrice(), dateBegin, payDayNextMonth);

      // Se for primeira parcela a competencia final é o vencimento do proximo mes.
      // LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());
      if (firstInvoice) {
         amountProRatePlan = NumberUtils.proRateCalculate(sub.getTotalPrice(), dateBegin, payDayNextMonth);
         amountProRateDependents = NumberUtils.proRateCalculate(sub.getTotalDependentsPrice(), dateBegin, payDayNextMonth);
      }

      List<Amount> amounts = Lists.newArrayList();

      if (onlyDependentsPrice) {
         amounts.add(new Amount(String.format("Inclusão de dependente(s) (pró rata) "), amountProRateDependents));
      }
      else {
         if (!exemptAccessionFee) {
            amounts.add(new Amount("Taxa de Adesão", sub.getPlan().getAccessionFee()));
         }
         amounts.add(new Amount(String.format("Mensalidade do plano (titular + dependentes)"), amountProRatePlan));
      }

      Invoice invoice = new InvoiceBuilder(sub).withCreditCard(card).withDueDate(baseData).addAmounts(amounts).build();

      if (firstInvoice) {
         invoice = new InvoiceBuilder(sub).withCreditCard(card)
                  .withDueDate(baseData)
                  .withCompetence(baseData, payDayNextMonth)
                  .addAmounts(amounts).build();
      }
      this.generateSingleInvoiceTypeGeneric(invoice, false);
   }

   /**
    * Pagamento por Boleto bancário
    * 
    * @param sub
    * @param baseData
    * @param onlyDependentsPrice
    * @param exemptAccessionFee
    */
   private void generateTicketInvoice(Subscription sub, LocalDate baseDataParam, LocalDate dateBeginSub,
      Boolean onlyDependentsPrice, Boolean exemptAccessionFee, Boolean firstInvoice) {

      LocalDate dateBegin = (dateBeginSub == null) ? LocalDate.now() : dateBeginSub;
      LocalDate baseData = baseDataParam;
      if (dateBegin.isAfter(baseData)) { // Data do contrato futura, baseData é a dateBegin
         baseData = dateBegin;
      }
      LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());

      // a conta é feita com base na data de cadastro (passado ou futuro)
      BigDecimal amountProRatePlan = NumberUtils.proRateCalculate(sub.getTotalPrice(), dateBegin, payDayNextMonth);
      BigDecimal amountProRateDependents = NumberUtils.proRateCalculate(sub.getTotalDependentsPrice(), dateBegin, payDayNextMonth);

      // Se for primeira parcela a competencia final é o vencimento do proximo mes.
      // LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());
      if (firstInvoice) {
         amountProRatePlan = NumberUtils.proRateCalculate(sub.getTotalPrice(), dateBegin, payDayNextMonth);
         amountProRateDependents = NumberUtils.proRateCalculate(sub.getTotalDependentsPrice(), dateBegin, payDayNextMonth);
      }

      List<Amount> amounts = Lists.newArrayList();

      if (onlyDependentsPrice) {
         amounts.add(new Amount(String.format("Inclusão de dependente(s) (pró rata) "), amountProRateDependents));
      }
      else {
         if (!exemptAccessionFee) {
            amounts.add(new Amount("Taxa de Adesão", sub.getPlan().getAccessionFee()));
         }
         amounts.add(new Amount(String.format("Mensalidade do plano (titular + dependentes)"), amountProRatePlan));
      }

      Invoice invoice = new InvoiceBuilder(sub).withDueDate(HolidayUtils.proximoDiaUtil(baseData.plusDays(2)))
               .addAmounts(amounts).withStatus(InvoiceStatus.GENERATING).build();

      if (firstInvoice) {
         invoice = new InvoiceBuilder(sub).withDueDate(HolidayUtils.proximoDiaUtil(baseData.plusDays(2)))
                  .withCompetence(baseData, payDayNextMonth)
                  .addAmounts(amounts).withStatus(InvoiceStatus.GENERATING)
                  .build();
      }
      this.generateSingleInvoiceTypeGeneric(invoice, true);
   }

   private void generateTickets12Months(Subscription sub, LocalDate baseDataParam, LocalDate dateBeginSub) {

      LocalDate dateBegin = (dateBeginSub == null) ? LocalDate.now() : dateBeginSub;
      LocalDate baseData = baseDataParam;
      if (dateBegin.isAfter(baseData)) { // Data do contrato futura, baseData é a dateBegin
         baseData = dateBegin;
      }
      List<Invoice> invoicesCreateads = Lists.newArrayList();
      // boleto gerar os primeiros 12
      Integer numbersTicketsGenerated = 0;
      while (numbersTicketsGenerated < TWELVE_MONTHS) {
         Invoice invoice = null;
         List<Amount> amounts = Lists.newArrayList();
         if (sub.getDependents() != null && !sub.getDependents().isEmpty()) {
            amounts.add(new Amount("Mensalidade do plano titular+dependente(s).", sub.getTotalPrice()));
         }
         else {
            amounts.add(new Amount("Mensalidade do plano titular.", sub.getTotalPrice()));
         }

         LocalDate dataVencimento = baseData.isBefore(LocalDate.now()) ? LocalDate.now().plusDays(2)
            : baseData.withDayOfMonth(sub.getDayForPayment());

         invoice = new InvoiceBuilder(sub).withBaseDate(baseData).withStatus(InvoiceStatus.GENERATING)
                  .withDueDate(HolidayUtils.proximoDiaUtil(dataVencimento))
                  .addAmounts(amounts).build();
         baseData = invoice.getCompetenceEnd().plusDays(1); // deixar data base como primeiro dia do proximo mes.
         numbersTicketsGenerated += 1;
         if (invoice.getAmount().doubleValue() > 0.0) {
            invoiceRepository.save(invoice); // salva
            invoicesCreateads.add(invoice); // adiciona de lista de geradas
         }
      }
      ticketGatewayService.generateTickets(invoicesCreateads); // enviar para fila para registro (boleto simples)
   }

   private void generateTicketsInvoices(Subscription sub, LocalDate baseDataParam, LocalDate dateBeginSub, Boolean exemptAccessionFee) {

      LocalDate dateBegin = (dateBeginSub == null) ? LocalDate.now() : dateBeginSub;
      LocalDate baseData = baseDataParam;
      if (dateBegin.isAfter(baseData)) { // Data do contrato futura, baseData é a dateBegin
         baseData = dateBegin;
      }
      LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());

      List<Invoice> invoicesCreateads = Lists.newArrayList();

      // boleto gerar os primeiros 12
      Integer numbersTicketsGenerated = 0;
      while (numbersTicketsGenerated < TWELVE_MONTHS) {

         Invoice invoice = null;

         // primeira parcela (pro rata ate o proximo vencimento + taxa de adesao)
         if (numbersTicketsGenerated == 0) {

            // LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(sub.getDayForPayment());
            BigDecimal amountPlanProRate = NumberUtils.proRateCalculate(sub.getTotalPrice(), dateBegin, payDayNextMonth);

            List<Amount> amounts = Lists.newArrayList();

            // isentar taxa de adesão
            if (!exemptAccessionFee) {
               amounts.add(new Amount("Taxa de Adesão", sub.getPlan().getAccessionFee()));
            }

            if (sub.getDependents() != null && !sub.getDependents().isEmpty()) {
               amounts.add(new Amount("Mensalidade do plano titular+dependente proporcional. (pró rata)",
                  amountPlanProRate));
            }
            else {
               amounts.add(new Amount("Mensalidade do plano titular proporcional. (pró rata)", amountPlanProRate));
            }

            LocalDate dataVencimento = baseData.isBefore(LocalDate.now()) ? LocalDate.now().plusDays(2) : baseData.plusDays(2);

            invoice = new InvoiceBuilder(sub)
                     .withCompetence(baseData, payDayNextMonth)
                     .withStatus(InvoiceStatus.GENERATING)
                     .withDueDate(HolidayUtils.proximoDiaUtil(dataVencimento))
                     .addAmounts(amounts)
                     .build();

            baseData = invoice.getCompetenceEnd().plusDays(1); // dia do vencimento + 1

            // segunda parcela (pro rata do vencimento+1 ao final do mes)
         }
         else if (numbersTicketsGenerated == 1) {

            BigDecimal amountPlanProRate = NumberUtils.proRateCalculate(sub.getTotalPrice(), baseData);

            List<Amount> amounts = Lists.newArrayList();
            if (sub.getDependents() != null && !sub.getDependents().isEmpty()) {
               amounts.add(new Amount("Mensalidade do plano titular+dependente proporcional. (pró rata)",
                  amountPlanProRate));
            }
            else {
               amounts.add(new Amount("Mensalidade do plano titular proporcional. (pró rata)", amountPlanProRate));
            }

            LocalDate dataVencimento = baseData.isBefore(LocalDate.now()) ? LocalDate.now().plusDays(2)
               : baseData.withDayOfMonth(sub.getDayForPayment());

            invoice = new InvoiceBuilder(sub).withBaseDate(baseData).withStatus(InvoiceStatus.GENERATING)
                     .withDueDate(HolidayUtils.proximoDiaUtil(dataVencimento))
                     .addAmounts(amounts).build();

            baseData = invoice.getCompetenceEnd().plusDays(1); // dia do vencimento + 1

            // demais parcelas
         }
         else {
            List<Amount> amounts = Lists.newArrayList();
            if (sub.getDependents() != null && !sub.getDependents().isEmpty()) {
               amounts.add(new Amount("Mensalidade do plano titular+dependente(s).", sub.getTotalPrice()));
            }
            else {
               amounts.add(new Amount("Mensalidade do plano titular.", sub.getTotalPrice()));
            }

            LocalDate dataVencimento = baseData.isBefore(LocalDate.now()) ? LocalDate.now().plusDays(2)
               : baseData.withDayOfMonth(sub.getDayForPayment());

            invoice = new InvoiceBuilder(sub).withBaseDate(baseData).withStatus(InvoiceStatus.GENERATING)
                     .withDueDate(HolidayUtils.proximoDiaUtil(dataVencimento))
                     .addAmounts(amounts).build();

            baseData = invoice.getCompetenceEnd().plusDays(1); // deixar data base como primeiro dia do proximo mes.
         }

         numbersTicketsGenerated += 1;

         if (invoice.getAmount().doubleValue() > 0.0) {
            invoiceRepository.save(invoice); // salva
            invoicesCreateads.add(invoice); // adiciona de lista de geradas
         }
      }
      ticketGatewayService.generateTickets(invoicesCreateads); // enviar para fila para registro (boleto simples)
   }

   @Transactional
   public void updateDueDateInvoices(Subscription sub, Integer newDay) {
      List<Invoice> invoicesOpened = listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
      invoicesOpened.forEach(invoice -> {
         if (InvoiceType.DEFAULT.equals(invoice.getType())) { // não atualiza boletos de acordo.
            LocalDate newDate = invoice.getDueDate().withDayOfMonth(newDay);
            if (PaymentType.TICKETS.equals(invoice.getPaymentType())) {
               ticketGatewayService.updateDueDateTicket(invoice, newDate);
               // ticketGatewayService.sincronizeTicketInfo(invoice);
            }
            invoice.setDueDate(newDate);
            invoice.setBarcodeTicket(null);
            invoiceRepository.save(invoice);
         }
      });
   }

   public List<Invoice> listInvoiceBySubscriptionIdAndStatus(Long subscriptionId, InvoiceStatus status) {
      return invoiceRepository.listInvoiceBySubscriptionIdAndStatus(subscriptionId, status);
   }

   @Transactional
   public void cancelInvoiceNotOverDue(Subscription sub, User userResponsable) {
      List<Invoice> invoicesOpened = listInvoiceBySubscriptionIdAndStatus(sub.getId(), InvoiceStatus.OPENED);
      invoicesOpened.forEach(invoice -> {
         if (!invoice.isOutDate() && InvoiceType.DEFAULT.equals(invoice.getType())) { // não cancela boletos de
            // acordo ou vencidos.
            cancelInvoice(invoice.getId(), userResponsable);
         }
      });
   }

   public void refundInvoice(Invoice invoice, BigDecimal refoundAmount) {

      if ((PaymentType.DEBIT_CARD.equals(invoice.getPaymentType())
         || PaymentType.CREDIT_CARD.equals(invoice.getPaymentType()))
         && InvoiceStatus.PAID.equals(invoice.getStatus())) {
         redeItauGatewayService.refundPartial(invoice, refoundAmount);
      }

      invoiceRepository.save(invoice);
   }

   /**
    * Método responsável por cancelar uma fatura, após a aprovação do gerente
    * 
    * @param invoice - Invoice
    * @param usuario - User
    * @return quitação da fatura
    */
   @Transactional
   public void cancelInvoiceApproved(Invoice invoice) {

      if (PaymentType.TICKETS.equals(invoice.getPaymentType())
         || PaymentType.TICKET.equals(invoice.getPaymentType())) {
         try {
            ticketGatewayService.cancelTicket(invoice);
            TimeUnit.MILLISECONDS.sleep(200); // podem ser varias, por isso o tempo de espera
         }
         catch (Exception e) {
            LOGGER.error("Erro ao cancelar fatura no boleto simples", e);
         }
      }

      if ((PaymentType.DEBIT_CARD.equals(invoice.getPaymentType())
         || PaymentType.CREDIT_CARD.equals(invoice.getPaymentType()))
         && InvoiceStatus.PAID.equals(invoice.getStatus())) {
         redeItauGatewayService.refund(invoice);
      }

      // setando usuario responsável pelo pagamento da fatura
      if (invoice.getUserResponsiblePayment() != null) {
         this.generateLog(invoice.getUserResponsiblePayment(), invoice.getSubscription().getId(),
            "Fatura: ".concat(invoice.getId().toString()), SubscriptionLogAction.CANCELAMENTO_FATURA);
      }

      invoice.setStatus(InvoiceStatus.CANCELLED);
      invoice.setPayAmount(BigDecimal.ZERO);
   }

   /**
    * Método responsável por cancelar uma fatura, após a aprovação do gerente
    * 
    * @param invoice - Invoice
    * @param usuario - User
    * @return quitação da fatura
    */
   @Transactional
   public void refundInvoiceApproved(Invoice invoice) {

      if (PaymentType.TICKETS.equals(invoice.getPaymentType())
         || PaymentType.TICKET.equals(invoice.getPaymentType())) {
         try {
            ticketGatewayService.cancelTicket(invoice);
            TimeUnit.MILLISECONDS.sleep(200); // podem ser varias, por isso o tempo de espera
         }
         catch (Exception e) {
            LOGGER.error("Erro ao estornar fatura no boleto simples", e);
         }
      }

      if ((PaymentType.DEBIT_CARD.equals(invoice.getPaymentType())
         || PaymentType.CREDIT_CARD.equals(invoice.getPaymentType()))
         && InvoiceStatus.PAID.equals(invoice.getStatus())) {
         redeItauGatewayService.refund(invoice);
      }

      // setando usuario responsável pelo pagamento da fatura
      if (invoice.getUserResponsiblePayment() != null) {
         this.generateLog(invoice.getUserResponsiblePayment(), invoice.getSubscription().getId(),
            "Fatura: ".concat(invoice.getId().toString()), SubscriptionLogAction.ESTORNO_FATURA);
      }

      invoice.setStatus(InvoiceStatus.TOTALREFUNDS);
      invoice.setPayAmount(BigDecimal.ZERO);
   }

   /**
    * Método responsável por deixar um cancelamento de fatura pendente de aprovação pelo gerente.
    * 
    * @param invoiceId
    * @param userResponsable - User
    * @return cancelamento da fatura
    */
   @Transactional
   public void cancelInvoice(Long invoiceId, User userResponsable) {
      Invoice invoice = invoiceRepository.findById(invoiceId);
      if (invoice == null || InvoiceStatus.CANCELLED.equals(invoice.getStatus())) {
         throw new ClubFlexException("Fatura não encontrada ou já cancelada.");
      }
      invoice.setPaymentCancelDate(LocalDateTime.now());

      invoice.setStatus(InvoiceStatus.CANCELLED);
      // invoice.setStatus(InvoiceStatus.PENDING);

      // Retirada a funcionalidade de Ratificação
      cancelInvoiceApproved(invoice);

      // Criar a ratificação para aprovação do gerente
      /*
       * Ratification rat = createRatification(userResponsable, invoice); rat.setFunctionality(Functionality.CANCELAR_FATURA);
       * rat.setAction("Cancelar uma Fatura"); ratificationRepository.save(rat);
       */

      // registrar log da fatura
      invoiceLogService.generateLog(userResponsable, invoice, invoice.getAmount().negate(), Functionality.CANCELAR_FATURA);

      invoiceRepository.save(invoice);
   }

   /**
    * Método responsável por validar uma quitação e verificar se há faturas em aberto
    * 
    * @param invoiceId
    * @param userResponsable - User
    * @return validação da quitação da fatura
    */
   @Transactional
   public PayInvoiceInfo validPayInvoice(Long invoiceId, User userResponsable) {

      Invoice invoice = invoiceRepository.findById(invoiceId);
      PayInvoiceInfo pInfo = new PayInvoiceInfo();
      pInfo.setValid(invoiceRepository.validPayInvoice(
         invoiceId, invoice.getSubscription().getId(), invoice.getDueDate()));
      return pInfo;
   }

   /**
    * Método responsável por quitar uma fatura, após a aprovação do gerente
    * 
    * @param invoice - Invoice
    * @return quitação da fatura
    */
   @Transactional
   public void payInvoiceApproved(Invoice invoice) {

      invoice.setStatus(InvoiceStatus.PAID);

      /*
       * Lógica com erro, pois precisa da informação de qual o tipo de pgto que está sendo quitado (preenchido pelo usuário) Essa lógica
       * voltou para o método payInvoiceAprroved() para antes da ratificação. Verificar uma solução para quando a ratificação estiver
       * funcionando try { //se a fatura era um boleto gerado e foi quitado com outra forma de pgto, cancela o boleto.
       * if(invoice.getBarcodeTicket()!=null && !PaymentType.TICKETS.equals(invoice.getPaymentType()) &&
       * !PaymentType.TICKET.equals(invoice.getPaymentType())) { ticketGatewayService.cancelTicket(invoice); } //se a fatura era um boleto e
       * foi quitado com um boleto, paga-se o boleto if(false && invoice.getBarcodeTicket()!=null &&
       * (PaymentType.TICKETS.equals(invoice.getPaymentType()) || PaymentType.TICKET.equals(invoice.getPaymentType()))) {
       * ticketGatewayService.paidTicket(invoice); } } catch(Exception e) {
       * LOGGER.error("Erro na operacao com o boleto simples, ao quitar pagamento", e); }
       */

      // se a fatura PAGA é a primeira, desbloquear assinatura
      Invoice firstInvoice = getFirstInvoice(invoice.getSubscription().getId());
      if (firstInvoice.equals(invoice)) {
         queueSubscriptionOperation
                  .convertAndSend(new SubscriptionOperation(Operation.UNBLOCK, invoice.getSubscription().getId()));
      }

      // enviar e-mail para o cliente com comprovante
      if (StringUtils.isNotBlank(invoice.getSubscription().getHolder().getEmail())) {
         MailTemplate mail = new MailTemplateBuilder().subject("Pagamento efetivado clubFlex")
                  .template("paid-manual.html").addParam("nome", invoice.getSubscription().getHolder().getName())
                  .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                  .addParam("valor", invoice.getPayAmountFmt()).to(invoice.getSubscription().getHolder().getEmail())
                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                  .build();
         mailService.scheduleSend(mail);
      }

      // pode ocorrer de vir nulo
      if (invoice.getUserResponsiblePayment() != null) {
         this.generateLog(invoice.getUserResponsiblePayment(), invoice.getSubscription().getId(),
            "Fatura: ".concat(invoice.getId().toString()), SubscriptionLogAction.BAIXA_PAGAMENTO_FATURA);
      }
   }

   /**
    * Método responsável por deixar a alteração pendente de aprovação pelo gerente.
    * 
    * @param payInfo - PayInvoiceInfo
    * @param userResponsable - User
    * @return quitação da fatura
    */
   @Transactional
   public void changePaymentTypeInvoice(PayInvoiceInfo payInfo, User userResponsable) {

      Invoice invoice = invoiceRepository.findById(payInfo.getInvoiceId());
      if (invoice == null) {
         throw new ClubFlexException("Fatura não encontrada ou cancelada.");
      }
      if (!invoice.getStatus().equals(InvoiceStatus.PAID)) {
         throw new ClubFlexException("Fatura deve estar paga.");
      }
      if (payInfo.getNewPaymentType() == null) {
         throw new ClubFlexException("Nova forma de pagamento não informada");
      }
      if (payInfo.getJustification() == null) {
         throw new ClubFlexException("Justificativa não informada");
      }

      // Se for um gerente alterando a forma de pagamento, não precisa de aprovação.
      if (UserProfile.MANAGER.equals(userResponsable.getProfile()) ||
         UserProfile.SUPERVISOR.equals(userResponsable.getProfile())) {

         // registrar log da fatura
         invoiceLogService.generateLog(userResponsable, invoice, Functionality.ALT_FORMA_PGTO_FATURA);

         // registrar log da assinatura
         if (userResponsable != null) {
            String descricao = "Justificativa: " + payInfo.getJustification() + " "
               + "De: " + payInfo.getPaymentType().getDescribe() + " Para: " + payInfo.getNewPaymentType().getDescribe();
            saveLog(userResponsable.getId(), invoice.getSubscription().getId(),
               descricao, SubscriptionLogAction.ALT_FORMA_PAGAMENTO_FATURA);
         }

         invoice.setPaymentType(payInfo.getNewPaymentType());

         if (payInfo.getNewPaymentType().equals(PaymentType.DEBIT_CARD_LOCAL)
            || payInfo.getNewPaymentType().equals(PaymentType.CREDIT_CARD_LOCAL)) {
            invoice.setAuthorizationCode(payInfo.getAltValue());
         }
         else {
            invoice.setAuthorizationCode(null);
         }

         invoiceRepository.save(invoice);

         // Se não for gerente, precisa de aprovação
      }
      else {
         // Criar a ratificação para aprovação do gerente
         Ratification rat = createRatification(userResponsable, invoice);
         rat.setFunctionality(Functionality.ALT_FORMA_PGTO_FATURA);
         rat.setTypeFunctionality(TypeFunctionality.FATURA);
         rat.setJustification(payInfo.getJustification());
         String action =
            "Forma de Pgto. De " + invoice.getPaymentType().getDescribe() + " para " + payInfo.getNewPaymentType().getDescribe();
         rat.setAction(action);
         ratificationRepository.save(rat);

         // registrar log da assinatura
         if (userResponsable != null) {
            String descricao = "Justificativa: " + payInfo.getJustification() + " "
               + "De: " + payInfo.getPaymentType().getDescribe() + " Para: " + payInfo.getNewPaymentType().getDescribe();
            saveLog(userResponsable.getId(), invoice.getSubscription().getId(),
               descricao, SubscriptionLogAction.SOLICITACAO_ALT_FORMA_PGTO_FATURA);
         }

         if (payInfo.getNewPaymentType().equals(PaymentType.DEBIT_CARD_LOCAL)
            || payInfo.getNewPaymentType().equals(PaymentType.CREDIT_CARD_LOCAL)) {
            invoice.setAuthorizationCode(payInfo.getAltValue());
         }
         else {
            invoice.setAuthorizationCode(null);
         }

         invoice.setStatus(InvoiceStatus.PENDING);
         invoice.setNewPaymentType(payInfo.getNewPaymentType());
         invoiceRepository.save(invoice);
      }
   }

   /**
    * Método responsável por alterar a forma de pgto de uma fatura, após a aprovação do gerente
    * 
    * @param invoice - Invoice
    */
   @Transactional
   public void changePaymentTypeInvoiceApproved(Invoice invoice, Ratification rat) {

      // Invoice invoice = invoiceRepository.findById(invoiceRatificacaotion.getId());
      User userResponsable = userService.findUserById(invoice.getUserResponsiblePayment());

      // registrar log da fatura
      invoiceLogService.generateLog(userResponsable, invoice, Functionality.ALT_FORMA_PGTO_FATURA);

      // registrar log da assinatura
      if (userResponsable != null) {
         String descricao = "Justificativa: " + rat.getJustification() + " "
            + "De: " + invoice.getPaymentType().getDescribe() + " Para: " + invoice.getNewPaymentType().getDescribe();
         this.saveLog(userResponsable.getId(), invoice.getSubscription().getId(),
            descricao, SubscriptionLogAction.ALT_FORMA_PAGAMENTO_FATURA);
      }

      invoice.setPaymentType(invoice.getNewPaymentType());
      invoice.setStatus(InvoiceStatus.PAID);

      invoiceRepository.save(invoice);
   }

   /**
    * Método responsável por deixar uma quitação de fatura pendente de aprovação pelo gerente.
    * 
    * @param payInfo - PayInvoiceInfo
    * @param userResponsable - User
    * @return quitação da fatura
    */
   @Transactional
   public BigDecimal payInvoice(PayInvoiceInfo payInfo, User userResponsable) {
      Invoice invoice = invoiceRepository.findById(payInfo.getInvoiceId());
      if (invoice == null) {
         throw new ClubFlexException("Fatura não encontrada ou cancelada.");
      }
      if (payInfo.getDateOfPay() == null) {
         throw new ClubFlexException("Informe a data de pagamento da fatura");
      }
      if (payInfo.getAmountPaid() == null) {
         throw new ClubFlexException("Informe o valor pago da fatura");
      }
      if (payInfo.getPaymentType() == null) {
         throw new ClubFlexException("Forma de pagamento não informada");
      }
      // if(!UserProfile.MANAGER.equals(userResponsable.getProfile())) {
      if (!payInfo.getDateOfPay().isEqual(LocalDate.now())) {
         throw new ClubFlexException(
            "Quitação para datas anteriores ou posteriores não permitida. Solicite ao gerente.");
      }
      // }
      if (!InvoiceStatus.OPENED.equals(invoice.getStatus())) {
         throw new ClubFlexException("Fatura necessita estar em ABERTO para ser quitada.");
      }
      if (PaymentType.CREDIT_CARD_LOCAL.equals(payInfo.getPaymentType())
         || PaymentType.DEBIT_CARD_LOCAL.equals(payInfo.getPaymentType())) {
         if (StringUtils.isBlank(payInfo.getAltValue())) {
            throw new ClubFlexException("Informar código de autorização da operadora do cartão (ALT).");
         }
      }
      BigDecimal amountDue = invoice.getAmount(); // armazenado o valor devido

      // Rennan liberou em 11/11/2019 efetuar varios pagamentos
      BigDecimal paidAmount = new BigDecimal(payInfo.getAmountPaid()).setScale(2);
      if (paidAmount.doubleValue() <= 0) {
         throw new ClubFlexException("Valor informado é menor zero.");
      }

      // setando dados para quitacao oou cancelamento
      invoice.setPaymentDate(payInfo.getDateOfPay());

      invoice.setPayAmount(paidAmount);
      invoice.setAuthorizationCode(payInfo.getAltValue());

      // setando usuario responsável pelo pagamento da fatura
      if (userResponsable != null) {
         invoice.setUserResponsiblePayment(userResponsable.getId());
      }

      // Retirada a funcionalidade de Ratificação com "true"
      boolean retiradaFuncionalidade = true;

      // Se for um gerente cadastrando, não precisa de aprovação.
      if (retiradaFuncionalidade || UserProfile.MANAGER.equals(userResponsable.getProfile()) ||
         UserProfile.SUPERVISOR.equals(userResponsable.getProfile())) {

         // se foi pago de uma forma diferente da original
         try {
            if (!payInfo.getPaymentType().equals(invoice.getPaymentType())) {
               if (PaymentType.TICKETS.equals(invoice.getPaymentType()) || PaymentType.TICKET.equals(invoice.getPaymentType())) {
                  ticketGatewayService.cancelTicket(invoice);
               }
            }
            else {
               if (PaymentType.TICKETS.equals(invoice.getPaymentType()) || PaymentType.TICKET.equals(invoice.getPaymentType())) {
                  ticketGatewayService.paidTicket(invoice);
               }
            }
         }
         catch (Exception e) {
            LOGGER.error("Erro na operacao com o boleto simples, ao quitar pagamento", e);
         }

         payInvoiceApproved(invoice);

         // Se o valor pago é maior que o valor da conta, é considerado juros.
         BigDecimal diffJuros = paidAmount.subtract(invoice.getOriginalAmount());
         if (diffJuros.compareTo(BigDecimal.ZERO) > 0) {
            // registrar log da fatura
            invoiceLogService.generateLog(userResponsable, invoice, diffJuros, Functionality.JUROS_FATURA);
         }
         // registrar log da fatura
         invoiceLogService.generateLog(userResponsable, invoice, paidAmount, Functionality.QUITAR_FATURA);

         // Se não for gerente, precisa de aprovação
      }
      else {
         invoice.setStatus(InvoiceStatus.PENDING);

         // Criar a ratificação para aprovação do gerente
         Ratification rat = createRatification(userResponsable, invoice);
         rat.setFunctionality(Functionality.QUITAR_FATURA);
         rat.setTypeFunctionality(TypeFunctionality.FATURA);
         String action = "Fatura em " + invoice.getPaymentType().getDescribe() + ". Quitado com " + payInfo.getPaymentType().getDescribe();
         rat.setAction(action);
         ratificationRepository.save(rat);
      }

      invoice.setPaymentType(payInfo.getPaymentType());
      invoiceRepository.save(invoice);

      return amountDue.subtract(paidAmount);
   }

   private Ratification createRatification(User usuario, Invoice invoice) {
      Ratification ratification = new Ratification();
      ratification.setHolder(invoice.getSubscription().getHolder());
      ratification.setInvoice(invoice);
      ratification.setIsApproved(Boolean.FALSE);
      ratification.setIsPending(Boolean.TRUE);
      ratification.setTypeFunctionality(TypeFunctionality.FATURA);
      ratification.setUpdatedAt(LocalDateTime.now());
      ratification.setUser(usuario);
      return ratification;
   }

   @Transactional
   public void refoundInvoice(PayInvoiceInfo payInfo, User userResponsable) {
      Invoice invoice = invoiceRepository.findById(payInfo.getInvoiceId());
      String template = "cancelled-invoice.html";
      String valorPago = "0,00";

      DecimalFormat vlrFmt = new DecimalFormat("#,###,##0.00");
      if (invoice == null) {
         throw new ClubFlexException("Fatura não encontrada ou cancelada.");
      }
      if (payInfo.getRefoundAmount() == null) {
         throw new ClubFlexException("Informe o valor a ser estornado.");
      }
      if (!UserProfile.MANAGER.equals(userResponsable.getProfile())) {
         throw new ClubFlexException("Estorno não permitido. Solicite ao gerente.");
      }
      if (!InvoiceStatus.PAID.equals(invoice.getStatus())) {
         throw new ClubFlexException("Fatura necessita estar PAGA para ser estornada.");
      }
      BigDecimal refoundAmount = new BigDecimal(payInfo.getRefoundAmount()).setScale(2);
      BigDecimal amountPaid = new BigDecimal(payInfo.getAmountPaid()).setScale(2);

      if (refoundAmount.doubleValue() <= 0) {
         throw new ClubFlexException("Valor a ser estornado deve ser maior que zero.");
      }

      if (refoundAmount.compareTo(amountPaid) > 0) {
         throw new ClubFlexException("Valor a ser estornado deve ser igual ou menor ao valor pago.");
      }

      if ((!invoice.getPayAmount().equals(refoundAmount)) && LocalDate.now().compareTo(invoice.getPaymentDate().plusDays(1)) <= 0
         && (invoice.getPaymentType().equals(PaymentType.CREDIT_CARD) || invoice.getPaymentType().equals(PaymentType.DEBIT_CARD))) {
         throw new ClubFlexException("Estorno parcial disponível a partir do próximo dia ao pagamento.");
      }

      BigDecimal finalAmount = amountPaid.subtract(refoundAmount);

      if (invoice.getPayAmount().equals(refoundAmount)) {
         valorPago = vlrFmt.format(invoice.getPayAmount());
         refundInvoiceApproved(invoice);

      }
      else {
         template = "refund-invoice.html";
         valorPago = invoice.getPayAmountFmt();
         // setando dados para estorno
         invoice.setPayAmount(finalAmount);
         refundInvoice(invoice, refoundAmount);

         // registrar log da fatura
         invoiceLogService.generateLog(userResponsable, invoice, refoundAmount.negate(),
            Functionality.ESTORNAR_FATURA);
         // pode ocorrer de vir nulo
         if (userResponsable != null) {
            this.generateLog(userResponsable.getId(), invoice.getSubscription().getId(),
               "Estorno de R$ " + vlrFmt.format(refoundAmount), SubscriptionLogAction.ESTORNO_FATURA);
         }
      }
      // enviar e-mail para o cliente com comprovante
      if (StringUtils.isNotBlank(invoice.getSubscription().getHolder().getEmail())) {
         MailTemplate mail = new MailTemplateBuilder().subject("Fatura clubFlex")
                  .template(template).addParam("nome", invoice.getSubscription().getHolder().getName())
                  .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                  .addParam("valor", valorPago).to(invoice.getSubscription().getHolder().getEmail())
                  .addParam("numero", invoice.getId().toString())
                  .addParam("estorno", vlrFmt.format(refoundAmount))
                  .addParam("valorFinal", invoice.getPayAmountFmt())
                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                  .build();
         mailService.scheduleSend(mail);
      }
   }

   public boolean hasInvoiceOverDue(Long subscriptionId) {
      List<Invoice> invoices = listInvoiceBySubscriptionIdAndStatus(subscriptionId, InvoiceStatus.OPENED);
      AtomicBoolean hasInvoiceOverDue = new AtomicBoolean(false);
      invoices.forEach(invoice -> {
         if (invoice.isOutDate()) {
            hasInvoiceOverDue.set(true);
         }
      });
      return hasInvoiceOverDue.get();
   }

   public Invoice getFirstInvoice(Long subscriptionId) {
      return invoiceRepository.getFirstInvoice(subscriptionId);
   }

   public Invoice getLastInvoicePay(Long subscriptionId) {
      return invoiceRepository.getLastInvoicePay(subscriptionId);
   }

   public Invoice getLastCompetence(Long subscriptionId) {
      return invoiceRepository.getLastCompetence(subscriptionId);
   }

   public Invoice getLastInvoicePay(Long subscriptionId, InvoiceType type) {
      return invoiceRepository.getLastInvoicePay(subscriptionId, type);
   }

   public List<Invoice> listLastBySubscriptionId(Long subscriptionId) {
      return invoiceRepository.listLastBySubscriptionId(subscriptionId);
   }

   public List<Invoice> listInvoiceBySubscriptionId(Long subscriptionId) {
      List<Invoice> listBySubscriptionId = listBySubscriptionId(subscriptionId);
      listBySubscriptionId.forEach(invoice -> {
         if (invoice.getStatus().equals(InvoiceStatus.DENIED)) {
            invoice.setJustificationDenied(ratificationRepository.getJustificationRatificaion(invoice.getId()));
         }
      });
      return listBySubscriptionId;
   }

   public List<Invoice> loadInvoiceBySubs(User userRequester, Long subscriptionId) {

      Subscription sub = subscriptionRepository.findById(subscriptionId);
      if (sub != null) { // && UserProfile.HOLDER.equals(userRequester.getProfile())) {
         // User user = userService.findUserByHolderId(sub.getHolder().getId());
         User user = userService.findUserById(userRequester.getId());

         List<Subscription> listaSubDep = subscriptionRepository.findByDependentOkByCpf(user.getLogin());
         if (listaSubDep != null && listaSubDep.isEmpty()) {
            List<Subscription> listaSubHolder = subscriptionRepository.findByHolderCpfCnpj(user.getLogin());
            if (listaSubHolder != null && listaSubHolder.isEmpty()) {
               throw new ClubFlexException("Acesso não permitido.");
            }
         }
      }

      List<Invoice> listBySubscriptionId = listBySubscriptionId(subscriptionId);
      listBySubscriptionId.forEach(invoice -> {
         if (invoice.getStatus().equals(InvoiceStatus.DENIED)) {
            invoice.setJustificationDenied(ratificationRepository.getJustificationRatificaion(invoice.getId()));
         }
      });
      return listBySubscriptionId;
   }

   public List<Invoice> listBySubscriptionId(Long subscriptionId) {
      return invoiceRepository.listBySubscriptionId(subscriptionId);
   }

   public List<Invoice> listLastWithoutCancelledBySubscriptionId(Long subscriptionId) {
      return invoiceRepository.listLastWithoutCancelledBySubscriptionId(subscriptionId);
   }

   public void sendToGenerateMonthlyInvoice(BigInteger subId) {
      queueMonthlyInvoice.convertAndSend(subId.longValue());
   }

   @Transactional
   public void generateMonthlyInvoice(Long subscriptionId) {
      Integer actualCompetence = Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
      Subscription sub = subscriptionRepository.findById(subscriptionId);
      Invoice lastInvoiceGenerated = invoiceRepository.getLastInvoice(subscriptionId, InvoiceType.DEFAULT);

      LocalDate beginCompetence = LocalDate.now();
      LocalDate endCompetence = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
      BigDecimal amountToPay = sub.getTotalPrice();

      // tratar invoice com valor zero ou menor.
      if (amountToPay.doubleValue() <= 0.0) {
         throw new ClubFlexException("Valor a pagar zerado ou inválido. Sem necessidade de fatura.");
      }

      Boolean hasProRate = TypeSub.PF.equals(sub.getTypeSub()) && lastInvoiceGenerated != null
         && actualCompetence.equals(Integer.valueOf(
            lastInvoiceGenerated.getCompetenceEnd().format(DateTimeFormatter.ofPattern("yyyyMM"))));
      if (hasProRate) {
         beginCompetence = lastInvoiceGenerated.getCompetenceEnd().plusDays(1);
         amountToPay = NumberUtils.proRateCalculate(sub.getTotalPrice(), beginCompetence, endCompetence);
      }

      if (PaymentType.TICKET.equals(sub.getPaymentType())) {

         LocalDate dataVencimento = LocalDate.now().withDayOfMonth(sub.getDayForPayment());
         Invoice invoice = new InvoiceBuilder(sub).addAmount(amountToPay, "Mensalidade de assinatura clubflex.")
                  .withType(InvoiceType.DEFAULT).withDueDate(HolidayUtils.proximoDiaUtil(dataVencimento))
                  .withStatus(InvoiceStatus.GENERATING).withCompetence(beginCompetence, endCompetence).build();
         invoiceRepository.save(invoice); // salvar invoice
         ticketGatewayService.generateTicket(invoice); // enviar para registro

      }
      else {
         CreditCard creditCard = creditCardService.findByHolderId(sub.getHolder().getId());
         Invoice invoice = new InvoiceBuilder(sub).addAmount(amountToPay, "Mensalidade de assinatura clubflex.")
                  .withType(InvoiceType.DEFAULT).withCreditCard(creditCard)
                  .withDueDate(LocalDate.now().withDayOfMonth(sub.getDayForPayment()))
                  .withCompetence(beginCompetence, endCompetence).build();
         invoiceRepository.save(invoice); // salvar invoice
      }

      // atualizar assinatura com competencia da fatura mensal gerada.
      sub.setDateLastCompetence(actualCompetence);
      subscriptionRepository.merge(sub);
   }

   public List<BigInteger> listInvoicesToRecurrence(LocalDate beginDueDate, LocalDate endDueDate) {
      return invoiceRepository.listInvoicesToRecurrence(beginDueDate, endDueDate);
   }

   public List<BigInteger> listInvoicesToRecurrenceReponsibleFinancial(LocalDate beginDueDate, LocalDate endDueDate, Boolean filter) {
      return invoiceRepository.listInvoicesToRecurrenceReponsibleFinancial(beginDueDate, endDueDate, filter);
   }

   @Transactional
   public void generateWithLocalPayment(Invoice invoice) {
      invoiceRepository.save(invoice);
   }

   @Transactional
   public void update(Invoice invoice) {
      invoiceRepository.update(invoice);
   }

   public List<Invoice> listPaidWithoutNfeId() {
      return invoiceRepository.listPaidWithoutNfeId();
   }

   public List<Invoice> listPaidWithoutNfeNumber() {
      return invoiceRepository.listPaidWithoutNfeNumber();
   }

   public Invoice findById(Long id) {
      return invoiceRepository.findById(id);
   }

   public SimulateFirstInvoice simulateFirstInvoice(CreateSubscriptionRequest form) {

      if (form.getDateBegin() != null && form.getDateBegin().isBefore(LocalDate.now())) {
         throw new ClubFlexException("Data de cadastro não pode ser no passado.");
      }

      if (form.getPlanId() == null) {
         throw new ClubFlexException("Informe o plano para a assinatura.");
      }

      /**
       * Alteração - Data do vencimento = data do cadastro if (form.getDayForPayment() == null) { throw new ClubFlexException("Informe o dia
       * de vencimento."); }
       */
      form.setDayForPayment(LocalDate.now().getDayOfMonth());

      if (form.getDependents() != null && form.getDependents().size() == 0 && form.getHolderOnlyResponsibleFinance()) {
         throw new ClubFlexException("É necessário informar pelo menos um dependente.");
      }

      if (form.getHolder().getNumber() == null) {
         throw new ClubFlexException("O número do endereço é numérico e obrigatório.");
      }

      SimulateFirstInvoice simulate = new SimulateFirstInvoice();

      Plan plan = planService.findById(form.getPlanId());

      BigDecimal totalPrice = BigDecimal.ZERO;
      int dependentesRestantes = form.getDependents().size();

      // Se há apenas Resp. Financeiro, o número restantes é o numero de dependentes - 1 (um dependente é titular)
      if (form.getHolderOnlyResponsibleFinance()) {
         dependentesRestantes = form.getDependents().size() - 1;
      }

      // Ou Titular ou Resp. Financeiro sempre terá um valor financeiro de titular.
      totalPrice = totalPrice.add(plan.getPriceHolder());

      // dependentes
      if (dependentesRestantes > 0) {
         totalPrice = totalPrice.add(plan.getPriceDependent().multiply(new BigDecimal(dependentesRestantes)));
      }

      LocalDate dateBegin = (form.getDateBegin() == null) ? LocalDate.now() : form.getDateBegin();
      LocalDate baseData = LocalDate.now();

      LocalDate payDayNextMonth = baseData.plusMonths(1).withDayOfMonth(form.getDayForPayment());
      if (dateBegin.isAfter(baseData)) { // Data do contrato futura
         payDayNextMonth = dateBegin.plusMonths(1).withDayOfMonth(form.getDayForPayment());
      }
      BigDecimal amountProRatePlan = NumberUtils.proRateCalculate(totalPrice, dateBegin, payDayNextMonth);

      // taxa de adesao
      if (plan.getAccessionFee() != null) {
         simulate.setAccessionFee(plan.getAccessionFee());
         simulate.setTotalAmount(amountProRatePlan.add(plan.getAccessionFee()));
      }
      else {
         simulate.setAccessionFee(BigDecimal.ZERO);
         simulate.setTotalAmount(amountProRatePlan);
      }

      simulate.setProRataAmount(amountProRatePlan);
      simulate.setDataBegin(dateBegin);
      simulate.setDataEnd(payDayNextMonth);
      simulate.setQuantityDays(ChronoUnit.DAYS.between(dateBegin, payDayNextMonth));

      return simulate;
   }

   public void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      log.setObs(obs);
      queueSubscriptionLog.convertAndSend(log);
   }

   public void saveLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      log.setObs(obs);
      subscriptionLogRepository.saveLog(log);
   }

   public List<BigInteger> listInvoiceWithoutPaymentAndDueToday() {
      return invoiceRepository.listInvoiceWithoutPaymentAndDueToday();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue10DaysBefore() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue10DaysBefore();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue3Days() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue3Days();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue3DaysCredit() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue3DaysCredit();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue8Days() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue8Days();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue8DaysCredit() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue8DaysCredit();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue13Days() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue13Days();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue13DaysCredit() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue13DaysCredit();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue18Days() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue18Days();
   }

   public List<BigInteger> ListInvoiceWithoutPaymentAndDue18DaysCredit() {
      return invoiceRepository.ListInvoiceWithoutPaymentAndDue18DaysCredit();
   }

   @Transactional
   public void updateAmountInvoice(Invoice invoice, BigDecimal newAmount) {
      if (InvoiceType.DEFAULT.equals(invoice.getType())) { // não atualiza boletos de acordo.
         if (PaymentType.TICKETS.equals(invoice.getPaymentType())) {
            ticketGatewayService.updateAmountTicket(invoice, newAmount);
            // ticketGatewayService.sincronizeTicketInfo(invoice);
         }
         invoice.setAmount(newAmount);
         invoice.setBarcodeTicket(null);
         invoiceRepository.save(invoice);
      }
   }

   public List<BigInteger> listInvoiceWithoutPaymentAndDue() {
      return invoiceRepository.listInvoiceWithoutPaymentAndDue();
   }

   public List<Invoice> findByNsu(String nsu) {
      return invoiceRepository.findByNsu(nsu);
   }

   public void regenerateTicketsOnGateway(Long invoiceId) {
      Invoice invoice = invoiceRepository.findById(invoiceId);
      if (invoice != null && StringUtils.isBlank(invoice.getTransactId())) {
         ticketGatewayService.generateTicket(invoiceId);
      }
   }
}
