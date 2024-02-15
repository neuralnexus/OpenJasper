package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.common.service.impl.ObjectFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Chaim Arbiv
 * @version $id$
 * Unit tests for ExternalUserProcessor
 */
public class ExternalUserSetupProcessorTest  extends UnitilsJUnit4 {
	private static final Logger logger = LogManager.getLogger(ExternalUserSetupProcessorTest.class);

    Mock<UserAuthorityService> userAuthorityServiceMock;
    Mock<RepositoryService> repositoryServiceMock;
    Mock<TenantService> tenantServiceMock;
    Mock<ProfileAttributeService> profileAttributeServiceMock;
    Mock<ObjectPermissionService> objectPermissionServiceMock;


    @Test
    public void testAlignInternalUser() throws Exception {
		final String organization_1 = "organization_1";

//        Scenario 1 - user has no internal roles and there are no roles in the organizationRoleMap
		Set<Role> remoteExternalUserRoles = new HashSet<Role>();

		// roles from LDAP
		Role externalRole = new RoleImpl();
		externalRole.setRoleName("ROLE_EXTERNAL");
		externalRole.setExternallyDefined(true);
		externalRole.setTenantId(organization_1);
        remoteExternalUserRoles.add(externalRole);

        // user
        User user = new UserImpl();

        // preparing objects and mockes
        ExternalUserSetupProcessor userProcessor = new ExternalUserSetupProcessor();
        userAuthorityServiceMock.oncePerforms(null).putRole(null, externalRole);
        userProcessor.setUserAuthorityService(userAuthorityServiceMock.getMock());

        // test
        userProcessor.alignInternalAndExternalUser(remoteExternalUserRoles, user);
        Assert.assertTrue("user should have one externally defined user",
                                user.getRoles().size()==1 &&((Role)user.getRoles().toArray()[0]).isExternallyDefined());


        // Scenario 2 - user has no internal roles and there are roles that needs to be mapped
        // populating OrganizationRoleMap
        Map<String, String> organizationRoleMap = new HashMap<String, String>();
        organizationRoleMap.put("ROLE_EXTERNAL", "ROLE_ADMINISTRATOR");
        userProcessor.setOrganizationRoleMap(organizationRoleMap);
		userProcessor.setUserAuthorityService(new UserAuthorityServiceMock());

        // test
		GrantedAuthority[] gaArr = new GrantedAuthority[1];
		gaArr[0] = new GrantedAuthorityImpl(externalRole.getRoleName());
		Set<Role> convertedExternalRoles = userProcessor.convertGrantedAuthoritiesToRoles(gaArr, organization_1);
		userProcessor.alignInternalAndExternalUser(convertedExternalRoles, user);
        Assert.assertTrue("user should have one internal role - ROLE_ADMINISTRATOR defined in root",
                                        user.getRoles().size()==1 &&
                                        !((Role)user.getRoles().toArray()[0]).isExternallyDefined() &&
                                        ((Role)user.getRoles().toArray()[0]).getTenantId()==null &&
                                        ((Role)user.getRoles().toArray()[0]).getRoleName().equals("ROLE_ADMINISTRATOR"));


//        Scenario 3 - user has internal roles which is the value of one of the rootRoleMap
        // creating user and role and the organizationRootMap
        user = new UserImpl();
        Role adminRole = new RoleImpl();
        adminRole.setRoleName("ROLE_ADMINISTRATOR");
        user.addRole(adminRole);

        organizationRoleMap.put("ROLE_SCENARIO_3", "ROLE_ADMINISTRATOR");

        // test
        userProcessor.alignInternalAndExternalUser(new HashSet<Role>(), user);
        Assert.assertTrue("user should have one externally defined user", user.getRoles() != null && user.getRoles().size() == 0);
    }

	@Test
	public void testConvertGrantedAuthoritiesToRoles() {
		try {
			ExternalUserSetupProcessor userProcessor = new ExternalUserSetupProcessor();
			userProcessor.setRepositoryService(repositoryServiceMock.getMock());

			final UserAuthorityServiceImpl userAuthorityService = new UserAuthorityServiceImpl();
			final ResourceFactoryImpl resourceFactory = new ResourceFactoryImpl();
			resourceFactory.setObjectFactory(new ObjectFactoryImpl());
			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("com.jaspersoft.jasperserver.api.metadata.user.domain.Role",
					"com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl");
			resourceFactory.setImplementationClassMappings(mapping);
			userAuthorityService.setObjectMappingFactory(resourceFactory);
			userProcessor.setUserAuthorityService(userAuthorityService);

			userProcessor.setTenantService(tenantServiceMock.getMock());
			userProcessor.setProfileAttributeService(profileAttributeServiceMock.getMock());
			userProcessor.setObjectPermissionService(objectPermissionServiceMock.getMock());

			Map<String, String> organizationRoleMap = new HashMap<String, String>();
			organizationRoleMap.put("ROLE_EXTERNAL", "ROLE$ADMINISTRATOR$");
			organizationRoleMap.put("ROLE$EXT", "ROLE$ADMINISTRATOR$|*");    //tenant id is set on the role
			userProcessor.setOrganizationRoleMap(organizationRoleMap);
			userProcessor.setPermittedExternalRoleNameRegex("[A-Za-z_]+");
			userProcessor.afterPropertiesSet();

			Map<String, String> roleMap = userProcessor.getOrganizationRoleMap();
			Assert.assertTrue("Internal role should be cleaned of $ (invalid according to "
					+ userProcessor.getPermittedExternalRoleNameRegex() + ")",
					"ROLE_ADMINISTRATOR".equals(roleMap.get("ROLE_EXTERNAL")));

			//Case 1: ROLE$EXT is mapped to internal ROLE_ADMINISTRATOR
			GrantedAuthority[] authorities = new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE$EXT")};
			Set<Role> roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 1: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			Role role = roleSet.iterator().next();
			Assert.assertTrue("Case 1: role name must be ROLE_ADMINISTRATOR.", "ROLE_ADMINISTRATOR".equals(role.getRoleName()));
			Assert.assertTrue("Case 1: role must be mapped to internal ROLE_ADMINISTRATOR.", !role.isExternallyDefined());
			Assert.assertTrue("Case 1: role must have tenantId 'tenant'.", "tenant".equalsIgnoreCase(role.getTenantId()));

			//Case 2: ROLE$EXTERNAL-NOT MAPPED+ is converted to external ROLE_EXTERNAL_NOT_MAPPED.
			// Illegal (not matching permittedExternalRoleNameRegex) char sequences are substituted by _
			authorities = new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE$EXTERNAL-NOT MAPPED+")};
			roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 2: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			role = roleSet.iterator().next();
			Assert.assertTrue("Case 2: role name must be ROLE_EXTERNAL_NOT_MAPPED.", "ROLE_EXTERNAL_NOT_MAPPED".equals(role.getRoleName()));
			Assert.assertTrue("Case 2: role must be external.", role.isExternallyDefined());
			Assert.assertTrue("Case 2: role must have tenantId 'tenant'.", "tenant".equalsIgnoreCase(role.getTenantId()));


			//Case 3: ROLE_EXTERNAL is mapped to internal ROLE_ADMINISTRATOR
			authorities = new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_EXTERNAL")};
			roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 3: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			role = roleSet.iterator().next();
			Assert.assertTrue("Case 3: role name must be ROLE_ADMINISTRATOR.", "ROLE_ADMINISTRATOR".equals(role.getRoleName()));
			Assert.assertTrue("Case 3: role must be mapped to internal ROLE_ADMINISTRATOR.", !role.isExternallyDefined());
			Assert.assertNull("Case 3: role must have tenantId null.", role.getTenantId());
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
