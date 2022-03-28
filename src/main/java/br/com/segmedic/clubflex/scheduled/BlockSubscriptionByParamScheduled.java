
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.ParamsSubscription;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.service.MailService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.service.SystemParamsService;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.exception.RestClientException;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;

@Component
public class BlockSubscriptionByParamScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(BlockSubscriptionByParamScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private SystemParamsService paramService;

   @Autowired
   private MailService mailService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   /**
    * Bloqueia todas as assinaturas ok, que se encaixam nos critérios dos parametros definidos
    */
   @Scheduled(cron = "0 0 8 * * *", zone = TIME_ZONE)
   public void executeBySubscription() {
      List<BigInteger> subscriptions = new ArrayList<BigInteger>();
      List<Subscription> listAdvertSub = new ArrayList<Subscription>();

      subscriptions = subscriptionService.listShouldBeBlockedByDateDue();
      if (subscriptions != null && !subscriptions.isEmpty()) {
         for (BigInteger id : subscriptions) {
            try {
               listAdvertSub.add(subscriptionService.blockWithParam(id.longValue()));
            }
            catch (Exception exc) {
               LOGGER.error("Erro na execução da Scheduled de bloqueio por parâmetros", exc);
            }
         }
      }

      if (listAdvertSub != null && !listAdvertSub.isEmpty()) {

         for (Subscription advertSub : listAdvertSub) {
            try {
               if (advertSub.getHolder().getEmail() != null && !advertSub.getHolder().getEmail().equals("")) {
                  this.sendBlockMail(advertSub.getHolder().getName(), advertSub.getHolder().getEmail());
               }
               if (advertSub.getHolder().getCellPhone() != null && !advertSub.getHolder().getCellPhone().equals("")) {
                  this.sendBlockedSMS(advertSub.getHolder().getName(), advertSub.getHolder().getCellPhone());
               }
            }
            catch (Exception exc) {
               LOGGER.error("Erro no envio de informacao de bloqueio -  execução da Scheduled de bloqueio por parâmetros", exc);
            }
         }
      }

   }

   public void sendBlockedSMS(String name, String telefone) {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      String mensagem = "Ola " + name.split(" ")[0]
         + ", \nSua assinatura foi bloqueada por falta de pagamento. Regularize ja!";
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

   private void sendBlockMail(String name, String email) {
      if (StringUtils.isNotBlank(email)) {
         MailTemplate mail = new MailTemplateBuilder()
                  .subject("Assinatura Bloqueada")
                  .template("blocked-subscription.html")
                  .addParam("nome", name)
                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                  .to(email)
                  .build();
         mailService.scheduleSend(mail);
      }
   }

   public void execute() {
      try {

         // Obter os parametros
         ParamsSubscription param = paramService.getParamsSubscription();

         if (param != null && param.getIsBlock() && param.getDaysDueBlock() > 0) {

            Long diasBlock = param.getDaysDueBlock();
            LocalDate dataComparacao = LocalDate.now().minusDays(diasBlock);

            List<BigInteger> subscriptions = subscriptionService.listShouldBeBlockedByParam(dataComparacao);
            subscriptions.forEach(subId -> {
               subscriptionService.block(subId.longValue());
            });
         }

      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da Scheduled de bloqueio por parâmetros", e);
      }
   }
}
