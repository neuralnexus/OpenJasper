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
package com.jaspersoft.jasperserver.dto.utils;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
class CloneOperationsContext {

    interface Executable<T> {
        T execute(T operand);
    }

    // Contains copy operations for all supported classes
    private static Map<Class, Executable> cloneOperationsByType = new HashMap<Class, Executable>();

    static {
        cloneOperationsByType.put(DeepCloneable.class, new Executable<DeepCloneable>() {
            @Override
            public DeepCloneable execute(DeepCloneable operand) {
                return operand.deepClone();
            }
        });
        cloneOperationsByType.put(List.class, new Executable<List>() {
            @Override
            public List execute(List operand) {
                List copied = createInstanceOfClass(operand.getClass(), ArrayList.class);
                for (Object item : operand) {
                    copied.add(copyOf(item));
                }
                return copied;
            }
        });
        cloneOperationsByType.put(Map.class, new Executable<Map>() {
            @Override
            public Map execute(Map operand) {
                Map copied = createInstanceOfClass(operand.getClass(), HashMap.class);
                for (Object key : operand.keySet()) {
                    Object value = operand.get(key);
                    copied.put(key, copyOf(value));
                }
                return copied;
            }
        });
        cloneOperationsByType.put(Set.class, new Executable<Set>() {
            @Override
            public Set execute(Set operand) {
                Set copied = createInstanceOfClass(operand.getClass(), TreeSet.class);
                for (Object item : operand) {
                    copied.add(copyOf(item));
                }
                return copied;
            }
        });
        cloneOperationsByType.put(Queue.class, new Executable<Queue>() {
            @Override
            public Queue execute(Queue operand) {
                Queue copied = createInstanceOfClass(operand.getClass(), ArrayBlockingQueue.class);
                for (Object item : operand) {
                    copied.add(copyOf(item));
                }
                return copied;
            }
        });
        cloneOperationsByType.put(Timestamp.class, new Executable<Timestamp>() {
            @Override
            public Timestamp execute(Timestamp operand) {
                return new Timestamp(operand.getTime());
            }
        });
        cloneOperationsByType.put(Date.class, new Executable<Date>() {
            @Override
            public Date execute(Date operand) {
                return (Date) operand.clone();
            }
        });
        cloneOperationsByType.put(TimeZone.class, new Executable<TimeZone>() {
            @Override
            public TimeZone execute(TimeZone operand) {
                return (TimeZone) operand.clone();
            }
        });
        cloneOperationsByType.put(Calendar.class, new Executable<Calendar>() {
            @Override
            public Calendar execute(Calendar operand) {
                return (Calendar) operand.clone();
            }
        });
        cloneOperationsByType.put(boolean[].class, new Executable<boolean[]>() {
            @Override
            public boolean[] execute(boolean[] operand) {
                return Arrays.copyOf(operand, operand.length);
            }
        });
        cloneOperationsByType.put(int[].class, new Executable<int[]>() {
            @Override
            public int[] execute(int[] operand) {
                return Arrays.copyOf(operand, operand.length);
            }
        });
        cloneOperationsByType.put(Object[].class, new Executable<Object[]>() {
            @Override
            public Object[] execute(Object[] operand) {
                if (operand instanceof DeepCloneable[]) {
                    DeepCloneable[] original = (DeepCloneable[]) operand;
                    DeepCloneable[] copied = (DeepCloneable[]) Array.newInstance(DeepCloneable.class, original.length);

                    int i = 0;
                    for (DeepCloneable item : original) {
                        copied[i++] = item.deepClone();
                    }
                    return copied;
                } else {
                    return Arrays.copyOf(operand, operand.length);
                }
            }
        });
    }

    <T> Executable<T> operationForClass(Class<T> klass) {
        for (Class c : cloneOperationsByType.keySet()) {
            if (c.isAssignableFrom(klass)) {
                return cloneOperationsByType.get(c);
            }
        }
        return null;
    }

    private static <T> T createInstanceOfClass(Class klass, Class defaultKlass) {
        T copied;

        try {

            Constructor<T> defaultConstructor = null;
            Constructor<T>[] publicConstructors = klass.getConstructors();

            if (publicConstructors.length == 0) {
                defaultConstructor = defaultKlass.getConstructor();
            } else {
                for (Constructor<T> constructor : publicConstructors) {
                    if (constructor.getParameterTypes().length == 0) {
                        defaultConstructor = constructor;
                        break;
                    }
                }
            }
            copied = defaultConstructor.newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return copied;
    }
    
}
