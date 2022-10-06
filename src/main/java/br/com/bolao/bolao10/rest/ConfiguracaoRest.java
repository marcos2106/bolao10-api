
package br.com.bolao.bolao10.rest;

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

import br.com.bolao.bolao10.domain.Gol;
import br.com.bolao.bolao10.domain.Jogador;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.model.ColocacaoRequest;
import br.com.bolao.bolao10.service.ConfiguracaoService;

@RestController
@RequestMapping("/configuracao")
public class ConfiguracaoRest extends BaseRest {

	@Autowired
	private ConfiguracaoService configuracaoService;

	@GetMapping(value = "/selecao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarSelecoes() {
		return createObjectReturn(configuracaoService.carregarSelecoes());
	}

	@GetMapping(value = "/jogador", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarJogadores() {
		return createObjectReturn(configuracaoService.carregarJogadores());
	}

	@DeleteMapping(value = "/jogador/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> excluirJogador(@PathVariable Long id) {
		return createObjectReturn(configuracaoService.excluirJogador(id));
	}

	@PostMapping(value = "/jogador", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> adicionarJogador(@RequestBody Jogador jogador) {
		return createObjectReturn(configuracaoService.adicionarJogador(jogador));
	}

	@PostMapping(value = "/colocacao", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvarColocacao(@RequestBody ColocacaoRequest request) {
		configuracaoService.salvarColocacao(request);
		return createObjectReturn(Boolean.TRUE);
	}

	@GetMapping(value = "/usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarUsuarios() {
		return createObjectReturn(configuracaoService.carregarUsuarios());
	}

	@PostMapping(value = "/usuario", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> adicionarUsuario(@RequestBody Usuario usuario) {
		return createObjectReturn(configuracaoService.adicionarUsuario(usuario));	
	}

	@PostMapping(value = "/usuario/pagamento/{idUsuario}")
	public @ResponseBody ResponseEntity<?> alterarPagamento(@PathVariable Long idUsuario) {
		configuracaoService.alterarPagamento(idUsuario);
		return createObjectReturn(Boolean.TRUE);	
	}

	@GetMapping(value = "/partida", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidas() {
		return createObjectReturn(configuracaoService.carregarPartidasConfiguracao());
	}

	@GetMapping(value = "/partida/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartida(@PathVariable Long id) {
		return createObjectReturn(configuracaoService.carregarPartida(id));
	}

	@GetMapping(value = "/outras/partidas/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarOutrasPartidas(@PathVariable Long id) {
		return createObjectReturn(configuracaoService.carregarOutrasPartidas(id));
	}

	@PostMapping(value = "/partida/inicio/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> iniciarPartida(@PathVariable Long id) {
		configuracaoService.iniciarPartida(id);
		return createObjectReturn(Boolean.TRUE);
	}

	@PostMapping(value = "/partida/final/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> finalizarPartida(@PathVariable Long id) {
		configuracaoService.finalizarPartida(id);
		return createObjectReturn(Boolean.TRUE);
	}

	@GetMapping(value = "/selecao/jogador/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarJogadoresPorSelecao(@PathVariable Long id) {
		return createObjectReturn(configuracaoService.carregarJogadoresPorSelecao(id));
	}

	@PostMapping(value = "/partida/gol", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> adicionarGol(@RequestBody Gol gol) {
		configuracaoService.adicionarGol(gol);
		return createObjectReturn(Boolean.TRUE);
	}

	@GetMapping(value = "/partida/gol/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarGolsPorPartida(@PathVariable Long id) {
		return createObjectReturn(configuracaoService.carregarGolsPorPartida(id));
	}

	@GetMapping(value = "/colocacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarColocacao() {
		return createObjectReturn(configuracaoService.carregarColocacao());
	}

	@GetMapping(value = "/pontuacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPontuacao() {
		return createObjectReturn(configuracaoService.carregarPontuacao());
	}

	@GetMapping(value = "/artilharia", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarArtilharia() {
		return createObjectReturn(configuracaoService.carregarArtilharia());
	}

	@GetMapping(value = "/situacao/ativa", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> situacaoAtiva() {
		return createObjectReturn(configuracaoService.situacaoAtiva());
	}

	@GetMapping(value = "/situacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarSituacao() {
		return createObjectReturn(configuracaoService.carregarSituacao());
	}

	@PostMapping(value = "/situacao/{situacao}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> ativarSituacao(@PathVariable Long situacao) {
		return createObjectReturn(configuracaoService.ativarSituacao(situacao));
	}

	@GetMapping(value = "/jogo/aovivo", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> quantidadeJogoAovivo() {
		return createObjectReturn(configuracaoService.quantidadeJogoAovivo());
	}
}
