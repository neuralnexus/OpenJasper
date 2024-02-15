/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.remote.services.async;

import com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException;

import java.util.Set;

/**
 *
 * Provides api for asynchronous processes.
 *
 */
public interface TasksManager {

    /**
     * Get task's uuids from storage
     *
     * @return List of ids
     */


    Set<String> getTaskIds();

    /**
     * Get task's by uuid from storage
     *
     *
     * @param taskId- contains some time consuming action
     * @throws com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException
     * @return Task
     */


    Task getTask(String taskId) throws NoSuchTaskException;

    /**
     * Put task to storage and starts provided task
     *
     *
     * @param task- contains some time consuming action
     * @return uuid for task
     */

    StateDto startTask(Task task);

    /**
     * Finish selected task, does clean up of used resources
     *
     *
     * @param taskId - uuid of task
     * @throws com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException
     */

    void finishTask(String taskId) throws NoSuchTaskException;

    /**
     * Returns state of task with provided id.
     *
     *
     * @param taskId - uuid of task
     * @return current state of task
     * @throws com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException
     */
    StateDto getTaskState(String taskId) throws NoSuchTaskException;

}