package br.com.segmedic.clubflex.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.ParamsSubscription;
import br.com.segmedic.clubflex.domain.SmsParams;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.repository.SystemParamsRepository;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;

@Service
public class SystemParamsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemParamsService.class);

	@Autowired
	private SystemParamsRepository spr;

	@Autowired
	private SubscriptionRepository sbr;

	@Value("${zenvia.api.remetente}")
	private String remetente;
	@Value("${zenvia.api.username}")
	private String username;
	@Value("${zenvia.api.password}")
	private String password;

	/**
	 * Obtem parametros do sistema para criacao de formulario
	 * 
	 * @return - Parametros
	 */
	public List<SmsParams> obterCamposTela() {
		return spr.obterCamposTela();
	}

	/**
	 * Atualiza parametro
	 * 
	 * @param params
	 */
	@Transactional
	public void updateParam(SmsParams params) {
		spr.update(params);
	}

	public void sendMessage(SmsParams params) {
		
		String tipos = "T";
		if (params.getDue_today() && !params.getThree_days_late()) {
			tipos = "B";
		} else if (!params.getDue_today() && params.getThree_days_late()) {
			tipos = "C";
		}
		List<Subscription> subscriptions =  sbr.listSubscriptionActiveTicketsAndCard(tipos);
		MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
		int contador = 0;
		int travaMensagens = 1;
		int qtdMensagens = (params.getUnlimited_message() ? 999999999 : params.getMessage_amount().intValue());
		if (subscriptions != null && !subscriptions.isEmpty()) {
			SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
				
			for (Subscription subscription : subscriptions) {

					if (subscription.getHolder().getCellPhone() != null) {
						
						if (travaMensagens <= qtdMensagens ) {
							travaMensagens++;
							contador++;
							try {
								MessageSmsElement messageSms = new MessageSmsElement();
								messageSms.setFrom(remetente);
								messageSms.setMsg(parametrizaMensagem(params.getMessage(), subscription.getHolder().getName().split(" ")[0]));
								messageSms.setTo("55".concat(
										subscription.getHolder().getCellPhone().replaceAll("[^0-9]", "")));
								
								multipleMessageSms.addMessageSms(messageSms);
								if (contador > 99) {
									multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
									smsBuilder.sendMultipleSms(multipleMessageSms);
									multipleMessageSms = new MultipleMessageSms();
									contador = 0;
								}
							} catch (Exception e) {
								LOGGER.error("Erro ao enviar sms. ", e);
								throw new ClubFlexException("Ocorreu um problema ao enviar SMS. Tente novamente, se o problema persistir nos avise.", e);
							}
						}else {
							break;
						}
				}
			}
			if (contador != 0) {
				try {
					multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
					smsBuilder.sendMultipleSms(multipleMessageSms);
				} catch (Exception e) {
					LOGGER.error("Erro ao enviar sms. ", e);
					throw new ClubFlexException("Ocorreu um problema ao enviar SMS. Tente novamente, se o problema persistir nos avise.", e);
				}
			}
		}
	}
	
	public String parametrizaMensagem(String mensagem, String nome) {
		String mensagemParm = mensagem;
		mensagemParm = mensagemParm.replaceAll("\\{primeiro_nome\\}", nome);
		return mensagemParm;
	}
	

	/**
	 * Obtem parametros da assinatura para criacao de formulario
	 * 
	 * @return - Parametros
	 */
	public ParamsSubscription getParamsSubscription() {
		return spr.getParamsSubscription();
	}

	/**
	 * Atualiza parametro de assinatura
	 * 
	 * @param params
	 */
	@Transactional
	public void saveParamsSubscription(ParamsSubscription params) {
		
		if (params.getIsBlock() && params.getDaysDueBlock() == null) {
			throw new ClubFlexException("Quantidade de dias do bloqueio não foi preenchida");
		}
		if (params.getIsSuspend() && params.getDaysDueSuspend() == null) {
			throw new ClubFlexException("Quantidade de dias da suspensão não foi preenchida");
		}
		if (params.getDaysDueBlock() < 0) {
			throw new ClubFlexException("Quantidade de dias de bloqueio inválida");
		}
		if (params.getDaysDueSuspend() < 0) {
			throw new ClubFlexException("Quantidade de dias de suspensão inválida");
		}
		
		params.setDaysDueBlock( (params.getDaysDueBlock()==null) ? 0 : params.getDaysDueBlock());
		params.setDaysDueSuspend( (params.getDaysDueSuspend()==null) ? 0 : params.getDaysDueSuspend());
		
		spr.update(params);
	}
}
