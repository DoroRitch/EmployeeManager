package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_lang")
@Getter
@Setter
public class EmpLang {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "employee_id", nullable = false)
	@Min(1)
	@Max(9999)
	private Integer empId;

	@Column(name = "language_id", nullable = false)
	@Min(1)
	@Max(9999)
	private Integer langId;
}
