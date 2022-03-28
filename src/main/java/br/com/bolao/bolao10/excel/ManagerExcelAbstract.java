package br.com.segmedic.clubflex.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilit�ria para gera��o do excel
 */
public abstract class ManagerExcelAbstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerExcelAbstract.class);

	public String path;
	public File fileExcel;
	public InputStream fileInputStream;
	public String fileName;
	public List<String> columns;

	public abstract Workbook createWb();

	public void createListColumns(Row row) {

		List<String> columns = new ArrayList<String>();
		Iterator<Cell> cellIterator = row.cellIterator();

		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			columns.add(cell.getStringCellValue().toUpperCase());
		}

		this.columns = columns;
	}

	public Cell cellDateFormat(Workbook wb, Cell cell) {
		HSSFCellStyle dateCellStyle = (HSSFCellStyle) wb.createCellStyle();
		short fmt = wb.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss");
		dateCellStyle.setDataFormat(fmt);
		cell.setCellStyle(dateCellStyle);
		return cell;
	}

	public static HSSFCell createCellString(HSSFCell cell) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		return cell;
	}

	public static HSSFCell createCellInteger(HSSFCell cell) {
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public File createFile(String path) {
		setPath(path);

		File file = new File(path);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LOGGER.error("Erro ao criar arquivo", e);
			}
		}

		this.fileExcel = file;
		return file;
	}

	public FileInputStream createFileInputStream(File file) {
		FileInputStream fileInputS = null;

		try {
			fileInputS = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("Erro ao criar input stream.", e);
		}

		this.fileInputStream = fileInputS;
		return fileInputS;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
