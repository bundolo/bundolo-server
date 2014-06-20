package org.bundolo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.DelegatingFilterProxy;

//@ComponentScan
@EnableAutoConfiguration
// @Configuration
@ImportResource("/applicationContext.xml")
public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());

    @Bean
    public FilterRegistrationBean springSecurityFilterChain() {
	FilterRegistrationBean registrationBean = new FilterRegistrationBean();
	DelegatingFilterProxy securityFilter = new DelegatingFilterProxy();
	registrationBean.setFilter(securityFilter);
	registrationBean.setOrder(1);
	List<String> urlPatterns = new ArrayList<String>();
	urlPatterns.add("/**");
	registrationBean.setUrlPatterns(urlPatterns);
	return registrationBean;
    }

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }
}