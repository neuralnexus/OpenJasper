package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.RoleManagerService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserManagerService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.json.JSONArray;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

/**
 */
public class RoleListFlowTest extends AbstractXmlFlowExecutionTests {
    private static String USER_NAME = "superuser";
    private static String PASSWORD = "superuser";

    private UserAuthorityService userAuthorityServiceMock;
    private RoleManagerService roleManagerServiceMock;
    private RepositoryService repositoryServiceMock;
    private RepositorySecurityChecker securityCheckerMock;
    private TenantService tenantServiceMock;
    private ConfigurationBean configurationMock;
    private AuditContext auditContextMock;

    private List<String> roleNames;
    private String demoRole;
    private String firstRole;
    private String secondRole;

    @Override
    protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
        return resourceFactory.createFileResource("src/main/webapp/WEB-INF/flows/roleListFlow.xml");
    }

    @Override
    protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
        builderContext.registerBean("userAuthorityService", this.userAuthorityServiceMock);
        builderContext.registerBean("roleManagerService", this.roleManagerServiceMock);
        builderContext.registerBean("tenantService", this.tenantServiceMock);
        builderContext.registerBean("configurationBean", this.configurationMock);
        builderContext.registerBean("dummyAuditContext", this.auditContextMock);
        builderContext.registerBean("repositoryService", this.repositoryServiceMock);
        builderContext.registerBean("repositoryServiceSecurityChecker", this.securityCheckerMock);
    }

    @Override
    protected void setUp() throws Exception {
        this.userAuthorityServiceMock = createMock(UserAuthorityService.class);
        this.roleManagerServiceMock = createMock(RoleManagerService.class);
        this.repositoryServiceMock = createMock(RepositoryService.class);
        this.securityCheckerMock = createMock(RepositorySecurityChecker.class);
        this.tenantServiceMock = createMock(TenantService.class);
        this.configurationMock = createMock(ConfigurationBean.class);
        this.auditContextMock = createMock(AuditContext.class);

        User user = new UserImpl();
        user.setUsername(USER_NAME);
        user.setPassword(PASSWORD);
        user.setEnabled(true);

        UserDetails userDetails = new MetadataUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        this.demoRole = "demo_role";
        this.firstRole = "first_role";
        this.secondRole = "second_role";


        this.roleNames = new ArrayList<String>();
        roleNames.add(this.demoRole);
        roleNames.add(this.firstRole);
        roleNames.add(this.secondRole);

    }


    public void testStartFlow() {
        expect(this.configurationMock.getUserNameSeparator()).andReturn("|").times(2);
        expect(this.configurationMock.getRoleNameNotSupportedSymbols()).andReturn("[]").times(2);
        expect(this.configurationMock.getDefaultRole()).andReturn("ROLE_USER").times(2);
        expect(this.configurationMock.getPasswordMask()).andReturn("*");

        replay(this.configurationMock);

        MockExternalContext context = new MockExternalContext();

        startFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("roleListView");
    }

    public void testDeleteRole() {

        this.userAuthorityServiceMock.deleteRole((ExecutionContext) isNull(), eq(this.demoRole));
        expectLastCall();

        replay(this.userAuthorityServiceMock);

        setCurrentState("roleListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("entity", this.demoRole);

        context.setEventId("delete");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.userAuthorityServiceMock);
    }

    public void testDeleteAll() {
        this.roleManagerServiceMock.deleteAll((ExecutionContext) isNull(), eq(roleNames));
        expectLastCall();

        replay(this.roleManagerServiceMock);

        setCurrentState("roleListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("entities", toJson(roleNames).toString());

        context.setEventId("deleteAll");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.roleManagerServiceMock);
    }

    private JSONArray toJson(List<String> list) {
        if (list != null) {
            JSONArray array = new JSONArray();

            for (String name : list) {
                array.put(name);
            }

            return array;
        } else {
            return null;
        }
    }
}