package br.com.bolao.bolao10.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.service.BolaoService;

@Component
public class PontuacaoDiariaScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(PontuacaoDiariaScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private BolaoService bolaoService;

	/**
	 * Atualizar os dados de pontuação do ranking de cada usuário
	 * 
	 */
	//roda 1:15 da madruga
	@Scheduled(cron = "0 20 22 * * *", zone = TIME_ZONE)
	public void execute() {
		try {
			bolaoService.atualizarPontuacaoDiaria();
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de Pontuação Diária", e);
		}
	}
}
