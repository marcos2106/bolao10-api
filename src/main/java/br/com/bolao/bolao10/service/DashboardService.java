package br.com.segmedic.clubflex.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.model.DashGraphSubs;
import br.com.segmedic.clubflex.model.DashValue;
import br.com.segmedic.clubflex.model.DebitHistoryResponse;
import br.com.segmedic.clubflex.repository.DashboardRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;
	
	public DashGraphSubs getSubsLastYear() {
		return dashboardRepository.getSubsLastYear();
	} 
	
	public DashValue getTraficAmount() {
		LocalDate beginActual = LocalDate.now().withDayOfMonth(1);
		LocalDate endActual = LocalDate.now();
		LocalDate beginPast = beginActual.minusMonths(1);
		LocalDate endPast = endActual.minusMonths(1);		
		
		BigDecimal traficAmountActual = dashboardRepository.getTraficAmount(beginActual, endActual);
		BigDecimal traficAmountPast = dashboardRepository.getTraficAmount(beginPast, endPast);
		
		BigDecimal percentPast = null;
		try {
			percentPast = traficAmountActual.divide(traficAmountPast, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).subtract(new BigDecimal("100"));
		} catch (Exception e) {
			percentPast = BigDecimal.ZERO;
		}
		
		DashValue dash = new DashValue();
		dash.setActualValue(traficAmountActual);
		dash.setPercentValue(percentPast);
		return dash;
		
	} 
	
	public DashValue getDebitQuantity() {
		
		LocalDate beginActual = LocalDate.now().withDayOfMonth(1);
		LocalDate endActual = LocalDate.now();
		LocalDate beginPast = beginActual.minusMonths(1);
		LocalDate endPast = endActual.minusMonths(1);		
		
		BigDecimal debitAmountActual = dashboardRepository.getDebitQuantity(beginActual, endActual);
		BigDecimal debitAmountPast = dashboardRepository.getDebitQuantity(beginPast, endPast);
		
		BigDecimal percentPast = null;
		try {
			percentPast = debitAmountActual.divide(debitAmountPast, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).subtract(new BigDecimal("100"));
		} catch (Exception e) {
			percentPast = BigDecimal.ZERO;
		}
		
		DashValue dash = new DashValue();
		dash.setActualValue(debitAmountActual);
		dash.setPercentValue(percentPast);
		return dash;
		
	} 
	
	public DashValue getTotalSubscriptions() {
		LocalDate beginActual = LocalDate.now().withDayOfMonth(1);
		LocalDate endActual = LocalDate.now();
		LocalDate beginPast = beginActual.minusMonths(1);
		LocalDate endPast = endActual.minusMonths(1);	
		
		BigDecimal subsActual = dashboardRepository.getTotalSubscriptions(beginActual, endActual);
		BigDecimal subsPast = dashboardRepository.getTotalSubscriptions(beginPast, endPast);
		
		BigDecimal percentPast = null;
		try {
			percentPast = subsActual.divide(subsPast, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).subtract(new BigDecimal("100"));
		} catch (Exception e) {
			percentPast = BigDecimal.ZERO;
		}
		
		DashValue dash = new DashValue();
		dash.setActualValue(subsActual);
		dash.setPercentValue(percentPast);
		return dash;
	}

	public DashValue getPerformance() {
		LocalDate beginActual = LocalDate.now().withDayOfMonth(1);
		LocalDate endActual = LocalDate.now();
		LocalDate beginPast = beginActual.minusMonths(1);
		LocalDate endPast = endActual.minusMonths(1);	
		//LocalDate actual = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
		//LocalDate past = actual.minusMonths(1);
		
		BigDecimal traficAmountActual = dashboardRepository.getTraficAmount(beginActual, endActual);
		BigDecimal traficAmountPast = dashboardRepository.getTraficAmount(beginPast, endPast);
		BigDecimal debitAmountActual = dashboardRepository.getDebitAmount(beginPast, endPast);
		BigDecimal debitAmountPast = dashboardRepository.getDebitAmount(beginPast, endPast);
		
		//Performance  = receita - inadimplencia atual comparada ao mes anterior
		BigDecimal trafficMinusDebitActual = null;
		BigDecimal trafficMinusDebitPast = null;
		try {
			trafficMinusDebitActual = traficAmountActual.subtract(debitAmountActual);
			trafficMinusDebitPast = traficAmountPast.subtract(debitAmountPast);
		} catch (Exception e) {
			trafficMinusDebitActual = BigDecimal.ZERO;
			trafficMinusDebitPast = BigDecimal.ZERO;
		}
		
		BigDecimal performance = null;
		try {
			performance = trafficMinusDebitActual.divide(trafficMinusDebitPast, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).subtract(new BigDecimal("100"));
		} catch (Exception e) {
			performance = BigDecimal.ZERO;
		}
		
		DashValue dash = new DashValue();
		dash.setActualValue(performance);
		dash.setPercentValue(BigDecimal.ZERO);
		return dash;
	}

	public DebitHistoryResponse getDebitHistory() {
		List<Object[]> data = dashboardRepository.getDebtHistoryLastSixMouth();
		
		DebitHistoryResponse debt = new DebitHistoryResponse();
		data.forEach(d->{
			debt.getMonths().add(getMonth(Integer.valueOf(d[0].toString())));
			debt.getValues().add(Integer.valueOf(d[2].toString()));
		});
		
		return debt;
	} 
	
	public String getMonth(Integer month) {
		switch (month) {
	        case 1:  return "Jan";
	        case 2:  return "Fev";
	        case 3:  return "Mar";
	        case 4:  return "Abr";
	        case 5:  return "Mai";
	        case 6:  return "Jun";
	        case 7:  return "Jul";
	        case 8:  return "Ago";
	        case 9:  return "Set";
	        case 10: return "Out";
	        case 11: return "Nov";
	        case 12: return "Dez";
		}
		return null;
	}
	
}
