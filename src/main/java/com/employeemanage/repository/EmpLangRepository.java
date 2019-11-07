package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.EmpLang;

@Repository
public interface EmpLangRepository extends JpaRepository<EmpLang, Integer>{

}
