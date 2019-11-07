package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Product;
import com.employeemanage.repository.ProductRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	public List<Product> findAll() {
		return productRepository.findAllByOrderByIdAsc();
	}

	public Optional<Product> findById(Integer id) {
		return productRepository.findById(id);
	}

	public void save(Product product) {

		productRepository.save(product);
	}
}
