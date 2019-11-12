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

import com.employeemanage.valid.Byte;

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
	@Byte(min = 1, max = 50)
	private String name;

	@Column(name = "position")
	@Byte(min = 0, max = 50)
	private String position;

	@Column(name = "department")
	@Byte(min = 0, max = 50)
	private String department;

	@Column(name = "phone_number")
	@NotBlank
	@Byte(min = 1, max = 50)
	private String phoneNumber;

	@Column(name = "address")
	@NotBlank
	@Byte(min = 1, max = 510)
	private String address;

	@Column(name = "resident_id")
	@NotNull
	@Min(0)
	private Integer residentId;

}
