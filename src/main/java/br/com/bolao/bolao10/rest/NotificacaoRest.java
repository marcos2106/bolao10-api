package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.domain.Notificacao;
import br.com.bolao.bolao10.service.NotificacaoService;

@RestController
@RequestMapping("/notificacao")
public class NotificacaoRest extends BaseRest {

	@Autowired
	private NotificacaoService notificacaoService;

	/**
	 * Endpoint chamado no widget da Home Antes/Durante.
	 * Devolve uma única notificação estocástica baseada nas 5 últimas geradas.
	 */
	@GetMapping("/sorteada")
	public @ResponseBody ResponseEntity<?> obterSorteada() {

		Notificacao notificacao = notificacaoService.obterNotificacaoSorteada();
		return createObjectReturn(notificacao);
	}

    @GetMapping("/listar")
    public Page<Notificacao> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return notificacaoService.listar(page, size);
    }

}
