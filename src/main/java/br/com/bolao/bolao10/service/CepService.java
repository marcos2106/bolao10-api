package br.com.segmedic.clubflex.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.ViaCepResponse;

@Service
public class CepService {
	
	public ViaCepResponse getAddress(String zipcode) {
		if(StringUtils.isBlank(zipcode)) {
			throw new ClubFlexException("CEP não informado!");
		}
		try {
			String url = "https://viacep.com.br/ws/"+zipcode.replace("-","")+"/json/";
			RestTemplate restTemplate = new RestTemplate();
			
			ViaCepResponse cepInformation = new ViaCepResponse();
			cepInformation = restTemplate.getForObject(url, ViaCepResponse.class);
			
			if(cepInformation.getCity() == null) {
				throw new ClubFlexException("CEP inexistente!");
			}else {
				return cepInformation;
			}
			
		} catch (Exception e) {
			throw new ClubFlexException("CEP inexistente!");
		}
	}
	
}
