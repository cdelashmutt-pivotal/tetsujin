package io.pivotal.pa;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the application.
 *
 * Created by cdelashmutt on 2/22/17.
 */
@ConfigurationProperties(prefix = "cf")
public class CFProperties {
	private String username;
	private String password;
	private String apiHost;

	public CFProperties() {
	}

	public CFProperties(String username, String password, String apiHost) {
		this.username = username;
		this.password = password;
		this.apiHost = apiHost;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@SuppressWarnings("WeakerAccess")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@SuppressWarnings("WeakerAccess")
	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}
}
