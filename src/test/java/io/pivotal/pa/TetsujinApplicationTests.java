package io.pivotal.pa;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
public class TetsujinApplicationTests {


	@Bean
	public MockWebServer mockServer() {
		return new MockWebServer();
	}

	@Bean
	public CFProperties cfProperties(MockWebServer mockServer) {
		return new CFProperties("admin", "admin",
				UriComponentsBuilder.newInstance()
						.scheme("http").host(mockServer.getHostName()).port(mockServer.getPort())
						.build().encode().toUriString()
		);
	}

	@Test
	public void contextLoads() {
	}

}
