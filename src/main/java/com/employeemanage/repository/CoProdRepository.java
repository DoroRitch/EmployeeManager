package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.CoProd;

@Repository
public interface CoProdRepository extends JpaRepository<CoProd, Integer>{

}
