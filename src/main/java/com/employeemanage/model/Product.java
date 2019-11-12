package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.employeemanage.valid.Byte;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "Product")
@Getter
@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	@NotBlank
	@Byte(min = 1, max = 255)
	private String name;

	@Column(name = "description")
	@NotBlank
	@Byte(min = 1, max = 500)
	private String description;
}
