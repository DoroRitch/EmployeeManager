package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.employeemanage.valid.Byte;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "Photo")
@Getter
@Setter
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "file_name", nullable = false)
	@Byte(max = 510)
	private String fileName;
}
