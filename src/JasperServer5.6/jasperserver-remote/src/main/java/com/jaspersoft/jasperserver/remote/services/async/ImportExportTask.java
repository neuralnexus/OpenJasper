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

import com.jaspersoft.jasperserver.remote.exception.NoResultException;
import com.jaspersoft.jasperserver.remote.exception.NotReadyResultException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/*
*  @author inesterenko
*/
public class ImportExportTask<T> implements Task {

    protected final static Log log = LogFactory.getLog(Task.class);

    protected String uuid;
    protected BaseImportExportTaskRunnable<T> taskRunner;
    protected Thread thread;

    public ImportExportTask(BaseImportExportTaskRunnable<T> taskRunner) {
        this.taskRunner = taskRunner;
        this.thread = new Thread(taskRunner);
    }

    @Override
    public String getUniqueId() {
        return uuid;
    }

    @Override
    public void setUniqueId(String uuid) {
       this.uuid = uuid;
    }

    @Override
    public StateDto getState() {
        return taskRunner.getState();
    }

    @Override
    public T getResult() throws NotReadyResultException, NoResultException {
        return (T)taskRunner.getResult();
    }

    @Override
    public void start() {
        taskRunner.prepare();
        thread.start();
    }

    @Override
    public void stop() {
        if(!thread.isInterrupted()){
            thread.interrupt();
        }
        if (!taskRunner.getState().getPhase().equals(INPROGRESS)) {
            new Remover(taskRunner.getFile()).start();
        }
    }

    protected class Remover extends Thread {
        private final File toRemove;
        private int counter = 0;

        public Remover(File toRemove) {
            this.toRemove = toRemove;
        }

        @Override
        public void run() {
            try {
                while (!toRemove.delete()) {
                    Thread.sleep(10000);
                    if (counter++ > 100) {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                log.error("File " + toRemove.getAbsolutePath() + " was not deleted!");
            }
        }
    }
}

