package com.employeemanage.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.EmpLang;
import com.employeemanage.repository.EmpLangRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class EmpLangService {

	@Autowired
	EmpLangRepository empLangRepository;

	private EntityManager entityManager;

	private Query query;

	public List<EmpLang> findAll() {
		return empLangRepository.findAll();
	}

	@SuppressWarnings("unchecked")
	public List<EmpLang> findByEmployeeIdOrderByIdAsc(Integer id) {

		query = entityManager
				.createNativeQuery("select * from emp_lang where employee_id = ? "
						+ "order by language_id asc", EmpLang.class);
		query.setParameter(1, id);
		return query.getResultList();
	}

	public EmpLang findByEmpIdAndLangId(Integer empId, Integer langId) {

		query = entityManager.createNativeQuery("select * from emp_lang"
				+ " where employee_id = ? and language_id = ?", EmpLang.class);

		try {
			Object empLang = query.setParameter(1, empId)
					.setParameter(2, langId).getSingleResult();

			return (EmpLang)empLang;
		} catch (NoResultException e) {

			return null;
		}
	}

	public void save(EmpLang empLang) {

		empLangRepository.save(empLang);
	}

	public void delete(Integer empId, Integer langId) {

		query = entityManager.createNativeQuery("delete from emp_lang"
				+ " where employee_id = ? and language_id = ?", EmpLang.class);
		query.setParameter(1, empId)
			.setParameter(2, langId);
		query.executeUpdate();

	}
}
