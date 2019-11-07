package com.employeemanage.service;

import java.util.List;
import java.util.Optional;

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

}
