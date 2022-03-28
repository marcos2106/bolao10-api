package br.com.segmedic.clubflex.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Classe utilit�ria para escrita no excel
 *
 */
public abstract class WriteExcelFile extends ManagerExcelAbstract {

	public abstract void writeFile();

	@Override
	public Workbook createWb() {
		Workbook wb = null;

		if (path.endsWith("xlsx")) {
			wb = new XSSFWorkbook();
		} else if (path.endsWith("xls")) {
			wb = new HSSFWorkbook();
		}

		return wb;
	}

}
