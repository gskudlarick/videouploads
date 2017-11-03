package com.ges.video.view.mvc;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Value("${api.mysql.connection.string}")
	private String dbConnString;
	
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/dummy").setViewName("dummy");
        registry.addViewController("/error").setViewName("error");
    }
    
    /*
    @Bean(name = "dataSource")
	public DriverManagerDataSource dataSource() {
    	

    		System.out.println("*****logging into DB for Creds");
	    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    //driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    //driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/vz_db"); // cloud digital ocean
	    driverManagerDataSource.setUrl(dbConnString); 
	    
	    
	    //driverManagerDataSource.setUrl("jdbc:mysql://localhost:8889/vz_db"); // local mac
	    driverManagerDataSource.setUsername("api");
	    driverManagerDataSource.setPassword("api100");
	    return driverManagerDataSource;
	}
	*/
    
    // spring mvc enable CORS , and also add in FileUploadController.jav using @CrossOrigin
    //https://spring.io/blog/2015/06/08/cors-support-in-spring-framework
    /*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }*/
    
    // Basic i18n
    @Bean
    public LocaleResolver localeResolver(){
         SessionLocaleResolver localeResolver = new SessionLocaleResolver();
         localeResolver.setDefaultLocale(Locale.US);
         return  localeResolver;
     }
    
    // Add interceptor to catch on URL.  http://alpha.com/home?lang=fr
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    
    

}
