/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HibernateDiagnosticService}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateDiagnosticServiceTest {
    @Mock
    private Statistics hibernateStatistics;
    @InjectMocks
    private HibernateDiagnosticService hibernateDiagnosticService;

    private long startTime = 100000;
    private boolean statisticsEnabled = true;
    private long entityDeleteCount = 3;
    private long entityInsertCount = 4;
    private long entityLoadCount = 5;
    private long entityFetchCount = 6;
    private long entityUpdateCount = 7;
    private long queryExecutionCount = 8;
    private long queryCacheHitCount = 9;
    private long queryExecutionMaxTime = 10;
    private long queryCacheMissCount = 11;
    private long queryCachePutCount = 12;
    private long flushCount = 13;
    private long connectCount = 14;
    private long secondLevelCacheHitCount = 15;
    private long secondLevelCacheMissCount = 16;
    private long secondLevelCachePutCount = 17;
    private long sessionCloseCount = 18;
    private long sessionOpenCount = 19;
    private long collectionLoadCount = 20;
    private long collectionFetchCount = 21;
    private long collectionUpdateCount = 22;
    private long collectionRemoveCount = 23;
    private long collectionRecreateCount = 24;
    private String[] roleNames;
    private String[] entityNames;
    private String[] queries;
    private String[] secondLevelCacheRegionNames;
    private long successfulTransactionCount = 25;
    private long transactionCount = 26;
    private long closeStatementCount = 27;
    private long prepareStatementCount = 28;
    private long optimisticFailureCount = 29;
    private String queryExecutionMaxTimeQueryString = "Execution max time query string";

    @Before
    public void setUp() {
        when(hibernateStatistics.getStartTime()).thenReturn(startTime);
        when(hibernateStatistics.isStatisticsEnabled()).thenReturn(statisticsEnabled);
        when(hibernateStatistics.getEntityDeleteCount()).thenReturn(entityDeleteCount);
        when(hibernateStatistics.getEntityInsertCount()).thenReturn(entityInsertCount);
        when(hibernateStatistics.getEntityLoadCount()).thenReturn(entityLoadCount);
        when(hibernateStatistics.getEntityFetchCount()).thenReturn(entityFetchCount);
        when(hibernateStatistics.getEntityUpdateCount()).thenReturn(entityUpdateCount);
        when(hibernateStatistics.getQueryExecutionCount()).thenReturn(queryExecutionCount);
        when(hibernateStatistics.getQueryCacheHitCount()).thenReturn(queryCacheHitCount);
        when(hibernateStatistics.getQueryExecutionMaxTime()).thenReturn(queryExecutionMaxTime);
        when(hibernateStatistics.getQueryCacheMissCount()).thenReturn(queryCacheMissCount);
        when(hibernateStatistics.getQueryCachePutCount()).thenReturn(queryCachePutCount);
        when(hibernateStatistics.getFlushCount()).thenReturn(flushCount);
        when(hibernateStatistics.getConnectCount()).thenReturn(connectCount);
        when(hibernateStatistics.getSecondLevelCacheHitCount()).thenReturn(secondLevelCacheHitCount);
        when(hibernateStatistics.getSecondLevelCacheMissCount()).thenReturn(secondLevelCacheMissCount);
        when(hibernateStatistics.getSecondLevelCachePutCount()).thenReturn(secondLevelCachePutCount);
        when(hibernateStatistics.getSessionCloseCount()).thenReturn(sessionCloseCount);
        when(hibernateStatistics.getSessionOpenCount()).thenReturn(sessionOpenCount);
        when(hibernateStatistics.getCollectionLoadCount()).thenReturn(collectionLoadCount);
        when(hibernateStatistics.getCollectionFetchCount()).thenReturn(collectionFetchCount);
        when(hibernateStatistics.getCollectionUpdateCount()).thenReturn(collectionUpdateCount);
        when(hibernateStatistics.getCollectionRemoveCount()).thenReturn(collectionRemoveCount);
        when(hibernateStatistics.getCollectionRecreateCount()).thenReturn(collectionRecreateCount);
        roleNames = new String[2];
        roleNames[0] = "Role1";
        roleNames[1] = "Role2";
        when(hibernateStatistics.getCollectionRoleNames()).thenReturn(roleNames);
        entityNames = new String[2];
        entityNames[0] = "Entity1";
        entityNames[1] = "Entity2";
        when(hibernateStatistics.getEntityNames()).thenReturn(entityNames);
        queries = new String[3];
        queries[0] = "Query 1";
        queries[1] = "Query 2";
        queries[2] = "Query 3";
        when(hibernateStatistics.getQueries()).thenReturn(queries);
        secondLevelCacheRegionNames = new String[4];
        secondLevelCacheRegionNames[0] = "Region 1";
        secondLevelCacheRegionNames[1] = "Region 2";
        secondLevelCacheRegionNames[2] = "Region 3";
        secondLevelCacheRegionNames[3] = "Region 4";
        when(hibernateStatistics.getSecondLevelCacheRegionNames()).thenReturn(secondLevelCacheRegionNames);
        when(hibernateStatistics.getSuccessfulTransactionCount()).thenReturn(successfulTransactionCount);
        when(hibernateStatistics.getTransactionCount()).thenReturn(transactionCount);
        when(hibernateStatistics.getCloseStatementCount()).thenReturn(closeStatementCount);
        when(hibernateStatistics.getPrepareStatementCount()).thenReturn(prepareStatementCount);
        when(hibernateStatistics.getOptimisticFailureCount()).thenReturn(optimisticFailureCount);
        when(hibernateStatistics.getQueryExecutionMaxTimeQueryString()).thenReturn(queryExecutionMaxTimeQueryString);
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = hibernateDiagnosticService.getDiagnosticData();
        //Test total size of diagnostic attributes
        assertEquals(34, resultDiagnosticData.size());

        //Test actual values of target diagnostic attributes
        assertEquals(startTime, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_START_TIME, null, null)).getDiagnosticAttributeValue());
        assertEquals(statisticsEnabled, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_STATISTICS_ENABLED, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityDeleteCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_DELETE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityInsertCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_INSERT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityLoadCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_LOAD_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityFetchCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_FETCH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityUpdateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_UPDATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCacheHitCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_HIT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionMaxTime, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCacheMissCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_MIS_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCachePutCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_PUT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(flushCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_FLUSH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(connectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_CONNECT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCacheHitCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_HIT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCacheMissCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_MISS_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCachePutCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_PUT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(sessionCloseCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SESSION_CLOSE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(sessionOpenCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SESSION_OPEN_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionLoadCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_LOAD_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionFetchCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_FETCH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionUpdateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_UPDATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionRemoveCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_REMOVE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionRecreateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_RECREATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(2, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_ROLE_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Role1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_ROLE_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(2, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Entity1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(3, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERIES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Query 1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERIES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(4, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_REGION_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Region 1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_REGION_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(successfulTransactionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SUCCESSFUL_TRANSACTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(transactionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_TRANSACTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(closeStatementCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_CLOSE_STATEMENT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(prepareStatementCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_PREPARE_STATEMENT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(optimisticFailureCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_OPTIMISTIC_FAILURE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionMaxTimeQueryString, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME_QUERY_STRING, null, null)).getDiagnosticAttributeValue());
    }
}
