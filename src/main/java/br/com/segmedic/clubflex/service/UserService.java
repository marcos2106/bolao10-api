
package br.com.segmedic.clubflex.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.RememberPasswordRequest;
import br.com.segmedic.clubflex.repository.UserRepository;
import br.com.segmedic.clubflex.support.CNPJValidator;
import br.com.segmedic.clubflex.support.CPFValidator;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.Cryptography;
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
public class UserService {

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private JWTService jwtService;

   @Autowired
   private MailService mailService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   // @Autowired
   // private ClubFlexCardService clubFlexCardService;

   // @Autowired
   // private SubscriptionRepository subscriptionRepository;

   public String externalLogin(String login, String passwd) {
      if (StringUtils.isBlank(login) || StringUtils.isBlank(passwd)) {
         throw new ClubFlexException("Login ou Senha não informado.");
      }

      // tentar como titular
      User user = userRepository.getUserHolderByLoginAndPasswd(login, passwd);

      // nao foi, tentar como dependente
      if (user == null) {
         user = userRepository.getUserDependentByLoginAndPasswd(login, passwd);
      }

      // se não foi como titular e dependente login ou senha inválido
      if (user == null) {
         throw new ClubFlexException("Login ou Senha inválido.");
      }

      return jwtService.createJwtToken(user);
   }

   @Transactional
   public String backofficeLogin(String login, String password) {

      // gerar cartoes clubflex - Solucao paleativa para gerar carteirinhas ao logar na aplicacao
      // Subscription sub = subscriptionRepository.findById(19368L);
      // clubFlexCardService.generateAllClubFlexCards(sub);
      // subscriptionRepository.save(sub);

      if (StringUtils.isBlank(login) || StringUtils.isBlank(password)) {
         throw new ClubFlexException("Login ou Senha não informado.");
      }

      User user = userRepository.getUserByLoginAndPasswd(login, password, true);

      if (user == null) {
         throw new ClubFlexException("Login ou Senha inválido.");
      }

      if (UserProfile.HOLDER.equals(user.getProfile()) || UserProfile.DEPENDENT.equals(user.getProfile())) {
         throw new ClubFlexException("Login ou Senha inválido. (Acesso externo somente).");
      }

      return jwtService.createJwtToken(user);
   }

   @Transactional
   public User save(User user) {
      String tempPASS = Strings.generateTempPassword();
      if (StringUtils.isBlank(user.getPassword())) {
         user.setPassword(tempPASS);
      }
      user.setPassword(Cryptography.encrypt(user.getPassword()));
      user.setIsActive(Boolean.TRUE);
      user.setLogin(Strings.removeNoNumericChars(user.getLogin()));

      // Validar existencia para Atendente e Gerente
      if (UserProfile.ATTENDANT.equals(user.getProfile()) || UserProfile.MANAGER.equals(user.getProfile())
         || UserProfile.SUPERVISOR.equals(user.getProfile())
         || UserProfile.BROKER.equals(user.getProfile())) {

         User registeredUser = userRepository.findByLogin(user.getLogin());

         if (registeredUser != null && registeredUser.getProfile().equals(UserProfile.HOLDER)
            && registeredUser.getProfile().equals(UserProfile.DEPENDENT)) {
            throw new ClubFlexException("Usuário já cadastrado!");
         }

         if (StringUtils.isNotBlank(user.getEmail())) {
            MailTemplate mail = new MailTemplateBuilder()
                     .subject("Bem vindo ao clubflex")
                     .template("welcome-mail.html")
                     .addParam("nome", user.getName())
                     .addParam("senha", tempPASS)
                     .addParam("linkclubflex", Constants.URL_LOGIN_CLUBFLEX_BACKOFFICE)
                     .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_WELCOME.getDescribe())
                     .to(user.getEmail())
                     .build();
            mailService.scheduleSend(mail);
         }
      }

      return userRepository.save(user);
   }

   @Transactional
   public User update(User user) {
      User userObj = userRepository.findById(user.getId());
      userObj.setName(user.getName());
      userObj.setIsActive(user.getIsActive());
      userObj.setProfile(user.getProfile());
      userObj.setEmail(user.getEmail());
      return userRepository.save(userObj);
   }

   public User findUserById(Long id) {
      return userRepository.findById(id);
   }

   public User findUserByHolderId(Long id) {
      return userRepository.findByHolderId(id);
   }

   public User findUserByDependentId(Long id) {
      return userRepository.findUserByDependentId(id);
   }

   public String loginByHolder(Long id) {
      if (id == null) {
         throw new ClubFlexException("Id do titular não informado.");
      }
      return jwtService.createJwtToken(findUserByHolderId(id));
   }

   @Transactional
   public String rememberPassword(RememberPasswordRequest request) {

      validarPreenchimento(request.getLogin());

      validarCPFCNPJValido(request.getLogin());

      List<User> users = userRepository.findByLoginList(request.getLogin());

      String email = null;
      String sms = null;

      for (User user : users) {
         validarUsuarioExistente(user);

         try {

            String newpasswd = Strings.generateTempPassword();
            user.setPassword(Cryptography.encrypt(newpasswd));
            userRepository.save(user);

            if (user.getEmail() != null && !user.getEmail().equals("")) {
               email = user.getEmail();
            }
            else if (user.getDependent() != null && user.getDependent().getEmail() != null && !user.getDependent().getEmail().equals("")) {
               email = user.getHolder().getEmail();
            }
            else if (user.getHolder().getEmail() != null && !user.getHolder().getEmail().equals("")) {
               email = user.getHolder().getEmail();
            }

            if (email != null) {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Solicitação de senha clubflex")
                        .template("remember-passwd.html")
                        .addParam("nome", user.getName())
                        .addParam("senha", newpasswd)
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_GERAL.getDescribe())
                        .to(email)
                        .build();

               mailService.scheduleSend(mail);
               email = "Email: ".concat(email);

            }

            if (user.getDependent() != null && user.getDependent().getPhone() != null && !user.getDependent().getPhone().equals("")) {
               sms = user.getDependent().getPhone();
            }

            else if (user.getHolder() != null && user.getHolder().getCellPhone() != null && !user.getHolder().getCellPhone().equals("")) {
               sms = user.getHolder().getCellPhone();
            }

            if (sms != null) {
               sendRememberPassSMS(user.getName(), user.getHolder().getCellPhone(), newpasswd);
               email = email.concat(" SMS: ".concat(sms));
            }

         }
         catch (Exception e) {
            throw new ClubFlexException("Erro ao enviar e-mail com a nova senha. Contate nosso SAC.");
         }
      }
      return email;
   }

   /**
    * Metodo responsanvel por enviar SMS de lembra Senha.
    * 
    * @param name
    * @param telefone
    * @param senha
    */
   public void sendRememberPassSMS(String name, String telefone, String senha) {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      String mensagem = "Ola  " + name.split(" ")[0]
         + ", \nSegue a sua nova senha... \nSenha:" + senha + "\n Att, Equipe Clubflex";
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

   private void validarPreenchimento(String login) {
      if (StringUtils.isBlank(login)) {
         throw new ClubFlexException("Preencha seu CPF/CNPJ.");
      }
   }

   private void validarCPFCNPJValido(String login) {
      if (login.length() > 14) {
         if (!CNPJValidator.isValido(login)) {
            throw new ClubFlexException("CNPJ não está correto.");
         }
      }
      else {
         if (!CPFValidator.isValido(login)) {
            throw new ClubFlexException("CPF não está correto.");
         }
      }
   }

   private void validarUsuarioExistente(User user) {
      if (user == null) {
         throw new ClubFlexException("Usuário não encontrado.");
      }
   }

   @SuppressWarnings("unused")
   private void validaEmailUsuario(String email) {
      if (StringUtils.isBlank(email)) {
         throw new ClubFlexException("E-mail não está cadastrado.");
      }
   }

   @Transactional
   public void updateMail(Long userId, String mail) {
      if (StringUtils.isBlank(mail)) {
         throw new ClubFlexException("E-mail não informado.");
      }
      if (!EmailValidator.isValido(mail)) {
         throw new ClubFlexException("E-mail inválido. Informe um e-mail válido.");
      }
      User user = findUserById(userId);
      if (UserProfile.HOLDER.equals(user.getProfile())) {
         user.getHolder().setEmail(mail);
      }
      else if (UserProfile.DEPENDENT.equals(user.getProfile())) {
         user.getDependent().setEmail(mail);
      }
      else {
         throw new ClubFlexException("Usuário não suportado.");
      }
      userRepository.merge(user);
   }

   @Transactional
   public void updatePasswd(Long userId, String passwd) {
      if (StringUtils.isBlank(passwd)) {
         throw new ClubFlexException("Nova senha não informada.");
      }
      if (passwd.length() < 6) {
         throw new ClubFlexException("Senha deve ter ao menos 6 caracteres.");
      }

      User user = findUserById(userId);
      user.setPassword(Cryptography.encrypt(passwd));
      userRepository.merge(user);
   }

   public List<User> findByName(String name) {
      if (StringUtils.isBlank(name)) {
         throw new ClubFlexException("Nome não informado.");
      }
      return userRepository.findByName(name);
   }

   public List<User> listAll() {
      return userRepository.listAll();
   }

   @Transactional
   public void deleteUserByDependente(Long dependentId) {
      User user = findUserByDependentId(dependentId);
      if (user != null) {
         userRepository.delete(user);
      }
   }

   @Transactional
   public void deleteUserByHolder(Long holderId) {
      User user = findUserByHolderId(holderId);
      if (user != null) {
         userRepository.delete(user);
      }
   }

}
