package io.pivotal.pa.domain;

/**
 * Request to delete a user and their org.
 *
 * Created by cdelashmutt on 2/26/17.
 */
public class UserDeleteRequest {
	private String userId;
	private String orgId;

	public UserDeleteRequest() {
	}

	public UserDeleteRequest(String userId, String orgId) {
		this.userId = userId;
		this.orgId = orgId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
}
