package com.webScraping.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webScraping.demo.model.User;
import com.webScraping.demo.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
       User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
               .orElseThrow(() ->
                       new UsernameNotFoundException("User not found with username or email:" + usernameOrEmail));
        return CustomUserDetails.build(user);
    }

    //private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
    //    return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    //}

}
