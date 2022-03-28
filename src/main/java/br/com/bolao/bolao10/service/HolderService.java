
package br.com.segmedic.clubflex.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.HolderFarma;
import br.com.segmedic.clubflex.model.HolderFilter;
import br.com.segmedic.clubflex.model.HolderStatus;
import br.com.segmedic.clubflex.repository.HolderRepository;
import br.com.segmedic.clubflex.repository.UserRepository;
import br.com.segmedic.clubflex.support.CNPJValidator;
import br.com.segmedic.clubflex.support.CPFValidator;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.EmailValidator;
import br.com.segmedic.clubflex.support.Strings;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.exception.RestClientException;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class HolderService {

   @Autowired
   private HolderRepository holderRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private UserService userService;

   @Autowired
   private MailService mailService;

   @Autowired
   private TicketGatewayService ticketGatewayService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   public Holder findByCpfCnpj(String cpfCnpj) {
      if (StringUtils.isBlank(cpfCnpj)) {
         throw new ClubFlexException("CPF/CNPJ não informado");
      }
      return holderRepository.findByCpfCnpj(Strings.removeNoNumericChars(cpfCnpj));
   }

   @Transactional
   public void save(Holder holder) {
      if (StringUtils.isBlank(holder.getName())) {
         throw new ClubFlexException("Nome não informado");
      }
      if (StringUtils.isBlank(holder.getCpfCnpj())) {
         throw new ClubFlexException("CPF/CNPJ não informado");
      }
      // if(StringUtils.isBlank(holder.getEmail())) {
      // throw new ClubFlexException("E-mail não informado");
      // }
      if (holder.getCpfCnpj().length() >= 14 && !CNPJValidator.isValido(holder.getCpfCnpj())) {
         throw new ClubFlexException("CNPJ inválido.");
      }
      if (holder.getCpfCnpj().length() < 14) {
         if (holder.getCpfCnpj().length() != 11 || !CPFValidator.isValido(holder.getCpfCnpj())) {
            throw new ClubFlexException("CPF inválido.");
         }
      }
      if (StringUtils.isNotBlank(holder.getEmail())) {
         if (!EmailValidator.isValido(holder.getEmail())) {
            throw new ClubFlexException("E-mail inválido.");
         }
      }
      if (StringUtils.isBlank(holder.getCellPhone())) {
         throw new ClubFlexException("Telefone Celular não informado");
      }

      // salvar titular
      Boolean isNewHolder = holder.getId() == null;
      holder.setUpdatedAt(LocalDateTime.now());
      holderRepository.save(holder);

      // se for novo titular, criar usuario de acesso
      if (isNewHolder) {
         String passwd = Strings.generateTempPassword();

         // verifica se já há um usuario para esse CPF
         User user = userRepository.findByLogin(holder.getCpfCnpj());

         if (user == null || !user.getIsActive()) {
            user = new User();
            user.setName(holder.getName());
            user.setLogin(holder.getCpfCnpj());
            user.setHolder(holder);
            user.setIsActive(Boolean.TRUE);
            user.setPassword(passwd);
            user.setProfile(UserProfile.HOLDER);
            userService.save(user);
            sendWelcomeMail(holder.getName(), holder.getEmail(), passwd);
         }
         else {
            user.setProfile(UserProfile.HOLDER);
            // user.setName(holder.getName());
            user.setDependent(null);
            user.setHolder(holder);
            userRepository.save(user);
         }
      }

      // atualiza no boleto simples se for o caso
      ticketGatewayService.updateCustomer(holder);

   }

   public void sendWelcomeMail(String name, String email, String password) {
      if (StringUtils.isNotBlank(email)) {
         MailTemplate mail = new MailTemplateBuilder()
                  .subject("Bem vindo ao clubflex")
                  .template("welcome-mail.html")
                  .addParam("nome", name)
                  .addParam("senha", password)
                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_WELCOME.getDescribe())
                  .addParam("linkclubflex", Constants.URL_LOGIN_CLUBFLEX_B2C)
                  .to(email)
                  .build();
         mailService.scheduleSend(mail);
      }
   }

   public void sendRequestCardMail(String name, String email) {
      if (StringUtils.isNotBlank(email)) {
         MailTemplate mail = new MailTemplateBuilder()
                  .subject("Bem vindo ao clubflex")
                  .template("request-card-mail.html")
                  .addParam("nome", name)
                  .addParam("linkclubflex", Constants.URL_LOGIN_CLUBFLEX_B2C)
                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                  .to(email)
                  .build();
         mailService.scheduleSend(mail);
      }
   }

   public void sendWelcomeSMS(String name, String telefone, String senha) {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      String mensagem = "Bem-vindo ao ClubFlex " + name.split(" ")[0]
         + ", \nCadastre seu cartao de credito em https://meu.clubflex.com.br/ \nSenha:" + senha;
      MessageSmsElement messageSms = new MessageSmsElement();
      messageSms.setFrom(remetente);
      messageSms.setMsg(mensagem);
      messageSms.setTo("55".concat(telefone.replaceAll("[^0-9]", "")));
      multipleMessageSms.addMessageSms(messageSms);
      try {
         smsBuilder.sendMultipleSms(multipleMessageSms);
      }
      catch (RestClientException e) {
         e.printStackTrace();
      }
   }

   public void sendRequestCardSMS(String name, String telefone) {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      String mensagem =
         "Ola " + name.split(" ")[0] + ", \nCadastre seu cartao de credito/debito em https://meu.clubflex.com.br/ \nTenha um otimo dia!";
      MessageSmsElement messageSms = new MessageSmsElement();
      messageSms.setFrom(remetente);
      messageSms.setMsg(mensagem);
      messageSms.setTo("55".concat(telefone.replaceAll("[^0-9]", "")));
      multipleMessageSms.addMessageSms(messageSms);
      try {
         smsBuilder.sendMultipleSms(multipleMessageSms);
      }
      catch (RestClientException e) {
         e.printStackTrace();
      }
   }

   public List<Holder> getAllHolders() {
      return holderRepository.allHolders();
   }

   public Holder findById(Long holderId) {
      return holderRepository.findById(holderId);
   }

   public List<HolderStatus> filter(HolderFilter filter, boolean ativos) {
      if (StringUtils.isBlank(filter.getCpfCnpjHolder())
         && StringUtils.isBlank(filter.getNameHolder())
         && filter.getNumCard() == null
         && filter.getDateBirth() == null) {
         throw new ClubFlexException("Informe ao menos um filtro");
      }

      // Pesquisa pelo Holder
      List<HolderStatus> listaHolder = holderRepository.filter(filter, ativos);

      // Se não achar holder, procura pelo depedente
      if (listaHolder != null && listaHolder.isEmpty()) {
         listaHolder = holderRepository.filterDependent(filter, ativos);
      }
      return listaHolder;
   }

   public List<HolderFarma> filterBasic(HolderFilter filter) {
      if (StringUtils.isBlank(filter.getCpfCnpjHolder())) {
         throw new ClubFlexException("Informe o CPF/CNPJ do assinante");
      }
      return holderRepository.filterBasic(filter);
   }

   @Transactional
   public void update(Holder holder) {
      holderRepository.update(holder);

      // atualiza no boleto simples se for o caso
      ticketGatewayService.updateCustomer(holder);
   }

   @Transactional
   public void deleteHolder(Long holderId) {
      Holder holder = findById(holderId);
      if (holder != null) {
         holderRepository.delete(holder);
      }
   }
}