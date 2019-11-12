package com.employeemanage.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employeemanage.model.Photo;
import com.employeemanage.repository.PhotoRepository;

import lombok.Setter;

@Service
@Transactional
@Setter
public class PhotoService {

	@Autowired
	PhotoRepository photoRepository;

	private EntityManager entityManager;

	private Query query;

	public Photo findByLatestOne() {

		query = entityManager
				.createNativeQuery("select * from ( select * from photo order by id desc ) "
						+ "where rownum <=1", Photo.class);
		return (Photo)query.getSingleResult();
	}

	public Photo findById(Integer id) {

		try {
			query = entityManager.createNativeQuery("select * from photo "
					+ "where id = ?", Photo.class);
			query.setParameter(1, id);
			return (Photo)query.getSingleResult();
		} catch (NoResultException e) {

			return null;
		}
	}

	public void save(Photo photo) {
		photoRepository.save(photo);
	}
}
