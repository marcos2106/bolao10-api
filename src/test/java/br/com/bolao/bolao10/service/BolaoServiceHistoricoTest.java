package br.com.bolao.bolao10.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.RankingHistorico;
import br.com.bolao.bolao10.domain.Situacao;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.repository.RankingHistoricoRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.support.Constants;

@RunWith(MockitoJUnitRunner.class)
public class BolaoServiceHistoricoTest {

	@InjectMocks
	private BolaoService bolaoService;

	@Mock
	private ConfiguracaoService configuracaoService;

	@Mock
	private RankingRepository rankingRepository;

	@Mock
	private RankingHistoricoRepository rankingHistoricoRepository;

	@Test
	public void naoDeveDuplicarUsuariosJaProcessadosNoDia() {
		Ranking primeiro = ranking(1L, 10);
		Ranking segundo = ranking(2L, 8);

		when(configuracaoService.situacaoAtiva()).thenReturn(situacaoDurante());
		when(rankingHistoricoRepository.carregarIdsUsuariosPorData(any(LocalDate.class)))
				.thenReturn(Arrays.asList(1L, 2L));
		when(rankingRepository.carregarRanking()).thenReturn(Arrays.asList(primeiro, segundo));

		bolaoService.atualizarPontuacaoDiaria();

		verify(rankingHistoricoRepository, never()).save(any(RankingHistorico.class));
	}

	@Test
	public void deveCompletarSomenteUsuariosAindaNaoProcessados() {
		Ranking primeiro = ranking(1L, 10);
		Ranking segundo = ranking(2L, 8);

		when(configuracaoService.situacaoAtiva()).thenReturn(situacaoDurante());
		when(rankingHistoricoRepository.carregarIdsUsuariosPorData(any(LocalDate.class)))
				.thenReturn(Arrays.asList(1L));
		when(rankingRepository.carregarRanking()).thenReturn(Arrays.asList(primeiro, segundo));

		bolaoService.atualizarPontuacaoDiaria();

		ArgumentCaptor<RankingHistorico> captor = ArgumentCaptor.forClass(RankingHistorico.class);
		verify(rankingHistoricoRepository).save(captor.capture());

		RankingHistorico historico = captor.getValue();
		assertEquals(Long.valueOf(2L), historico.getUsuario().getId());
		assertEquals(Integer.valueOf(2), historico.getPosicao());
		assertEquals(Integer.valueOf(8), historico.getPontuacao());
		assertEquals(LocalDate.now(), historico.getDataRegistro());
	}

	private Situacao situacaoDurante() {
		Situacao situacao = new Situacao();
		situacao.setId(Long.valueOf(Constants.SITUACAO_DURANTE));
		return situacao;
	}

	private Ranking ranking(Long idUsuario, Integer pontuacao) {
		Usuario usuario = new Usuario();
		usuario.setId(idUsuario);

		Ranking ranking = new Ranking();
		ranking.setUsuario(usuario);
		ranking.setPontuacao(pontuacao);
		return ranking;
	}
}
