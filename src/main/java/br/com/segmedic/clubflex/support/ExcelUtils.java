
package br.com.segmedic.clubflex.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public final class ExcelUtils {

   private ExcelUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static boolean checkIfRowIsEmpty(Row row) {
      if (row == null) {
         return true;
      }
      if (row.getLastCellNum() <= 0) {
         return true;
      }
      for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
         Cell cell = row.getCell(cellNum);
         if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
            return false;
         }
      }
      return true;
   }

   public static String getCellStr(Cell cell) {
      return cell != null ? cell.toString() : "";

   }

   public static String getCellStr2(Cell cell) {
      try {
         return cell != null ? cell.getStringCellValue() : "";
      }
      catch (Exception e) {
         long l = (long) cell.getNumericCellValue();
         return "" + l;
      }

   }

   public static Double getCellDouble(Cell cell) {
      return cell != null ? cell.getNumericCellValue() : null;
   }

   public static LocalDate getCellDate(Cell cell) {
      if (null == cell || StringUtils.isBlank(cell.toString()))
         return null;
      return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   }

   public static LocalDateTime getCellDateTime(Cell cell) {
      return cell != null ? cell.getDateCellValue().toInstant()
               .atZone(ZoneId.systemDefault())
               .toLocalDateTime()
         : null;
   }

   public static boolean isCabecalho(Row row) {
      return row.getRowNum() == 0 || row.getRowNum() == 1;
   }

}
