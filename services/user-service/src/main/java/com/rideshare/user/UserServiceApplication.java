package com.rideshare.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@SpringBootApplication
@Slf4j
public class UserServiceApplication {

	public static void main(String[] args) {
		log.info("Starting User Service Application...");
		try {
			SpringApplication application = new SpringApplication(UserServiceApplication.class);
			application.run(args);
		} catch (Exception e) {
			log.error("Failed to start User Service Application: {}", e.getMessage(), e);
			System.exit(1);
		}
	}

	@Component
	public static class ApplicationEventListener {

		private final Environment environment;

		public ApplicationEventListener(Environment environment) {
			this.environment = environment;
		}

		@EventListener(ApplicationReadyEvent.class)
		public void onApplicationReady() {
			String serverPort = environment.getProperty("server.port", "8080");
			String contextPath = environment.getProperty("server.servlet.context-path", "");
			String activeProfiles = String.join(", ", environment.getActiveProfiles());
			
			log.info("==========================================");
			log.info(" User Service Application Started Successfully");
			log.info(" Server running on port: {}", serverPort);
			log.info(" Context path: {}", contextPath.isEmpty() ? "/" : contextPath);
			log.info(" Active profiles: {}", activeProfiles.isEmpty() ? "default" : activeProfiles);
			log.info(" Database URL: {}", environment.getProperty("spring.datasource.url"));
			log.info(" API Base URL: http://localhost:{}{}/api/auth", serverPort, contextPath);
			log.info("==========================================");
		}

		@PreDestroy
		public void onShutdown() {
			log.info("User Service Application is shutting down...");
		}
	}
}
