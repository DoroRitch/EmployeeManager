package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Language;
import com.employeemanage.repository.LanguageRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class LanguageService {

	@Autowired
	LanguageRepository languageRepository;

	private EntityManager entityManager;

	public List<Language> findAll() {
		return languageRepository.findByIdIsNotNullOrderByIdAsc();
	}

	public Optional<Language> findById(Integer id) {
		return languageRepository.findById(id);
	}

	public void save(String name) {

		Query query = entityManager
				.createNativeQuery("insert into Language(name) values(?)");
		query.setParameter(1, name);
		query.executeUpdate();
	}
}
