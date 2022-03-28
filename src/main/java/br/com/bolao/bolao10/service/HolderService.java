
package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Holder;
import br.com.bolao.bolao10.domain.User;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.model.HolderFarma;
import br.com.bolao.bolao10.model.HolderFilter;
import br.com.bolao.bolao10.model.HolderStatus;
import br.com.bolao.bolao10.repository.HolderRepository;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.support.CNPJValidator;
import br.com.bolao.bolao10.support.CPFValidator;
import br.com.bolao.bolao10.support.EmailValidator;
import br.com.bolao.bolao10.support.Strings;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class HolderService {

   @Autowired
   private HolderRepository holderRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private UserService userService;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   public Holder findByCpfCnpj(String cpfCnpj) {
      if (StringUtils.isBlank(cpfCnpj)) {
         throw new Bolao10Exception("CPF/CNPJ não informado");
      }
      return holderRepository.findByCpfCnpj(Strings.removeNoNumericChars(cpfCnpj));
   }

   @Transactional
   public void save(Holder holder) {
      if (StringUtils.isBlank(holder.getName())) {
         throw new Bolao10Exception("Nome não informado");
      }
      if (StringUtils.isBlank(holder.getCpfCnpj())) {
         throw new Bolao10Exception("CPF/CNPJ não informado");
      }
      // if(StringUtils.isBlank(holder.getEmail())) {
      // throw new Bolao10Exception("E-mail não informado");
      // }
      if (holder.getCpfCnpj().length() >= 14 && !CNPJValidator.isValido(holder.getCpfCnpj())) {
         throw new Bolao10Exception("CNPJ inválido.");
      }
      if (holder.getCpfCnpj().length() < 14) {
         if (holder.getCpfCnpj().length() != 11 || !CPFValidator.isValido(holder.getCpfCnpj())) {
            throw new Bolao10Exception("CPF inválido.");
         }
      }
      if (StringUtils.isNotBlank(holder.getEmail())) {
         if (!EmailValidator.isValido(holder.getEmail())) {
            throw new Bolao10Exception("E-mail inválido.");
         }
      }
      if (StringUtils.isBlank(holder.getCellPhone())) {
         throw new Bolao10Exception("Telefone Celular não informado");
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
//            sendWelcomeMail(holder.getName(), holder.getEmail(), passwd);
         }
         else {
            user.setProfile(UserProfile.HOLDER);
            // user.setName(holder.getName());
            user.setHolder(holder);
            userRepository.save(user);
         }
      }
   }

//   public void sendWelcomeMail(String name, String email, String password) {
//      if (StringUtils.isNotBlank(email)) {
//         MailTemplate mail = new MailTemplateBuilder()
//                  .subject("Bem vindo ao clubflex")
//                  .template("welcome-mail.html")
//                  .addParam("nome", name)
//                  .addParam("senha", password)
//                  .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_WELCOME.getDescribe())
//                  .addParam("linkclubflex", Constants.URL_LOGIN_CLUBFLEX_B2C)
//                  .to(email)
//                  .build();
//         mailService.scheduleSend(mail);
//      }
//   }

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
         throw new Bolao10Exception("Informe ao menos um filtro");
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
         throw new Bolao10Exception("Informe o CPF/CNPJ do assinante");
      }
      return holderRepository.filterBasic(filter);
   }

   @Transactional
   public void update(Holder holder) {
      holderRepository.update(holder);
   }

   @Transactional
   public void deleteHolder(Long holderId) {
      Holder holder = findById(holderId);
      if (holder != null) {
         holderRepository.delete(holder);
      }
   }
}