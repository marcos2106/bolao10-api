package br.com.bolao.bolao10.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.service.BadgeService;
import br.com.bolao.bolao10.service.BolaoService;

@Component
public class PontuacaoDiariaScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(PontuacaoDiariaScheduled.class);
	//private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private BolaoService bolaoService;

	@Autowired
	private BadgeService badgeService;

	/**
	 * Atualizar os dados de pontução do ranking e os Badges de cada usuário.
	 * Roda às 02h da madrugada (após todos os jogos do dia serem computados)
	 */
	@Scheduled(cron = "0 0 2 * * *", zone = "America/Sao_Paulo")
	public void execute() {
		try {
			bolaoService.atualizarPontuacaoDiaria();
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de Pontuação Diária", e);
		}
		try {
			badgeService.atualizarTodosBadges();
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de Badges", e);
		}
	}
}
