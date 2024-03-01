package com.jaspersoft.jasperserver.war;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.jaspersoft.jasperserver.war.RESTLoginAuthenticationFilter.LOGIN_PATH_INFO;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class RESTLoginAuthenticationFilterTest {
    private final RESTLoginAuthenticationFilter filter = new RESTLoginAuthenticationFilter();

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    private final HttpServletResponse response = mock(HttpServletResponse.class);

    private final PrintWriter writer = mock(PrintWriter.class);

    private final FilterChain filterChain = mock(FilterChain.class);

    @Before
    public void setUp() throws IOException {
        doReturn(writer).when(response).getWriter();
        doReturn(LOGIN_PATH_INFO).when(request).getPathInfo();
    }

    @Test
    public void doFilter_nonLoginPath_invokeNextFilter() throws Exception {
        doReturn(null).when(request).getPathInfo();

        filter.doFilter(request, response, filterChain);

        verify(request).getPathInfo();
        verifyNoMoreInteractions(request);
        verifyZeroInteractions(response);
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    public void doFilter_optionsMethod_noContentResponse() throws Exception {
        doReturn(OPTIONS.name()).when(request).getMethod();
        filter.setPostOnly(false);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void doFilter_nonPostMethodWithPostOnly_unauthorizedResponse() throws Exception {
        doReturn(OPTIONS.name()).when(request).getMethod();
        filter.setPostOnly(true);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}