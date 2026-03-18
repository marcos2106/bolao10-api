package br.com.bolao.bolao10.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		//try {
			Notificacao notificacao = notificacaoService.obterNotificacaoSorteada();
			
			//ResponseClient response = new ResponseClient(true, "Notificação sorteada com sucesso!");
			//response.setObject(notificacao);
			//return new ResponseEntity<>(response, HttpStatus.OK);
			return createObjectReturn(notificacao);
			
		//} catch (Exception e) {
			//ResponseClient responseError = new ResponseClient(false, "Erro ao obter notificação aleatória.");
			//return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
		//}
	}
	
	/**
	 * Endpoint chamado na tela de lista completa (/bolao10/notificacoes)
	 * Usando o padrao jw-vue-pagination normalmente offset e limite.
	 */
	/*
	@GetMapping("/listar")
	public @ResponseBody ResponseEntity<?> listarPaginado(
			@RequestParam(defaultValue = "50") int tamanhoPagina,
			@RequestParam(defaultValue = "0") int pagina) {
		
		//try {
			List<Notificacao> lista = notificacaoService.obterTodasPaginado(pagina, tamanhoPagina);
			Long total = notificacaoService.countTotalNotificacoes();
			
			Map<String, Object> mapRetorno = new HashMap<>();
			mapRetorno.put("dados", lista);
			mapRetorno.put("total", total);
			
			//ResponseClient response = new ResponseClient(true, "Listagem gerada.");
			//response.setObject(mapRetorno);
			//return new ResponseEntity<>(response, HttpStatus.OK);
			return createObjectReturn(mapRetorno);
			
		//} catch (Exception e) {
		//	ResponseClient responseError = new ResponseClient(false, "Erro ao obter lista paginada.");
		//	return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
		//}
	}
	 */

    @GetMapping("/listar")
    public Page<Notificacao> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificacaoService.listar(page, size);
    }

}
