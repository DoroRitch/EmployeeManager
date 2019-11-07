package com.employeemanage.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.CoProd;
import com.employeemanage.repository.CoProdRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class CoProdService {

	@Autowired
	CoProdRepository coProdRepository;

	private EntityManager entityManager;

	Query query;

	public List<CoProd> findAll() {
		return coProdRepository.findAll();
	}

	@SuppressWarnings("unchecked")
	public List<CoProd> findByCompanyIdOrderByIdAsc(Integer comId) {

		query = entityManager
				.createNativeQuery("select * from co_prod "
						+ "where company_id = ? order by id asc", CoProd.class);
		query.setParameter(1, comId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CoProd> findByProductIdOrderByIdAsc(Integer prodId) {

		query = entityManager
				.createNativeQuery("select * from co_prod "
						+ "where product_id = ? order by id asc", CoProd.class);
		query.setParameter(1,  prodId);
		return query.getResultList();
	}

	public CoProd findByComIdAndProdId(Integer comId, Integer prodId) {

		query = entityManager
				.createNativeQuery("select * from co_prod "
						+ "where company_id = ? and product_id = ?", CoProd.class);
		query.setParameter(1,  comId)
			.setParameter(2, prodId);
		try {
			Object coProd = query.getSingleResult();

			return (CoProd)coProd;
		} catch (NoResultException e) {

			return null;
		}
	}


	public void save (CoProd coProd) {
		coProdRepository.save(coProd);
	}
}
