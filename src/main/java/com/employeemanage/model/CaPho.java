package com.employeemanage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Ca_pho")
@Getter
@Setter
public class CaPho {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "card_id", nullable = false)
	private Integer cardId;

	@Column(name = "photo_id", nullable = false)
	private Integer photoId;
}
