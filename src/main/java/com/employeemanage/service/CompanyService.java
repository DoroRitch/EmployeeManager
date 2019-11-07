package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Company;
import com.employeemanage.repository.CompanyRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class CompanyService {

	@Autowired
	CompanyRepository companyRepository;

	public List<Company> findAll() {
		return companyRepository.findAllByOrderByIdAsc();
	}

	public Optional<Company> findById(Integer id) {
		return companyRepository.findById(id);
	}

	public void save(Company company) {

		companyRepository.save(company);
	}
}
