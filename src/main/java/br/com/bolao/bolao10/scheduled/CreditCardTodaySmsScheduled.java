
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.SmsParams;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.SystemParamsService;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;

@Component
public class CreditCardTodaySmsScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardTodaySmsScheduled.class);
   // private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private SystemParamsService paramsService;

   @Value("${zenvia.api.remetente}")
   private String remetente;
   @Value("${zenvia.api.username}")
   private String username;
   @Value("${zenvia.api.password}")
   private String password;

   public List<Invoice> ListInvoiceWithoutPaymentAndDue3Days() {
      try {
         List<BigInteger> invoicesId = invoiceService.ListInvoiceWithoutPaymentAndDue3DaysCredit();
         List<Invoice> invoices = new ArrayList<Invoice>();
         invoicesId.forEach(invoice -> {
            Invoice invoiceObj = invoiceService.findById(invoice.longValue());
            invoices.add(invoiceObj);
         });
         return invoices;
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> ListInvoiceWithoutPaymentAndDue8Days() {
      try {
         List<BigInteger> invoicesId = invoiceService.ListInvoiceWithoutPaymentAndDue8DaysCredit();
         List<Invoice> invoices = new ArrayList<Invoice>();
         invoicesId.forEach(invoice -> {
            Invoice invoiceObj = invoiceService.findById(invoice.longValue());
            invoices.add(invoiceObj);
         });
         return invoices;
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> ListInvoiceWithoutPaymentAndDue13Days() {
      try {
         List<BigInteger> invoicesId = invoiceService.ListInvoiceWithoutPaymentAndDue13DaysCredit();
         List<Invoice> invoices = new ArrayList<Invoice>();
         invoicesId.forEach(invoice -> {
            Invoice invoiceObj = invoiceService.findById(invoice.longValue());
            invoices.add(invoiceObj);
         });
         return invoices;
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> ListInvoiceWithoutPaymentAndDue18Days() {
      try {
         List<BigInteger> invoicesId = invoiceService.ListInvoiceWithoutPaymentAndDue18DaysCredit();
         List<Invoice> invoices = new ArrayList<Invoice>();
         invoicesId.forEach(invoice -> {
            Invoice invoiceObj = invoiceService.findById(invoice.longValue());
            invoices.add(invoiceObj);
         });
         return invoices;
      }
      catch (Exception e) {
         return null;
      }
   }

   public String parametrizaMensagem(String mensagem, Invoice invoice, Integer qtdDias) {

      String mensagemParm = mensagem;

      mensagemParm = mensagemParm.replaceAll("\\{primeiro_nome\\}",
         invoice.getSubscription().getHolder().getName().split(" ")[0]);
      mensagemParm = mensagemParm.replaceAll("\\{d\\}", qtdDias.toString());
      mensagemParm = mensagemParm.replaceAll("\\{valor\\}", invoice.getAmountFmt());
      return mensagemParm;
   }

   public void executeInvoiceWithoutPaymentAndDue(SMSSendBuilder smsBuilder, List<Invoice> invoices, String mensagem,
      Long quantidadeMensagens, int qtdeDias, boolean mensagemIlimitada) {

      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      int contador = 0;
      int travaMensagens = 1;
      int qtdMensagens = (mensagemIlimitada ? 999999999 : quantidadeMensagens.intValue());

      if (invoices != null && !invoices.isEmpty()) {
         for (Invoice invoice : invoices) {
            if (invoice.getSubscription().getHolder().getCellPhone() != null) {
               if (travaMensagens <= qtdMensagens) {
                  travaMensagens++;
                  contador++;
                  MessageSmsElement messageSms = new MessageSmsElement();
                  messageSms.setFrom(remetente);
                  messageSms.setMsg(parametrizaMensagem(mensagem, invoice, qtdeDias));
                  messageSms.setTo("55"
                           .concat(invoice.getSubscription().getHolder().getCellPhone().replaceAll("[^0-9]", "")));

                  multipleMessageSms.addMessageSms(messageSms);
                  if (contador > 99) {
                     try {
                        multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
                        smsBuilder.sendMultipleSms(multipleMessageSms);
                        multipleMessageSms = new MultipleMessageSms();
                        contador = 0;
                     }
                     catch (Exception e) {
                        LOGGER.error("Erro no envio de mensagem", e);
                     }
                  }
               }
               else {
                  break;
               }
            }
         }
         if (contador != 0) {
            try {
               multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
               smsBuilder.sendMultipleSms(multipleMessageSms);
            }
            catch (Exception e) {
               LOGGER.error("Erro no envio de mensagem", e);
            }
         }
      }
   }

   // @Scheduled(cron = "0 30 9 * * *", zone = TIME_ZONE)
   public void execute() {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);

      List<SmsParams> paramsSms = paramsService.obterCamposTela();
      List<Invoice> invoices = new ArrayList<Invoice>();

      long quantidadeMensagens = 0;
      boolean mensagemIlimitada = false;

      if (paramsSms.size() > 0) {

         mensagemIlimitada = paramsSms.get(0).getUnlimited_message();
         quantidadeMensagens = paramsSms.get(0).getMessage_amount();

         if (paramsSms.get(1).getThree_days_late()) {
            invoices = ListInvoiceWithoutPaymentAndDue3Days();
            executeInvoiceWithoutPaymentAndDue(smsBuilder, invoices, paramsSms.get(1).getMessage(),
               quantidadeMensagens, 3, mensagemIlimitada);
         }

         if (paramsSms.get(1).getEigth_days_late()) {
            invoices = ListInvoiceWithoutPaymentAndDue8Days();
            executeInvoiceWithoutPaymentAndDue(smsBuilder, invoices, paramsSms.get(1).getMessage(),
               quantidadeMensagens, 8, mensagemIlimitada);
         }

         if (paramsSms.get(1).getThirteen_days_late()) {
            invoices = ListInvoiceWithoutPaymentAndDue13Days();
            executeInvoiceWithoutPaymentAndDue(smsBuilder, invoices, paramsSms.get(1).getMessage(),
               quantidadeMensagens, 13, mensagemIlimitada);
         }

         if (paramsSms.get(1).getEighteen_days_late()) {
            invoices = ListInvoiceWithoutPaymentAndDue18Days();
            executeInvoiceWithoutPaymentAndDue(smsBuilder, invoices, paramsSms.get(1).getMessage(),
               quantidadeMensagens, 18, mensagemIlimitada);
         }
      }
   }
}
