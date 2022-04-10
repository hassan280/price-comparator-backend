package com.webScraping.demo.service;

import java.util.Set;


import com.webScraping.demo.model.ResponseDTO;

public interface ScraperService {

	Set<ResponseDTO> getVehicleByModel(String vehicleModel);
	
	
}
