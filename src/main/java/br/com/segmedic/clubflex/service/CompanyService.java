package br.com.segmedic.clubflex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.Company;
import br.com.segmedic.clubflex.repository.CompanyRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;

	public List<Company> listAll() {
		return companyRepository.listAll();
	}
	
	public Company findById(Long id) {
		return companyRepository.find(Company.class, id);
	}
}
