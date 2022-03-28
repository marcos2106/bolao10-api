
package br.com.segmedic.clubflex.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.Reason;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.repository.ReasonRepository;
import br.com.segmedic.clubflex.repository.SubscriptionRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReasonService {

   @Autowired
   private ReasonRepository reasonRepository;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   public List<Reason> findByDescription(String descricao) {
      if (StringUtils.isBlank(descricao)) {
         throw new ClubFlexException("Descrição não informada.");
      }
      return reasonRepository.findByDescription(descricao);
   }

   public List<Reason> listAll() {
      return reasonRepository.listAll();
   }

   public List<Reason> listAllActive() {
      return reasonRepository.listAllActive();
   }

   @Transactional
   public Reason save(Reason reason) {

      if (StringUtils.isBlank(reason.getDescription())) {
         throw new ClubFlexException("Descrição não informada.");
      }

      Reason reasonObj = new Reason();
      if (reason.getId() != null) {
         reasonObj = reasonRepository.findById(reason.getId());
         reasonObj.setId(reason.getId());
      }
      reasonObj.setDescription(reason.getDescription());
      reasonObj.setIsActive(reason.getIsActive());
      return reasonRepository.save(reasonObj);
   }

   @Transactional
   public void deleteReason(Long idReason) {
      if (idReason == null) {
         throw new ClubFlexException("Motivo não selecionado.");
      }

      List<Subscription> findByReasonId = subscriptionRepository.findByReasonId(idReason);
      if (findByReasonId.size() > 0) {
         throw new ClubFlexException("Motivo não pode ser excluído, pois há assinatura utilizando.");
      }

      Reason reasonObj = reasonRepository.findById(idReason);

      if (reasonObj == null) {
         throw new ClubFlexException("Motivo não encontrado.");
      }
      reasonRepository.deleteReason(reasonObj);
   }

   public Reason findById(Long idReason) {
      if (idReason == null) {
         throw new ClubFlexException("Motivo não selecionado.");
      }

      Reason reason = reasonRepository.findById(idReason);

      if (reason == null) {
         throw new ClubFlexException("Motivo não encontrado.");
      }

      return reason;
   }
}
