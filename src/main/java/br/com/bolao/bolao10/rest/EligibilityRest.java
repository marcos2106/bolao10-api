package br.com.segmedic.clubflex.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.segmedic.clubflex.service.SubscriptionService;

@RestController
public class EligibilityRest extends BaseRest {
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@GetMapping(value="/eligibility/by/cpf/{cpf}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> eligibilityByCpf(@PathVariable String cpf) {
		return createObjectReturn(subscriptionService.verifyEligibility(cpf));
	}
	
	@GetMapping(value="/eligibility/by/card/{cardNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> eligibilityByCardNumber(@PathVariable Long cardNumber) {
		return createObjectReturn(subscriptionService.verifyEligibility(cardNumber));
	}
	
}
