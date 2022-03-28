package br.com.segmedic.clubflex.scheduled;

//@Component
public class TicketScheduled {

//	private static final Logger LOGGER = LoggerFactory.getLogger(TicketScheduled.class);
//	//private static final String TIME_ZONE = "America/Sao_Paulo";
//
//	@Autowired
//	private TicketGatewayService ticketGatewayService;
//
//	@Autowired
//	private InvoiceService invoiceService;
//
//	/**
//	 * Verifica se existe algum boleto não registrado no boleto simples a cada 60
//	 * minutos de seg a sex.
//	 */
//	// A cada 60 minutos
//	//@Scheduled(cron = "* 1 * * * MON-FRI", zone = TIME_ZONE)
//	//DESLIGADO pois estava causando consumo excessivo do boleto simples.
//	public void registerTicketsNotSend() {
//		try {
//			invoiceService.listInvoiceByStatus(InvoiceStatus.GENERATING).forEach(invoice -> {
//				try {
//					if (PaymentType.TICKETS.equals(invoice.getPaymentType()) && StringUtils.isBlank(invoice.getTransactId())) {
//						ticketGatewayService.registerTicket(invoice.getId(), 1);
//					}
//					TimeUnit.MILLISECONDS.sleep(500);
//				} catch (Exception e) {
//					LOGGER.info("[TicketScheduled] - Erro no processamento de invoice.", e);
//				}
//			});
//		} catch (Exception e) {
//			LOGGER.error("Erro na execução da Scheduled de envio de boleto", e);
//		}
//	}
}
