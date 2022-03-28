
package br.com.segmedic.clubflex.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.beust.jcommander.internal.Lists;
import br.com.segmedic.clubflex.domain.Plan;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.PaymentTypeObject;
import br.com.segmedic.clubflex.repository.PlanRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PlanService {

   @Autowired
   private PlanRepository planRepository;

   public List<Plan> listAvaliableToSite() {
      return planRepository.listAvaliableToSite();
   }

   public Plan findById(Long planId) {
      return planRepository.findById(planId);
   }

   public List<Plan> listAllActive() {
      return planRepository.listAllActive();
   }

   public List<Plan> listAllActive(TypeSub type) {
      return planRepository.listAllActive(type);
   }

   public List<Plan> listAll() {
      return planRepository.listAll();
   }

   public List<Plan> listAll(TypeSub type) {
      return planRepository.listAll(type);
   }

   @Transactional
   public Plan save(Plan plan) {
      if (StringUtils.isBlank(plan.getName())) {
         throw new ClubFlexException("Nome do plano não informado.");
      }
      // if (plan.getDaysToBlock() == null || plan.getDaysToBlock() <= 0) {
      // throw new ClubFlexException("Dias para bloqueio deve ser maior que zero.");
      // }
      if (plan.getId() == null) {
         return planRepository.persist(plan);
      }
      else {
         return planRepository.merge(plan);
      }
   }

   @Transactional
   public void savePaymentTypes(Long planId, List<PaymentType> paymentsType) {
      this.removeAllPaymentsType(planId);
      if (paymentsType != null && !paymentsType.isEmpty()) {
         paymentsType.forEach(type -> {
            this.addPaymentType(planId, type);
         });
      }
   }

   private void addPaymentType(Long planId, PaymentType type) {
      planRepository.addPaymentType(planId, type);
   }

   private void removeAllPaymentsType(Long planId) {
      planRepository.removeAllPaymentsType(planId);
   }

   public List<PaymentType> listAllPaymentsType(Long planId) {
      return planRepository.listAllPaymentsType(planId);
   }

   public List<PaymentTypeObject> listAllPaymentsTypeObject(Long planId) {
      List<PaymentTypeObject> list = Lists.newArrayList();
      planRepository.listAllPaymentsType(planId).forEach(type -> {
         list.add(new PaymentTypeObject(type));
      });
      return list;
   }
}
