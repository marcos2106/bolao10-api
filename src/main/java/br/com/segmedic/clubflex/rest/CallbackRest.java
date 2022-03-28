package br.com.segmedic.clubflex.rest;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.segmedic.clubflex.model.GatewayTicketCallbackTicket;
import br.com.segmedic.clubflex.service.TicketGatewayService;
import br.com.segmedic.clubflex.support.JsonUtils;

@RestController
@RequestMapping("/callbacks")
public class CallbackRest extends BaseRest{

	@Value("${ticket.gateway.api.callback}")
	private String ticketsCallbackKey;
	
	@Autowired
	private TicketGatewayService ticketGatewayService;
	
	@PostMapping(value="/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
	public void tickets(HttpServletRequest request, HttpServletResponse response) {
		//obter corpo
		String body = getRequestBody(request);
		
		//validar
		validateTokenBoletoSimplesGateway(request, response, body);
		
		//obtendo dado
		GatewayTicketCallbackTicket data = JsonUtils.jsonStringToObject(body, GatewayTicketCallbackTicket.class);
		
		//enviar para processamento
		ticketGatewayService.processCallback(data);
		
		//responder ok
		response.setStatus(HttpStatus.OK.value());
	}

	private String getRequestBody(HttpServletRequest request){
		try {
			return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			return null;
		}
	}

	private void validateTokenBoletoSimplesGateway(HttpServletRequest request, HttpServletResponse response, String payloadContent) {
		String cript = HmacUtils.hmacSha1Hex(ticketsCallbackKey, payloadContent);
		if(!request.getHeader("X-Hub-Signature").equals("sha1=".concat(cript))) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
	}
	
}
