package br.com.segmedic.clubflex.support.sms;

import br.com.zenvia.client.RestClient;
import br.com.zenvia.client.domain.ProxyConfiguration;
import br.com.zenvia.client.domain.ReceivedMessageFilter;
import br.com.zenvia.client.exception.RestClientException;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;
import br.com.zenvia.client.response.CancelSmsResponse;
import br.com.zenvia.client.response.GetSmsStatusResponse;
import br.com.zenvia.client.response.ReceivedMessage;
import br.com.zenvia.client.response.ReceivedMessageResponse;
import br.com.zenvia.client.response.SendMultipleSmsResponse;
import br.com.zenvia.client.response.SendSmsResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSSendBuilder {

	private static Logger log = LoggerFactory.getLogger(SMSSendBuilder.class);

	private RestClient client;

	public SMSSendBuilder(String username, String password, Integer socketTimeout) {
		String restServerUrl = null;
		Integer connectTimeout = 1000;
		
		ProxyConfiguration proxyConfiguration = null;
		if (StringUtils.isBlank(restServerUrl)) {
			this.client = new RestClient(proxyConfiguration);
		} else {
			this.client = new RestClient(restServerUrl, proxyConfiguration);
		}
		this.client.setUsername(username);
		this.client.setPassword(password);
		if (connectTimeout != null)
			this.client.setConnectTimeout(connectTimeout.intValue());
		if (socketTimeout != null)
			this.client.setSocketTimeout(socketTimeout.intValue());
	}

	public void sendMultipleSms(MultipleMessageSms multipleMessageSms) throws RestClientException {
		SendMultipleSmsResponse sendMultipleSmsResponse = this.client.sendMultipleSms(multipleMessageSms);
		for (int i = 0; i < sendMultipleSmsResponse.getResponses().size(); i++) {
			MessageSmsElement message = multipleMessageSms.getMessages().get(i);
			SendSmsResponse sendSmsResponse = sendMultipleSmsResponse.getResponses().get(i);
			String field = StringUtils.isBlank(message.getId()) ? "mobile" : "id";
			String value = StringUtils.isBlank(message.getId()) ? message.getTo() : message.getId();
			log.info("Response #{} to message with {} [{}]", new Object[] { Integer.valueOf(i + 1), field, value });
			log.info("    Status code        : {}", sendSmsResponse.getStatusCode());
			log.info("    Status description : {}", sendSmsResponse.getStatusDescription());
			log.info("    Detail code        : {}", sendSmsResponse.getDetailCode());
			log.info("    Detail description : {}", sendSmsResponse.getDetailDescription());
		}
	}

	public void cancelSms() throws RestClientException {
		CancelSmsResponse cancelSmsResponse = this.client.cancelSms("id-de-mensagem");
		log.info("Status code        : {}", cancelSmsResponse.getStatusCode());
		log.info("Status description : {}", cancelSmsResponse.getStatusDescription());
		log.info("Detail code        : {}", cancelSmsResponse.getDetailCode());
		log.info("Detail description : {}", cancelSmsResponse.getDetailDescription());
	}

	public void getSmsStatus() throws RestClientException {
		GetSmsStatusResponse getSmsStatusResponse = this.client.getSmsStatus("id-de-mensagem");
		log.info("ID                 : {}", getSmsStatusResponse.getId());
		log.info("Operadora          : {}", getSmsStatusResponse.getMobileOperatorName());
		log.info("Status recebido em : {}", getSmsStatusResponse.getReceived());
		log.info("LA de sa       : {}", getSmsStatusResponse.getShortcode());
		log.info("Status code        : {}", getSmsStatusResponse.getStatusCode());
		log.info("Status description : {}", getSmsStatusResponse.getStatusDescription());
		log.info("Detail code        : {}", getSmsStatusResponse.getDetailCode());
		log.info("Detail description : {}", getSmsStatusResponse.getDetailDescription());
	}

	public void listUnreadMessages() throws RestClientException {
		ReceivedMessageResponse listUnreadMessagesResponse = this.client.listUnreadMessages();
		log.info("Status code        : {}", listUnreadMessagesResponse.getStatusCode());
		log.info("Status description : {}", listUnreadMessagesResponse.getStatusDescription());
		log.info("Detail code        : {}", listUnreadMessagesResponse.getDetailCode());
		log.info("Detail description : {}", listUnreadMessagesResponse.getDetailDescription());
		if (listUnreadMessagesResponse.hasMessages())
			for (int i = 0; i < listUnreadMessagesResponse.getReceivedMessages().size(); i++) {
				ReceivedMessage receivedMessage = listUnreadMessagesResponse.getReceivedMessages().get(i);
				log.info("Message #{}", Integer.valueOf(i + 1));
				log.info("    Texto                               : {}", receivedMessage.getBody());
				log.info("    Recebida em                         : {}", receivedMessage.getDateReceived());
				log.info("    ID                                  : {}", receivedMessage.getId());
				log.info("    Celular                             : {}", receivedMessage.getMobile());
				log.info("    Operadora                           : {}", receivedMessage.getMobileOperatorName());
				log.info("    Identificador da mensagem de origem : {}", receivedMessage.getMtId());
				log.info("    LA de recebimento                   : {}", receivedMessage.getShortcode());
			}
	}

	public void searchReceivedMessages() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date start = format.parse("2014-07-01");
		Date end = format.parse("2014-08-01");
		ReceivedMessageFilter filter = (new ReceivedMessageFilter.Builder(start, end)).byMobile("this.mobile")
				.byReferenceMessageId("provided-id").build();
		ReceivedMessageResponse response = this.client.searchReceivedMessages(filter);
		log.info("Status code        : {}", response.getStatusCode());
		log.info("Status description : {}", response.getStatusDescription());
		log.info("Detail code        : {}", response.getDetailCode());
		log.info("Detail description : {}", response.getDetailDescription());
		if (response.hasMessages())
			for (int i = 0; i < response.getReceivedMessages().size(); i++) {
				ReceivedMessage receivedMessage = response.getReceivedMessages().get(i);
				log.info("Message #{}", Integer.valueOf(i + 1));
				log.info("    Texto                               : {}", receivedMessage.getBody());
				log.info("    Recebida em                         : {}", receivedMessage.getDateReceived());
				log.info("    ID                                  : {}", receivedMessage.getId());
				log.info("    Celular                             : {}", receivedMessage.getMobile());
				log.info("    Operadora                           : {}", receivedMessage.getMobileOperatorName());
				log.info("    Identificador da mensagem de origem : {}", receivedMessage.getMtId());
				log.info("    LA de recebimento                   : {}", receivedMessage.getShortcode());
			}
	}

}
