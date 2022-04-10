package com.webScraping.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webScraping.demo.model.LoginDto;
import com.webScraping.demo.model.ResponseDTO;
import com.webScraping.demo.model.SignUpDto;
import com.webScraping.demo.model.User;
import com.webScraping.demo.repositories.ResponseDTORepository;
import com.webScraping.demo.repositories.UserRepository;
import com.webScraping.demo.service.CustomUserDetails;
import com.webScraping.demo.service.ScraperService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import securityConfiguration.JwtUtils;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Set;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/")
public class ScraperController {

	  @Autowired
	  AuthenticationManager authenticationManager;
	  @Autowired
	  UserRepository userRepository;
	  @Autowired
	  PasswordEncoder encoder;
	  @Autowired
	  JwtUtils jwtUtils;
	
	@Autowired
    ScraperService scraperService;
	@Autowired
	ResponseDTORepository re;

    @GetMapping(path = "/{vehicleModel}")
    public Set<ResponseDTO> getVehicleByModel(@PathVariable String vehicleModel) {
        return  scraperService.getVehicleByModel(vehicleModel);
    }
    
    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpRequest) {
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        return ResponseEntity.badRequest().body("Error: Username is already in use!");
      }
      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest().body("Error: Email is already in use!");
      }
      // Create new user's account
      User user = new User(signUpRequest.getUsername(),
                           encoder.encode(signUpRequest.getPassword()),
                           signUpRequest.getEmail());
      userRepository.save(user);
      return ResponseEntity.ok("User registered successfully!");
    }
    
    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginRequest) {
      Authentication authentication = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      System.out.println(userDetails.getEmail());
      ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
          .body(userDetails);
    }
    
    @PostMapping("/auth/signout")
    public ResponseEntity<?> logoutUser() {
      ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body("You've been signed out!");
    }
    
    @GetMapping("api/users/{user_id}/products")
    //@PreAuthorize("authentication.principal.equals(user_id)")
    public ResponseEntity<?> getUserProducts(@PathVariable("user_id") Long userId, Principal principal){
    	
    	User user1=userRepository.findById(userId).get();
    	return ResponseEntity.ok()
    	          .body(user1.getProducts());
    	    }
    
    @PatchMapping("api/users/{user_id}/products")
    public ResponseEntity<?> addUserProduct(@RequestBody ResponseDTO responseDTO,
    		@PathVariable("user_id") Long userId, Principal principal){
    	
    	User user1=userRepository.findById(userId).get();
    	user1.getProducts().add(responseDTO);
    	userRepository.save(user1); 
    	return ResponseEntity.ok()
    	          .body(user1);
    	    }
    
    
    
    
}
