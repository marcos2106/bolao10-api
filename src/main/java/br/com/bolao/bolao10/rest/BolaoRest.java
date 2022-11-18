package br.com.bolao.bolao10.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.model.ApostaFilter;
import br.com.bolao.bolao10.model.RankingCustomizadoRequest;
import br.com.bolao.bolao10.service.BolaoService;

@RestController
@RequestMapping("/bolao")
public class BolaoRest extends BaseRest {

	@Autowired
	private BolaoService bolaoService;

	@GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRanking() {
		return createObjectReturn(bolaoService.carregarRanking());
	}

	@GetMapping(value = "/aposta/partida/{idPartida}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarApostaPorPartida(@PathVariable Long idPartida) {
		return createObjectReturn(bolaoService.carregarApostaPorPartida(idPartida));
	}

	@GetMapping(value = "/aposta/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarApostaPorUsuario(@PathVariable Long idUsuario) {
		return createObjectReturn(bolaoService.carregarApostaPorUsuario(idUsuario));
	}

	@PostMapping(value = "/salvar/aposta", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvarAposta(HttpServletRequest request, @RequestBody ApostaFilter apostaFilter) {
		bolaoService.salvarAposta(apostaFilter, getUserToken(request));
		return createObjectReturn(Boolean.TRUE);
	}

	@PostMapping(value = "/finalizar/aposta", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> finalizarAposta(HttpServletRequest request, @RequestBody ApostaFilter aposta) {
		bolaoService.finalizarAposta(aposta, getUserToken(request));
		return createObjectReturn(Boolean.TRUE);
	}

	@GetMapping(value = "/partida", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidas(HttpServletRequest request) {
		return createObjectReturn(bolaoService.carregarPartidas(getUserToken(request)));
	}

	@GetMapping(value = "/colocacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarApostaColocacao(HttpServletRequest request) {
		return createObjectReturn(bolaoService.carregarApostaColocacao(getUserToken(request)));
	}

	@GetMapping(value = "/colocacao/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarApostaColocacaoPorUsuario(@PathVariable Long idUsuario) {
		return createObjectReturn(bolaoService.carregarApostaColocacaoPorUsuario(idUsuario));
	}

	@GetMapping(value = "/finalizada", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> verificarFinalizada(HttpServletRequest request) {
		return createObjectReturn(bolaoService.verificarFinalizada(getUserToken(request)));
	}

	@GetMapping(value = "/aposta/colocacao/{idSelecaoA}/{idSelecaoB}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarApostaColocacaoPorSelecao(@PathVariable Long idSelecaoA, @PathVariable Long idSelecaoB) {
		return createObjectReturn(bolaoService.carregarApostaColocacaoPorSelecao(idSelecaoA, idSelecaoB));
	}

	@GetMapping(value = "/dados/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarDadosUsuarios(@PathVariable Long idUsuario) {
		return createObjectReturn(bolaoService.carregarDadosUsuario(idUsuario));
	}

	@GetMapping(value = "/dados/grafico/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarDadosUsuarioGrafico(@PathVariable Long idUsuario) {
		return createObjectReturn(bolaoService.carregarDadosUsuarioGrafico(idUsuario));
	}

	@GetMapping(value = "/aposta/grafico/{idPartida}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> calcularApostasPorPartida(@PathVariable Long idPartida) {
		return createObjectReturn(bolaoService.calcularApostasPorPartida(idPartida));
	}

	@GetMapping(value = "/pontuacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPontuacaoPartidas() {
		return createObjectReturn(bolaoService.carregarPontuacaoPartidas());
	}

	@GetMapping(value = "/ranking/usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRankingAtivo() {
		return createObjectReturn(bolaoService.carregarRankingAtivo());
	}

	@GetMapping(value = "/ranking/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRankingCustomizado(@PathVariable Long idUsuario) {
		return createObjectReturn(bolaoService.carregarRankingCustomizado(idUsuario));
	}

	@PostMapping(value = "/ranking/usuario/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvarRankingCustomizado
			(@PathVariable Long idUsuario, @RequestBody RankingCustomizadoRequest ranking) {
		bolaoService.salvarRankingCustomizado(idUsuario, ranking);
		return createObjectReturn(Boolean.TRUE);
	}

	@DeleteMapping(value = "/ranking/usuario/{idRanking}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> apagarRankingCustomizado(@PathVariable Long idRanking) {
		bolaoService.apagarRankingCustomizado(idRanking);
		return createObjectReturn(Boolean.TRUE);
	}

}
