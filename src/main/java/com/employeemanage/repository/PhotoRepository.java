package com.employeemanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employeemanage.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer>{

}
