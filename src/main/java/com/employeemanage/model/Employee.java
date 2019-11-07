package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Employee")
@Getter
@Setter
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	@NotBlank
	@Size(min = 1, max = 50)
	private String name;

	@Column(name = "position")
	@Size(min = 0, max = 50)
	private String position;

	@Column(name = "department")
	@Size(min = 0, max = 50)
	private String department;

	@Column(name = "phone_number")
	@NotBlank
	@Size(min = 1, max = 50)
	private String phoneNumber;

	@Column(name = "address")
	@NotBlank
	@Size(min = 1, max = 510)
	private String address;

	@Column(name = "resident_id")
	@NotNull
	@Min(0)
	private Integer residentId;

}
