package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.security.RequireAuthentication;
import br.com.bolao.bolao10.service.BadgeService;

@RestController
public class BadgeRest extends BaseRest {

	@Autowired
	private BadgeService badgeService;

	/**
	 * Retorna os badges ativos de um usuário específico.
	 */
	@GetMapping(value = "/badge/usuario/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getBadgesUsuario(@PathVariable Long id) {
		return createObjectReturn(badgeService.carregarBadgesDoUsuario(id));
	}

	/**
	 * Retorna histórico completo de badges (ativos e inativos) com contagem por tipo.
	 * Usado na aba 'Selos de Qualidade' no perfil do usuário.
	 */
	@GetMapping(value = "/badge/usuario/{id}/historico", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getHistoricoBadgesUsuario(@PathVariable Long id) {
		return createObjectReturn(badgeService.carregarHistoricoBadgesDoUsuario(id));
	}

	/**
	 * Retorna um mapa idUsuario → lista de badges ativos.
	 * Usado para carregar badges de todo o ranking em uma única chamada.
	 */
	@GetMapping(value = "/badge/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getBadgesRanking() {
		return createObjectReturn(badgeService.carregarMapaBadgesAtivos());
	}

	/**
	 * Atualiza todos os badges manualmente (uso ADMIN / testes).
	 */
	@PostMapping(value = "/badge/atualizar", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.ADMIN})
	public @ResponseBody ResponseEntity<?> atualizarBadges() {
		badgeService.atualizarTodosBadges();
		return createObjectReturn(Boolean.TRUE);
	}
}
