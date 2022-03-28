package br.com.segmedic.clubflex.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.DashboardService;

@RestController
public class DashboardRest extends BaseRest {

	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping(value="/dashboard/debitQuantity", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getDebitQuantity() {
		return createObjectReturn(dashboardService.getDebitQuantity());
	}
	
	@GetMapping(value="/dashboard/totalSubscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getTotalSubscriptions() {
		return createObjectReturn(dashboardService.getTotalSubscriptions());
	}
	
	@GetMapping(value="/dashboard/traficAmount", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getTraficAmount() {
		return createObjectReturn(dashboardService.getTraficAmount());
	}
	
	@GetMapping(value="/dashboard/performance", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getPerformance() {
		return createObjectReturn(dashboardService.getPerformance());
	}
	
	@GetMapping(value="/dashboard/graph/subs", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getGraphSubs() {
		return createObjectReturn(dashboardService.getSubsLastYear());
	}
	
	@GetMapping(value="/dashboard/graph/debitHistory", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequireAuthentication({UserProfile.MANAGER})
	public @ResponseBody ResponseEntity<?> getDebitHistory() {
		return createObjectReturn(dashboardService.getDebitHistory());
	}
}
