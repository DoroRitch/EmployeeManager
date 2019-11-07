package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.CoLang;

@Repository
public interface CoLangRepository extends JpaRepository<CoLang, Integer>{

}
