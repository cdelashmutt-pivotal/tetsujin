package io.pivotal.pa.controller;

import io.pivotal.pa.CFProperties;
import io.pivotal.pa.domain.UserDeleteRequest;
import io.pivotal.pa.domain.UserProvisioningRequest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.spaces.*;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.util.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * Main REST endpoint for application.
 *
 * Created by cdelashmutt on 2/22/17.
 */
@SuppressWarnings("WeakerAccess")
@RestController
public class UserController {

	@SuppressWarnings("CanBeFinal")
	@Autowired
	private CFProperties cfProperties;

	@Autowired
	private ConfigurableEnvironment env;

	@SuppressWarnings("CanBeFinal")
	@Autowired
	private CloudFoundryClient cfClient;

	@SuppressWarnings("CanBeFinal")
	@Autowired
	private UaaClient uaaClient;

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public ResponseEntity<String> user(@RequestBody UserProvisioningRequest req) {

		return uaaClient.users().create(CreateUserRequest.builder()
				.userName(req.getEmail())
				.password(req.getPassword())
				.name(Name.builder().givenName("Student").familyName("User").build())
				.email(Email.builder().value(req.getEmail()).primary(true).build())
				.build()
		)
				.map(CreateUserResponse::getId)
				.then(
						uaaUserId -> cfClient.users().create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
								.uaaId(uaaUserId).build()
						)
				)
				.map(ResourceUtils::getId)
				.then(userId -> Mono.when(
						Mono.just(userId),
						cfClient.organizations()
								.create(CreateOrganizationRequest.builder()
										.name(req.getOrg())
										.build()
								).map(ResourceUtils::getId)
						)
				)
				.then(function((String userId, String orgId) -> Mono.when(
						cfClient.organizations().associateUser(AssociateOrganizationUserRequest.builder()
								.organizationId(orgId)
								.userId(userId)
								.build()
						).then(Mono.just(userId)),
						Mono.just(orgId),
						cfClient.spaces().create(CreateSpaceRequest.builder()
								.name("development")
								.organizationId(orgId).build()
						).map(ResourceUtils::getId),
						cfClient.spaces().create(CreateSpaceRequest.builder()
								.name("production")
								.organizationId(orgId).build()
						).map(ResourceUtils::getId)
				)))
				.then(
						function((String userId, String orgId, String devSpaceId, String prodSpaceId) -> Mono.when(
								cfClient.organizations().associateManager(AssociateOrganizationManagerRequest.builder()
										.organizationId(orgId)
										.managerId(userId)
										.build()
								),
								cfClient.spaces().associateManager(AssociateSpaceManagerRequest.builder()
										.spaceId(devSpaceId)
										.managerId(userId)
										.build()
								),
								cfClient.spaces().associateDeveloper(AssociateSpaceDeveloperRequest.builder()
										.spaceId(devSpaceId)
										.developerId(userId)
										.build()
								),
								cfClient.spaces().associateManager(AssociateSpaceManagerRequest.builder()
										.spaceId(prodSpaceId)
										.managerId(userId)
										.build()
								),
								cfClient.spaces().associateDeveloper(AssociateSpaceDeveloperRequest.builder()
										.spaceId(prodSpaceId)
										.developerId(userId)
										.build()
								)
						))).map((Tuple5) -> ResponseEntity.ok("created"))
				.otherwiseReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error"))
				.block();

	}

	@RequestMapping(value = "/user", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@RequestBody UserDeleteRequest req) {
		return Mono.when(
				cfClient.organizations().delete(DeleteOrganizationRequest.builder()
						.organizationId(req.getOrgId())
						.async(true)
						.recursive(true)
						.build()
				),
				cfClient.users().delete(DeleteUserRequest.builder()
						.userId(req.getUserId()).build()),
				uaaClient.users().delete(org.cloudfoundry.uaa.users.DeleteUserRequest.builder()
						.userId(req.getUserId()).build())
		).map((Tuple3) -> ResponseEntity.ok("deleted"))
				.otherwiseReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error"))
				.block();

	}
}
