
package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.service.SelecaoService;

@RestController
@RequestMapping("/selecao")
public class SelecaoRest extends BaseRest {
	
	   @Autowired
	   private SelecaoService selecaoService;
	
	   @GetMapping(value = "/grupo", produces = MediaType.APPLICATION_JSON_VALUE)
	   public @ResponseBody ResponseEntity<?> carregarSelecoesOrderGrupo() {
	      return createObjectReturn(selecaoService.carregarSelecoesOrderGrupo());
	   }

}
