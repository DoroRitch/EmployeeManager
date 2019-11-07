package com.employeemanage.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.CaPho;
import com.employeemanage.repository.CaPhoRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class CaPhoService {

	@Autowired
	CaPhoRepository caPhoRepository;

	private EntityManager entityManager;

	private Query query;

	public CaPho findByCardId(Integer id) {

		try {
			query = entityManager
					.createNativeQuery("select * from ca_pho where "
							+ "card_id = ?", CaPho.class);
			query.setParameter(1, id);
			return (CaPho)query.getSingleResult();
		} catch (NoResultException e) {

			return null;
		}
	}

	public void save(CaPho caPho) {
		caPhoRepository.save(caPho);
	}
}
