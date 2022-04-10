package com.webScraping.demo.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class RequestDTO {
	
	private @Id @GeneratedValue Long id;
	private String keyWord;

	public RequestDTO(String keyWord) {
		super();
		this.keyWord = keyWord;
	}

	public RequestDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	
	

}
