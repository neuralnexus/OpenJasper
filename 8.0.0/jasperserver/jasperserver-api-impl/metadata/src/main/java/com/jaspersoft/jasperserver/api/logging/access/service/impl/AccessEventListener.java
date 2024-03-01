/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.api.logging.access.service.impl;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryExtendListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Debasish Halder
 * @version $Id$
 */
public class AccessEventListener extends RepositoryEventListenerSupport implements RepositoryExtendListener {
  private AccessService accessService;
  private ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  public void onResourceDelete(Class resourceItf, String resourceURI) {
    accessService.deleteAccessEvent(resourceURI,false);
  }
  @Override
  public void onFolderDelete(String folderURI) {
    accessService.deleteAccessEvent(folderURI,true);
  }

  @Override
  public void folderMoved(FolderMoveEvent folderMove) {

  }

  @Override
  public void resourceMoved(ResourceMoveEvent resourceMove) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        accessService.updateAccessEventsByResourceURI(resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
      }
    });
    }

  @Override
  public void resourceCopied(ResourceCopiedEvent event) {

  }

  public AccessService getAccessService() {
    return accessService;
  }

  public void setAccessService(AccessService accessService) {
    this.accessService = accessService;
  }
}
