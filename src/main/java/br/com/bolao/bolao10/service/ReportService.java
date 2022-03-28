
package br.com.segmedic.clubflex.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import br.com.segmedic.clubflex.domain.Broker;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.excel.ExcelBuilder;
import br.com.segmedic.clubflex.excel.ExcelTypeFile;
import br.com.segmedic.clubflex.excel.ExcelWriterFactory;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.DebtReport;
import br.com.segmedic.clubflex.model.DebtReportCreditCardDetail;
import br.com.segmedic.clubflex.model.DebtReportDetail;
import br.com.segmedic.clubflex.model.DebtReportFilter;
import br.com.segmedic.clubflex.model.DebtReportTotal;
import br.com.segmedic.clubflex.model.FinanceReportResult;
import br.com.segmedic.clubflex.model.LeadReport;
import br.com.segmedic.clubflex.model.MotionDetail;
import br.com.segmedic.clubflex.model.MotionReport;
import br.com.segmedic.clubflex.model.MotionReportFilter;
import br.com.segmedic.clubflex.model.OperationalReportFilter;
import br.com.segmedic.clubflex.model.PlanReportFilter;
import br.com.segmedic.clubflex.model.ProductivityReport;
import br.com.segmedic.clubflex.model.ProductivityReportFilter;
import br.com.segmedic.clubflex.model.ReceiptDetail;
import br.com.segmedic.clubflex.model.ReceiptReport;
import br.com.segmedic.clubflex.model.SubscriptionDetail;
import br.com.segmedic.clubflex.model.SubscriptionReport;
import br.com.segmedic.clubflex.model.SubscriptionReportFilter;
import br.com.segmedic.clubflex.model.UserByPlanReport;
import br.com.segmedic.clubflex.model.UserByPlanReportFilter;
import br.com.segmedic.clubflex.model.UserByPlanReportHorizontal;
import br.com.segmedic.clubflex.model.UserByPlanReportItem;
import br.com.segmedic.clubflex.model.UserByPlanReportPlan;
import br.com.segmedic.clubflex.repository.BrokerRepository;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.repository.ReportRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.repository.UserRepository;
import br.com.segmedic.clubflex.support.Constants;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReportService {

   private static final String DD_MM_YYYY = "dd/MM/yyyy";

   @Autowired
   private ReportRepository reportRepository;

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private BrokerRepository brokerRepository;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private RedeItauGatewayService redeItauService;

   public ReceiptReport generateReceiptReport(OperationalReportFilter filter) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now());
      }
      if (filter.getDateEnd() == null) {
         filter.setDateEnd(LocalDate.now());
      }

      return reportRepository.generateReceiptReport(filter);
   }

   public InputStream generateReceiptReportExport(OperationalReportFilter filter) {
      ReceiptReport report = generateReceiptReport(filter);

      try {

         ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);

         // Criando a Aba de Recebidos
         Sheet abaRecebidos =
            excel.criarPlanilhaAba("Recebidos " + report.getReceivedFmt() + " (" + report.getDetailsReceived().size() + ")");

         // Criando Header
         Row linhaHeader1 = excel.criarLinhaAba(abaRecebidos);
         excel.criarCelula(linhaHeader1, 0).setCellValue("Assinatura");
         excel.criarCelula(linhaHeader1, 1).setCellValue("Fatura");
         excel.criarCelula(linhaHeader1, 2).setCellValue("Titular");
         excel.criarCelula(linhaHeader1, 3).setCellValue("Forma de Pag Assinatura");
         excel.criarCelula(linhaHeader1, 4).setCellValue("Dt Pagamento");
         excel.criarCelula(linhaHeader1, 5).setCellValue("Plano");
         excel.criarCelula(linhaHeader1, 6).setCellValue("Preço");
         excel.criarCelula(linhaHeader1, 7).setCellValue("Tipo Fatura");
         excel.criarCelula(linhaHeader1, 8).setCellValue("Tipo Pag Realizado");
         excel.criarCelula(linhaHeader1, 9).setCellValue("Telefone");
         excel.criarCelula(linhaHeader1, 10).setCellValue("Bairro");
         excel.criarCelula(linhaHeader1, 11).setCellValue("CEP");

         // Criando Body
         for (ReceiptDetail detail : report.getDetailsReceived()) {
            Row linhaBody = excel.criarLinhaAba(abaRecebidos);
            excel.criarCelula(linhaBody, 0).setCellValue(detail.getIdSubscription());
            excel.criarCelula(linhaBody, 1).setCellValue((detail.getIdInvoice() == null) ? "" : detail.getIdInvoice().toString());
            excel.criarCelula(linhaBody, 2).setCellValue(detail.getHolderName());
            excel.criarCelula(linhaBody, 3).setCellValue(detail.getSubsPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 4).setCellValue(
               (detail.getPaymentDate() == null) ? "" : detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
            excel.criarCelula(linhaBody, 5).setCellValue(detail.getPlanName());
            excel.criarCelula(linhaBody, 6).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
            excel.criarCelula(linhaBody, 7).setCellValue((detail.getInvoiceType() == null) ? "" : detail.getInvoiceType().getDescribe());
            excel.criarCelula(linhaBody, 8).setCellValue((detail.getPaymentType() == null) ? "" : detail.getPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 9).setCellValue(detail.getCellphone());
            excel.criarCelula(linhaBody, 10).setCellValue(detail.getNeighborhood());
            excel.criarCelula(linhaBody, 11).setCellValue(detail.getZipCode());
         }

         // Criando a Aba de A Receber
         Sheet abaReceber =
            excel.criarPlanilhaAba("A Receber " + report.getReceivableFmt() + " (" + report.getDetailsReceivable().size() + ")");

         // Criando Header
         Row linhaHeader2 = excel.criarLinhaAba(abaReceber);
         excel.criarCelula(linhaHeader2, 0).setCellValue("Assinatura");
         excel.criarCelula(linhaHeader2, 1).setCellValue("Fatura");
         excel.criarCelula(linhaHeader2, 2).setCellValue("Titular");
         excel.criarCelula(linhaHeader2, 3).setCellValue("Forma de Pag Assinatura");
         excel.criarCelula(linhaHeader2, 4).setCellValue("Dt Vencimento");
         excel.criarCelula(linhaHeader2, 5).setCellValue("Plano");
         excel.criarCelula(linhaHeader2, 6).setCellValue("Preço");
         excel.criarCelula(linhaHeader2, 7).setCellValue("Tipo Fatura");
         excel.criarCelula(linhaHeader2, 8).setCellValue("Tipo Pag Realizado");
         excel.criarCelula(linhaHeader2, 9).setCellValue("Telefone");
         excel.criarCelula(linhaHeader2, 10).setCellValue("Bairro");
         excel.criarCelula(linhaHeader2, 11).setCellValue("CEP");

         // Criando Body
         for (ReceiptDetail detail : report.getDetailsReceivable()) {
            Row linhaBody = excel.criarLinhaAba(abaReceber);
            excel.criarCelula(linhaBody, 0).setCellValue(detail.getIdSubscription());
            excel.criarCelula(linhaBody, 1).setCellValue((detail.getIdInvoice() == null) ? "" : detail.getIdInvoice().toString());
            excel.criarCelula(linhaBody, 2).setCellValue(detail.getHolderName());
            excel.criarCelula(linhaBody, 3).setCellValue(detail.getSubsPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 4).setCellValue(
               (detail.getPaymentDate() == null) ? "" : detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
            excel.criarCelula(linhaBody, 5).setCellValue(detail.getPlanName());
            excel.criarCelula(linhaBody, 6).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
            excel.criarCelula(linhaBody, 7).setCellValue((detail.getInvoiceType() == null) ? "" : detail.getInvoiceType().getDescribe());
            excel.criarCelula(linhaBody, 8).setCellValue((detail.getPaymentType() == null) ? "" : detail.getPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 9).setCellValue(detail.getCellphone());
            excel.criarCelula(linhaBody, 10).setCellValue(detail.getNeighborhood());
            excel.criarCelula(linhaBody, 11).setCellValue(detail.getZipCode());
         }

         // Criando a Aba de Inadimplentes
         Sheet abaInadimp =
            excel.criarPlanilhaAba("Inadimplentes " + report.getDefaultersFmt() + " (" + report.getDetailsDefaulters().size() + ")");

         // Criando Header
         Row linhaHeader3 = excel.criarLinhaAba(abaInadimp);
         excel.criarCelula(linhaHeader3, 0).setCellValue("Assinatura");
         excel.criarCelula(linhaHeader3, 1).setCellValue("Fatura");
         excel.criarCelula(linhaHeader3, 2).setCellValue("Titular");
         excel.criarCelula(linhaHeader3, 3).setCellValue("Forma de Pag Assinatura");
         excel.criarCelula(linhaHeader3, 4).setCellValue("Dt Vencimento");
         excel.criarCelula(linhaHeader3, 5).setCellValue("Plano");
         excel.criarCelula(linhaHeader3, 6).setCellValue("Preço");
         excel.criarCelula(linhaHeader3, 7).setCellValue("Tipo Fatura");
         excel.criarCelula(linhaHeader3, 8).setCellValue("Tipo Pag Realizado");
         excel.criarCelula(linhaHeader3, 9).setCellValue("Telefone");
         excel.criarCelula(linhaHeader3, 10).setCellValue("Bairro");
         excel.criarCelula(linhaHeader3, 11).setCellValue("CEP");

         // Criando Body
         for (ReceiptDetail detail : report.getDetailsDefaulters()) {
            Row linhaBody = excel.criarLinhaAba(abaInadimp);
            excel.criarCelula(linhaBody, 0).setCellValue(detail.getIdSubscription());
            excel.criarCelula(linhaBody, 1).setCellValue((detail.getIdInvoice() == null) ? "" : detail.getIdInvoice().toString());
            excel.criarCelula(linhaBody, 2).setCellValue(detail.getHolderName());
            excel.criarCelula(linhaBody, 3).setCellValue(detail.getSubsPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 4).setCellValue(
               (detail.getPaymentDate() == null) ? "" : detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
            excel.criarCelula(linhaBody, 5).setCellValue(detail.getPlanName());
            excel.criarCelula(linhaBody, 6).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
            excel.criarCelula(linhaBody, 7).setCellValue((detail.getInvoiceType() == null) ? "" : detail.getInvoiceType().getDescribe());
            excel.criarCelula(linhaBody, 8).setCellValue((detail.getPaymentType() == null) ? "" : detail.getPaymentType().getDescribe());
            excel.criarCelula(linhaBody, 9).setCellValue(detail.getCellphone());
            excel.criarCelula(linhaBody, 10).setCellValue(detail.getNeighborhood());
            excel.criarCelula(linhaBody, 11).setCellValue(detail.getZipCode());
         }

         return new ByteArrayInputStream(excel.writeToFileByteArray());

      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao exportar relatório de recebimento", e);
      }
   }

   public MotionReport generateMotionReport(MotionReportFilter filter, boolean isDetail) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now());
      }
      if (filter.getDateEnd() == null) {
         filter.setDateEnd(LocalDate.now());
      }
      return new MotionReport(reportRepository.generateMotionReport(filter, isDetail));
   }

   public SubscriptionReport generateSubscriptionReport(SubscriptionReportFilter filter) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now());
      }
      if (filter.getDateEnd() == null) {
         filter.setDateEnd(LocalDate.now());
      }
      return new SubscriptionReport(reportRepository.generateSubscriptionReport(filter));
   }

   public DebtReport generateDebtOthersReport(DebtReportFilter filter) {

      DebtReport report = new DebtReport();
      List<DebtReportDetail> details = reportRepository.generateDetailDebtReport(filter.getDays());
      List<DebtReportTotal> total = reportRepository.generateTotalDebtReport(filter);

      if (details.isEmpty()) {
         return null;
      }
      report.setDetails(details);
      report.setTotal(total);

      return report;
   }

   public DebtReport generateDebtCardReport(DebtReportFilter filter) {

      DebtReport report = new DebtReport();
      List<DebtReportCreditCardDetail> detailsCreditCard = reportRepository.generateCreditCardReport(filter);
      // List<DebtReportTotal> total = reportRepository.generateTotalDebtReport(filter);
      List<DebtReportTotal> total = new ArrayList<>();

      // if (detailsCreditCard.isEmpty()) {
      // return null;
      // }
      report.setDetailsCreditCard(detailsCreditCard);
      report.setTotal(total);

      return report;
   }

   public List<LeadReport> generateLeadReport() {
      return reportRepository.generateLeadReport();
   }

   public InputStream generateMotionReportExport(MotionReportFilter filter) {
      MotionReport motion = generateMotionReport(filter, false);
      if (motion.getDetails() != null) {
         try {
            // Criando Excel
            ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);
            excel.criarPlanilha("relatorio");

            // Criando Header
            Row linhaHeader = excel.criarLinha();
            excel.criarCelula(linhaHeader, 0).setCellValue("Fatura");
            excel.criarCelula(linhaHeader, 1).setCellValue("Tipo");
            excel.criarCelula(linhaHeader, 2).setCellValue("Comp.Inicial");
            excel.criarCelula(linhaHeader, 3).setCellValue("Comp.Final");
            excel.criarCelula(linhaHeader, 4).setCellValue("Valor Pago");
            excel.criarCelula(linhaHeader, 5).setCellValue("Dt. Pagamento");
            excel.criarCelula(linhaHeader, 6).setCellValue("Dt. Vencimento");
            excel.criarCelula(linhaHeader, 7).setCellValue("Num. Boleto");
            excel.criarCelula(linhaHeader, 8).setCellValue("Titular");
            excel.criarCelula(linhaHeader, 9).setCellValue("Titular CPF");
            excel.criarCelula(linhaHeader, 10).setCellValue("Dias Atraso");
            excel.criarCelula(linhaHeader, 11).setCellValue("Tp. Pagamento");
            excel.criarCelula(linhaHeader, 12).setCellValue("Assinatura");
            excel.criarCelula(linhaHeader, 13).setCellValue("Nfe");
            excel.criarCelula(linhaHeader, 14).setCellValue("Autorização");
            excel.criarCelula(linhaHeader, 15).setCellValue("Usuário");
            excel.criarCelula(linhaHeader, 16).setCellValue("NSU");

            // Criando estilo para valores negativos
            CellStyle redStyle = excel.getExcel().createCellStyle();
            Font fonte = excel.getExcel().createFont();
            fonte.setColor(IndexedColors.RED.getIndex());
            redStyle.setFont(fonte);

            // Criando Body
            for (MotionDetail detail : motion.getDetails()) {

               Row linhaBody = excel.criarLinha();

               excel.criarCelula(linhaBody, 0).setCellValue(detail.getInvoiceId());
               excel.criarCelula(linhaBody, 1).setCellValue(detail.getInvoiceType().getDescribe());
               excel.criarCelula(linhaBody, 2).setCellValue(detail.getCompetenceBegin().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               excel.criarCelula(linhaBody, 3).setCellValue(detail.getCompetenceEnd().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               excel.criarCelula(linhaBody, 4).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
               if (detail.getPaymentDate() != null) {
                  excel.criarCelula(linhaBody, 5).setCellValue(detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               }
               excel.criarCelula(linhaBody, 6).setCellValue(detail.getDueDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               if (detail.getPaymentType() == PaymentType.TICKET || detail.getPaymentType() == PaymentType.TICKETS) {
                  excel.criarCelula(linhaBody, 7).setCellValue(detail.getTransactionId());
               }
               else {
                  excel.criarCelula(linhaBody, 7).setCellValue("-");
               }
               excel.criarCelula(linhaBody, 8).setCellValue(detail.getHolderName());
               excel.criarCelula(linhaBody, 9).setCellValue(detail.getHolderCpf());
               excel.criarCelula(linhaBody, 10).setCellValue(detail.getDelay());
               excel.criarCelula(linhaBody, 11).setCellValue(detail.getPaymentType().getDescribe());
               excel.criarCelula(linhaBody, 12).setCellValue(detail.getSubscriptionId());
               excel.criarCelula(linhaBody, 13).setCellValue(detail.getNfeNumber());
               excel.criarCelula(linhaBody, 14).setCellValue(detail.getAuthorizationCode());
               excel.criarCelula(linhaBody, 15).setCellValue(detail.getUserResponsable());
               excel.criarCelula(linhaBody, 16).setCellValue(detail.getNsu());
            }
            return new ByteArrayInputStream(excel.writeToFileByteArray());
         }
         catch (Exception e) {
            throw new ClubFlexException("Erro ao exportar relatório de movimento", e);
         }
      }
      return null;
   }

   public InputStream generateSubscriptionReportExport(SubscriptionReportFilter filter) {
      SubscriptionReport report = generateSubscriptionReport(filter);
      if (report.getDetails() != null) {
         try {
            // Criando Excel
            ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);
            excel.criarPlanilha("relatorio");

            // Criando Header
            Row linhaHeader = excel.criarLinha();
            excel.criarCelula(linhaHeader, 0).setCellValue("Assinatura");
            excel.criarCelula(linhaHeader, 1).setCellValue("Tipo de Pagamento");
            excel.criarCelula(linhaHeader, 2).setCellValue("Cliente");
            excel.criarCelula(linhaHeader, 3).setCellValue("Situação");
            excel.criarCelula(linhaHeader, 4).setCellValue("Dt. Assinatura");
            excel.criarCelula(linhaHeader, 5).setCellValue("Valor");
            excel.criarCelula(linhaHeader, 6).setCellValue("Num Dependentes");
            excel.criarCelula(linhaHeader, 7).setCellValue("Plano");
            excel.criarCelula(linhaHeader, 8).setCellValue("Alteração");
            excel.criarCelula(linhaHeader, 9).setCellValue("Dt. Hr. Alteração");
            excel.criarCelula(linhaHeader, 10).setCellValue("Responsável");

            // Criando Body
            for (SubscriptionDetail detail : report.getDetails()) {

               Row linhaBody = excel.criarLinha();

               excel.criarCelula(linhaBody, 0).setCellValue(detail.getSubscriptionId());
               excel.criarCelula(linhaBody, 1).setCellValue(detail.getPaymentType().getDescribe());
               excel.criarCelula(linhaBody, 2).setCellValue(detail.getHolderName());
               excel.criarCelula(linhaBody, 3).setCellValue(detail.getStatus());
               excel.criarCelula(linhaBody, 4).setCellValue(detail.getDateBegin().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               excel.criarCelula(linhaBody, 5).setCellValue(detail.getAmount().replace("R$", "").trim());
               excel.criarCelula(linhaBody, 6).setCellValue(detail.getNumberDependents());
               excel.criarCelula(linhaBody, 7).setCellValue(detail.getPlan());
               excel.criarCelula(linhaBody, 8).setCellValue(detail.getLog());
               excel.criarCelula(linhaBody, 9).setCellValue(detail.getDateLog());
               excel.criarCelula(linhaBody, 10).setCellValue(detail.getUserResponsable());
            }
            return new ByteArrayInputStream(excel.writeToFileByteArray());
         }
         catch (Exception e) {
            throw new ClubFlexException("Erro ao exportar relatório de forma de pagamento", e);
         }
      }
      return null;
   }

   public InputStream generateMotionReportDetailExport(MotionReportFilter filter) {
      MotionReport motion = generateMotionReport(filter, true);
      if (motion.getDetails() != null) {
         try {
            // Criando Excel
            ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);
            excel.criarPlanilha("relatorio");

            // Criando Header
            Row linhaHeader = excel.criarLinha();
            excel.criarCelula(linhaHeader, 0).setCellValue("Fatura");
            excel.criarCelula(linhaHeader, 1).setCellValue("Tipo");
            excel.criarCelula(linhaHeader, 2).setCellValue("Comp.Inicial");
            excel.criarCelula(linhaHeader, 3).setCellValue("Comp.Final");
            excel.criarCelula(linhaHeader, 4).setCellValue("Valor Pago");
            excel.criarCelula(linhaHeader, 5).setCellValue("Dt. Pagamento");
            excel.criarCelula(linhaHeader, 6).setCellValue("Dt. Vencimento");
            excel.criarCelula(linhaHeader, 7).setCellValue("Titular");
            excel.criarCelula(linhaHeader, 8).setCellValue("Titular CPF");
            excel.criarCelula(linhaHeader, 9).setCellValue("Dias Atraso");
            excel.criarCelula(linhaHeader, 10).setCellValue("Tp. Pagamento");
            excel.criarCelula(linhaHeader, 11).setCellValue("Assinatura");
            excel.criarCelula(linhaHeader, 12).setCellValue("Nfe");
            excel.criarCelula(linhaHeader, 13).setCellValue("Autorização");
            excel.criarCelula(linhaHeader, 14).setCellValue("Usuário");
            excel.criarCelula(linhaHeader, 15).setCellValue("NSU");

            // Criando estilo para valores negativos
            CellStyle redStyle = excel.getExcel().createCellStyle();
            Font fonte = excel.getExcel().createFont();
            fonte.setColor(IndexedColors.RED.getIndex());
            redStyle.setFont(fonte);

            // Criando Body
            for (MotionDetail detail : motion.getDetails()) {

               Row linhaBody = excel.criarLinha();

               excel.criarCelula(linhaBody, 0).setCellValue(detail.getInvoiceId());

               if (detail.getDescription() == null) {
                  excel.criarCelula(linhaBody, 1).setCellValue(detail.getInvoiceType().getDescribe());
                  excel.criarCelula(linhaBody, 4).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
               }
               else {
                  CellStyle style = (detail.getAmountPaid().compareTo(BigDecimal.ZERO) < 0) ? redStyle : null;
                  excel.criarCelula(linhaBody, 1).setCellValue(detail.getDescription());
                  excel.criarCelula(linhaBody, 4, style).setCellValue(detail.getAmountPaidFmt().replace("R$", "").trim());
               }
               excel.criarCelula(linhaBody, 2).setCellValue(detail.getCompetenceBegin().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               excel.criarCelula(linhaBody, 3).setCellValue(detail.getCompetenceEnd().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               if (detail.getPaymentDate() != null) {
                  excel.criarCelula(linhaBody, 5).setCellValue(detail.getPaymentDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               }
               excel.criarCelula(linhaBody, 6).setCellValue(detail.getDueDate().format(DateTimeFormatter.ofPattern(DD_MM_YYYY)));
               excel.criarCelula(linhaBody, 7).setCellValue(detail.getHolderName());
               excel.criarCelula(linhaBody, 8).setCellValue(detail.getHolderCpf());
               excel.criarCelula(linhaBody, 9).setCellValue(detail.getDelay());
               excel.criarCelula(linhaBody, 10).setCellValue(detail.getPaymentType().getDescribe());
               excel.criarCelula(linhaBody, 11).setCellValue(detail.getSubscriptionId());
               excel.criarCelula(linhaBody, 12).setCellValue(detail.getNfeNumber());
               excel.criarCelula(linhaBody, 13).setCellValue(detail.getAuthorizationCode());
               excel.criarCelula(linhaBody, 14).setCellValue(detail.getUserResponsable());
               excel.criarCelula(linhaBody, 15).setCellValue(detail.getNsu());
            }
            return new ByteArrayInputStream(excel.writeToFileByteArray());
         }
         catch (Exception e) {
            throw new ClubFlexException("Erro ao exportar relatório de movimento", e);
         }
      }
      return null;
   }

   public InputStream generateInvoiceReport(Long invoiceId) {
      Invoice invoice = invoiceRepository.findById(invoiceId);
      if (invoice == null) {
         throw new ClubFlexException("Fatura não encontrada ou inválida");
      }
      if (!(InvoiceStatus.PAID.equals(invoice.getStatus()) || InvoiceStatus.TOTALREFUNDS.equals(invoice.getStatus()))) {
         throw new ClubFlexException("A fatura não consta como paga");
      }
      try {
         String hashIdBase64 = Base64.getEncoder().encodeToString(invoice.getId().toString().getBytes());

         Map<String, Object> parameters = Maps.newConcurrentMap();
         parameters.put("invoiceDate", invoice.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
         parameters.put("invoiceNumber", invoice.getId().toString());
         parameters.put("holderName", invoice.getSubscription().getHolder().getName());
         parameters.put("holderCpf", invoice.getSubscription().getHolder().getCpfCnpjFmt());
         parameters.put("invoiceAmount", invoice.getOriginalAmountFmt());
         parameters.put("invoiceTax", invoice.getPayAmountTaxFmt());
         parameters.put("invoiceTotalAmount", invoice.getPayAmountFmt());
         parameters.put("invoicePaymentType", invoice.getPaymentType().getDescribe());
         parameters.put("invoiceToken", hashIdBase64);
         parameters.put("linkQrcode", String.format(Constants.URL_QRCODE_REPORT_INVOICE, hashIdBase64));
         parameters.put("invoiceRefound", invoice.getRefoundAmountFmt());

         if (StringUtils.isNotBlank(invoice.getTransactNsu())) {
            parameters.put("invoiceNSU", invoice.getTransactNsu());
         }
         else {
            parameters.put("invoiceNSU", "-");
         }
         if (StringUtils.isNotBlank(invoice.getAuthorizationCode())) {
            parameters.put("invoiceAut", invoice.getAuthorizationCode());
         }
         else {
            parameters.put("invoiceAut", "-");
         }

         if (invoice.getUserResponsiblePayment() != null) {
            parameters.put("invoiceUser", userRepository.findById(invoice.getUserResponsiblePayment()).getName());
         }
         else {
            parameters.put("invoiceUser", "Sistema");
         }

         parameters.put("invoiceSub", invoice.getSubscription().getId().toString());
         parameters.put("invoiceCompet",
            String.format("%s a %s", invoice.getCompetenceBegin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
               invoice.getCompetenceEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
         parameters.put("invoiceOverdue", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
         parameters.put("invoiceType", invoice.getType().getDescribe());

         InputStream template = this.getClass().getResourceAsStream("/reports/InvoiceReport.jrxml");
         JasperReport report = JasperCompileManager.compileReport(template);
         JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

         // gerando arquivo
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         JasperExportManager.exportReportToPdfStream(print, outputStream);

         return new ByteArrayInputStream(outputStream.toByteArray());
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro desconhecido ao gerar recibo de pagamento.", e);
      }
   }

   public ProductivityReport generateReportProductivity(ProductivityReportFilter filter) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now());
      }
      if (filter.getDateEnd() == null) {
         filter.setDateBegin(LocalDate.now());
      }

      ProductivityReport report = new ProductivityReport();
      report.setBrokers(reportRepository.generateReportProductivityBrokers(filter));
      report.setCompanies(reportRepository.generateReportProductivityCompanies(filter));
      report.setUsers(reportRepository.generateReportProductivityUsers(filter));
      report.setPlans(reportRepository.generateReportProductivityPlans(filter));
      return report;
   }

   public UserByPlanReport generateUserByPlanReport(UserByPlanReportFilter filter) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now().minusDays(30));
      }
      if (filter.getDateEnd() == null) {
         filter.setDateBegin(LocalDate.now());
      }

      UserByPlanReport report = new UserByPlanReport();

      List<UserByPlanReportPlan> plans = reportRepository.plansAvaliablesOnPeriodByUser(filter);

      report.setHeader(plans);
      report.setItens(generateUserByPlanReportItens(filter, plans));
      report.setFooter(generatePlanReportFooter(filter, plans));

      return report;
   }

   private List<UserByPlanReportPlan> generatePlanReportFooter(UserByPlanReportFilter filter, List<UserByPlanReportPlan> plans) {
      List<UserByPlanReportPlan> finalPlans = Lists.newArrayList(plans);

      // somando total
      Integer generalTotal = 0;
      for (UserByPlanReportPlan plan : plans) {
         generalTotal += plan.getQuantity();
      }

      UserByPlanReportPlan planTotal = new UserByPlanReportPlan();
      planTotal.setId(0L);
      planTotal.setName("Total Geral");
      planTotal.setQuantity(generalTotal);
      finalPlans.add(planTotal);

      return finalPlans;
   }

   private List<UserByPlanReportItem> generateUserByPlanReportItens(UserByPlanReportFilter filter, List<UserByPlanReportPlan> plans) {
      List<User> attendents = userRepository.findAttendentsAndManager();

      List<UserByPlanReportItem> reports = Lists.newArrayList();
      attendents.forEach(attendent -> {
         UserByPlanReportItem report = new UserByPlanReportItem();
         report.setUserId(attendent.getId());
         report.setUserName(attendent.getName().toUpperCase());

         // montando horizontal data
         List<UserByPlanReportHorizontal> horizontal = Lists.newArrayList();
         plans.forEach(plan -> {
            UserByPlanReportHorizontal data = new UserByPlanReportHorizontal();
            data.setPlanId(plan.getId());
            data.setPlanName(plan.getName());
            data.setQuantity(reportRepository.getUserByPlanReportHorizontal(filter, plan.getId(), attendent.getId()));
            horizontal.add(data);
         });

         report.setHorizontalData(horizontal);
         reports.add(report);
      });

      return reports;
   }

   public UserByPlanReport generateBrokerByPlanReport(UserByPlanReportFilter filter) {
      if (filter.getDateBegin() == null) {
         filter.setDateBegin(LocalDate.now().minusDays(30));
      }
      if (filter.getDateEnd() == null) {
         filter.setDateBegin(LocalDate.now());
      }

      UserByPlanReport report = new UserByPlanReport();

      List<UserByPlanReportPlan> plans = reportRepository.plansAvaliablesOnPeriodByBroker(filter);

      report.setHeader(plans);
      report.setItens(generateBrokerByPlanReportItens(filter, plans));
      report.setFooter(generatePlanReportFooter(filter, plans));

      return report;
   }

   private List<UserByPlanReportItem> generateBrokerByPlanReportItens(UserByPlanReportFilter filter, List<UserByPlanReportPlan> plans) {
      List<Broker> brokers = brokerRepository.listAll();

      List<UserByPlanReportItem> reports = Lists.newArrayList();
      brokers.forEach(broker -> {
         UserByPlanReportItem report = new UserByPlanReportItem();
         report.setUserId(broker.getId());
         report.setUserName(broker.getName().toUpperCase());

         // montando horizontal data
         List<UserByPlanReportHorizontal> horizontal = Lists.newArrayList();
         plans.forEach(plan -> {
            UserByPlanReportHorizontal data = new UserByPlanReportHorizontal();
            data.setPlanId(plan.getId());
            data.setPlanName(plan.getName());
            data.setQuantity(reportRepository.getBrokerByPlanReportHorizontal(filter, plan.getId(), broker.getId()));
            horizontal.add(data);
         });

         report.setHorizontalData(horizontal);
         reports.add(report);
      });

      return reports;
   }

   public List<Subscription> generateDetailPlanReport(PlanReportFilter filter) {
      return subscriptionRepository.filterReportFilter(filter);
   }

   public List<FinanceReportResult> generateReportOperational(OperationalReportFilter filter) {
      return subscriptionRepository.generateReportOperational(filter);
   }

   public InputStream generateReportOperationalExport(OperationalReportFilter filter) {
      try {

         List<FinanceReportResult> financeReportResult = generateReportOperational(filter);
         List<FinanceReportResult> financeReportResultFinal = new ArrayList<FinanceReportResult>();

         financeReportResult.forEach(financeReport -> {

            FinanceReportResult financeReportBuilder = new FinanceReportResult();
            financeReportBuilder = financeReport;

            if (financeReport.getPaymentType().equals("Cartão de Crédito") || financeReport.getPaymentType().equals("Cartão de Débito")) {
               if (subscriptionRepository.SubscriptionIdWithoutCard(Long.parseLong(financeReport.getSubscription())) == null) {
                  financeReportBuilder.setNoCard("Informado");
               }
               else {
                  financeReportBuilder.setNoCard("Não informado");
               }
            }
            else {
               financeReportBuilder.setNoCard("n/a");
            }
            financeReportResultFinal.add(financeReportBuilder);
         });

         return new ExcelBuilder<FinanceReportResult>().withList(financeReportResultFinal).build();
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao exportar relatório operacional", e);
      }
   }

   public InputStream generateDebtOthersReportExport(DebtReportFilter filter) {

      DebtReport debtReportResult = generateDebtOthersReport(filter);

      List<DebtReportDetail> reportDetailBuilder = new ArrayList<DebtReportDetail>();
      debtReportResult.getDetails().forEach(detail -> {
         DebtReportDetail reportDetail = new DebtReportDetail();
         reportDetail = detail;

         if (detail.getPaymentType().equals("Cartão de Crédito") || detail.getPaymentType().equals("Cartão de Débito")) {
            if (subscriptionRepository.SubscriptionIdWithoutCard(detail.getSubscriptionId()) == null) {
               reportDetail.setNoCard("Informado");
            }
            else {
               reportDetail.setNoCard("Não informado");
            }
         }
         else {
            reportDetail.setNoCard("n/a");
         }
         reportDetailBuilder.add(reportDetail);
      });
      debtReportResult.setDetails(reportDetailBuilder);

      // Criando Excel
      ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);

      // Criando a Aba de Demais
      Sheet abaDemais = excel.criarPlanilhaAba("Demais inadimplências");

      // Criando Header
      Row linhaHeader = excel.criarLinhaAba(abaDemais);
      excel.criarCelula(linhaHeader, 0).setCellValue("Assinatura");
      excel.criarCelula(linhaHeader, 1).setCellValue("Titular");
      excel.criarCelula(linhaHeader, 2).setCellValue("Celular");
      excel.criarCelula(linhaHeader, 3).setCellValue("Residencial");
      excel.criarCelula(linhaHeader, 4).setCellValue("Email");
      excel.criarCelula(linhaHeader, 5).setCellValue("Forma de Pagamento");
      excel.criarCelula(linhaHeader, 6).setCellValue("Forma de Pagamento");
      excel.criarCelula(linhaHeader, 7).setCellValue("Fatura em aberto");
      excel.criarCelula(linhaHeader, 8).setCellValue("Valor em aberto");

      // Criando Body
      for (DebtReportDetail detail : debtReportResult.getDetails()) {
         Row linhaBody = excel.criarLinhaAba(abaDemais);
         excel.criarCelula(linhaBody, 0).setCellValue(detail.getSubscriptionId());
         excel.criarCelula(linhaBody, 1).setCellValue(detail.getHolderName());
         excel.criarCelula(linhaBody, 2).setCellValue(detail.getCellphone());
         excel.criarCelula(linhaBody, 3).setCellValue(detail.getHomephone());
         excel.criarCelula(linhaBody, 4).setCellValue(detail.getMail());
         excel.criarCelula(linhaBody, 5).setCellValue(detail.getPaymentType());
         excel.criarCelula(linhaBody, 6).setCellValue(detail.getNoCard());
         excel.criarCelula(linhaBody, 7).setCellValue(detail.getQuantityInvoices());
         excel.criarCelula(linhaBody, 8).setCellValue(detail.getAmountDebtFmt());
      }

      return new ByteArrayInputStream(excel.writeToFileByteArray());
   }

   public InputStream generateDebtCardReportExport(DebtReportFilter filter) {

      DebtReport debtReportResult = generateDebtCardReport(filter);

      List<DebtReportCreditCardDetail> reportDetailCCBuilder = new ArrayList<DebtReportCreditCardDetail>();
      debtReportResult.getDetailsCreditCard().forEach(detail -> {
         DebtReportCreditCardDetail reportDetail = new DebtReportCreditCardDetail();
         reportDetail = detail;

         if (detail.getPaymentType().equals("Cartão de Crédito") || detail.getPaymentType().equals("Cartão de Débito")) {
            if (subscriptionRepository.SubscriptionIdWithoutCard(detail.getSubscriptionId()) == null) {
               reportDetail.setNoCard("Informado");
            }
            else {
               reportDetail.setNoCard("Não informado");
            }
         }
         else {
            reportDetail.setNoCard("n/a");
         }
         reportDetailCCBuilder.add(reportDetail);
      });
      debtReportResult.setDetailsCreditCard(reportDetailCCBuilder);

      // Criando Excel
      ExcelWriterFactory excel = new ExcelWriterFactory(ExcelTypeFile.XLSX, true);

      // Criando a Aba de Cartão de Crédito
      Sheet abaCC = excel.criarPlanilhaAba("Cartão de Crédito");

      // Criando Header
      Row linhaHeaderCC = excel.criarLinhaAba(abaCC);
      excel.criarCelula(linhaHeaderCC, 0).setCellValue("Assinatura");
      excel.criarCelula(linhaHeaderCC, 1).setCellValue("Titular");
      excel.criarCelula(linhaHeaderCC, 2).setCellValue("Celular");
      excel.criarCelula(linhaHeaderCC, 3).setCellValue("Residencial");
      excel.criarCelula(linhaHeaderCC, 4).setCellValue("Email");
      excel.criarCelula(linhaHeaderCC, 5).setCellValue("Forma de Pagamento");
      excel.criarCelula(linhaHeaderCC, 6).setCellValue("Cartão Informado");
      excel.criarCelula(linhaHeaderCC, 7).setCellValue("Valor em aberto");
      excel.criarCelula(linhaHeaderCC, 8).setCellValue("Última Tentativa");
      excel.criarCelula(linhaHeaderCC, 9).setCellValue("Código Retorno");
      excel.criarCelula(linhaHeaderCC, 10).setCellValue("Número Tentativas");

      // Criando Body
      for (DebtReportCreditCardDetail detail : debtReportResult.getDetailsCreditCard()) {
         Row linhaBodyCC = excel.criarLinhaAba(abaCC);
         excel.criarCelula(linhaBodyCC, 0).setCellValue(detail.getSubscriptionId());
         excel.criarCelula(linhaBodyCC, 1).setCellValue(detail.getHolderName());
         excel.criarCelula(linhaBodyCC, 2).setCellValue(detail.getCellphone());
         excel.criarCelula(linhaBodyCC, 3).setCellValue(detail.getHomephone());
         excel.criarCelula(linhaBodyCC, 4).setCellValue(detail.getMail());
         excel.criarCelula(linhaBodyCC, 5).setCellValue(detail.getPaymentType());
         excel.criarCelula(linhaBodyCC, 6).setCellValue(detail.getNoCard());
         excel.criarCelula(linhaBodyCC, 7).setCellValue(detail.getAmountDebtFmt());
         excel.criarCelula(linhaBodyCC, 8).setCellValue(detail.getDateLastTry());
         excel.criarCelula(linhaBodyCC, 9).setCellValue(detail.getLastReturnCode());
         excel.criarCelula(linhaBodyCC, 10).setCellValue(detail.getNumTries());
      }

      return new ByteArrayInputStream(excel.writeToFileByteArray());
   }

   @Transactional
   public void renewTries(Long holderId) {

      List<CreditCard> listaCartoes = creditCardService.listByHolderId(holderId);

      for (CreditCard creditCard : listaCartoes) {
         creditCard.setRecurrency(0l);
         creditCardService.update(creditCard);
      }
   }

   public void retryPayCreditCard(Long[] invoices) {

      for (Long invoice : invoices) {
         try {
            redeItauService.payRetry(invoice);
            TimeUnit.SECONDS.sleep(2);
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
