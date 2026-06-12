package br.com.bolao.bolao10.scheduled;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

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
	private static final String LOCK_PONTUACAO_DIARIA = "bolao10:pontuacao-diaria";

	@Autowired
	private BolaoService bolaoService;

	@Autowired
	private BadgeService badgeService;

	@Autowired
	private ConfiguracaoService configuracaoService;

	@Autowired
	private DataSource dataSource;

	/**
	 * Atualiza a pontuacao do ranking e os badges diariamente, as 02h.
	 */
	@Scheduled(cron = "0 0 2 * * *", zone = "America/Sao_Paulo")
	public void execute() {

		try (Connection connection = dataSource.getConnection()) {
			if (!adquirirLock(connection)) {
				LOGGER.info("Scheduled de pontuacao diaria ignorada: outra instancia ja esta processando.");
				return;
			}

			try {
				processarPontuacaoDiaria();
			} finally {
				liberarLock(connection);
			}
		} catch (Exception e) {
			LOGGER.error("Erro ao controlar a execucao exclusiva da Scheduled de Pontuacao Diaria", e);
		}
	}

	private void processarPontuacaoDiaria() {
		Situacao situacao = configuracaoService.situacaoAtiva();

		if (situacao != null && situacao.getId() == Constants.SITUACAO_DURANTE) {
			try {
				bolaoService.atualizarPontuacaoDiaria();
			} catch (Exception e) {
				LOGGER.error("Erro na execucao da Scheduled de Pontuacao Diaria", e);
			}
			try {
				badgeService.atualizarTodosBadges();
			} catch (Exception e) {
				LOGGER.error("Erro na execucao da Scheduled de Badges", e);
			}
		}
	}

	private boolean adquirirLock(Connection connection) throws Exception {
		try (PreparedStatement statement = connection.prepareStatement("select get_lock(?, 0)")) {
			statement.setString(1, LOCK_PONTUACAO_DIARIA);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() && result.getInt(1) == 1;
			}
		}
	}

	private void liberarLock(Connection connection) {
		try (PreparedStatement statement = connection.prepareStatement("select release_lock(?)")) {
			statement.setString(1, LOCK_PONTUACAO_DIARIA);
			statement.executeQuery();
		} catch (Exception e) {
			LOGGER.error("Erro ao liberar lock da Scheduled de Pontuacao Diaria", e);
		}
	}
}
