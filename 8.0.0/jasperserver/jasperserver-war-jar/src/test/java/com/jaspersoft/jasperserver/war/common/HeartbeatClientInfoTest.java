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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class HeartbeatClientInfoTest {

    private static Object TEST_OBJECT = new Object();

    private static final String TEST_NAVIGATOR_APP_NAME = "TEST_NAVIGATOR_APP_NAME";
    private static final String TEST_NAVIGATOR_APP_NAME_ALT = "TEST_NAVIGATOR_APP_NAME_ALT";

    private static final String TEST_NAVIGATOR_APP_VERSION = "TEST_NAVIGATOR_APP_VERSION";
    private static final String TEST_NAVIGATOR_APP_VERSION_ALT = "TEST_NAVIGATOR_APP_VERSION_ALT";

    private static final Locale TEST_NAVIGATOR_LOCALE = Locale.ENGLISH;
    private static final Locale TEST_NAVIGATOR_LOCALE_ALT = Locale.CHINESE;

    private static final Locale TEST_USER_LOCALE = Locale.CANADA_FRENCH;
    private static final Locale TEST_USER_LOCALE_ALT = Locale.GERMAN;

    private static final Integer TEST_SCREEN_WIDTH = 100;
    private static final Integer TEST_SCREEN_WIDTH_ALT = 1001;

    private static final Integer TEST_SCREEN_HEIGHT = 100;
    private static final Integer TEST_SCREEN_HEIGHT_ALT = 1001;

    private static final Integer TEST_SCREEN_COLOR_DEPTH = 100;
    private static final Integer TEST_SCREEN_COLOR_DEPTH_ALT = 1001;

    private static final String TEST_USER_AGENT = "TEST_USER_AGENT";
    private static final String TEST_USER_AGENT_ALT = "TEST_USER_AGENT_ALT";

    private static final String NAV_APP_NAME = "navAppName[]";
    private static final String NAV_APP_VERSION = "navAppVersion[]";
    private static final String NAV_LOCALE = "navLocale[]";
    private static final String USER_LOCALE = "userLocale[]";
    private static final String SCR_WIDTH = "scrWidth[]";
    private static final String SCR_HEIGHT = "scrHeight[]";
    private static final String SCR_COLOR_DEPTH = "scrColorDepth[]";
    private static final String USER_AGENT = "userAgent[]";
    private static final String CLIENT_COUNT = "clientCount[]";

    @Nested
    @DisplayName("accessors and mutators")
    class GetAndSet {
        @Test
        void getAndSet_instanceWithDefaultValues() {
            final HeartbeatClientInfo instance = createInstanceWithDefaultParameters();

            assertAll("an instance with default values",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getNavigatorAppName());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getNavigatorAppVersion());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getNavigatorLocale());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getUserLocale());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getScreenWidth());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getScreenHeight());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getScreenColorDepth());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getUserAgent());
                        }
                    });
        }

        @Test
        void getAndSet_fullyConfiguredInstance() {
            final HeartbeatClientInfo instance = createFullyConfiguredInstance();

            assertAll("a fully configured instance",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_NAVIGATOR_APP_NAME, instance.getNavigatorAppName());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_NAVIGATOR_APP_VERSION, instance.getNavigatorAppVersion());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_NAVIGATOR_LOCALE, instance.getNavigatorLocale());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_USER_LOCALE, instance.getUserLocale());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_SCREEN_WIDTH, instance.getScreenWidth());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_SCREEN_HEIGHT, instance.getScreenHeight());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_SCREEN_COLOR_DEPTH, instance.getScreenColorDepth());
                        }
                    },
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_USER_AGENT, instance.getUserAgent());
                        }
                    });
        }
    }

    @Nested
    @DisplayName("contributeToHttpCall")
    class ContributeToHttpCall {
        @Test
        void contributeToHttpCall_defaultConfig_defaultParamsAreContributed() {
            HeartbeatCall mockHeartBeatCall = mock(HeartbeatCall.class);

            HeartbeatClientInfo instance = createInstanceWithDefaultParameters();
            instance.contributeToHttpCall(mockHeartBeatCall);

            verify(mockHeartBeatCall).addParameter(NAV_APP_NAME, "");
            verify(mockHeartBeatCall).addParameter(NAV_APP_VERSION, "");
            verify(mockHeartBeatCall).addParameter(NAV_LOCALE, "");
            verify(mockHeartBeatCall).addParameter(USER_LOCALE, "");
            verify(mockHeartBeatCall).addParameter(SCR_WIDTH, "");
            verify(mockHeartBeatCall).addParameter(SCR_HEIGHT, "");
            verify(mockHeartBeatCall).addParameter(SCR_COLOR_DEPTH, "");
            verify(mockHeartBeatCall).addParameter(USER_AGENT, "");
            verify(mockHeartBeatCall).addParameter(CLIENT_COUNT, "0");
        }

        @Test
        void contributeToHttpCall_someConfig_paramsAreContributed() {
            HeartbeatCall mockHeartBeatCall = mock(HeartbeatCall.class);

            HeartbeatClientInfo instance = createFullyConfiguredInstance();
            instance.contributeToHttpCall(mockHeartBeatCall);

            verify(mockHeartBeatCall).addParameter(NAV_APP_NAME, TEST_NAVIGATOR_APP_NAME);
            verify(mockHeartBeatCall).addParameter(NAV_APP_VERSION, TEST_NAVIGATOR_APP_VERSION);
            verify(mockHeartBeatCall).addParameter(NAV_LOCALE, TEST_NAVIGATOR_LOCALE.toString());
            verify(mockHeartBeatCall).addParameter(USER_LOCALE, TEST_USER_LOCALE.toString());
            verify(mockHeartBeatCall).addParameter(SCR_WIDTH, TEST_SCREEN_WIDTH.toString());
            verify(mockHeartBeatCall).addParameter(SCR_HEIGHT, TEST_SCREEN_HEIGHT.toString());
            verify(mockHeartBeatCall).addParameter(SCR_COLOR_DEPTH, TEST_SCREEN_COLOR_DEPTH.toString());
            verify(mockHeartBeatCall).addParameter(USER_AGENT, TEST_USER_AGENT);
            verify(mockHeartBeatCall).addParameter(CLIENT_COUNT, "1");
        }

    }

    @Nested
    @DisplayName("key")
    class Key {
        @Test
        void key_defaultConfig_keyForDefaultConfig() {
            HeartbeatClientInfo instance = createInstanceWithDefaultParameters();

            String key = instance.getKey();
            assertEquals(constructKeyForInstanceWithDefaultValues(), key);
        }

        @Test
        void key_someConfig_keyForConfig() {
            HeartbeatClientInfo instance = createFullyConfiguredInstance();

            String key = instance.getKey();
            assertEquals(constructKeyForInstanceWithTestValues(), key);
        }
    }

    @TestInstance(PER_CLASS)
    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        void instancesWithDifferentParametersAreNotEqual() {
            HeartbeatClientInfo fullyConfiguredTestInstance = createFullyConfiguredInstance();
            HeartbeatClientInfo testInstanceWithDefaultParameters = createInstanceWithDefaultParameters();

            assertNotEquals(fullyConfiguredTestInstance, testInstanceWithDefaultParameters);
            assertNotEquals(testInstanceWithDefaultParameters, fullyConfiguredTestInstance);
        }

        @ParameterizedTest
        @MethodSource(value = "prepareInstancesWithAlternativeParameters")
        void instancesWithDifferentParametersAreNotEqual(HeartbeatClientInfo instance) {
            HeartbeatClientInfo fullyConfiguredTestInstance = createFullyConfiguredInstance();

            assertNotEquals(instance, fullyConfiguredTestInstance);
            assertNotEquals(fullyConfiguredTestInstance, instance);
        }

        @Test
        void instancesWithDefaultParametersAreEquals() {
            HeartbeatClientInfo testInstanceWithDefaultParameters = createInstanceWithDefaultParameters();
            HeartbeatClientInfo otherTestInstanceWithDefaultParameters = createInstanceWithDefaultParameters();

            assertEquals(testInstanceWithDefaultParameters, otherTestInstanceWithDefaultParameters);
        }

        @Test
        void instanceIsEqualsToItself() {
            HeartbeatClientInfo fullyConfiguredTestInstance = createFullyConfiguredInstance();

            assertEquals(fullyConfiguredTestInstance, fullyConfiguredTestInstance);
        }

        @Test
        void instanceIsNotEqualsToNull() {
            assertNotEquals(null, createFullyConfiguredInstance());
        }

        @Test
        void instanceIsNotEqualsToObject() {
            HeartbeatClientInfo fullyConfiguredTestInstance = createFullyConfiguredInstance();

            assertNotEquals(fullyConfiguredTestInstance, TEST_OBJECT);
        }

        private List<HeartbeatClientInfo> prepareInstancesWithAlternativeParameters() {
            return Arrays.asList(
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorAppName(TEST_NAVIGATOR_APP_NAME_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorAppVersion(TEST_NAVIGATOR_APP_VERSION_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorLocale(TEST_NAVIGATOR_LOCALE_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setUserLocale(TEST_USER_LOCALE_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenWidth(TEST_SCREEN_WIDTH_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenHeight(TEST_SCREEN_HEIGHT_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenColorDepth(TEST_SCREEN_COLOR_DEPTH_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setUserAgent(TEST_USER_AGENT_ALT);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorAppName(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorAppVersion(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setNavigatorLocale(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setUserLocale(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenWidth(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenHeight(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setScreenColorDepth(null);
                            return instance;
                        }
                    }),
                    mutateFullyConfiguredInstance(new Mutator() {
                        @Override
                        public HeartbeatClientInfo mutate(HeartbeatClientInfo instance) {
                            instance.setUserAgent(null);
                            return instance;
                        }
                    })
            );
        }
    }

    @Nested
    @DisplayName("hash code")
    class HashCode {
        @Test
        public void equalInstancesWithDefaultParametersHaveEqualHashCodes() {
            HeartbeatClientInfo first = createInstanceWithDefaultParameters();
            HeartbeatClientInfo second = createInstanceWithDefaultParameters();

            assertEquals(first.hashCode(), second.hashCode());
        }

        @Test
        public void twoFullyConfiguredInstancesHaveEqualHashCodes() {
            HeartbeatClientInfo first = createFullyConfiguredInstance();
            HeartbeatClientInfo second = createFullyConfiguredInstance();

            assertEquals(first.hashCode(), second.hashCode());
        }
    }

    /*
     * Helpers
     */

    private static HeartbeatClientInfo createInstanceWithDefaultParameters() {
        return new HeartbeatClientInfo();
    }

    private static HeartbeatClientInfo createFullyConfiguredInstance() {
        HeartbeatClientInfo instance = new HeartbeatClientInfo();

        instance.incrementCount();

        instance.setNavigatorAppName(TEST_NAVIGATOR_APP_NAME);
        instance.setNavigatorAppVersion(TEST_NAVIGATOR_APP_VERSION);
        instance.setNavigatorLocale(TEST_NAVIGATOR_LOCALE);
        instance.setUserLocale(TEST_USER_LOCALE);
        instance.setScreenWidth(TEST_SCREEN_WIDTH);
        instance.setScreenHeight(TEST_SCREEN_HEIGHT);
        instance.setScreenColorDepth(TEST_SCREEN_COLOR_DEPTH);
        instance.setUserAgent(TEST_USER_AGENT);

        return instance;
    }

    private static HeartbeatClientInfo mutateFullyConfiguredInstance(Mutator mutator) {
        return mutator.mutate(createFullyConfiguredInstance());
    }

    private String constructKeyForInstanceWithDefaultValues() {
        return "null|null|null|null|null|null|null|null";
    }

    private String constructKeyForInstanceWithTestValues() {
        return "TEST_NAVIGATOR_APP_NAME|TEST_NAVIGATOR_APP_VERSION|en|fr_CA|100|100|100|TEST_USER_AGENT";
    }

    private interface Mutator {
        HeartbeatClientInfo mutate(HeartbeatClientInfo instance);
    }

}