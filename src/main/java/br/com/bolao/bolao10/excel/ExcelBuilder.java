package br.com.segmedic.clubflex.excel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Gerador de excel
 * 
 * @param <T>
 */

public class ExcelBuilder<T> {

	private static final String SHEET_NAME = "Planilha";
	private ExcelWriterFactory excel;
	private InputStream file;

	private List<T> list;

	public ExcelBuilder<T> withList(List<T> list) {
		this.list = list;
		return this;
	}

	public InputStream build() throws Exception {
		if (this.list == null || this.list.isEmpty()) {
			throw new Exception("A lista de objetos est� vazia!");
		}
		try {
			this.createSheet();
			this.createHeader();
			this.createBody();
			this.createFile();
			return this.file;
		} catch (ExcelFactoryException e) {
			throw new Exception("Erro ao gerar excel. ", e);
		} catch (Exception e) {
			throw new Exception("Erro desconhecido ao gerar excel. ", e);
		}
	}

	private void createFile() throws Exception {
		try {
			byte[] fileByteArray = this.excel.writeToFileByteArray();
			this.file = new ByteArrayInputStream(fileByteArray);
		} catch (ExcelFactoryException e) {
			throw new Exception("Erro ao criar arquivo excel. ", e);
		} catch (Exception e) {
			throw new Exception("Erro desconhecido ao gerar arquivo excel. ", e);
		}

	}

	private void createSheet() throws ExcelFactoryException {
		this.excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);
		this.excel.criarPlanilha(SHEET_NAME);
	}

	private void createBody() throws IllegalAccessException, InvocationTargetException, ExcelFactoryException {
		Map<Integer, List<ExcelBody>> mapsLines = Maps.newConcurrentMap();
		int indexLine = 0;
		for (T object : list) {
			for (Method method : object.getClass().getMethods()) {
				ExcelField annotation = method.getAnnotation(ExcelField.class);
				if (annotation != null) {
					ExcelBody body = null;
					if(method.invoke(object) == null) {
						body = new ExcelBody(annotation.posicao(), "");
					}else {
						body = new ExcelBody(annotation.posicao(), method.invoke(object).toString());
					}
					List<ExcelBody> line = mapsLines.get(indexLine);
					if (line == null) {
						mapsLines.put(indexLine, Lists.newArrayList(body));
					} else {
						line.add(body);
					}
				}
			}
			indexLine += 1;
		}
		for (int i = 0; i < list.size(); i++) {
			Row linhaBody = excel.criarLinha();
			List<ExcelBody> bodies = mapsLines.get(i);
			for (int j = 0; j < bodies.size(); j++) {
				for (ExcelBody body : bodies) {
					if (j == body.getPosicao()) {
						this.excel.criarCelula(linhaBody, j).setCellValue(body.getValor());
						this.excel.getExcel().getSheet(SHEET_NAME).autoSizeColumn(j);
						break;
					}
				}
			}
		}
	}

	private void createHeader() throws ExcelFactoryException {
		List<ExcelHeader> headers = Lists.newArrayList();
		for (T object : list) {
			for (Method method : object.getClass().getMethods()) {
				ExcelField annotation = method.getAnnotation(ExcelField.class);
				if (annotation != null) {
					headers.add(new ExcelHeader(annotation.posicao(), annotation.nome()));
				}
			}
			break;
		}
		Row linhaHeader = excel.criarLinha(0);
		for (int i = 0; i <= headers.size(); i++) {
			for (ExcelHeader header : headers) {
				if (i == header.getPosicao()) {
					this.excel.criarCelula(linhaHeader, i, headerStyle()).setCellValue(header.getNome());
					this.excel.getExcel().getSheet(SHEET_NAME).autoSizeColumn(i);
				}
			}
		}
	}

	private CellStyle headerStyle() {
		CellStyle style = excel.getExcel().createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		style.setTopBorderColor(IndexedColors.WHITE.getIndex());
		style.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		style.setRightBorderColor(IndexedColors.WHITE.getIndex());
		style.setFillForegroundColor(new HSSFColor.DARK_RED().getIndex());
		style.setFillBackgroundColor(new HSSFColor.DARK_RED().getIndex());

		Font font = excel.getExcel().createFont();
		font.setColor(HSSFColor.WHITE.index);
		font.setFontName("Calibri");
		font.setFontHeight((short) 190);
		style.setFont(font);

		return style;
	}
}
