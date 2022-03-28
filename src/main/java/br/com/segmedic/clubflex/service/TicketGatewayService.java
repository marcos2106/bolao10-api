
package br.com.segmedic.clubflex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.GatewayTicketCallbackTicket;
import br.com.segmedic.clubflex.model.GatewayTicketCustomerRequest;
import br.com.segmedic.clubflex.model.GatewayTicketCustomerResponse;
import br.com.segmedic.clubflex.model.GatewayTicketPayRequest;
import br.com.segmedic.clubflex.model.GatewayTicketRegisterRequest;
import br.com.segmedic.clubflex.model.GatewayTicketRegisterResponse;
import br.com.segmedic.clubflex.repository.HolderRepository;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.JsonUtils;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class TicketGatewayService {

   private static final Logger LOGGER = LoggerFactory.getLogger(TicketGatewayService.class);

   private static final String BANK_BILLET_OVERDUE = "bank_billet.overdue";
   private static final String BANK_BILLET_PAID = "bank_billet.paid";
   private static final String BANK_BILLET_CANCELED = "bank_billet.canceled";
   private static final String BANK_BILLET_GENERATED = "bank_billet.generated";
   private static final String BANK_BILLET_CREATED = "bank_billet.created";
   private static final Integer MAX_ATTEMPT = 3;
   private static final String CUSTOMERS = "customers";
   private static final String CUSTOMERS_CPF_CNPJ = "customers/cnpj_cpf";
   private static final String BANK_BILLETS = "bank_billets";
   private static final int HTTP_CREATED = 201;
   private static final int HTTP_OK = 200;
   private static final int HTTP_NO_CONTENT = 204;

   @Autowired
   private JmsTemplate queueInvoice;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private HolderRepository holderRepository;

   @Autowired
   private MailService mailService;

   @Value("${ticket.gateway.api.url}")
   private String gatewayApiUrl;

   @Value("${ticket.gateway.api.token}")
   private String gatewayApiToken;

   @Value("${ticket.gateway.api.layout}")
   private Integer gatewayApiLayout;

   public void generateTickets(List<Invoice> invoices) {
      invoices.forEach(i -> {
         queueInvoice.convertAndSend(i.getId());
      });
   }

   public void generateTicket(Invoice invoice) {
      queueInvoice.convertAndSend(invoice.getId());
   }

   public void generateTicket(Long invoiceId) {
      queueInvoice.convertAndSend(invoiceId);
   }

   @Transactional
   public Long getCustomerByCpfCnpj(Holder holder) {
      try {
         if (StringUtils.isNotBlank(holder.getCpfCnpj())) {

            HttpResponse<String> response =
               Unirest.get(gatewayApiUrl.concat(CUSTOMERS_CPF_CNPJ).concat("?q=").concat(holder.getCpfCnpjFmt()))
               .header("Authorization", "Bearer ".concat(gatewayApiToken))
                        .header("Content-type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                        .asString();

            if (response.getStatus() != HTTP_OK) {
               throw new ClubFlexException("Erro de integracao com boleto simples. Detalhe: ".concat(response.getBody()));
            }

            // salvando id do cliente
            GatewayTicketCustomerResponse responseData =
               JsonUtils.jsonStringToObject(response.getBody(), GatewayTicketCustomerResponse.class);
            holder.setGatewayTicketId(responseData.getId());
            holderRepository.save(holder);

            return responseData.getId();
         }

         return holder.getGatewayTicketId();

      }
      catch (Exception e) {
         return null;
      }
   }

   @Transactional
   public Long createCustomer(Holder holder) {
      try {
         String body = JsonUtils.objectToJsonString(new GatewayTicketCustomerRequest(holder));

         HttpResponse<String> response = Unirest.post(gatewayApiUrl.concat(CUSTOMERS))
                  .header("Authorization", "Bearer ".concat(gatewayApiToken))
                  .header("Content-type", "application/json")
                  .header("Accept", "application/json")
                  .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                  .body(body).asString();

         if (response.getStatus() != HTTP_CREATED) {
            throw new ClubFlexException("Erro de integracao com boleto simples. Detalhe: ".concat(response.getBody()));
         }

         // salvando id do cliente
         GatewayTicketCustomerResponse responseData = JsonUtils.jsonStringToObject(response.getBody(), GatewayTicketCustomerResponse.class);
         holder.setGatewayTicketId(responseData.getId());
         holderRepository.save(holder);

         return responseData.getId();

      }
      catch (Exception e) {
         return null;
      }
   }

   public void updateCustomer(Holder holder) {
      try {
         if (holder.getGatewayTicketId() != null) {
            String body = JsonUtils.objectToJsonString(new GatewayTicketCustomerRequest(holder));

            HttpResponse<String> response =
               Unirest.put(gatewayApiUrl.concat(CUSTOMERS).concat("/").concat(holder.getGatewayTicketId().toString()))
               .header("Authorization", "Bearer ".concat(gatewayApiToken))
                        .header("Content-type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                        .body(body).asString();

            if (response.getStatus() != HTTP_NO_CONTENT) {
               throw new ClubFlexException(
                  "Erro de integracao ao atualizar cliente no boleto simples. Detalhe: ".concat(response.getBody()));
            }
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro ao atualizar dados do cliente no boleto simples.", e);
      }
   }

   @Transactional
   public Long getCustomer(Holder holder) {
      Long id = holder.getGatewayTicketId();
      if (id == null) {
         id = getCustomerByCpfCnpj(holder);
         if (id == null) {
            id = createCustomer(holder);
         }
      }
      return id;
   }

   @Transactional
   public void registerTicket(Long invoiceId, Integer numberOfAttempt) throws InterruptedException {
      Invoice invoice = null;
      try {
         invoice = invoiceRepository.findById(invoiceId);
         if (invoice == null) {
            throw new ClubFlexException("Fatura não encontrada.");
         }
         if (StringUtils.isNotBlank(invoice.getTransactId())) {
            throw new ClubFlexException("Fatura já registrada.");
         }

         Long customerById = getCustomer(invoice.getSubscription().getHolder());
         String body = JsonUtils.objectToJsonString(new GatewayTicketRegisterRequest(invoice, customerById, gatewayApiLayout));

         HttpResponse<String> response = Unirest.post(gatewayApiUrl.concat(BANK_BILLETS))
                  .header("Authorization", "Bearer ".concat(gatewayApiToken))
                  .header("Content-type", "application/json")
                  .header("Accept", "application/json")
                  .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                  .body(body).asString();

         LOGGER.error("BOLETO SIMPLES RET registerTicket: ".concat(response.getBody()));

         if (response.getStatus() != HTTP_CREATED) {
            throw new ClubFlexException("Erro de integracao com boleto simples. Detalhe: ".concat(response.getBody()));
         }

         // salvando invoice com numeracao e link de boleto
         GatewayTicketRegisterResponse responseData = JsonUtils.jsonStringToObject(response.getBody(), GatewayTicketRegisterResponse.class);
         invoice.setStatus(InvoiceStatus.valueOf(responseData.getStatus().toUpperCase()));
         invoice.setUrlTicket(responseData.getUrl());
         invoice.setTransactId(responseData.getId());
         invoice.setBarcodeTicket(responseData.getLine());
         invoiceRepository.save(invoice);

         // envia boleto para pagamento
         try {
            MailTemplate mail = new MailTemplateBuilder()
                     .subject("Boleto para pagamento clubFlex")
                     .template("new-ticket-generated.html")
                     .addParam("nome", invoice.getSubscription().getHolder().getName())
                     .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                     .addParam("valor", invoice.getAmountFmt())
                     .addParam("link", invoice.getUrlTicket())
                     .addParam("linkdescricao", invoice.getUrlTicket())
                     .addParam("linhadigitavel", invoice.getBarcodeTicket())
                     .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                     .to(invoice.getSubscription().getHolder().getEmail())
                     .build();
            mailService.scheduleSend(mail);
         }
         catch (Exception e) {
            LOGGER.error("Erro ao enviar e-mail com boleto para pagamento", e);
         }

      }
      catch (Exception e) {
         if (numberOfAttempt <= MAX_ATTEMPT) {
            TimeUnit.MILLISECONDS.sleep(500); // esperar meio/segundo antes de tentar novamente
            registerTicket(invoiceId, numberOfAttempt + 1);
         }
         else {
            LOGGER.error("Erro ao registrar boleto no gateway de pgt invoice =>".concat(invoiceId.toString()), e);
            throw new ClubFlexException("Erro geral ao integrar pagamento com boleto simples.", e);
         }
      }
   }

   @Transactional
   public void updateAmountTicket(Invoice invoice, BigDecimal newAmount) {
      try {
         String body = String.format("{\"bank_billet\":{\"amount\":\"%s\"}}", newAmount);

         HttpResponse<String> response = Unirest.put(gatewayApiUrl.concat(BANK_BILLETS).concat("/").concat(invoice.getTransactId()))
                  .header("Authorization", "Bearer ".concat(gatewayApiToken))
                  .header("Content-type", "application/json")
                  .header("Accept", "application/json")
                  .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                  .body(body).asString();

         if (response.getStatus() != HTTP_NO_CONTENT) {
            throw new ClubFlexException("Erro para alterar valor do boleto. Detalhe: ".concat(response.getBody()));
         }

      }
      catch (Exception e) {
         LOGGER.error("Erro para alterar data de vencimento no boleto simples =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro na alteração de data de vencimento no boleto simples.", e);
      }
   }

   @Transactional
   public void updateDueDateTicket(Invoice invoice, LocalDate newDate) {
      try {
         String body = String.format("{\"bank_billet\":{\"expire_at\":\"%s\"}}", newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

         HttpResponse<String> response = Unirest.put(gatewayApiUrl.concat(BANK_BILLETS).concat("/").concat(invoice.getTransactId()))
                  .header("Authorization", "Bearer ".concat(gatewayApiToken))
                  .header("Content-type", "application/json")
                  .header("Accept", "application/json")
                  .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                  .body(body).asString();

         if (response.getStatus() != HTTP_NO_CONTENT) {
            throw new ClubFlexException("Erro para alterar data de vencimento do boleto. Detalhe: ".concat(response.getBody()));
         }

      }
      catch (Exception e) {
         LOGGER.error("Erro para alterar data de vencimento no boleto simples =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro na alteração de data de vencimento no boleto simples.", e);
      }
   }

   public void cancelTicket(Invoice invoice) {
      try {
         if (invoice.getTransactId() != null) {
            HttpResponse<String> response =
               Unirest.put(gatewayApiUrl.concat(BANK_BILLETS).concat("/").concat(invoice.getTransactId()).concat("/cancel"))
               .header("Authorization", "Bearer ".concat(gatewayApiToken))
                        .header("Content-type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                        .asString();

            if (response.getStatus() != HTTP_NO_CONTENT) {
               throw new ClubFlexException("Erro para cancelar boleto. Detalhe: ".concat(response.getBody()));
            }
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro para cancelar boleto no boleto simples =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro para cancelar pagamentos junto ao gateway de PGT.", e);
      }
   }

   public void paidTicket(Invoice invoice) {
      try {
         if (invoice.getTransactId() != null) {

            String body = JsonUtils.objectToJsonString(new GatewayTicketPayRequest(invoice));

            HttpResponse<String> response =
               Unirest.put(gatewayApiUrl.concat(BANK_BILLETS).concat("/").concat(invoice.getTransactId()).concat("/pay"))
               .header("Authorization", "Bearer ".concat(gatewayApiToken))
                        .header("Content-type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                        .body(body).asString();

            if (response.getStatus() != HTTP_NO_CONTENT) {
               throw new ClubFlexException("Erro para quitar boleto. Detalhe: ".concat(response.getBody()));
            }
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro para quitar boleto no boleto simples =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro para quitar pagamentos junto ao gateway de PGT.", e);
      }
   }

   @Transactional
   public void processCallback(GatewayTicketCallbackTicket data) {
      Invoice invoice = invoiceRepository.findGatewayId(data.getObject().getId());

      try {
         if (data.getEventCode().equals(BANK_BILLET_GENERATED) && InvoiceStatus.GENERATING.equals(invoice.getStatus())) {
            invoice.setStatus(InvoiceStatus.OPENED);
            invoice.setBarcodeTicket(data.getObject().getLine());
            invoice.setUrlTicket(data.getObject().getUrl());
            invoice.setDateTimeRegisteredBolSimplesLog(LocalDateTime.now());

            // enviar e-mail para o cliente
            if (StringUtils.isNotBlank(invoice.getSubscription().getHolder().getEmail())) {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Boleto para pagamento clubFlex")
                        .template("new-ticket-generated.html")
                        .addParam("nome", invoice.getSubscription().getHolder().getName())
                        .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .addParam("valor", invoice.getAmountFmt())
                        .addParam("link", invoice.getUrlTicket())
                        .addParam("linkdescricao", invoice.getUrlTicket())
                        .addParam("linhadigitavel", invoice.getBarcodeTicket())
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                        .to(invoice.getSubscription().getHolder().getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }

         }
         else if (data.getEventCode().equals(BANK_BILLET_CREATED) && InvoiceStatus.GENERATING.equals(invoice.getStatus())) {
            invoice.setStatus(InvoiceStatus.OPENED);
            invoice.setBarcodeTicket(data.getObject().getLine());
            invoice.setUrlTicket(data.getObject().getUrl());
            invoice.setDateTimeRegisteredBolSimplesLog(LocalDateTime.now());

         }
         else if (data.getEventCode().equals(BANK_BILLET_CANCELED)) {
            // Retirado pois a resposta do boleto simples estava chegando antes do dado persistir na base de dados e sobrescrevia o status
            // if(!InvoiceStatus.PAID.equals(invoice.getStatus())) {
            // invoice.setStatus(InvoiceStatus.CANCELLED);
            // invoice.setPaymentCancelDate(LocalDateTime.now());
            // invoice.setPayAmount(BigDecimal.ZERO);
            //
            // //enviar e-mail para o cliente
            // MailTemplate mail = new MailTemplateBuilder()
            // .subject("Boleto cancelado clubFlex")
            // .template("cancelled-ticket.html")
            // .addParam("nome", invoice.getSubscription().getHolder().getName())
            // .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            // .addParam("valor", invoice.getPayAmountFmt())
            // .addParam("numero", invoice.getId().toString())
            // .to(invoice.getSubscription().getHolder().getEmail())
            // .build();
            // mailService.scheduleSend(mail);
            // }
         }
         else if (data.getEventCode().equals(BANK_BILLET_PAID)) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaymentDate(LocalDate.parse(data.getObject().getPaidAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            invoice.setPayAmount(new BigDecimal(data.getObject().getPaidAmount()));
            invoice.getSubscription().setWaitingFirstPay(false);

            if (StringUtils.isNotBlank(invoice.getSubscription().getHolder().getEmail())) {
               // enviar e-mail para o cliente
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Pagamento efetivado clubFlex")
                        .template("paid-ticket.html")
                        .addParam("nome", invoice.getSubscription().getHolder().getName())
                        .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .addParam("valor", invoice.getPayAmountFmt())
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                        .to(invoice.getSubscription().getHolder().getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }
         }
         else if (data.getEventCode().equals(BANK_BILLET_OVERDUE)) {
            // enviar e-mail para o cliente
            if (StringUtils.isNotBlank(invoice.getSubscription().getHolder().getEmail())) {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Boleto vencendo clubFlex")
                        .template("overdue-ticket.html")
                        .addParam("nome", invoice.getSubscription().getHolder().getName())
                        .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .addParam("valor", invoice.getAmountFmt())
                        .addParam("link", invoice.getUrlTicket())
                        .addParam("linkdescricao", invoice.getUrlTicket())
                        .addParam("linhadigitavel", invoice.getBarcodeTicket())
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                        .to(invoice.getSubscription().getHolder().getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }
         }

         // salvar invoice
         invoiceRepository.save(invoice);

      }
      catch (Exception e) {
         LOGGER.error("Erro no processo de callback =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro no processo de callback.", e);
      }

   }

   @Transactional
   public void sincronizeTicketInfo(Invoice invoice) {
      try {
         if (invoice.getTransactId() != null) {
            HttpResponse<String> response = Unirest.get(gatewayApiUrl.concat(BANK_BILLETS).concat("/").concat(invoice.getTransactId()))
                     .header("Authorization", "Bearer ".concat(gatewayApiToken))
                     .header("Content-type", "application/json")
                     .header("Accept", "application/json")
                     .header("User-Agent", "ClubFlex (danvsantos@gmail.com)")
                     .asString();

            GatewayTicketRegisterResponse responseData =
               JsonUtils.jsonStringToObject(response.getBody(), GatewayTicketRegisterResponse.class);

            if (response.getStatus() != HTTP_OK) {
               throw new ClubFlexException("Erro ao sincronizar boleto. Detalhe: ".concat(response.getBody()));
            }

            if (responseData.getStatus().toUpperCase().equals("CANCELED")) {
               // Somente deixar cancelar boletos com menos de 60 dias de vencido
               // O boleto simples está cancelando automaticamente e estamos verificando o motivo.
               long days = ChronoUnit.DAYS.between(invoice.getDueDate(), LocalDateTime.now());
               if (days < 60) {
                  invoice.setStatus(InvoiceStatus.CANCELLED);
               }
            }
            else if (responseData.getStatus().toUpperCase().equals("PAID")) {
               invoice.setStatus(InvoiceStatus.PAID);
               invoice.setPaymentDate(LocalDate.parse(responseData.getPaidAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
               invoice.setPayAmount(new BigDecimal(responseData.getPaidAmount()));
            }
            else {
               if (!responseData.getStatus().toUpperCase().equals("OVERDUE") &&
                  !responseData.getStatus().toUpperCase().equals("GENERATION_FAILED") &&
                  !responseData.getStatus().toUpperCase().equals("VALIDATION_FAILED")) {
                  invoice.setStatus(InvoiceStatus.valueOf(responseData.getStatus().toUpperCase()));
                  invoice.setDateTimeRegisteredBolSimplesLog(LocalDateTime.now());
                  invoice.setUrlTicket(responseData.getUrl());
                  invoice.setBarcodeTicket(responseData.getLine());
               }
            }

            // salvar invoice
            invoiceRepository.save(invoice);
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro para sincronizar boleto com o boleto simples =>".concat(invoice.getId().toString()), e);
         throw new ClubFlexException("Erro para sincronizar pagamentos junto ao gateway de PGT.", e);
      }

   }
}
