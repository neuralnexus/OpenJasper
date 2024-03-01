package com.jaspersoft.buildomatic;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;

import java.util.Vector;
import java.util.Iterator;

public class LogListener extends Task {
    private int logLevel = -1;

    public void execute()
    {
        if (logLevel == -1) {
            throw new BuildException("You must specify a log level");
        }

        Vector listeners = this.getProject().getBuildListeners();
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            BuildListener listener = (BuildListener) i.next();

            if (listener instanceof BuildLogger) {
                BuildLogger logger = (BuildLogger) listener;
                logger.setMessageOutputLevel(logLevel);
            }
        }
    }

    public void setLevel(Echo.EchoLevel level) {
        this.logLevel = level.getLevel();
    }
}
