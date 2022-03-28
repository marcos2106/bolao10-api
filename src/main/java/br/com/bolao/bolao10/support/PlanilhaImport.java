
package br.com.bolao.bolao10.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class PlanilhaImport {

   public PlanilhaImport() {

   }

   List<List<Cell>> planilha;
   Hashtable<String, Integer> header;

   public List<List<Cell>> getPlanilha() {
      return planilha;
   }

   public void setPlanilha(List<List<Cell>> planilha) {
      this.planilha = planilha;
   }

   public Hashtable<String, Integer> getHeader() {
      return header;
   }

   public void setHeader(Hashtable<String, Integer> header) {
      this.header = header;
   }

   public static PlanilhaImport PanilhaImportAssinaturaPJ(File file) throws IOException {
      PlanilhaImport planilhaAssinatura = new PlanilhaImport();
      planilhaAssinatura.planilha = readCsvAssinaturaPJ(file);
      planilhaAssinatura.header = new Hashtable<String, Integer>();

      if (!planilhaAssinatura.planilha.isEmpty()) {
         for (int i = 0; i < planilhaAssinatura.planilha.get(0).size(); i++)
            planilhaAssinatura.header.put(ExcelUtils.getCellStr(planilhaAssinatura.planilha.get(0).get(i)), i);
         planilhaAssinatura.planilha.remove(0);
      }
      return planilhaAssinatura;
   }

   public static PlanilhaImport PanilhaImportBeneficiarioPJ(File file) throws IOException {
      PlanilhaImport planilhaBeneficiario = new PlanilhaImport();
      planilhaBeneficiario.planilha = readCsvAssinaturaBeneficiario(file);
      planilhaBeneficiario.header = new Hashtable<String, Integer>();
      if (!planilhaBeneficiario.planilha.isEmpty()) {
         for (int i = 0; i < planilhaBeneficiario.planilha.get(0).size(); i++)
            planilhaBeneficiario.header.put(ExcelUtils.getCellStr(planilhaBeneficiario.planilha.get(0).get(i)), i);
         planilhaBeneficiario.planilha.remove(0);
      }
      return planilhaBeneficiario;
   }

   public PlanilhaImport(File file) throws IOException {
      planilha = readCsv(file);
      header = new Hashtable<String, Integer>();
      for (int i = 0; i < planilha.get(0).size(); i++)
         header.put(ExcelUtils.getCellStr(planilha.get(0).get(i)), i);
      planilha.remove(0);
   }

   public String getPlanilhaString(int index, String titulo) {
      List<Cell> linha = planilha.get(index);
      return getPlanilhaString(linha, titulo);
   }

   public String getPlanilhaString(List<Cell> linha, String titulo) {
      if (null == header.get(titulo))
         return null;
      if (linha.size() <= header.get(titulo) || "".equals(linha.get(header.get(titulo))))
         return null;
      return ExcelUtils.getCellStr2(linha.get(header.get(titulo)));
   }

   public BigDecimal getPlanilhaBigDecimal(int index, String titulo) {
      List<Cell> linha = planilha.get(index);
      return getPlanilhaBigDecimal(linha, titulo);
   }

   public BigDecimal getPlanilhaBigDecimal(List<Cell> linha, String titulo) {
      if (null == header.get(titulo))
         return null;
      try {
         Double d = ExcelUtils.getCellDouble(linha.get(header.get(titulo)));
         return d == null ? null : new BigDecimal(d);
      }
      catch (Exception e) {
         throw new RuntimeException("Erro converter " + titulo + " em decimal.");
      }
   }

   public Integer getPlanilhaInteger(int index, String titulo) {
      List<Cell> linha = planilha.get(index);
      return getPlanilhaInteger(linha, titulo);
   }

   public Integer getPlanilhaInteger(List<Cell> linha, String titulo) {
      if (null == header.get(titulo) || header.get(titulo) >= linha.size() || StringUtils.isBlank(linha.get(header.get(titulo)).toString()))
         return null;
      try {
         Double d = ExcelUtils.getCellDouble(linha.get(header.get(titulo)));
         return d == null ? null : d.intValue();
      }
      catch (Exception e) {
         throw new RuntimeException("Erro converter " + titulo + " em inteiro.");
      }
   }

   public LocalDate getPlanilhaDate(int index, String titulo) {
      List<Cell> linha = planilha.get(index);
      return getPlanilhaDate(linha, titulo);
   }

   public LocalDate getPlanilhaDate(List<Cell> linha, String titulo) {
      if (null == header.get(titulo))
         return null;
      try {
         return ExcelUtils.getCellDate(linha.get(header.get(titulo)));
      }
      catch (Exception e) {
         throw new RuntimeException("Erro converter " + titulo + " em data.");
      }
   }

   public List<List<Cell>> readCsv(File file) throws IOException {

      List<List<Cell>> result = new ArrayList<List<Cell>>();
      try {
         FileInputStream excelFile = new FileInputStream(file);
         Workbook workbook = new HSSFWorkbook(excelFile);

         for (Row row : workbook.getSheetAt(0)) {

            if (!ExcelUtils.checkIfRowIsEmpty(row)) {
               List<Cell> data = new ArrayList<Cell>();
               for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                  Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
                  // data.add(ExcelUtils.getCellStr2(cell));
                  data.add(cell);
               }
               result.add(data);

            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }

   public static List<List<Cell>> readCsvAssinaturaPJ(File file) throws IOException {

      List<List<Cell>> result = new ArrayList<List<Cell>>();
      try {
         FileInputStream excelFile = new FileInputStream(file);
         Workbook workbook = new HSSFWorkbook(excelFile);

         for (Row row : workbook.getSheetAt(0)) {

            if (row.getCell(0).toString().trim().equals("Assinatura Empresa") || row.getCell(0).toString().trim().equals("Beneficiarios")) {
               continue;
            }
            else if (row.getCell(0).toString().trim().equals("Nome")) {
               break;
            }

            if (!ExcelUtils.checkIfRowIsEmpty(row)) {
               List<Cell> data = new ArrayList<Cell>();
               for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                  Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
                  data.add(cell);
               }
               result.add(data);
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }

   public static List<List<Cell>> readCsvAssinaturaBeneficiario(File file) throws IOException {

      List<List<Cell>> result = new ArrayList<List<Cell>>();
      try {
         FileInputStream excelFile = new FileInputStream(file);
         Workbook workbook = new HSSFWorkbook(excelFile);
         Boolean continua = false;

         for (Row row : workbook.getSheetAt(0)) {

            try {
               if (row.getCell(0).toString().trim().equals("Nome") && !continua) {
                  continua = true;
               }
               if (continua) {
                  if (!ExcelUtils.checkIfRowIsEmpty(row)) {
                     List<Cell> data = new ArrayList<Cell>();
                     for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                        Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
                        data.add(cell);
                     }
                     result.add(data);
                  }
               }
            }
            catch (Exception exc) {
               // TODO: handle exception
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return result;

   }
}
