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
package com.jaspersoft.jasperserver.api.logging.diagnostic.helper;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author ogavavka
 */
public class OperatingSystemMXBeanWrapper{
    OperatingSystemMXBean osBean= ManagementFactory.getOperatingSystemMXBean();
    enum JVMTypes {
        sun, ibm, other
    }
    JVMTypes jvmType;
    public OperatingSystemMXBeanWrapper() {
        jvmType =JVMTypes.other;
        if (osBean.getClass().getName().equals("com.sun.management.OperatingSystem"))
            jvmType = JVMTypes.sun;
        if (osBean.getClass().getName().equals("com.ibm.lang.management.OperatingSystem"))
            jvmType = JVMTypes.ibm;
    }
    public String getArch() {


        return osBean.getArch();
    }
    public int getAvailableProcessors() {
        return osBean.getAvailableProcessors();
    }

    public String getName() {
        return osBean.getName();
    }

    public String getVersion() {
        return osBean.getVersion();
    }

    public double getSystemLoadAverage() {
        return osBean.getSystemLoadAverage();
    }
    public long getTotalPhysicalMemorySize() {
        Long physicalMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            physicalMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getTotalPhysicalMemorySize();
        }
        return physicalMemorySize;
    }
    public long getTotalSwapSpaceSize() {
        Long swapSpaceSize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            swapSpaceSize = ((com.sun.management.OperatingSystemMXBean)osBean).getTotalSwapSpaceSize();
        }
        return swapSpaceSize;
    }
    public long getFreeSwapSpaceSize() {
        Long freeSwapSpaceSize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            freeSwapSpaceSize = ((com.sun.management.OperatingSystemMXBean)osBean).getFreeSwapSpaceSize();
        }
        return freeSwapSpaceSize;
    }
    public long getFreePhysicalMemorySize() {
        Long freePhysicalMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            freePhysicalMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getFreePhysicalMemorySize();
        }
        return freePhysicalMemorySize;
    }
    public long getProcessCpuTime() {
        Long processCpuTime = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            processCpuTime = ((com.sun.management.OperatingSystemMXBean)osBean).getProcessCpuTime();
        }
        return processCpuTime;
    }
    public long getCommittedVirtualMemorySize() {
        Long committedVirtualMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            committedVirtualMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getCommittedVirtualMemorySize();
        }
        return committedVirtualMemorySize;
    }
}
