package io.pivotal.pa;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * Main application launch point
 */
@SuppressWarnings("WeakerAccess")
@SpringBootApplication
@EnableConfigurationProperties(CFProperties.class)
public class TetsujinApplication {

	public static void main(String[] args) {
		SpringApplication.run(TetsujinApplication.class, args);
	}

	@Bean
	DefaultConnectionContext connectionContext(@Autowired CFProperties properties) {
		return DefaultConnectionContext.builder()
				.apiHost(properties.getApiHost())
				.skipSslValidation(true)
				.build();
	}

	@Bean
	PasswordGrantTokenProvider tokenProvider(@Autowired CFProperties properties) {
		return PasswordGrantTokenProvider.builder()
				.password(properties.getPassword())
				.username(properties.getUsername())
				.build();
	}

	@Bean
	ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
		return ReactorCloudFoundryClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	ReactorUaaClient uaaClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
		return ReactorUaaClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

}
