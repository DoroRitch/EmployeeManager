package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_prod")
@Getter
@Setter
public class EmpProd {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "employee_id", nullable = false)
	@Min(1)
	@Max(9999)
	private Integer empId;

	@Column(name = "product_id", nullable = false)
	@Min(1)
	@Max(9999)
	private Integer prodId;

	@Column(name = "skill", nullable = false)
	@Size(min = 1, max = 510)
	private String skill;

	@Column(name = "type", nullable = false)
	@Size(min = 1, max = 50)
	private String type;
}
