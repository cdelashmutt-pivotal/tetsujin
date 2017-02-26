package io.pivotal.pa.domain;

/**
 * A simple request object for creating a single student.
 *
 * Created by cdelashmutt on 2/23/17.
 */
public class UserProvisioningRequest {
	private String email;
	private String password;
	private String org;

	public UserProvisioningRequest() {
	}

	public UserProvisioningRequest(String email, String password, String org) {
		this.email = email;
		this.password = password;
		this.org = org;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}
}
