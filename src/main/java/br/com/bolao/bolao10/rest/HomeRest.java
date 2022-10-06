
package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.service.HomeService;

@RestController
@RequestMapping("/home")
public class HomeRest extends BaseRest {

	@Autowired
	private HomeService homeService;

	@GetMapping(value = "/antes/estreia", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarEstreia() {
		return createObjectReturn(homeService.carregarEstreia());
	}

	@GetMapping(value = "/antes/inicio", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarDadosInciais() {
		return createObjectReturn(homeService.carregarDadosInciais());
	}

	@GetMapping(value = "/antes/participantes", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarParticipantes() {
		return createObjectReturn(homeService.carregarParticipantes());
	}

	@GetMapping(value = "/durante/partidas", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidas() {
		return createObjectReturn(homeService.carregarPartidas());
	}

	@GetMapping(value = "/durante/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRanking() {
		return createObjectReturn(homeService.carregarRanking());
	}

	@GetMapping(value = "/durante/grupo", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarGrupos() {
		return createObjectReturn(homeService.carregarGrupos());
	}

	@GetMapping(value = "/depois/colocacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarColocacao() {
		return createObjectReturn(homeService.carregarColocacao());
	}

	@GetMapping(value = "/depois/curiosidade", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarCuriosidade() {
		return createObjectReturn(homeService.carregarCuriosidade());
	}

}
