package br.com.bolao.bolao10.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Situacao;
import br.com.bolao.bolao10.service.BadgeService;
import br.com.bolao.bolao10.service.BolaoService;
import br.com.bolao.bolao10.service.ConfiguracaoService;
import br.com.bolao.bolao10.support.Constants;

@Component
public class PontuacaoDiariaScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(PontuacaoDiariaScheduled.class);
	//private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private BolaoService bolaoService;

	@Autowired
	private BadgeService badgeService;

	@Autowired
	private ConfiguracaoService configuracaoService;

	/**
	 * Atualizar os dados de pontução do ranking e os Badges de cada usuário.
	 * Roda às 02h da madrugada (após todos os jogos do dia serem computados)
	 */
	@Scheduled(cron = "0 0 2 * * *", zone = "America/Sao_Paulo")
	public void execute() {

		// primeiro verifica a situação do bolão, se está DURANTE
		Situacao situacao = configuracaoService.situacaoAtiva();

		if (situacao != null && situacao.getId() == Constants.SITUACAO_DURANTE) {
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
}
