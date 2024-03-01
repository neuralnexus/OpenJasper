package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

public class RepoQueryTest {
    
    /**
     * The Query repo object has a "sql" field which contains the actual query to run.
     * It was mapped to a varchar, which has lower limits on some platforms (looking at you, Oracle).
     * Domain queries were getting rather huge and exceeding the 3600-char limit, so we did compression and Base64 encoding.
     * At the time, the only encoder built in was sun.misc.Base64Encoder, which wasn't "official".
     * To support Java 9, we needed to get rid of these kinds of dependencies, and Java 8 added public Base64 encoding.
     * However, there are various flavors; MIME encoding has line breaks, and "basic" does not.
     * The old Base64Encoder was really a MIME encoder and we picked a new encoder that doesn't like the line feeds.
     * Here we are trying to read a query encoded with the old code to make sure it works.
     */
    @Test
    public void readCompressedQueryFromPreviousVersion() {
        RepoQuery repoQuery = new RepoQuery();
        repoQuery.setSql(readFileFromClasspath("js55928.oldCompressedQuery.txt"));
        Query clientQuery = new QueryImpl();
        repoQuery.copyTo(clientQuery, null);
        String expectedQuery = readFileFromClasspath("js55928.expectedQuery.txt");
        assertEquals(expectedQuery, clientQuery.getSql());
    }
    
    public String readFileFromClasspath(String fileName) {
        InputStream is = getClass().getResourceAsStream("/" + fileName);
        byte[] bytes = DataContainerStreamUtil.readData(is);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
