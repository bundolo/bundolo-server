package org.bundolo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.DelegatingFilterProxy;

//@ComponentScan
@EnableAutoConfiguration
// @Configuration
@ImportResource("/applicationContext.xml")
public class Application {

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
		if (StringUtils.isBlank(System.getProperty("env"))) {
			System.setProperty("env", "dev");
		}
		System.out.println("bundolo " + System.getProperty("env") + " startup at " + new Date());
		SpringApplication application = new SpringApplication(Application.class);
		application.setShowBanner(false);
		ConfigurableApplicationContext applicationContext = application.run(args);

		// MailingUtils mailingUtils = (MailingUtils)
		// applicationContext.getBean("mailingUtils");
		// mailingUtils.newsletterSender();
	}
}