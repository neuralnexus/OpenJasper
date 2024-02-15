package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserManagerService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserDetailsServiceImpl;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import org.easymock.EasyMock;
import org.json.JSONArray;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.test.*;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class UserListFlowTest extends AbstractXmlFlowExecutionTests {
    private static String USER_NAME = "superuser";
    private static String PASSWORD = "superuser";

    private UserAuthorityService userAuthorityServiceMock;
    private UserManagerService userManagerServiceMock;
    private RepositoryService repositoryServiceMock;
    private RepositorySecurityChecker securityCheckerMock;
    private TenantService tenantServiceMock;
    private ConfigurationBean configurationMock;
    private AuditContext auditContextMock;

    private List<String> userNames;
    private String demoUser;
    private String firstUser;
    private String secondUser;

    @Override
    protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
        return resourceFactory.createFileResource("src/main/webapp/WEB-INF/flows/userListFlow.xml");
    }

    @Override
    protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
        builderContext.registerBean("userAuthorityService", this.userAuthorityServiceMock);
        builderContext.registerBean("userManagerService", this.userManagerServiceMock);
        builderContext.registerBean("tenantService", this.tenantServiceMock);
        builderContext.registerBean("configurationBean", this.configurationMock);
        builderContext.registerBean("dummyAuditContext", this.auditContextMock);
        builderContext.registerBean("repositoryService", this.repositoryServiceMock);
        builderContext.registerBean("repositoryServiceSecurityChecker", this.securityCheckerMock);
    }

    @Override
    protected void setUp() throws Exception {
        this.userAuthorityServiceMock = createMock(UserAuthorityService.class);
        this.userManagerServiceMock = createMock(UserManagerService.class);
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

        this.demoUser = "demo";
        this.firstUser = "first";
        this.secondUser = "second";

        this.userNames = new ArrayList<String>();
        userNames.add(this.demoUser);
        userNames.add(this.firstUser);
        userNames.add(this.secondUser);

    }


    public void testStartFlow() {
        expect(this.configurationMock.getUserNameSeparator()).andReturn("|").times(2);
        expect(this.configurationMock.getUserNameNotSupportedSymbols()).andReturn("[]").times(2);
        expect(this.configurationMock.getDefaultRole()).andReturn("ROLE_USER").times(2);
        expect(this.configurationMock.getPasswordMask()).andReturn("*");

        replay(this.configurationMock);

        MockExternalContext context = new MockExternalContext();

        startFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("userListView");
    }

    public void testDeleteUser() {

        this.userAuthorityServiceMock.deleteUser((ExecutionContext) isNull(), eq(this.demoUser));
        expectLastCall();

        replay(this.userAuthorityServiceMock);

        setCurrentState("userListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("entity", this.demoUser);

        context.setEventId("delete");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.userAuthorityServiceMock);
    }

    public void testDeleteAll() {
        this.userManagerServiceMock.deleteAll((ExecutionContext) isNull(), eq(userNames));
        expectLastCall();

        replay(this.userManagerServiceMock);

        setCurrentState("userListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("entities", toJson(userNames).toString());

        context.setEventId("deleteAll");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.userManagerServiceMock);
    }

    public void testEnableAll() {
        this.userManagerServiceMock.enableAll((ExecutionContext) isNull(), eq(userNames));
        expectLastCall();

        replay(this.userManagerServiceMock);

        setCurrentState("userListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("userNames", toJson(userNames).toString());

        context.setEventId("enableAll");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.userManagerServiceMock);
    }

    public void testDisableAll() {
        this.userManagerServiceMock.disableAll((ExecutionContext) isNull(), eq(userNames));
        expectLastCall();

        replay(this.userManagerServiceMock);

        setCurrentState("userListView");

        MockExternalContext context = new MockExternalContext();
        context.putRequestParameter("userNames", toJson(userNames).toString());

        context.setEventId("disableAll");

        resumeFlow(context);

        assertFlowExecutionActive();
        assertCurrentStateEquals("ajaxView");

        verify(this.userManagerServiceMock);
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