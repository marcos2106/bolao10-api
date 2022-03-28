
package br.com.segmedic.clubflex.rest;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Reason;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.Sex;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.AddDependentRequest;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.DependentType;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.ReasonService;
import br.com.segmedic.clubflex.service.RedeItauGatewayService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.service.UserService;
import br.com.segmedic.clubflex.support.FileUtils;
import br.com.segmedic.clubflex.support.PlanilhaImport;
import br.com.segmedic.clubflex.support.Strings;

@RestController
@RequestMapping("/support")
public class SuportRest extends BaseRest {

   @Autowired
   private RedeItauGatewayService redeItauService;

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private ReasonService reasonService;

   @Autowired
   private UserService userService;

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private JmsTemplate queueRequestDependents;

   /**
    * Faz retry de um pagamento em cartao recorrente ainda pendente.
    * 
    * @param request
    * @param invoiceId
    * @return
    */
   @GetMapping(value = "/invoice/creditcard/retry/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.ATTENDANT})
   public @ResponseBody ResponseEntity<?> invoiceCreditCardPay(HttpServletRequest request, @PathVariable Long invoiceId) {
      redeItauService.pay(invoiceId);
      return createObjectReturn(Boolean.TRUE);
   }

   /**
    * Faz retry de todos os pagamentos com cartao recorrente em aberto
    * 
    * @param request
    * @return
    */
   @GetMapping(value = "/invoice/creditcard/retry/all", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> invoiceCreditCardPayAll(HttpServletRequest request) {
      LocalDate begin = LocalDate.now().minusDays(91);
      LocalDate end = LocalDate.now().minusDays(1);
      List<BigInteger> invoices = invoiceService.listInvoicesToRecurrence(begin, end);
      invoices.forEach(invoiceId -> {
         try {
            redeItauService.pay(invoiceId.longValue());
            TimeUnit.MILLISECONDS.sleep(200);
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      });
      return createObjectReturn(Boolean.TRUE);
   }

   /**
    * Faz refund de um pagamento em cartao recorrente.
    * 
    * @param request
    * @param invoiceId
    * @return
    */
   @GetMapping(value = "/invoice/creditcard/refund/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> invoiceRefund(HttpServletRequest request, @PathVariable Long invoiceId) {
      redeItauService.refund(invoiceId);
      return createObjectReturn(Boolean.TRUE);
   }

   /**
    * Regera os boletos pendentes que nao foram gerados no boleto simples.
    */
   @GetMapping(value = "/invoice/tickets/regenerate/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> invoiceTicketRegenerate(HttpServletRequest request, @PathVariable Long invoiceId) {
      invoiceService.regenerateTicketsOnGateway(invoiceId);
      return createObjectReturn(Boolean.TRUE);
   }

   /**
    * Método responsável por carregar/importar os arquivos em Excel selecionado pelo usuário
    */
   @PostMapping(value = "/loadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   @Transactional(rollbackFor = ClubFlexException.class)
   public @ResponseBody ResponseEntity<?> invoiceTicketRegenerate(HttpServletRequest request,
      MultipartFile file) {

      Subscription createdSubscription = new Subscription();

      try {

         if (file == null) {
            throw new ClubFlexException("Informe o arquivo excel para a carga de dados.");
         }

         File arquivo = FileUtils.salvarArquivoPastaTemporariaJVM(file).toFile();
         PlanilhaImport planilhaAssinatura = PlanilhaImport.PanilhaImportAssinaturaPJ(arquivo);
         PlanilhaImport planilhaBeneficiario = PlanilhaImport.PanilhaImportBeneficiarioPJ(arquivo);

         if (planilhaAssinatura.getHeader().isEmpty()) {
            throw new ClubFlexException("Arquivo incompatível para carga.");
         }

         CreateSubscriptionRequest subscriptionRequest = new CreateSubscriptionRequest();
         Holder holder = new Holder();
         List<Dependent> beneficiarios = new ArrayList<Dependent>();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

         User user = userService.findUserById(getUserToken(request).getId());

         for (int j = 0; j < planilhaAssinatura.getPlanilha().size(); j++) {

            String razaosocial = validaCampoAssinatura("Razão Social", planilhaAssinatura.getPlanilhaString(j, "Razão Social"));
            String cnpj = validaCampoAssinatura("CNPJ da empresa", planilhaAssinatura.getPlanilhaString(j, "CNPJ"));
            String email = validaCampoAssinatura("Email da empresa", planilhaAssinatura.getPlanilhaString(j, "Email"));
            String celular = validaCampoAssinatura("Celular da empresa", planilhaAssinatura.getPlanilhaString(j, "Celular"));
            String telComercial = planilhaAssinatura.getPlanilhaString(j, "Tel comercial");
            String cep = validaCampoAssinatura("CEP", planilhaAssinatura.getPlanilhaString(j, "CEP"));
            String endereco = validaCampoAssinatura("Endereço", planilhaAssinatura.getPlanilhaString(j, "Endereço"));

            String complemento = planilhaAssinatura.getPlanilhaString(j, "Complemento");
            String uf = planilhaAssinatura.getPlanilhaString(j, "Uf");
            String cidade = planilhaAssinatura.getPlanilhaString(j, "Cidade");
            String bairro = planilhaAssinatura.getPlanilhaString(j, "Bairro");
            String plano = planilhaAssinatura.getPlanilhaString(j, "Código Plano");
            Integer numero = null;
            Integer diaVencimento = null;
            try {
               numero = validaCampoAssinaturaInteiro("Número", planilhaAssinatura.getPlanilhaInteger(j, "Numero"));
            }
            catch (Exception exc) {
               throw new ClubFlexException("Número de endereço inválido.");
            }
            try {
               diaVencimento = validaCampoAssinaturaInteiro("", planilhaAssinatura.getPlanilhaInteger(j, "Dia Vencimento"));
            }
            catch (Exception exc) {
               throw new ClubFlexException("Dia de vencimento inválido.");
            }
            holder.setName(razaosocial);
            holder.setAddress(endereco);
            holder.setCellPhone(Strings.formatString(celular, "(##)#####-####"));
            holder.setCity(cidade);
            holder.setComplementAddress(complemento);
            holder.setCpfCnpj(Strings.normalizeCNPJ(cnpj));
            holder.setEmail(email);
            holder.setNeighborhood(bairro);
            holder.setNumber(numero != null ? new Long(numero) : 0);
            holder.setZipcode(cep);
            holder.setUf(uf);
            holder.setHomePhone(telComercial != null && telComercial != "" ? Strings.formatString(telComercial, "(##)####-####") : "");
            holder.setIsHolder(true);

            subscriptionRequest.setDayForPayment(diaVencimento);
            subscriptionRequest.setPaymentType(PaymentType.TICKET);
            subscriptionRequest.setPlanId(plano != null ? new Long(plano) : 26);
            subscriptionRequest.setHolder(holder);
            subscriptionRequest.setHolderOnlyResponsibleFinance(true);
            subscriptionRequest.setType(TypeSub.PJ);

         }
         for (int j = 0; j < planilhaBeneficiario.getPlanilha().size(); j++) {
            Dependent beneficiario = new Dependent();
            String nome = validaCampoAssinatura("Nome", planilhaBeneficiario.getPlanilhaString(j, "Nome"));
            String cpf = validaCampoAssinatura("CPF do beneficiário " + nome, planilhaBeneficiario.getPlanilhaString(j, "CPF"));
            String email = planilhaBeneficiario.getPlanilhaString(j, "Email");
            String celular = planilhaBeneficiario.getPlanilhaString(j, "Celular");
            String nascimento =
               validaCampoAssinatura("Data de Nascimento do beneficiário " + nome, planilhaBeneficiario.getPlanilhaString(j, "Nascimento"));
            String sexo = planilhaBeneficiario.getPlanilhaString(j, "Sexo");
            String cpfTitular = planilhaBeneficiario.getPlanilhaString(j, "Cpf do titular");
            beneficiario.setName(nome);
            beneficiario.setCpf(Strings.unnormalizeCPF(cpf));
            beneficiario.setEmail(email);
            beneficiario.setPhone(Strings.formatString(celular, "(##)#####-####"));
            try {
               beneficiario.setDateOfBirth(LocalDate.parse(nascimento, formatter));
            }
            catch (Exception exc) {
               throw new ClubFlexException("Data de nascimento inválida");
            }
            beneficiario.setSex(sexo == "" || sexo == null ? null : sexo.toUpperCase().equals("M") ? Sex.MALE : Sex.FEMALE);
            beneficiario.setCpfHolder(cpfTitular == null || cpfTitular == "" ? null : Strings.unnormalizeCPF(cpfTitular));
            if (beneficiario.getCpfHolder() == null) {
               beneficiario.setType(DependentType.HOLDER);
            }
            else {
               beneficiario.setType(DependentType.DEPENDENT);
            }
            beneficiarios.add(beneficiario);

         }

         createdSubscription = subscriptionService.createSubscription(subscriptionRequest, user);
         AddDependentRequest dependenteRequest = new AddDependentRequest();
         dependenteRequest.setDependents(beneficiarios);
         dependenteRequest.setSubscriptionId(createdSubscription.getId());
         dependenteRequest.setUser(user);
         queueRequestDependents.convertAndSend(dependenteRequest);
      }
      catch (IOException exc) {
         throw new ClubFlexException("Erro na importação do arquivo.");
      }

      return createObjectReturn(createdSubscription);
   }

   public String validaCampoAssinatura(String campo, String valor) {
      if (valor == null || valor == "")
         throw new ClubFlexException("O campo " + campo + " é obrigatório para criação da assinatura.");

      return valor;
   }

   public Integer validaCampoAssinaturaInteiro(String campo, Integer valor) {
      if (valor == null)
         throw new ClubFlexException("O campo " + campo + " é obrigatório para criação da assinatura.");

      return valor;
   }

   @GetMapping(value = "/reason", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listAll(HttpServletRequest request) {
      return createObjectReturn(reasonService.listAll());
   }

   @GetMapping(value = "/reason/list", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listAllActive(HttpServletRequest request) {
      return createObjectReturn(reasonService.listAllActive());
   }

   @GetMapping(value = "/reason/{idreason}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listAll(HttpServletRequest request, @PathVariable Long idreason) {
      return createObjectReturn(reasonService.findById(idreason));
   }

   @GetMapping(value = "/reason/find/{description}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> findByDescription(HttpServletRequest request, @PathVariable String description) {
      return createObjectReturn(reasonService.findByDescription(description));
   }

   @PostMapping(value = "/reason/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> save(@RequestBody Reason reason) {
      reasonService.save(reason);
      return createObjectReturn(Boolean.TRUE);
   }

   @DeleteMapping(value = "/reason/delete/{idReason}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> deleteReason(HttpServletRequest request, @PathVariable Long idReason) {
      reasonService.deleteReason(idReason);
      return createObjectReturn(Boolean.TRUE);
   }

}
