
package br.com.segmedic.clubflex.support;

import java.math.BigDecimal;

public class Constants {

   public static final String QUEUE_MAIL = "queue-mails";
   public static final String QUEUE_INVOICE = "queue-invoice";
   public static final String QUEUE_PAY_SINGLE_INVOICE = "queue-pay-single-invoice";
   public static final String QUEUE_MONTHLY_INVOICE = "queue-monthly-invoice";
   public static final String QUEUE_CHANGE_CREDCARD = "queue-change-credcard";
   public static final String QUEUE_AUDIT = "queue-audit";
   public static final String QUEUE_SUB_OPERATION = "queue-sub-operation";
   public static final String QUEUE_SUB_LOG = "queue-sub-log";
   public static final String QUEUE_CREDIT_CARD_UPDATE = "queue-credit-card-update";
   public static final String QUEUE_REQUEST_DEPENDENTS = "queue-request-dependents";
   public static final String QUEUE_REQUEST_DEPENDENT = "queue-request-dependent";

   public static final BigDecimal TAX_FIXED = new BigDecimal("2");
   public static final BigDecimal TAX_DAY = new BigDecimal("0.033");
   public static final Object TOKEN_ACCESS_CLUBFLEX_PRINT = "b30735498259985510a9559004ecaffc";
   public static final String URL_LOGIN_CLUBFLEX_B2C = "https://meu.clubflex.com.br/";
   public static final String URL_LOGIN_CLUBFLEX_BACKOFFICE = "https://backoffice.clubflex.com.br/login";
   public static final String TEL_SAC_CLUBFLEX = "(21) 2666-5808";
   public static final String URL_GET_REPORT_INVOICE = "https://api.clubflex.com.br/report/invoice/token/%s";
   public static final String URL_QRCODE_REPORT_INVOICE = "https://api.clubflex.com.br/utils/qrcode/invoice-report/%s";
   public static final Long COMPANY_SITE = 1L;
   public static final String SEGMEDIC_MAIL = "segmedic@segmedic.com.br";
   public static final String SEGMEDIC_FINANC_MAIL = "financeiro@clubflex.com.br";
   public static final String TOKEN_PREVIEW_INVOICE = "20fd7747c40ffd3ee6e41f598dec4d3e";

   public static final String MAIL_SECTOR = "SETOR";
}
