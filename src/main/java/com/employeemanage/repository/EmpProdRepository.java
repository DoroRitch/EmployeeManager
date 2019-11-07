package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.EmpProd;

@Repository
public interface EmpProdRepository extends JpaRepository<EmpProd, Integer>{

}
