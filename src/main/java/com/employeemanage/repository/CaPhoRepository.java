package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.CaPho;

@Repository
public interface CaPhoRepository extends JpaRepository<CaPho, Integer>{

}
