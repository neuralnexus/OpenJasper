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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid;

// import org.mockito.Mockito;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;

public class FakeWorkManager implements WorkManager {
	private Thread t;
	
	@Override
	public void doWork(Work arg0) throws WorkException {
		execute(arg0, null, true);
	}

	@Override
	public void doWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		execute(arg0, arg3, true);
	}

	@Override
	public void scheduleWork(Work arg0) throws WorkException {
		execute(arg0, null, false);
	}

	@Override
	public void scheduleWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		execute(arg0, arg3, false);
	}

	@Override
	public long startWork(Work arg0) throws WorkException {
		execute(arg0, null, false);
		return 0;
	}

	@Override
	public long startWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		execute(arg0, arg3, false);
		return 0;
	}

	void execute(final Work arg0, final WorkListener arg3, boolean join) throws WorkException {
		if (arg3 != null) {
		//	arg3.workAccepted(Mockito.mock(WorkEvent.class));
		//	arg3.workStarted(Mockito.mock(WorkEvent.class));
		}
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				arg0.run();
				if (arg3 != null) {
			//		arg3.workCompleted(Mockito.mock(WorkEvent.class));
				}							
			}
		});
		t.start();
		if (join) {
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new WorkException(e);
			}
		}
	}
		
}
