
package br.com.segmedic.clubflex.service;

import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.Card;
import br.com.segmedic.clubflex.repository.CreditCardRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CreditCardService {

   @Autowired
   private JWTService jwtService;

   @Autowired
   private CreditCardRepository creditCardRepository;

   @Autowired
   private UserService userService;

   @Autowired
   private RedeItauGatewayService redeGatewayItau;

   @Autowired
   private SubscriptionLogService subscriptionLogService;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   @Autowired
   private HolderService holderService;

   @Autowired
   private JmsTemplate queueChangeCreditCard;

   @Autowired
   private JmsTemplate queueCreditCardUpdate;

   @Transactional
   public CreditCard update(Card card, Boolean payOutDate, Long usuario) {
      Holder holder = holderService.findById(card.getHolderId());
      return save(card, null, holder, payOutDate, usuario);
   }

   @Transactional
   public Boolean updateRequest(Card card, Boolean payOutDate, Long usuario) {
      Holder holder = holderService.findById(card.getHolderId());
      return saveRequest(card, null, holder, payOutDate, usuario);
   }

   @Transactional
   public CreditCard update(Card card, PaymentType paymentType, Boolean payOutDate, Long usuario) {
      Holder holder = holderService.findById(card.getHolderId());
      return save(card, paymentType, holder, payOutDate, usuario);
   }

   @Transactional
   public Boolean saveRequest(Card card, PaymentType paymentType, Holder holder, Boolean payOutDate, Long usuario) {
      if (holder == null) {
         throw new ClubFlexException("Titular do cartão não informado.");
      }
      if (card == null) {
         throw new ClubFlexException("Cartão de crédito não informado.");
      }
      if (StringUtils.isBlank(card.getBrand())) {
         throw new ClubFlexException("Informe a bandeira do cartão.");
      }
      if (StringUtils.isBlank(card.getNumber())) {
         throw new ClubFlexException("Número do cartão não informado.");
      }
      if (StringUtils.isBlank(card.getValidate())) {
         throw new ClubFlexException("Validade do cartão não informada.");
      }
      if (StringUtils.isBlank(card.getName())) {
         throw new ClubFlexException("Informe o nome como está no cartão.");
      }
      if (StringUtils.isBlank(card.getSecurityCode())) {
         throw new ClubFlexException("Código se segurança do cartão não informado. Informe os 3 números do verso do cartão.");
      }

      CreditCard creditCard = findByHolderId(holder.getId());

      String finalCartaoAnterior = null;
      if (creditCard == null) {
         creditCard = new CreditCard();
      }
      else {

         if ((card.getNumber().substring(card.getNumber().length() - 4, card.getNumber().length())
                  .equalsIgnoreCase(creditCard.getFinalNumbers()))) {

            subscriptionLogService.generateLog(usuario, card.getIdSubscription(),
               "Tentativa de alteração de cartão já existente", SubscriptionLogAction.TENTATIVA_ALTERACAO_CARTAO);

            throw new ClubFlexException("Esse cartão de crédito já está cadastrado para esse cliente.");
         }

         finalCartaoAnterior = creditCard.getFinalNumbers();
      }

      creditCard.setCardHash(jwtService.createJwtToken(card));
      creditCard.setFinalNumbers(StringUtils.right(card.getNumber(), 4));
      creditCard.setBrand(card.getBrand());
      creditCard.setDateLastRecurrency(LocalDate.now());

      if (creditCard.getHolder() == null)
         creditCard.setHolder(holder);
      creditCard.setLastReturnCode("00");
      creditCard.setRecurrency(0l);

      Subscription sub = subscriptionRepository.getLastByHolderId(holder.getId());
      // Testando cartao antes de salvar

      boolean cardValidated = false;
      if (payOutDate) {

         // Testar cartao pelo tipo pre informado
         if (paymentType != null) {
            redeGatewayItau.test(creditCard, paymentType);
            cardValidated = true;
         }

         // Testar o cartao pelo tipo de pagamento da assinatura
         if (sub != null && !cardValidated && !SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
            redeGatewayItau.test(creditCard, sub.getPaymentType());
            cardValidated = true;
         }
      }

      try {
         creditCardRepository.save(creditCard);
         if (usuario != null && sub != null) {

            card.setIdSubscription(sub.getId());
            String descricao = "Alteração de Cartão";
            if (finalCartaoAnterior != null) {
               descricao += " - De: " + finalCartaoAnterior + " Para: " + creditCard.getFinalNumbers();
            }
            subscriptionLogService.generateLog(usuario, card.getIdSubscription(),
               descricao, SubscriptionLogAction.ATUALIZACAO_CARTAO_CREDITO);
         }
      }
      catch (Exception exc) {
         cardValidated = false;
      }
      return cardValidated;
   }

   @Transactional
   public CreditCard save(Card card, PaymentType paymentType, Holder holder, Boolean payOutDate, Long usuario) {
      if (holder == null) {
         throw new ClubFlexException("Titular do cartão não informado.");
      }
      if (card == null) {
         throw new ClubFlexException("Cartão de crédito não informado.");
      }
      if (StringUtils.isBlank(card.getBrand())) {
         throw new ClubFlexException("Informe a bandeira do cartão.");
      }
      if (StringUtils.isBlank(card.getNumber())) {
         throw new ClubFlexException("Número do cartão não informado.");
      }
      if (StringUtils.isBlank(card.getValidate())) {
         throw new ClubFlexException("Validade do cartão não informada.");
      }
      if (StringUtils.isBlank(card.getName())) {
         throw new ClubFlexException("Informe o nome como está no cartão.");
      }
      if (StringUtils.isBlank(card.getSecurityCode())) {
         throw new ClubFlexException("Código se segurança do cartão não informado. Informe os 3 números do verso do cartão.");
      }

      CreditCard creditCard = findByHolderId(holder.getId());
      String finalCartaoAnterior = null;
      if (creditCard == null) {
         creditCard = new CreditCard();
      }
      else {
         finalCartaoAnterior = creditCard.getFinalNumbers();
      }

      creditCard.setCardHash(jwtService.createJwtToken(card));
      creditCard.setFinalNumbers(StringUtils.right(card.getNumber(), 4));
      creditCard.setBrand(card.getBrand());
      if (creditCard.getHolder() == null)
         creditCard.setHolder(holder);
      creditCard.setDateLastRecurrency(LocalDate.now());
      creditCard.setLastReturnCode("00");
      creditCard.setRecurrency(0l);

      Subscription sub = subscriptionRepository.getLastByHolderId(holder.getId());
      // Testando cartao antes de salvar
      if (payOutDate) {
         boolean cardValidated = false;

         // Testar cartao pelo tipo pre informado
         if (paymentType != null) {
            redeGatewayItau.test(creditCard, paymentType);
            cardValidated = true;
         }

         // Testar o cartao pelo tipo de pagamento da assinatura
         if (sub != null && !cardValidated && !SubscriptionStatus.CANCELED.equals(sub.getStatus())) {
            redeGatewayItau.test(creditCard, sub.getPaymentType());
            cardValidated = true;
         }

         if (cardValidated) {
            // Se houve troca de cartão/cred/deb sinalizar sistema para efetuar quitação de parcelas vencidas.
            queueChangeCreditCard.convertAndSend(sub.getId());
         }
      }

      if (usuario != null && sub != null) {

         card.setIdSubscription(sub.getId());
         String descricao = "Alteração de Cartão";
         if (finalCartaoAnterior != null) {
            descricao += " - De: " + finalCartaoAnterior + " Para: " + creditCard.getFinalNumbers();
         }
         subscriptionLogService.generateLog(usuario, card.getIdSubscription(),
            descricao, SubscriptionLogAction.ATUALIZACAO_CARTAO_CREDITO);
      }

      return creditCardRepository.save(creditCard);
   }

   public void updateCreditCardQueue(CreditCard card) {
      queueCreditCardUpdate.convertAndSend(card);
   }

   public CreditCard findByHolderId(Long holderId) {
      return creditCardRepository.findByHolderId(holderId);
   }

   public List<CreditCard> listByHolderId(Long holderId) {
      return creditCardRepository.listByHolderId(holderId);
   }

   @Transactional
   public void update(CreditCard card) {
      creditCardRepository.save(card);
   }

   public CreditCard findByUserId(Long userId) {
      User user = userService.findUserById(userId);
      if (user == null) {
         throw new ClubFlexException("Usuário inválido.");
      }
      if (!UserProfile.HOLDER.equals(user.getProfile())) {
         throw new ClubFlexException("Somente TITULAR possui cartão de crédito.");
      }
      return findByHolderId(user.getHolder().getId());
   }

}
