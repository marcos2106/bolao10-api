
package br.com.segmedic.clubflex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import br.com.segmedic.clubflex.domain.ClubCard;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.ClubflexCardLite;
import br.com.segmedic.clubflex.repository.ClubCardRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;
import br.com.segmedic.clubflex.support.QrCodeUtils;
import br.com.segmedic.clubflex.support.Strings;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ClubFlexCardService {

   @Autowired
   private UserService userService;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private ClubCardRepository clubCardRepository;

   @Value("${url.clubflex.card.valida.qrcode}")
   private String urlQrCode;

   public Set<ClubCard> listLastClubCardsByUser(User user) {
      final User userObject = userService.findUserById(user.getId());

      Subscription sub = null;
      if (UserProfile.HOLDER.equals(userObject.getProfile()) || UserProfile.DEPENDENT.equals(userObject.getProfile())) {
         sub = subscriptionRepository.getLastByHolderId(userObject.getHolder().getId());
      }
      else {
         throw new ClubFlexException("Usuário não suportado pelo método.");
      }

      if (sub == null) {
         throw new ClubFlexException("Nenhum cartão disponível.");
      }

      // setando qrcode
      sub.getCards().forEach(c -> {
         c.setQrcode(QrCodeUtils.generateQRCodeAsBase64(urlQrCode.concat(c.getToken())));
      });

      // no caso de dependente filtrar apenas os cartoes do dependente
      if (UserProfile.DEPENDENT.equals(userObject.getProfile())) {
         Predicate<ClubCard> isFromDependent = new Predicate<ClubCard>() {

            public boolean apply(ClubCard clubcard) {
               return clubcard.getDependent() != null && clubcard.getDependent().equals(userObject.getDependent());
            }
         };
         return Sets.newConcurrentHashSet(sub.getCards().stream().filter(isFromDependent).collect(Collectors.toList()));
      }

      return sub.getCards();
   }

   @Transactional
   public void generateAllClubFlexCards(Subscription sub) {
      if (TypeSub.PF.equals(sub.getTypeSub())) {
         if (!sub.getHolderOnlyResponsibleFinance()) {
            generrateClubCardFlexHolder(sub);
         }
         generateClubCardFlexDependents(sub);
      }
   }

   @Transactional
   public void generateClubCardFlexDependents(Subscription sub) {
      if (sub.getCards() == null) {
         sub.setCards(Sets.newConcurrentHashSet());
      }
      if (sub.getDependents() != null) {
         sub.getDependents().forEach(dependent -> {
            // Somente gerar um novo cartao para o dependente que não tiver cartão.
            AtomicBoolean generateCard = new AtomicBoolean(true);
            sub.getCards().forEach(card -> {
               if (card.getDependent() != null && card.getDependent().equals(dependent)) {
                  generateCard.set(false);
               }
            });
            if (generateCard.get()) {
               sub.getCards().add(createCard(sub, dependent, false));
            }
         });
      }
   }

   @Transactional
   public void generrateClubCardFlexHolder(Subscription sub) {
      if (sub.getCards() == null) {
         sub.setCards(Sets.newConcurrentHashSet());
      }
      sub.getCards().add(createCard(sub, null, true));
   }

   public ClubCard createCard(Subscription sub, Dependent dependent, boolean isHolder) {
      ClubCard card = new ClubCard();
      card.setSubscription(sub);
      card.setHolder(sub.getHolder());
      card.setDependent(dependent);
      card.setIsCardOfHolder(isHolder);
      card.setToken(Strings.generateUniqueToken());
      card.setDateGenerated(LocalDateTime.now());
      if (SubscriptionStatus.OK.equals(sub.getStatus()) || TypeSub.PJ.equals(sub.getTypeSub())) {
         card.setStatus(ClubCardStatus.OK);
      }
      else {
         card.setStatus(ClubCardStatus.BLOCKED);
      }
      return card;
   }

   public ClubCard createCard(Holder holder, Subscription sub, Dependent dependent, boolean isHolder) {
      ClubCard card = new ClubCard();
      card.setSubscription(sub);
      card.setHolder(holder);
      card.setDependent(dependent);
      card.setIsCardOfHolder(isHolder);
      card.setToken(Strings.generateUniqueToken());
      card.setDateGenerated(LocalDateTime.now());
      if (SubscriptionStatus.OK.equals(sub.getStatus()) || TypeSub.PJ.equals(sub.getTypeSub())) {
         card.setStatus(ClubCardStatus.OK);
      }
      else {
         card.setStatus(ClubCardStatus.BLOCKED);
      }
      return card;
   }

   public ClubCard getByToken(String token) {
      if (StringUtils.isBlank(token)) {
         throw new ClubFlexException("Token inválido ou não informado.");
      }
      return clubCardRepository.findByToken(token);
   }

   public ClubCard getByDependentId(Long dependentId) {
      return clubCardRepository.findByDependentId(dependentId);
   }

   @Transactional
   public void save(ClubCard clubCard) {
      clubCardRepository.save(clubCard);

   }

   public Set<ClubCard> listLastClubCardsBySubscriptionId(Long subscriptionId) {
      Subscription sub = subscriptionRepository.findById(subscriptionId);

      if (sub == null) {
         throw new ClubFlexException("Assinatura inválida.");
      }

      // setando qrcode
      sub.getCards().forEach(c -> {
         c.setQrcode(QrCodeUtils.generateQRCodeAsBase64(urlQrCode.concat(c.getToken())));
      });

      return sub.getCards();
   }

   public List<ClubflexCardLite> listLastClubCardsLiteBySubscriptionId(Long subscriptionId) {
      List<ClubflexCardLite> ret = Lists.newArrayList();

      Set<ClubCard> cards = listLastClubCardsBySubscriptionId(subscriptionId);
      cards.forEach(card -> {
         if (ClubCardStatus.OK == card.getStatus()) {
            if (card.getIsCardOfHolder()) {
               ret.add(new ClubflexCardLite(card.getNumberOnCard(), card.getNameOnCard(), "Titular"));
            }
            else {
               ret.add(new ClubflexCardLite(card.getNumberOnCard(), card.getNameOnCard(), "Dependente"));
            }
         }
      });

      return ret;
   }

   public ClubCard findById(Long cardNumber) {
      return clubCardRepository.find(ClubCard.class, cardNumber);
   }

   public List<ClubCard> findByHolderId(Long holderId) {
      return clubCardRepository.findByHolderId(holderId);
   }

   public List<ClubCard> findByHolder(Long holderId) {
      return clubCardRepository.findByHolderId(holderId);
   }

   @Transactional
   public void deleteClubCard(Long clubeCardId) {
      ClubCard clubCard = findById(clubeCardId);
      try {
         clubCardRepository.delete(clubCard);
      }
      catch (Exception exc) {
         System.out.println(exc);
      }
   }

   @Transactional
   public void deleteCardById(Long id) {
      clubCardRepository.deleteCardById(id);
   }

   public ClubCard findByHolderSubIsHolder(Holder holder, Subscription sub) {
      return clubCardRepository.findByHolderSubIsHolder(holder, sub);
   }
}
