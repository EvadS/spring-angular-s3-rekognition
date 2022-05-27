package com.se.sample.repository;


import com.se.sample.domain.Image;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaSpecificationExecutor<Image>, PagingAndSortingRepository<Image, Long> {

}