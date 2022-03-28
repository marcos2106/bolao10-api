
package br.com.segmedic.clubflex.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Sets;
import br.com.segmedic.clubflex.domain.Pet;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.PetEnum;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.model.CreateSubscriptionRequest;
import br.com.segmedic.clubflex.model.PetRequest;
import br.com.segmedic.clubflex.repository.PetRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PetService {

   @Autowired
   private PetRepository petRepository;

   @Autowired
   private SubscriptionService subscriptionService;

   @Transactional
   public void generatePets(Subscription sub, CreateSubscriptionRequest subscription) {

      if (subscription.getPets() != null) {
         sub.setPets(Sets.newConcurrentHashSet());
         try {
            subscription.getPets().forEach(p -> {
               Pet pet = new Pet();
               pet.setQuantity(p.getQuantity());
               pet.setSubscription(sub);
               for (PetEnum pEnum : PetEnum.values()) {
                  if (pEnum.getDescribe().equalsIgnoreCase(p.getDescription())) {
                     pet.setPet(pEnum);
                     break;
                  }
               }
               sub.getPets().add(pet);
            });
         }
         catch (Exception e) {
            throw new ClubFlexException("Erro ao gerar os dados dos Pets.");
         }
      }
   }

   public List<Pet> findPetsBySub(Long subscriptionId) {

      return petRepository.findPetsBySub(subscriptionId);
   }

   @Transactional
   public void deletePet(Long petId) {

      Pet pet = petRepository.findById(petId);
      petRepository.delete(pet);
   }

   @Transactional
   public void addPet(PetRequest petRequest) {

      try {

         Pet pet;
         if (petRequest.getId() == null) {
            pet = new Pet();
         }
         else {
            pet = petRepository.findById(petRequest.getId());
         }
         pet.setQuantity(petRequest.getQuantity());

         Subscription sub = subscriptionService.findById(petRequest.getSubscriptionId());
         pet.setSubscription(sub);

         for (PetEnum pEnum : PetEnum.values()) {
            if (pEnum.getDescribe().equalsIgnoreCase(petRequest.getDescription())) {
               pet.setPet(pEnum);
               break;
            }
         }
         petRepository.merge(pet);
      }
      catch (Exception exc) {
         throw new ClubFlexException("Erro ao salvar o Pet.");
      }
   }

}
