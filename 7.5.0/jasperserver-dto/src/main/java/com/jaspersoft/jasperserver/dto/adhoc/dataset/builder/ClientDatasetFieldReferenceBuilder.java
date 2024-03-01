package com.jaspersoft.jasperserver.dto.adhoc.dataset.builder;

import com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientDatasetFieldReference;

import java.util.List;

import static java.util.Arrays.asList;

public class ClientDatasetFieldReferenceBuilder {
    public static ClientDatasetFieldReference fieldRef(String referenceName, String referenceType) {
        return new ClientDatasetFieldReference().setReference(referenceName).setType(referenceType);
    }

    public static List<ClientDatasetFieldReference> fieldRefs(ClientDatasetFieldReference... fieldRefs) {
        return asList(fieldRefs);
    }

}
