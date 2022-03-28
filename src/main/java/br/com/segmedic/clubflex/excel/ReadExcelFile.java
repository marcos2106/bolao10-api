package br.com.segmedic.clubflex.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilit�ria para leitura do arquivo excel
 *
 */
public abstract class ReadExcelFile extends ManagerExcelAbstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadExcelFile.class);

	@Override
	public Workbook createWb() {
		Workbook wb = null;

		try {
			wb = WorkbookFactory.create(this.fileInputStream);
		} catch (InvalidFormatException e) {
			LOGGER.error("Erro ao criar workbook.", e);
			if (fileExcel.exists() && !fileExcel.delete()) {
				throw new ExcelFactoryException("Nao foi possivel deletar o arquivo excel.");
			}
		} catch (IOException e) {
			LOGGER.error("Erro ao criar workbook.", e);
			if (fileExcel.exists() && !fileExcel.delete()) {
				throw new ExcelFactoryException("Nao foi possivel deletar o arquivo excel.");
			}
		}
		return wb;
	}

}
