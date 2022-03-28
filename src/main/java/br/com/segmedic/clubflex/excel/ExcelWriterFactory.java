
package br.com.segmedic.clubflex.excel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Classe que facilita a geração de um arquivo Microsoft Excel.
 * 
 * @version 1.0
 * @requisite Lib Apache POI 3.10-FINAL e Apache Commons Lang 2.3
 */
public class ExcelWriterFactory {

   private Workbook excel;
   private Sheet planilha;

   public ExcelWriterFactory() throws ExcelFactoryException {
      super();
      criarExcel(ExcelTypeFile.XLSX, false);
   }

   public ExcelWriterFactory(ExcelTypeFile tipo) throws ExcelFactoryException {
      super();
      criarExcel(tipo, false);
   }

   public ExcelWriterFactory(ExcelTypeFile tipo, boolean ehArquivoGrande) throws ExcelFactoryException {
      super();
      criarExcel(tipo, ehArquivoGrande);
   }

   private void criarExcel(ExcelTypeFile tipo, boolean ehArquivoGrande) throws ExcelFactoryException {
      if (tipo == null) {
         throw new ExcelFactoryException("Erro ao criar excel. Tipo não informado.");
      }

      if (ehArquivoGrande) {
         excel = new SXSSFWorkbook(100);
      }
      else {
         if (tipo == ExcelTypeFile.XLSX) {
            excel = new XSSFWorkbook();
         }
         else {
            excel = new HSSFWorkbook();
         }
      }
   }

   public void criarMerge(int primeiraLinha, int ultimaLinha, int primeiraColuna, int ultimaColuna) throws ExcelFactoryException {
      if (planilha == null) {
         throw new ExcelFactoryException("Erro ao criar merge. Planilha inválida ou excel não criado.");
      }
      planilha.addMergedRegion(new CellRangeAddress(primeiraLinha, ultimaLinha, primeiraColuna, ultimaColuna));
   }

   public void criarPlanilha(String nome) throws ExcelFactoryException {
      if (StringUtils.isBlank(nome)) {
         throw new ExcelFactoryException("Erro ao criar planilha. Nome da planilha não informado.");
      }
      planilha = excel.createSheet(nome);
   }

   public Sheet criarPlanilhaAba(String nome) throws ExcelFactoryException {
      if (StringUtils.isBlank(nome)) {
         throw new ExcelFactoryException("Erro ao criar planilha. Nome da planilha não informado.");
      }
      planilha = excel.createSheet(nome);
      return planilha;
   }

   public Row criarLinha(Integer numero, String nomePlanilha) throws ExcelFactoryException {
      try {

         if (numero == null || numero < 0) {
            throw new ExcelFactoryException("Erro ao criar linha. Número da linha inválido.");
         }

         if (StringUtils.isBlank(nomePlanilha)) {
            throw new ExcelFactoryException("Erro ao criar linha. Nome da planilha inválido inexistente.");
         }

         return excel.getSheet(nomePlanilha).createRow(numero);
      }
      catch (Exception e) {
         throw new ExcelFactoryException("Erro ao criar linha. Planilha inexistente.");
      }

   }

   public Row criarLinha(Integer numero, Integer indexPlanilha) throws ExcelFactoryException {
      try {

         if (numero == null || numero < 0) {
            throw new ExcelFactoryException("Erro ao criar linha. Número da linha inválido.");
         }

         if (indexPlanilha == null || indexPlanilha < 0) {
            throw new ExcelFactoryException("Erro ao criar linha. Index da planilha inválido.");
         }

         return excel.getSheetAt(indexPlanilha).createRow(numero);
      }
      catch (IllegalArgumentException e) {
         throw new ExcelFactoryException("Erro ao criar linha. Planilha inexistente.");
      }
   }

   public Row criarLinha(Integer numero) throws ExcelFactoryException {
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar linha. Número da linha inválido.");
      }
      if (planilha == null) {
         criarPlanilha("Plan1");
      }
      return planilha.createRow(numero);
   }

   public Row criarLinhaAba(Integer numero, Sheet aba) throws ExcelFactoryException {
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar linha. Número da linha inválido.");
      }
      if (aba == null) {
         criarPlanilha("Plan1");
      }
      return aba.createRow(numero);
   }

   public Row criarLinha() throws ExcelFactoryException {
      Integer prox = planilha.getLastRowNum() + 1;
      return criarLinha(prox);
   }

   public Row criarLinhaAba(Sheet aba) throws ExcelFactoryException {
      Integer prox = aba.getLastRowNum() + 1;
      return criarLinhaAba(prox, aba);
   }

   public void criarLinhaEmBranco() throws ExcelFactoryException {
      Integer prox = planilha.getLastRowNum() + 1;
      criarLinha(prox);
   }

   public void criarLinhaEmBranco(Integer numeroLinha) throws ExcelFactoryException {
      if (numeroLinha == null || numeroLinha < 0) {
         throw new ExcelFactoryException("Erro ao criar linha em branco. Número da linha inválida.");
      }
      criarLinha(numeroLinha);
   }

   public void criarVariasLinhasEmBranco(Integer quantidade) throws ExcelFactoryException {
      if (quantidade == null || quantidade < 1) {
         throw new ExcelFactoryException("Erro ao criar várias linhas. Quantidade inválida.");
      }
      for (int i = 0; i < quantidade; i++) {
         criarLinha();
      }
   }

   public Cell criarCelula(Row linha, Integer numero) throws ExcelFactoryException {
      if (linha == null) {
         throw new ExcelFactoryException("Erro ao criar célula. Linha não pode ser nula.");
      }
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar célula. Número da célula inválido.");
      }
      return linha.createCell(numero);
   }

   public Cell criarCelula(Row linha, Integer numero, int tipoCelula) throws ExcelFactoryException {
      if (linha == null) {
         throw new ExcelFactoryException("Erro ao criar célula. Linha não pode ser nula.");
      }
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar célula. Número da célula inválido.");
      }
      return linha.createCell(numero, tipoCelula);
   }

   public Cell criarCelula(Row linha, Integer numero, int tipoCelula, CellStyle style) throws ExcelFactoryException {
      if (linha == null) {
         throw new ExcelFactoryException("Erro ao criar célula. Linha não pode ser nula.");
      }
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar célula. Número da célula inválido.");
      }
      Cell celula = linha.createCell(numero, tipoCelula);
      if (style != null) {
         celula.setCellStyle(style);
      }
      return celula;
   }

   public Cell criarCelula(Row linha, Integer numero, CellStyle style) throws ExcelFactoryException {
      if (linha == null) {
         throw new ExcelFactoryException("Erro ao criar célula. Linha não pode ser nula.");
      }
      if (numero == null || numero < 0) {
         throw new ExcelFactoryException("Erro ao criar célula. Número da célula inválido.");
      }
      Cell celula = linha.createCell(numero);
      celula.setCellStyle(style);
      return celula;
   }

   public void mesclarCelula(Integer primeiraLinha, Integer ultimaLinha, Integer primeiraColuna, Integer ultimaColuna)
      throws ExcelFactoryException {
      try {
         planilha.addMergedRegion(new CellRangeAddress(primeiraLinha, ultimaLinha, primeiraColuna, ultimaColuna));
      }
      catch (Exception e) {
         throw new ExcelFactoryException("Erro ao mesclar célula");
      }
   }

   public void mesclarCelula(Integer primeiraLinha, Integer ultimaLinha, Integer primeiraColuna, Integer ultimaColuna, String nomePlanilha)
      throws ExcelFactoryException {
      try {
         excel.getSheet(nomePlanilha).addMergedRegion(new CellRangeAddress(primeiraLinha, ultimaLinha, primeiraColuna, ultimaColuna));
      }
      catch (Exception e) {
         throw new ExcelFactoryException("Erro ao mesclar célula.");
      }
   }

   public void mesclarCelula(Integer primeiraLinha, Integer ultimaLinha, Integer primeiraColuna, Integer ultimaColuna,
      Integer indexPlanilha) throws ExcelFactoryException {
      try {
         excel.getSheetAt(indexPlanilha).addMergedRegion(new CellRangeAddress(primeiraLinha, ultimaLinha, primeiraColuna, ultimaColuna));
      }
      catch (Exception e) {
         throw new ExcelFactoryException("Erro ao mesclar célula.");
      }
   }

   public File writeToFile(String nome) throws ExcelFactoryException {
      try {
         File file = new File(nome);
         FileOutputStream fileOut = new FileOutputStream(file);
         excel.write(fileOut);
         fileOut.close();
         return file;
      }
      catch (FileNotFoundException e) {
         throw new ExcelFactoryException("Erro ao criar arquivo excel.");
      }
      catch (IOException e) {
         throw new ExcelFactoryException("Erro ao salvar arquivo excel.");
      }
   }

   public byte[] writeToFileByteArray() throws ExcelFactoryException {
      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         excel.write(bos);
         return bos.toByteArray();
      }
      catch (Exception e) {
         throw new ExcelFactoryException("Erro ao salvar arquivo excel em bytes.");
      }
   }

   public void setPlanilhaAtiva(String nome) {
      planilha = excel.getSheet(nome);
   }

   public void setPlanilhaAtiva(Integer index) {
      planilha = excel.getSheetAt(index);
   }

   public Workbook getExcel() {
      return excel;
   }

   public Sheet getPlanilha() {
      return this.planilha;
   }

}
