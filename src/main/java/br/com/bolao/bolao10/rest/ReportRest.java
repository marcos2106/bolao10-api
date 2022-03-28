
package br.com.segmedic.clubflex.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.DebtReport;
import br.com.segmedic.clubflex.model.DebtReportCreditCardDetail;
import br.com.segmedic.clubflex.model.DebtReportDetail;
import br.com.segmedic.clubflex.model.DebtReportFilter;
import br.com.segmedic.clubflex.model.FinanceReportResult;
import br.com.segmedic.clubflex.model.MotionReportFilter;
import br.com.segmedic.clubflex.model.OperationalReportFilter;
import br.com.segmedic.clubflex.model.PlanReportFilter;
import br.com.segmedic.clubflex.model.ProductivityReportFilter;
import br.com.segmedic.clubflex.model.SubscriptionReportFilter;
import br.com.segmedic.clubflex.model.UserByPlanReportFilter;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.ReportService;
import br.com.segmedic.clubflex.service.SubscriptionService;

@RestController
public class ReportRest extends BaseRest {

   @Autowired
   private ReportService reportService;

   @Autowired
   private InvoiceService invoiceservice;

   @Autowired
   private SubscriptionService subscriptionService;

   private static final Logger LOGGER = LoggerFactory.getLogger(ReportRest.class);

   @GetMapping(value = "/report/invoice/id/{invoiceId}", produces = MediaType.APPLICATION_PDF_VALUE)
   public ResponseEntity<InputStreamResource> getInvoiceReport(HttpServletResponse response, @PathVariable Long invoiceId) {
      try {
         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "inline; filename=recibo.pdf");
         return ResponseEntity
                  .ok()
                  .headers(headers)
                  .contentType(MediaType.APPLICATION_PDF)
                  .body(new InputStreamResource(reportService.generateInvoiceReport(invoiceId)));
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar Recibo. RECIBO_ERROR", e);
      }
   }

   @GetMapping(value = "/receipt/invoice/id/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> generateReceiptLog(HttpServletRequest request, @PathVariable Long invoiceId) {
      try {

         Invoice invoice = invoiceservice.findById(invoiceId);

         SubscriptionLog log = new SubscriptionLog();
         log.setUserId(getUserToken(request).getId());
         log.setSubscriptionId(invoice.getSubscription().getId());
         log.setAction(SubscriptionLogAction.IMPRESSAO_RECIBO);
         log.setObs("Comprovante impresso da fatura " + invoiceId);
         subscriptionService.saveLog(log);

         return createObjectReturn(Boolean.TRUE);

      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar Recibo. RECIBO_ERROR", e);
      }
   }

   @GetMapping(value = "/report/invoice/token/{hash}", produces = MediaType.APPLICATION_PDF_VALUE)
   public ResponseEntity<InputStreamResource> getInvoiceReportHash(HttpServletResponse response, @PathVariable String hash) {
      try {
         Long invoiceId = Long.valueOf(new String(Base64.getDecoder().decode(hash)));

         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "inline; filename=recibo.pdf");
         return ResponseEntity
                  .ok()
                  .headers(headers)
                  .contentType(MediaType.APPLICATION_PDF)
                  .body(new InputStreamResource(reportService.generateInvoiceReport(invoiceId)));
      }
      catch (Exception e) {
         throw new ClubFlexException("Erro ao gerar Recibo. RECIBO_ERROR", e);
      }
   }

   @PostMapping(value = "/report/receipt", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> getReceiptReport(HttpServletRequest request, @RequestBody OperationalReportFilter filter) {
      return createObjectReturn(reportService.generateReceiptReport(filter));
   }

   @PostMapping(value = "/report/receipt/export", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.BROKER})
   public OutputStream getReceiptReportExport(HttpServletRequest request, HttpServletResponse response,
      @RequestBody OperationalReportFilter filter) {
      try {
         InputStream excel = reportService.generateReceiptReportExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();

         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @PostMapping(value = "/report/motion", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.ATTENDANT, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> getMotionReport(HttpServletRequest request, @RequestBody MotionReportFilter filter) {
      User userToken = getUserToken(request);
      if (!UserProfile.MANAGER.equals(userToken.getProfile()) && !UserProfile.SUPERVISOR.equals(userToken.getProfile())) {
         filter.setUserResponsiblePayment(userToken.getId());
      }
      return createObjectReturn(reportService.generateMotionReport(filter, false));
   }

   @PostMapping(value = "/report/motion/export", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.ATTENDANT, UserProfile.BROKER})
   public OutputStream getMotionReportExport(HttpServletRequest request, HttpServletResponse response,
      @RequestBody MotionReportFilter filter) {
      User userToken = getUserToken(request);
      if (!UserProfile.MANAGER.equals(userToken.getProfile())) {
         filter.setUserResponsiblePayment(userToken.getId());
      }
      try {
         InputStream excel = reportService.generateMotionReportExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();

         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @PostMapping(value = "/report/motion/exportDetail", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.ATTENDANT, UserProfile.BROKER})
   public OutputStream getMotionReportDetailExport(HttpServletRequest request, HttpServletResponse response,
      @RequestBody MotionReportFilter filter) {
      User userToken = getUserToken(request);
      if (!UserProfile.MANAGER.equals(userToken.getProfile())) {
         filter.setUserResponsiblePayment(userToken.getId());
      }
      try {
         InputStream excel = reportService.generateMotionReportDetailExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();

         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @PostMapping(value = "/report/subscription", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> getSubscriptionReport(HttpServletRequest request, @RequestBody SubscriptionReportFilter filter) {
      User userToken = getUserToken(request);
      if (!UserProfile.MANAGER.equals(userToken.getProfile())) {
         filter.setUserResponsiblePayment(userToken.getId());
      }
      return createObjectReturn(reportService.generateSubscriptionReport(filter));
   }

   @PostMapping(value = "/report/subscription/export", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT, UserProfile.BROKER})
   public OutputStream getSubscriptionReportExport(HttpServletRequest request, HttpServletResponse response,
      @RequestBody SubscriptionReportFilter filter) {
      User userToken = getUserToken(request);
      if (!UserProfile.MANAGER.equals(userToken.getProfile())) {
         filter.setUserResponsiblePayment(userToken.getId());
      }
      try {
         InputStream excel = reportService.generateSubscriptionReportExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();

         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @PostMapping(value = "/report/debt/renewTries/{holderId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> renewTries(@PathVariable Long holderId) {

      reportService.renewTries(holderId);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/report/debt/retry", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> retryPayCreditCard(@RequestBody Long[] invoices) {
      try {
         reportService.retryPayCreditCard(invoices);
      }
      catch (Exception exc) {
         LOGGER.error(exc.getMessage());
      }

      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/report/debtOthers", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> getDebtOthersReport(@RequestBody DebtReportFilter filter) {

      DebtReport debtReportResult = reportService.generateDebtOthersReport(filter);

      List<DebtReportDetail> reportDetailBuilder = new ArrayList<DebtReportDetail>();
      debtReportResult.getDetails().forEach(detail -> {

         DebtReportDetail reportDetail = new DebtReportDetail();
         reportDetail = detail;

         if (detail.getPaymentType().equals("Cartão de Crédito") || detail.getPaymentType().equals("Cartão de Débito")) {
            if (subscriptionService.SubscriptionIdWithoutCard(detail.getSubscriptionId()) == null) {
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

      return createObjectReturn(debtReportResult);
   }

   @PostMapping(value = "/report/debtCard", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> getDebtCardReport(@RequestBody DebtReportFilter filter) {

      DebtReport debtReportResult = reportService.generateDebtCardReport(filter);

      List<DebtReportCreditCardDetail> reportDetailBuilderCC = new ArrayList<DebtReportCreditCardDetail>();
      debtReportResult.getDetailsCreditCard().forEach(detail -> {

         DebtReportCreditCardDetail reportDetail = new DebtReportCreditCardDetail();
         reportDetail = detail;

         if (detail.getPaymentType().equals("Cartão de Crédito") || detail.getPaymentType().equals("Cartão de Débito")) {
            if (subscriptionService.SubscriptionIdWithoutCard(detail.getSubscriptionId()) == null) {
               reportDetail.setNoCard("Informado");
            }
            else {
               reportDetail.setNoCard("Não informado");
            }
         }
         else {
            reportDetail.setNoCard("n/a");
         }

         reportDetailBuilderCC.add(reportDetail);
      });
      debtReportResult.setDetailsCreditCard(reportDetailBuilderCC);

      return createObjectReturn(debtReportResult);
   }

   @PostMapping(value = "/report/debtOthers/export", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public OutputStream getDebtOthersReportExport(@RequestBody DebtReportFilter filter, HttpServletResponse response) {
      try {
         InputStream excel = reportService.generateDebtOthersReportExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();
         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @PostMapping(value = "/report/debtCard/export", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public OutputStream getDebtCardReportExport(@RequestBody DebtReportFilter filter, HttpServletResponse response) {
      try {
         InputStream excel = reportService.generateDebtCardReportExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();
         return out;
      }
      catch (Exception e) {
         return null;
      }
   }

   @GetMapping(value = "/report/lead", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.ATTENDANT})
   public @ResponseBody ResponseEntity<?> getLeadReport() {
      return createObjectReturn(reportService.generateLeadReport());
   }

   @PostMapping(value = "/report/productivity", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> geProductivity(@RequestBody ProductivityReportFilter filter) {
      return createObjectReturn(reportService.generateReportProductivity(filter));
   }

   @PostMapping(value = "/report/user-plan-report", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getUserPlanReportFilter(@RequestBody UserByPlanReportFilter filter) {
      return createObjectReturn(reportService.generateUserByPlanReport(filter));
   }

   @PostMapping(value = "/report/broker-plan-report", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getBrokerPlanReportFilter(@RequestBody UserByPlanReportFilter filter) {
      return createObjectReturn(reportService.generateBrokerByPlanReport(filter));
   }

   @PostMapping(value = "/report/detail-plan-report", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getDetailPlanReportFilter(@RequestBody PlanReportFilter filter) {
      return createObjectReturn(reportService.generateDetailPlanReport(filter));
   }

   @PostMapping(value = "/report/operacional", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public @ResponseBody ResponseEntity<?> getReportOperational(@RequestBody OperationalReportFilter filter) {

      List<FinanceReportResult> financeReportResult = reportService.generateReportOperational(filter);
      List<FinanceReportResult> financeReportResultFinal = new ArrayList<FinanceReportResult>();

      financeReportResult.forEach(financeReport -> {

         FinanceReportResult financeReportBuilder = new FinanceReportResult();
         financeReportBuilder = financeReport;

         if (financeReport.getPaymentType().equals("Cartão de Crédito") || financeReport.getPaymentType().equals("Cartão de Débito")) {
            if (subscriptionService.SubscriptionIdWithoutCard(Long.parseLong(financeReport.getSubscription())) == null) {
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

      return createObjectReturn(financeReportResult);
   }

   @PostMapping(value = "/report/operacional/export", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER})
   public OutputStream getReportOperationalExport(HttpServletResponse response, @RequestBody OperationalReportFilter filter) {
      try {
         InputStream excel = reportService.generateReportOperationalExport(filter);

         response.setContentType("application/vnd.ms-excel");

         OutputStream out = response.getOutputStream();
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = excel.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }
         excel.close();
         out.close();

         return out;
      }
      catch (Exception e) {
         return null;
      }
   }
}
