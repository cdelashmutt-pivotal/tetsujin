package io.pivotal.pa.actuator;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Reports on CF Connection Health
 *
 * Created by cdelashmutt on 2/26/17.
 */
@Component
public class CFConnectionHealth implements HealthIndicator {

	@Autowired
	private CloudFoundryClient cfClient;

	@Override
	public Health health() {
		Health.Builder health = new Health.Builder();

		health.up().withDetail("api_version", cfClient.info().get(GetInfoRequest.builder().build()).block().getApiVersion());

		return health.build();
	}
}
