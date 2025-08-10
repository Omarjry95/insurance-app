package com.cms.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cms.user.constants.DatabaseConstant.UserRole;
import com.cms.user.constants.DatabaseConstant.UserStatus;
import com.cms.user.entity.User;
import com.cms.user.service.UserService;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class CmsUserServiceApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(CmsUserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		User irda = this.userService.getUserByEmailAndRole("irda@gmail.com", UserRole.IRDA.value());
		User insurance_company = this.userService.getUserByEmailAndRole("company@gmail.com", UserRole.COMPANY.value());
		User customer = this.userService.getUserByEmailAndRole("customer@gmail.com", UserRole.CUSTOMER.value());
		
		if (irda == null) {
			System.out.println("IRDA user not found");
			
			User default_irda = new User();
			default_irda.setEmailId("irda@gmail.com");
			default_irda.setPassword(passwordEncoder.encode("123456"));
			default_irda.setRole(UserRole.IRDA.value());
			default_irda.setStatus(UserStatus.ACTIVE.value());
			
			this.userService.registerUser(default_irda);
			
			System.out.println("default IRDA user added succesful!!!");
		}
		
		if (insurance_company == null) {
			System.out.println("insurance_company user not found");
			
			User default_irda = new User();
			default_irda.setEmailId("company@gmail.com");
			default_irda.setPassword(passwordEncoder.encode("123456"));
			default_irda.setRole(UserRole.COMPANY.value());
			default_irda.setStatus(UserStatus.ACTIVE.value());
			
			this.userService.registerUser(default_irda);
			
			System.out.println("default insurance_company user added succesful!!!");
		}

		if (customer == null) {
			System.out.println("customer user not found");

			User default_customer = new User();
			default_customer.setEmailId("customer@gmail.com");
			default_customer.setPassword(passwordEncoder.encode("123456"));
			default_customer.setRole(UserRole.CUSTOMER.value());
			default_customer.setStatus(UserStatus.ACTIVE.value());

			this.userService.registerUser(default_customer);

			System.out.println("default customer user added successfully!!!");
		}
	}
}
