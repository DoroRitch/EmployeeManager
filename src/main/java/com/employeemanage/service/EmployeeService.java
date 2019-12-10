package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Employee;
import com.employeemanage.repository.EmployeeRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class EmployeeService {

	private EntityManager entityManager;

	private Query query;

	@Autowired
	EmployeeRepository employeeRepository;

	public List<Employee> findAll() {
		return employeeRepository.findAllByOrderByIdAsc();
	}

	public Optional<Employee> findById(Integer id) {
		return employeeRepository.findById(id);
	}

	public void save(Employee employee) {

		employeeRepository.save(employee);
	}

//	企業詳細ページに派遣されている社員のリストを表示するために検索
	@SuppressWarnings("unchecked")
	public List<Employee> dispatchedEmployee(Integer id) {

		query = entityManager.createNativeQuery("select * from Employee where resident_id = ? ", Employee.class);
		query.setParameter(1, id);
		return query.getResultList();
	}

}
