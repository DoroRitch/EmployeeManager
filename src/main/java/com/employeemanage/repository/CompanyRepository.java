package com.employeemanage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer>{

	public Optional<Company> findById(Integer id);
	public List<Company> findAllByOrderByIdAsc();
}
