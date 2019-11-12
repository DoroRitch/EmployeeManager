package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

import com.employeemanage.valid.Byte;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Company")
@Getter
@Setter
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	@NotBlank
	@Byte(min = 1, max = 50)
	private String name;

	@Column(name = "address")
	@NotBlank
	@Byte(min = 1, max = 510)
	private String address;

	@Column(name = "phone_number")
	@NotBlank
	@Byte(min = 1, max = 50)
	private String phoneNumber;

	@Column(name = "url")
	@URL
	@Byte(min = 0, max = 510)
	private String url;


}
