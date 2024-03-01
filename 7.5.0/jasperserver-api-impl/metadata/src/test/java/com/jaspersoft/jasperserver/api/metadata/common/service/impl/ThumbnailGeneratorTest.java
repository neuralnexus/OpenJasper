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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import net.sf.jasperreports.engine.JasperPrint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ThumbnailGeneratorTest {

    @Spy
    private ThumbnailGenerationServiceImpl generationService;

    private JasperPrint jasperPrint = new JasperPrint();
    private BufferedImage fakeImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

    final int longestEdge = 300;
    final int pageHeight = 768;
    final int pageWidth = 1024;
    final float expectedZoomRatio = 299 / 1024f; // (longestEdge - 1) / max(width, height)


    @Before
    public void before() {

        // Setup JasperPrint
        jasperPrint.setPageHeight(pageHeight);
        jasperPrint.setPageWidth(pageWidth);

        // Setup ThumbnailGenerationService
        generationService.setLongestEdge(longestEdge);
        generationService.setJpgQuality(0.8f);
        doReturn(fakeImage).when(generationService).obtainImage(jasperPrint, null, expectedZoomRatio);
    }

    /**
     * This test ensures that the zoom ratio is properly calculated and used when obtaining a JasperPrint image
     */
    @Test
    public void zoomRatioCalculatedProperlyTest() {
        generationService.createThumbnail(jasperPrint, null);

        verify(generationService).createThumbnail(jasperPrint, null);
        verify(generationService).obtainImage(jasperPrint, null, expectedZoomRatio);
    }
}