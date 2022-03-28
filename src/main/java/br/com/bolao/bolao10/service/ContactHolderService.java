package br.com.segmedic.clubflex.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.ContactHolder;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.repository.ContactHolderRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ContactHolderService {

	@Autowired
	private ContactHolderRepository contactHolderRepository;
	
	@Transactional
	public ContactHolder save(ContactHolder contact) {
		if(StringUtils.isBlank(contact.getDescription())) {
			throw new ClubFlexException("Dados do contato não informado");
		}
		contact.setHolder(contactHolderRepository.find(Holder.class, contact.getHolder().getId()));
		contact.setUser(contactHolderRepository.find(User.class, contact.getUser().getId()));
		contact.setDateTimeContact(LocalDateTime.now());
		return contactHolderRepository.persist(contact);
	}

	public ContactHolder findById(Long id) {
		return contactHolderRepository.find(ContactHolder.class, id);
	}

	public List<ContactHolder> listByHolderId(Long holderId) {
		return contactHolderRepository.listByHolderId(holderId);
	}

}
