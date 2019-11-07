package com.employeemanage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer>{

	public List<Card> findAllByOrderByIdAsc();

	public Optional<Card> findById(Integer id);
}
