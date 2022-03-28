package br.com.segmedic.clubflex.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class NumberUtils {

	private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

	public static String formatMoney(BigDecimal valor) {
		if (valor == null) {
			return NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(BigDecimal.ZERO);
		}
		return NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(valor);
	}
	
	public static BigDecimal proRateCalculate(BigDecimal amountBase, LocalDate begin, LocalDate end, boolean inclusive) {
		Long daysBetweenCompetence = ChronoUnit.DAYS.between(begin, end);
		if(inclusive) {
			daysBetweenCompetence = ChronoUnit.DAYS.between(begin, end)+1;
		}
		BigDecimal amountDay = amountBase.divide(new BigDecimal(begin.lengthOfMonth()), 10, RoundingMode.UP);
		return amountDay.multiply(new BigDecimal(daysBetweenCompetence)).setScale(2, RoundingMode.DOWN);
	}
	
	public static BigDecimal proRateCalculate(BigDecimal amountBase, LocalDate begin, LocalDate end) {
		return proRateCalculate(amountBase, begin, end ,false);
	}
	
	public static BigDecimal proRateCalculate(BigDecimal amountBase, LocalDate baseDate) {
		return proRateCalculate(amountBase, baseDate, baseDate.withDayOfMonth(baseDate.lengthOfMonth()) ,false);
	}
	
	public static BigDecimal proRateCalculate(BigDecimal amountBase) {
		return proRateCalculate(amountBase, LocalDate.now());
	}

	public static BigDecimal calculateTax(LocalDate dueDate, BigDecimal amount) {
		try {
			//Long outDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
			int outDays = 1;
			for (LocalDate i=dueDate; i.isBefore(LocalDate.now()); i = i.plusDays(1)) {
				if (i.getDayOfWeek().getValue()<7) {
					outDays++;
				}
			}
			BigDecimal taxFixed = amount.multiply(Constants.TAX_FIXED).divide(new BigDecimal("100"), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
			BigDecimal taxDay = amount.multiply(Constants.TAX_DAY).multiply(new BigDecimal(outDays)).divide(new BigDecimal("100"), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
			BigDecimal newAmmount = amount.add(taxFixed).add(taxDay);
			return newAmmount;
		} catch (Exception e) {
			return amount;
		}
	}
	
}
