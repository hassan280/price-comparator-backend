package com.webScraping.demo.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class User {
	
	private @Id @GeneratedValue Long id;
	
	@Column(nullable = false,length = 20)
	private String username;
	
	@Column(nullable = false, length = 64)
	private String password;
	
	@Column(nullable = false, unique = true, length = 65)
	private String email;
	
	@OneToMany
    private Set<ResponseDTO> products;

	public User() {
		super();
	}

	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<ResponseDTO> getProducts() {
		return products;
	}

	public void setProducts(Set<ResponseDTO> products) {
		this.products = products;
	}
	
	

}
