package com.webScraping.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webScraping.demo.model.ResponseDTO;

public interface ResponseDTORepository extends JpaRepository<ResponseDTO,Long>{

	//Set<ResponseDTO> findByRequest(RequestDTO request);
	
	ResponseDTO findByTitle(String title);
	ResponseDTO findByUrl(String url);
}
