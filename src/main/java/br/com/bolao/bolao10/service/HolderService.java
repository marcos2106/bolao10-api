
package br.com.bolao.bolao10.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Holder;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.model.HolderFarma;
import br.com.bolao.bolao10.model.HolderFilter;
import br.com.bolao.bolao10.model.HolderStatus;
import br.com.bolao.bolao10.repository.HolderRepository;
import br.com.bolao.bolao10.support.Strings;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class HolderService {

   @Autowired
   private HolderRepository holderRepository;


   public Holder findByCpfCnpj(String cpfCnpj) {
      if (StringUtils.isBlank(cpfCnpj)) {
         throw new Bolao10Exception("CPF/CNPJ não informado");
      }
      return holderRepository.findByCpfCnpj(Strings.removeNoNumericChars(cpfCnpj));
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