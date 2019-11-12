package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.employeemanage.valid.Byte;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Card")
@Getter
@Setter
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	@NotBlank
	@Byte(min = 1, max = 50)
	private String name;

	@Column(name = "phone_number")
	@NotBlank
	@Byte(min = 1, max = 50)
	private String phoneNumber;

	@Column(name = "position", nullable = true)
	@Byte(min = 0, max = 50)
	private String position;

	@Column(name = "department", nullable = true)
	@Byte(min = 0, max = 50)
	private String department;

	@Column(name = "company_id")
	@NotNull
	private Integer companyId;

}
