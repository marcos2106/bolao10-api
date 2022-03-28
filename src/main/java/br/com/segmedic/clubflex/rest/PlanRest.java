
package br.com.segmedic.clubflex.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.Plan;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.PlanService;

@RestController
@RequestMapping("/plan")
public class PlanRest extends BaseRest {

   @Autowired
   private PlanService planService;

   @GetMapping(value = "/list/avaliable/site", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listAvaliableToSite() {
      return createObjectReturn(planService.listAvaliableToSite());
   }

   @GetMapping(value = "/list/active", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listAllActive() {
      return createObjectReturn(planService.listAllActive());
   }

   @GetMapping(value = "/list/active/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listAllActive(@PathVariable TypeSub type) {
      return createObjectReturn(planService.listAllActive(type));
   }

   @GetMapping(value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listAll() {
      return createObjectReturn(planService.listAll());
   }

   @GetMapping(value = "/list/all/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listAll(@PathVariable TypeSub type) {
      return createObjectReturn(planService.listAll(type));
   }

   @GetMapping(value = "/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getPLan(@PathVariable Long planId) {
      return createObjectReturn(planService.findById(planId));
   }

   @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> savePlan(@RequestBody Plan plan) {
      return createObjectReturn(planService.save(plan));
   }

   @PostMapping(value = "/save/paymenttype/{planId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> savePaymentType(@PathVariable Long planId, @RequestBody List<PaymentType> paymentsType) {
      planService.savePaymentTypes(planId, paymentsType);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/{planId}/list/payments", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listPayments(@PathVariable Long planId) {
      return createObjectReturn(planService.listAllPaymentsType(planId));
   }

   @GetMapping(value = "/{planId}/list/payments/object", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> listPaymentsObject(@PathVariable Long planId) {
      return createObjectReturn(planService.listAllPaymentsTypeObject(planId));
   }
}
