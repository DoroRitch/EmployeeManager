package com.employeemanage.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.CoLang;
import com.employeemanage.repository.CoLangRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class CoLangService {

	@Autowired
	CoLangRepository coLangRepository;

	private EntityManager entityManager;

	Query query;

	@SuppressWarnings("unchecked")
	public List<CoLang> findByCompanyIdOrderByIdAsc(Integer comId) {

		query = entityManager
				.createNativeQuery("select * from co_lang "
						+ "where company_id = ? order by language_id asc", CoLang.class);
		query.setParameter(1, comId);
		return query.getResultList();
	}

	public CoLang findByComIdAndLangId(Integer comId, Integer langId) {

		query = entityManager
				.createNativeQuery("select * from co_lang "
						+ "where company_id = ? and language_id = ?", CoLang.class);

		try {

			Object coLang = query.setParameter(1, comId)
					.setParameter(2, langId).getSingleResult();

			return (CoLang)coLang;
		} catch (NoResultException e) {

			return null;
		}
	}

	public void save(CoLang coLang) {
		coLangRepository.save(coLang);
	}

	public void delete(Integer comId, Integer langId) {

		query = entityManager
				.createNativeQuery("delete from co_lang "
						+ "where company_id = ? and language_id = ?", CoLang.class);
		query.setParameter(1, comId)
			.setParameter(2, langId);
		query.executeUpdate();
	}
}
