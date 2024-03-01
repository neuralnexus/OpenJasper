package com.jaspersoft.jasperserver.war.control;

import com.jaspersoft.jasperserver.api.common.properties.Log4jSettingsService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogSettingsControllerTest {

    LogSettingsController logSettingsController = new LogSettingsController();


    @Test
    public void handleRequestTest() throws Exception{
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter("level")).thenReturn("ABC");

        HttpServletResponse res = mock(HttpServletResponse.class);
        logSettingsController.handleRequest(req, res);

        verify(res).sendError(HttpStatus.BAD_REQUEST.value(), "Invalid level for logging: [ABC]");
    }

    @Test
    public void handleRequestTestNoLevel() throws Exception{
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        logSettingsController.setLog4jSettingsService(mock(Log4jSettingsService.class));
        logSettingsController.setPropertiesManagementService(mock(PropertiesManagementService.class));
        logSettingsController.handleRequest(req, res);
        verify(res, times(0)).sendError(HttpStatus.BAD_REQUEST.value(), "Invalid level for logging: [null]");


    }

}
