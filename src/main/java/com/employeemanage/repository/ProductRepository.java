package com.employeemanage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	public List<Product> findAllByOrderByIdAsc();

	public Optional<Product> findById(Integer id);
}
