package com.jaspersoft.jasperserver.war;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class QueryProxyServlet extends ProxyServlet {

    @Override
    protected String getConfigParam(String key) {
        if ("targetUri".equals(key)) {
            return Optional
                    .ofNullable(System.getenv("SCALABLE_QUERY_ENGINE_URL"))
                    .orElseGet(() -> super.getConfigParam(key));
        } else {
            return super.getConfigParam(key);
        }
    }

    protected Optional<Header> basicAuth() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        String auth = getCredentials(authenticationToken);

        if (auth != null) {
            byte[] encodedAuth =
                    Base64.encodeBase64(
                            auth.getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);

            return Optional.of(new BasicHeader(AUTHORIZATION, authHeader));
        } else {
            return Optional.empty();
        }
    }

    protected String getCredentials(Authentication auth) {
        if (auth == null) return null;
        if (auth.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) auth.getPrincipal();
            return user.getUsername() + ":" + user.getPassword();
        } else return null;
    }

}
