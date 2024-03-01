package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

public class JWTMetadataUserDetails extends MetadataUserDetails {

    private String jwtToken;

    public JWTMetadataUserDetails(User u) {
        super(u);
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }







}
