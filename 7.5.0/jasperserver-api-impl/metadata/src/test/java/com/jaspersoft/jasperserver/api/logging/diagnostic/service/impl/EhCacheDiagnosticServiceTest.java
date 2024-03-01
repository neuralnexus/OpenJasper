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
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import net.sf.ehcache.management.CacheStatistics;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link EhCacheDiagnosticService}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class EhCacheDiagnosticServiceTest {
    @Mock
    private CacheStatistics cacheStatistics;

    @InjectMocks
    private EhCacheDiagnosticService ehCacheDiagnosticService;

    private Long objectCount = 6L;
    private Double cacheHitPercentage = 7.0;
    private Long cacheHits = 8L;
    private Double cacheMissPercentage = 9.0;
    private Long cacheMisses = 10L;
    private Long diskStoreObjectCount = 11L;
    private Double onDiskHitPercentage = 12.0;
    private Long onDiskHits = 13L;
    private Long onDiskMisses = 14L;
    private Long memoryStoreObjectCount = 15L;
    private Double inMemoryHitPercentage = 16.0;
    private Long inMemoryHits = 17L;
    private Long inMemoryMisses = 18L;
    private Long offHeapStoreObjectCount = 19L;
    private Double OffHeapHitPercentage = 20.0;
    private Long OffHeapHits = 21L;
    private Long offHeapMisses = 22L;
    private Integer writeMaxQueueSize = 23;
    private Long writeQueueLength = 24L;

    private Integer conf_DiskSpoolBufferSizeMB = 25;
    private Long conf_DiskExpiryThreadIntervalSeconds = 26L;
    private Boolean conf_LoggingEnabled = false;
    private Long conf_MaxBytesLocalDisk = 27L;
    private Long conf_MaxBytesLocalHeap = 28L;
    private Long conf_MaxBytesLocalOffHeap = 29L;
    private Integer conf_MaxElementsOnDisk = 30;
    private Long conf_TimeToIdleSeconds = 32L;
    private Long conf_TimeToLiveSeconds = 33L;
    private Long conf_MaxEntriesLocalDisk = 34L;
    private Long conf_MaxEntriesLocalHeap = 35L;
    private Boolean diskPersistent = true;
    private Boolean eternal = false;
    private Boolean overflowToDisk = true;
    private Boolean overflowToOffHeap = false;

    @Before
    public void setUp() {
        when(cacheStatistics.getObjectCount()).thenReturn(objectCount);
        when(cacheStatistics.getCacheHitPercentage()).thenReturn(cacheHitPercentage);
        when(cacheStatistics.getCacheHits()).thenReturn(cacheHits);
        when(cacheStatistics.getCacheMissPercentage()).thenReturn(cacheMissPercentage);
        when(cacheStatistics.getCacheMisses()).thenReturn(cacheMisses);
        when(cacheStatistics.getDiskStoreObjectCount()).thenReturn(diskStoreObjectCount);
        when(cacheStatistics.getOnDiskHitPercentage()).thenReturn(onDiskHitPercentage);
        when(cacheStatistics.getOnDiskHits()).thenReturn(onDiskHits);
        when(cacheStatistics.getOnDiskMisses()).thenReturn(onDiskMisses);
        when(cacheStatistics.getMemoryStoreObjectCount()).thenReturn(memoryStoreObjectCount);
        when(cacheStatistics.getInMemoryHitPercentage()).thenReturn(inMemoryHitPercentage);
        when(cacheStatistics.getInMemoryHits()).thenReturn(inMemoryHits);
        when(cacheStatistics.getInMemoryMisses()).thenReturn(inMemoryMisses);
        when(cacheStatistics.getOffHeapStoreObjectCount()).thenReturn(offHeapStoreObjectCount);
        when(cacheStatistics.getOffHeapHitPercentage()).thenReturn(OffHeapHitPercentage);
        when(cacheStatistics.getOffHeapHits()).thenReturn(OffHeapHits);
        when(cacheStatistics.getOffHeapMisses()).thenReturn(offHeapMisses);
        when(cacheStatistics.getWriterMaxQueueSize()).thenReturn(writeMaxQueueSize);
        when(cacheStatistics.getWriterQueueLength()).thenReturn(writeQueueLength);

        CacheConfiguration cacheConfig = Mockito.mock(CacheConfiguration.class);

        when(cacheConfig.getDiskSpoolBufferSizeMB()).thenReturn(conf_DiskSpoolBufferSizeMB);
        when(cacheConfig.getDiskExpiryThreadIntervalSeconds()).thenReturn(conf_DiskExpiryThreadIntervalSeconds);
        when(cacheConfig.getLogging()).thenReturn(conf_LoggingEnabled);
        when(cacheConfig.getMaxBytesLocalDisk()).thenReturn(conf_MaxBytesLocalDisk);
        when(cacheConfig.getMaxBytesLocalHeap()).thenReturn(conf_MaxBytesLocalHeap);
        when(cacheConfig.getMaxBytesLocalOffHeap()).thenReturn(conf_MaxBytesLocalOffHeap);
        when(cacheConfig.getMaxElementsOnDisk()).thenReturn(conf_MaxElementsOnDisk);
        when(cacheConfig.getMaxEntriesLocalHeap()).thenReturn(conf_MaxEntriesLocalHeap);
        when(cacheConfig.getTimeToIdleSeconds()).thenReturn(conf_TimeToIdleSeconds);
        when(cacheConfig.getTimeToLiveSeconds()).thenReturn(conf_TimeToLiveSeconds);
        when(cacheConfig.getMaxEntriesLocalDisk()).thenReturn(conf_MaxEntriesLocalDisk);
        when(cacheConfig.getMaxEntriesLocalHeap()).thenReturn(conf_MaxEntriesLocalHeap);
        when(cacheConfig.isDiskPersistent()).thenReturn(diskPersistent);
        when(cacheConfig.isEternal()).thenReturn(eternal);
        when(cacheConfig.isOverflowToDisk()).thenReturn(overflowToDisk);
        when(cacheConfig.isOverflowToOffHeap()).thenReturn(overflowToOffHeap);

        Ehcache ehCache = Mockito.mock(Ehcache.class);

        when(ehCache.getCacheConfiguration()).thenReturn(cacheConfig);
        when(cacheStatistics.getEhcache()).thenReturn(ehCache);
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = ehCacheDiagnosticService.getDiagnosticData();

        //Test total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(37, resultDiagnosticData.size());

        assertEquals(objectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OBJECTCOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHIT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheMissPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISS_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(diskStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(memoryStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(offHeapStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(OffHeapHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(OffHeapHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(offHeapMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(writeMaxQueueSize, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEMAXQUEUE, null, null)).getDiagnosticAttributeValue());
        assertEquals(writeQueueLength, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEQUEUELENGTH, null, null)).getDiagnosticAttributeValue());

        assertEquals(conf_DiskSpoolBufferSizeMB, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKSPOOL, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_DiskExpiryThreadIntervalSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEXPIRYTHREAD, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_LoggingEnabled, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_LOGGING, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALDISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalOffHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALOFFHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxElementsOnDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_LOCALDISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_MEMORY, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_TimeToIdleSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_IDLE, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_TimeToLiveSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_LIVE, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_MEMORY, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_LOCALHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(diskPersistent, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEPERSISTENT, null, null)).getDiagnosticAttributeValue());
        assertEquals(eternal, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_ETERNAL, null, null)).getDiagnosticAttributeValue());
        assertEquals(overflowToDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_DISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(overflowToOffHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_OFFHEAP, null, null)).getDiagnosticAttributeValue());
    }
}
