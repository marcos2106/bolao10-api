package br.com.segmedic.clubflex.support;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayUtils {
	
	static final private List<LocalDate> datas = new ArrayList<LocalDate>();

	static {
		
		int year = LocalDate.now().getYear();
	
		// new year
		datas.add(date(year, 1, 1));
		
		// carnival
		datas.add(easter(year).minusDays(48));
		datas.add(easter(year).minusDays(47));
		datas.add(easter(year).minusDays(46));
		
		// tiradentes
		datas.add(date(year, 4, 21));
		
		// good friday
		datas.add(easter(year).minusDays(2));
		
		// labour
		datas.add(date(year, 5, 1));
		
		// corpus christi
		datas.add(easter(year).plusDays(60));
		
		// independence
		datas.add(date(year, 9, 7));
		
		// aparedica
		if (year >= 1980) {
			datas.add(date(year, 10, 12));
		}

		// Servidor publico (LOCAL)
		//datas.add(date(year, 10, 28));
		
		// dead
		datas.add(date(year, 11, 2));
		
		// republic
		datas.add(date(year, 11, 15));
		
		// Dia do Evangélico (LOCAL)
		//datas.add(date(year, 11, 30));
		
		// christmas
		datas.add(date(year, 12, 25));
	}

	static LocalDate easter(int year) {
		int a = year % 19;
		int b = year / 100;
		int c = year % 100;
		int d = b / 4;
		int e = b % 4;
		int f = (b + 8) / 25;
		int g = (b - f + 1) / 3;
		int h = (19 * a + b - d - g + 15) % 30;
		int i = c / 4;
		int k = c % 4;
		int l = (32 + 2 * e + 2 * i - h - k) % 7;
		int m = (a + 11 * h + 22 * l) / 451;
		int month = (h + l - 7 * m + 114) / 31;
		int day = ((h + l - 7 * m + 114) % 31) + 1;
		return LocalDate.of(year, month, day);
	}

	private static LocalDate date(int year, int month, int day) {
		return LocalDate.of(year, month, day);
	}

	public boolean verificaFeriado(LocalDate data) throws ParseException {
		if (datas.contains(data))
			return true;
		return false;
	}

	public static List<LocalDate> getDatas() {
		return datas;
	}
	
	/**
	 * Método responsável para verificar se a data de vencimento da fatura não esseja no final de semana e nem em feriados.
	 * 
	 * @param baseData - data de vencimento da fatura
	 * 
	 * @return próximo dia útil
	 */
	public static LocalDate proximoDiaUtil(LocalDate dateDue) {

		while (isDiaNaoUtil(dateDue)) {
			dateDue = dateDue.plusDays(1);
		}
		return dateDue;
	}

	private static boolean isDiaNaoUtil(LocalDate dateDue) {
		boolean diaNaoUtil = false;
		try {
			HolidayUtils hu = new HolidayUtils();
			diaNaoUtil = (dateDue.getDayOfWeek() == DayOfWeek.SATURDAY) 
					|| (dateDue.getDayOfWeek() == DayOfWeek.SUNDAY)
					|| (hu.verificaFeriado(dateDue));
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		return diaNaoUtil;
	}	
}
