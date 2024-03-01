package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.error.handling.ExceptionOutputManagerImpl;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandlerImpl;
import com.jaspersoft.jasperserver.dto.connection.FtpConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.testng.annotations.BeforeClass;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FtpContextStrategyTest {


    @InjectMocks
    private FtpContextStrategy ftpContextStrategy = new FtpContextStrategy();

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createContext() throws IllegalParameterValueException {
        Map<String, FtpConnectionDescriptionProvider> connectionDescriptionProviders = new HashMap<>();
        FtpConnection ftpConnection = new FtpConnection();
        ftpConnection.setHolder("ABC:ABC");
        FtpConnection ftpConnection2 = new FtpConnection();
        ftpConnection2.setPassword("DONTSHOW");

        SecureExceptionHandler secureExceptionHandler = new SecureExceptionHandlerImpl();
        ((SecureExceptionHandlerImpl) secureExceptionHandler).setExceptionOutputManager(new ExceptionOutputManagerImpl());
        ((SecureExceptionHandlerImpl) secureExceptionHandler).setMessageSource(new MessageSource() {
            @Override
            public String getMessage(String s, Object[] objects, String s1, Locale locale) {
                return s;
            }

            @Override
            public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
                return s;
            }

            @Override
            public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
                return messageSourceResolvable.getDefaultMessage();
            }
        });
        ftpContextStrategy.setSecureExceptionHandler(secureExceptionHandler);

        Map<String, Object> data = new HashMap<>();
        FtpConnectionDescriptionProvider provider = new FtpConnectionDescriptionProvider() {
            @Override
            public FtpConnection getFtpConnectionDescription(String id) {
                return ftpConnection2;
            }
        };
        connectionDescriptionProviders.put("ABC", provider);
        ftpContextStrategy.setConnectionDescriptionProviders(connectionDescriptionProviders);
        try {
            ftpContextStrategy.createContext(ExecutionContextImpl.getRuntimeExecutionContext(), ftpConnection, data);
        } catch (ContextCreationFailedException ex) {
            String[] parameters = ex.getErrorDescriptor().getParameters();
            boolean isPasswordNull = false;
            for (String param : parameters) {
                if (param.contains("password='null'")) {
                    isPasswordNull = true;
                    break;
                }
            }
            Assert.assertTrue(isPasswordNull);
        }
    }

    @Test
    public void getContextForClient() {
        FtpConnection ftpConnection = new FtpConnection();
        ftpConnection.setPassword("DONTSHOW");
        ftpConnection.setSshPassphrase("Doshow2");
        ftpConnection = ftpContextStrategy.getContextForClient(ftpConnection, null, null);
        Assert.assertNull(ftpConnection.getPassword());
        Assert.assertNull(ftpConnection.getSshPassphrase());
    }
}
