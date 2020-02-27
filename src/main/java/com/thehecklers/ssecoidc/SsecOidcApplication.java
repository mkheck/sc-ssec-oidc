package com.thehecklers.ssecoidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@SpringBootApplication
public class SsecOidcApplication {
	@Bean
	WebClient client(ClientRegistrationRepository regRepo,
					 OAuth2AuthorizedClientRepository cliRepo) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction fFunc =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(
						regRepo,
						cliRepo
				);

		fFunc.setDefaultOAuth2AuthorizedClient(true);

		return WebClient.builder()
				.baseUrl("http://localhost:8081/resources")
				.apply(fFunc.oauth2Configuration())
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(SsecOidcApplication.class, args);
	}

}

@RestController
class OidcController {
	private final WebClient client;

	public OidcController(WebClient client) {
		this.client = client;
	}

	@GetMapping("/")
	String hello() {
		return "Bonjour Montreal!";
	}

	@GetMapping("/something")
	String getSomethingFromRServer() {
		return client.get()
				.uri("/something")
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}

	@GetMapping("/claims")
	Map getClaimsFromRServer() {
		return client.get()
				.uri("/claims")
				.retrieve()
				.bodyToMono(Map.class)
				.block();
	}

	@GetMapping("/email")
	String getSubjectFromRServer() {
		return client.get()
				.uri("/email")
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}
}