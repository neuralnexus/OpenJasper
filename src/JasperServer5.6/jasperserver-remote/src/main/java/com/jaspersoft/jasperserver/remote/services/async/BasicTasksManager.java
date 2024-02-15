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
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
*  @author inesterenko
*/

@Component
public class BasicTasksManager implements TasksManager {

    protected Map<String, Task> tasks = new ConcurrentHashMap<String, Task>();

    public BasicTasksManager(Map<String, Task> tasks) {
        this.tasks = new ConcurrentHashMap<String, Task>(tasks);
    }

    public BasicTasksManager() {
        super();
    }

    protected  String generateUniqueId(){
        String uuid = UUID.randomUUID().toString();
        if (tasks.containsKey(uuid)) {
            uuid = generateUniqueId();
        }
        return uuid;
    }

    @Override
    public StateDto startTask(Task task) {
        StateDto stateDto = task.getState();
        stateDto.setPhase(Task.INPROGRESS);
        String uuid = generateUniqueId();
        task.setUniqueId(uuid);
        stateDto.setId(uuid);
        tasks.put(uuid, task);
        task.start();
        return stateDto;
    }

    @Override
    public Set<String> getTaskIds() {
        return Collections.unmodifiableSet(tasks.keySet());
    }

    @Override
    public Task getTask(String taskId) throws NoSuchTaskException {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        } else {
            throw new NoSuchTaskException(taskId);
        }
    }

    @Override
    public void finishTask(String taskId) throws NoSuchTaskException {
        getTask(taskId).stop();
        tasks.remove(taskId);
    }

    public StateDto getTaskState(String taskId) throws NoSuchTaskException {
        return getTask(taskId).getState();
    }
}
