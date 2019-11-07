package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Card;
import com.employeemanage.repository.CardRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class CardService {

	@Autowired
	CardRepository cardRepository;

	public List<Card> findAll() {
		return cardRepository.findAllByOrderByIdAsc();
	}

	public Optional<Card> findById(Integer id){
		return cardRepository.findById(id);
	}

	public void save(Card card) {

		cardRepository.save(card);
	}

}
