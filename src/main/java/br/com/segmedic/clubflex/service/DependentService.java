
package br.com.segmedic.clubflex.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.DependentFilter;
import br.com.segmedic.clubflex.model.HolderStatus;
import br.com.segmedic.clubflex.repository.DependentRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.repository.UserRepository;
import br.com.segmedic.clubflex.support.CPFValidator;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.Strings;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DependentService {

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private DependentRepository dependentRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private UserService userService;

   @Autowired
   private MailService mailService;

   @Transactional
   public void generateDependents(Subscription sub, CreateSubscriptionRequest subscription) {
      sub.setDependents(Sets.newConcurrentHashSet());
      if (subscription.getDependents() != null && TypeSub.PF.equals(subscription.getType())) {
         subscription.getDependents().forEach(d -> {
            validateDependentData(d);
            d.setCpf(Strings.removeNoNumericChars((d.getCpf())));
            d.setDateOfInsert(LocalDateTime.now());
            d.setSubscription(sub);
            d.setStatus(DependentStatus.OK);
            sub.getDependents().add(d);
            createUserToDependent(d);
         });
      }
   }

   private void createUserToDependent(Dependent d) {
      if (StringUtils.isNotBlank(d.getCpf())) {
         String passwd = Strings.generateTempPassword();

         // verifica se já há um usuario para esse CPF
         User user = userRepository.findByLogin(d.getCpf());
         if (user == null) {
            user = new User();
            user.setName(d.getName());
            user.setLogin(d.getCpf());
            user.setDependent(d);
            user.setHolder(d.getSubscription().getHolder());
            user.setIsActive(Boolean.TRUE);
            user.setProfile(UserProfile.DEPENDENT);
            user.setPassword(passwd);
            userService.save(user);

            if (StringUtils.isNotBlank(d.getEmail())) {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Bem vindo ao clubflex")
                        .template("welcome-mail.html")
                        .addParam("nome", user.getName())
                        .addParam("senha", passwd)
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_WELCOME.getDescribe())
                        .to(d.getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }
         }

      }
   }

   @Transactional
   public void updateDependents(Subscription sub, List<Dependent> dependents) {
      if (sub.getDependents() == null) {
         sub.setDependents(Sets.newConcurrentHashSet());
      }
      if (dependents != null) {
         dependents.forEach(d -> {
            validateDependentData(d);
            d.setCpf(Strings.removeNoNumericChars((d.getCpf())));
            d.setDateOfInsert(LocalDateTime.now());
            d.setSubscription(sub);
            d.setStatus(DependentStatus.OK);
            sub.getDependents().add(d);
            if (d.getId() == null) {
               createUserToDependent(d);
            }
         });
      }
   }

   public void validateDependentData(Dependent d) {
      if (StringUtils.isBlank(d.getName())) {
         throw new ClubFlexException("Nome do(s) dependente(s) não foi informado");
      }
      if (d.getDateOfBirth() == null) {
         throw new ClubFlexException(String.format("Data de nascimento do dependente %s não informada.", d.getName()));
      }

      Boolean isChild = ChronoUnit.YEARS.between(d.getDateOfBirth(), LocalDateTime.now()) < 18;

      if (StringUtils.isBlank(d.getCpf()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do dependente %s não informado.", d.getName()));
      }
      if (!CPFValidator.isValido(d.getCpf()) && !isChild) {
         throw new ClubFlexException(String.format("CPF do dependente %s não está correto.", d.getName()));
      }

      if (d.getPhone() == null || d.getPhone().equals("")) {
         throw new ClubFlexException(String.format("Telefone do dependente %s não informado.", d.getName()));
      }

      if (!isChild) {
         // Verifica se o cpf do dependent é dependent ou holder em outra assinatura
         List<Subscription> subsDependent = Lists.newArrayList();
         subsDependent.addAll(subscriptionRepository.findByDependentOkByCpf(d.getCpf()));
         subsDependent.addAll(subscriptionRepository.findByHolderCpfCnpjNotResponsibleFinancial(d.getCpf()));
         AtomicBoolean isDepedent = new AtomicBoolean(false);
         subsDependent.forEach(s -> {
            if (SubscriptionStatus.OK.equals(s.getStatus()) || SubscriptionStatus.BLOCKED.equals(s.getStatus())) {
               isDepedent.set(true);
            }
         });
         if (isDepedent.get()) {
            throw new ClubFlexException(
               String.format("O dependente %s já pertence a uma assinatura e não pode ser incluido novamente.", d.getName()));
         }
      }

      if (isChild) {
         if (StringUtils.isBlank(d.getCpf())) {
            d.setCpf("00000000000");
         }
      }
   }

   public Dependent getById(Long dependentId) {
      return dependentRepository.findByDependentId(dependentId);
   }

   @Transactional
   public void save(Dependent dependent) {
      subscriptionRepository.merge(dependent);
   }

   public List<BigInteger> listPreCancelled() {
      return dependentRepository.listPreCancelled();
   }

   public boolean existsHolder(Long subscriptionId, String cpfHolder) {
      return dependentRepository.existsHolder(subscriptionId, cpfHolder);
   }

   public boolean existsCpfHolder(String cpfHolder) {
      return dependentRepository.existsCpfHolder(cpfHolder);
   }

   public List<Dependent> listDependentsByStatus(DependentStatus status) {
      return dependentRepository.listDependentsByStatus(status);
   }

   public List<Dependent> listAllDependents() {
      return dependentRepository.listAllDependents();
   }

   @Transactional
   public void update(Dependent dependent) {
      dependentRepository.update(dependent);
   }

   @Transactional
   public void deleteDependent(Long dependentId) {
      Dependent dependente = dependentRepository.findById(dependentId);
      if (dependente != null) {
         dependentRepository.delete(dependente);
      }
   }

   public List<HolderStatus> filter(DependentFilter filter) {
      if (StringUtils.isBlank(filter.getCpfCnpjDep())
         && StringUtils.isBlank(filter.getNameDep())
         && filter.getNumCard() == null
         && filter.getDateBirth() == null) {
         throw new ClubFlexException("Informe ao menos um filtro");
      }
      if (filter.getDateBirth() != null && StringUtils.isBlank(filter.getNameDep())) {
         throw new ClubFlexException("Se preencher o Nome, é necessário informar também a Data de Nascimento");
      }
      if (filter.getDateBirth() == null && !StringUtils.isBlank(filter.getNameDep())) {
         throw new ClubFlexException("Se preencher a Data de Nascimento, é necessário informar também o Nome");
      }
      return dependentRepository.filter(filter);
   }
}
