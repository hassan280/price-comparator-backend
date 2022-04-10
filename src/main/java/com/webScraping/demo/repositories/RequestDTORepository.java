package com.webScraping.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webScraping.demo.model.RequestDTO;

public interface RequestDTORepository extends JpaRepository<RequestDTO,Long>{

	RequestDTO findByKeyWord(String keyWord);
}
