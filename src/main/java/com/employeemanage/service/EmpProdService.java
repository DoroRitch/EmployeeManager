package com.employeemanage.service;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.EmpProd;
import com.employeemanage.repository.EmpProdRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class EmpProdService {

	@Autowired
	EmpProdRepository empProdRepository;

	private EntityManager entityManager;

	private Query query;

	public List<EmpProd> findAll() {
		return empProdRepository.findAll();
	}

	@SuppressWarnings("unchecked")
	public List<EmpProd> findByEmployeeIdOrderByIdAsc(Integer empId) {

		query = entityManager
				.createNativeQuery("select * from emp_prod"
						+ " where employee_id = ? order by id asc", EmpProd.class);
		query.setParameter(1, empId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EmpProd> findByProductIdOrderByIdAsc(Integer prodId) {

		query = entityManager
				.createNativeQuery("select * from emp_prod "
						+ "where product_id = ? order by id asc", EmpProd.class);
		query.setParameter(1, prodId);
		return query.getResultList();
	}

	public EmpProd findByEmpIdAndProdId(Integer empId, Integer prodId) {

		query = entityManager
				.createNativeQuery("select * from emp_prod "
						+ "where employee_id = ? and product_id = ?", EmpProd.class);
		query.setParameter(1, empId)
			.setParameter(2, prodId);
		try {
			Object empProd = query.getSingleResult();

			return (EmpProd)empProd;
		} catch(NoResultException e) {

			return null;
		}

	}

	public void save(EmpProd empProd) {

		empProdRepository.save(empProd);
	}

	public void delete(Integer empId, Integer prodId) {

		query = entityManager.createNativeQuery("delete from emp_prod"
				+ " where employee_id = ? and product_id = ?", EmpProd.class);
		query.setParameter(1, empId)
			.setParameter(2, prodId);
		query.executeUpdate();
	}
}
