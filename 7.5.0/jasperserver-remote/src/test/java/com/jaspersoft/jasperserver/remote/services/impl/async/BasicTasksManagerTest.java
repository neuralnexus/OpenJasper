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

package com.jaspersoft.jasperserver.remote.services.impl.async;

import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException;
import com.jaspersoft.jasperserver.remote.services.async.BasicTasksManager;
import com.jaspersoft.jasperserver.remote.services.async.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author inesterenko
 */

public class BasicTasksManagerTest {

    private BasicTasksManager manager;
    private Task task;
    private Task taskMock = mock(Task.class);

    @Before
    public void setUp(){
        manager = new BasicTasksManager();
    }

    @Test
    public void startTask_Normal(){
        Task task = mock(Task.class);
        when(task.getState()).thenReturn(new State());
        State uuid = manager.startTask(task);
        verify(task, times(1)).start(manager.getExecutor());
        assertNotNull(uuid);
        assertEquals(task.getState().getPhase(), Task.INPROGRESS);
    }

    @Test
    public void getTask() throws NoSuchTaskException {
        setUpTasks();
        assertEquals(manager.getTask("1"), task);
    }

    @Test(expected = NoSuchTaskException.class)
    public void getTask_NoTasks() throws NoSuchTaskException {
        manager.getTask("0");
    }

    private void setUpTasks() {
        ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<String, Task>();
        taskMock = mock(Task.class);
        when(taskMock.getUniqueId()).thenReturn("1");
        State state = new State();
        state.setPhase(Task.INPROGRESS);
        when(taskMock.getState()).thenReturn(state);
        task = taskMock;
        tasks.put(task.getUniqueId(), task);
        manager = new BasicTasksManager(tasks);
    }

    @Test(expected = NoSuchTaskException.class)
    public void finishTask() throws NoSuchTaskException {
        setUpTasks();
        manager.finishTask("1");
        verify(taskMock, times(1)).stop();
        manager.getTask("1");
    }

    @Test
    public void getTaskState() throws NoSuchTaskException {
        setUpTasks();
        assertEquals(manager.getTaskState("1").getPhase(), Task.INPROGRESS);
    }

}
