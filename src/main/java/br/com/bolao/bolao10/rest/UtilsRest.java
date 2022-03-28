package br.com.segmedic.clubflex.rest;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.segmedic.clubflex.service.CepService;
import br.com.segmedic.clubflex.service.ClubFlexCardService;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.QrCodeUtils;

@RestController
@RequestMapping("/utils")
public class UtilsRest extends BaseRest{
	
	@Autowired
	private CepService cepService;
	
	@Autowired
	private ClubFlexCardService cluflexCardService;
	
	@GetMapping(value="/zipcode/{zipcode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> zipCodeSearch(HttpServletRequest request, @PathVariable String zipcode) {
		return createObjectReturn(cepService.getAddress(zipcode));
	}
	
	@GetMapping(value="/datetime", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> datetimenow(HttpServletRequest request) {
		return createObjectReturn(LocalDateTime.now());
	}
	
	@GetMapping(value="/clubprint/cards/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> listClubCards(HttpServletRequest request, @PathVariable Long subscriptionId) {
		if(request.getHeader("token-access-clubprint").equals(Constants.TOKEN_ACCESS_CLUBFLEX_PRINT)) {
			return createObjectReturn(cluflexCardService.listLastClubCardsLiteBySubscriptionId(subscriptionId));
		}
		return createObjectReturn(null);
	}
	
	@GetMapping(value="/qrcode/invoice-report/{hash}", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getQrCodeInvoiceReport (HttpServletRequest request, @PathVariable String hash) {
		try {
			return IOUtils.toByteArray(QrCodeUtils.generateQRCodeAsInputStream(String.format(Constants.URL_GET_REPORT_INVOICE, hash)));
		} catch (Exception e) {
			return null;
		}
	}
}
