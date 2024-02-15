package com.jaspersoft.jasperserver.remote.services.impl.async;

import com.jaspersoft.jasperserver.remote.exception.NoSuchTaskException;
import com.jaspersoft.jasperserver.remote.services.async.BasicTasksManager;
import com.jaspersoft.jasperserver.remote.services.async.StateDto;
import com.jaspersoft.jasperserver.remote.services.async.Task;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.core.MockObject;

import java.util.concurrent.ConcurrentHashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author inesterenko
 */

public class BasicTasksManagerTest {

    private BasicTasksManager manager;
    private Task task;
    private MockObject<Task> taskMock;

    @Before
    public void setUp(){
        manager = new BasicTasksManager();
    }

    @Test
    public void startTask_Normal(){
        MockObject<Task> task = new MockObject<Task>(Task.class, this);
        Task mock = task.getMock();
        task.returns(new StateDto()).getState();
        StateDto uuid = manager.startTask(mock);
        task.assertInvoked().start();
        assertNotNull(uuid);
        assertEquals(mock.getState().getPhase(), Task.INPROGRESS);
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
        taskMock = (new MockObject<Task>(Task.class, this));
        taskMock.returns("1").getUniqueId();
        StateDto stateDto = new StateDto();
        stateDto.setPhase(Task.INPROGRESS);
        taskMock.returns(stateDto).getState();
        task = taskMock.getMock();
        tasks.put(task.getUniqueId(), task);
        manager = new BasicTasksManager(tasks);
    }

    @Test(expected = NoSuchTaskException.class)
    public void finishTask() throws NoSuchTaskException {
        setUpTasks();
        manager.finishTask("1");
        taskMock.assertInvoked().stop();
        manager.getTask("1");
    }

    @Test
    public void getTaskState() throws NoSuchTaskException {
        setUpTasks();
        assertEquals(manager.getTaskState("1").getPhase(), Task.INPROGRESS);
    }

}
