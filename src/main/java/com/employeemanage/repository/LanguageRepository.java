package com.employeemanage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer>{

	public List<Language> findByIdIsNotNullOrderByIdAsc();
}
