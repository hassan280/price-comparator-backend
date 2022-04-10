package securityConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.webScraping.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
    private CustomUserDetailsService userDetailsService;

	@Autowired
	  private AuthEntryPointJwt unauthorizedHandler;
	@Bean
	  public AuthTokenFilter authenticationJwtTokenFilter() {
	    return new AuthTokenFilter();
	  }
	
    
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/h2-console/**");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.cors().and().csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests().antMatchers("/**").permitAll()
        .antMatchers("/api/**").authenticated()
        .anyRequest().authenticated();
      http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
      
        /*http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2-console").permitAll()
                .antMatchers("/auth").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();*/
        
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception 
    {
        //auth.parentAuthenticationManager(authenticationManagerBean()).userDetailsService(userDetailsService);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    
}
