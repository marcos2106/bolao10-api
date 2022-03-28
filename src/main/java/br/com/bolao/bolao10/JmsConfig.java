
package br.com.bolao.bolao10;

import java.util.Arrays;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import br.com.bolao.bolao10.support.Constants;

@Configuration
@EnableJms
public class JmsConfig {

   @Value("${spring.activemq.broker-url}")
   private String brokerUrl;

   @Value("${spring.activemq.user}")
   private String user;

   @Value("${spring.activemq.password}")
   private String password;

   @Bean
   public ActiveMQConnectionFactory connectionFactory() {
      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, brokerUrl);
      factory.setTrustedPackages(Arrays.asList("br.com.bolao.bolao10", "java"));
      return factory;
   }

   @Bean
   public JmsTemplate queueMail() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_MAIL);
      return template;
   }

   @Bean
   public JmsTemplate queueInvoice() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_INVOICE);
      return template;
   }

   @Bean
   public JmsTemplate queuePaySingleInvoice() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_PAY_SINGLE_INVOICE);
      return template;
   }

   @Bean
   public JmsTemplate queueMonthlyInvoice() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_MONTHLY_INVOICE);
      return template;
   }

   @Bean
   public JmsTemplate queueChangeCreditCard() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_CHANGE_CREDCARD);
      return template;
   }

   @Bean
   public JmsTemplate queueAudit() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_AUDIT);
      return template;
   }

   @Bean
   public JmsTemplate queueSubscriptionOperation() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_SUB_OPERATION);
      return template;
   }

   @Bean
   public JmsTemplate queueSubscriptionLog() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_SUB_LOG);
      return template;
   }

   @Bean
   public JmsTemplate queueCreditCardUpdate() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_CREDIT_CARD_UPDATE);
      return template;
   }

   @Bean
   public JmsTemplate queueRequestDependents() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_REQUEST_DEPENDENTS);
      return template;
   }

   @Bean
   public JmsTemplate queueRequestDependent() throws Exception {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(Constants.QUEUE_REQUEST_DEPENDENT);
      return template;
   }
}
