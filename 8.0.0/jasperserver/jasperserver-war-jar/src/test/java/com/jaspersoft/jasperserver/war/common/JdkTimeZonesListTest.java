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

import com.jaspersoft.jasperserver.war.dto.StringOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class JdkTimeZonesListTest {

    private JdkTimeZonesList objectUnderTest = new JdkTimeZonesList();

    private static final List<String> AVAILABLE_TIMEZONE_IDS = asList(TimeZone.getAvailableIDs());

    private static final String TEST_TIMEZONE_ID = "America/Los_Angeles";
    private static final TimeZone TEST_TIMEZONE_FROM_NAME = TimeZone.getTimeZone(TEST_TIMEZONE_ID);
    private static final TimeZone TEST_TIMEZONE_FROM_GMT = TimeZone.getTimeZone("GMT+05:00");
    private static final List<String> TEST_TIMEZONE_IDS = asList(
            TEST_TIMEZONE_FROM_NAME.getID(),
            TEST_TIMEZONE_FROM_GMT.getID()
    );

    private static final List<String> TEST_TIMEZONE_IDS_WITH_DEFAULT_TIME_ZONE = asList(
            TEST_TIMEZONE_FROM_NAME.getID(),
            TimeZone.getDefault().getID()
    );

    private static final List<String> TEST_TIMEZONE_IDS_EMPTY = new ArrayList<String>();

    private static final Locale TEST_LOCALE = Locale.CHINA;
    private static final Locale TEST_LOCALE_ALT = Locale.JAPANESE;

    @Nested
    @DisplayName("accessors and mutators")
    class GetAndSet {
        @Test
        void getAndSet_instanceWithDefaultValues() {
            final JdkTimeZonesList instance = new JdkTimeZonesList();

            assertAll("an instance with default values",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getTimeZonesIds());
                        }
                    });
        }

        @Test
        void getAndSet_fullyConfiguredInstance() {
            final JdkTimeZonesList instance = new JdkTimeZonesList();
            instance.setTimeZonesIds(TEST_TIMEZONE_IDS);

            assertAll("a fully configured instance",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_TIMEZONE_IDS, instance.getTimeZonesIds());
                        }
                    });
        }
    }

    @Nested
    @DisplayName("afterPropertiesSet")
    class AfterPropertiesSet {
        @Test
        void afterPropertiesSet_timeZonesIdsIsNull_availableTimezoneIds() throws Exception {
            objectUnderTest.afterPropertiesSet();

            List actual = objectUnderTest.getTimeZonesIds();
            assertEquals(AVAILABLE_TIMEZONE_IDS, actual);
        }

        @Test
        void afterPropertiesSet_timeZonesIdsIsEmptyList_emptyList() throws Exception {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_EMPTY);

            objectUnderTest.afterPropertiesSet();

            List actual = objectUnderTest.getTimeZonesIds();
            assertEquals(TEST_TIMEZONE_IDS_EMPTY, actual);
        }

        @Test
        void afterPropertiesSet_timeZonesIdsIsSomeIds_timezoneIds() throws Exception {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS);

            objectUnderTest.afterPropertiesSet();

            List actual = objectUnderTest.getTimeZonesIds();
            assertEquals(TEST_TIMEZONE_IDS, actual);
        }
    }

    @Nested
    @DisplayName("loadTimeZone")
    class LoadTimeZone {
        @Test
        void loadTimeZone_someTimezoneId_timeZoneForProvidedId() {
            TimeZone actual = objectUnderTest.loadTimeZone(TEST_TIMEZONE_ID);

            assertEquals(TimeZone.getTimeZone(TEST_TIMEZONE_ID), actual);
        }

        @Test
        void loadTimeZone_null_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.loadTimeZone(null);
                }
            });
        }
    }

    @Nested
    @DisplayName("find existing default timezone")
    class FindExistingDefaultTZ {
        @Test
        void findExistingDefaultTZ_timeZonesIdsIsNull_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.findExistingDefaultTZ();
                }
            });
        }

        @Test
        void findExistingDefaultTZ_timeZonesIdsIsEmptyList_null() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_EMPTY);

            TimeZone existingDefaultTZ = objectUnderTest.findExistingDefaultTZ();

            assertNull(existingDefaultTZ);
        }

        @Test
        void findExistingDefaultTZ_timeZonesIdsContainsNotDefaultTimeZoneId_null() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS);

            TimeZone actual = objectUnderTest.findExistingDefaultTZ();

            assertNull(actual);
        }

        @Test
        void findExistingDefaultTZ_timeZonesIdsContainsDefaultTimeZoneId_defaultTimeZone() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_WITH_DEFAULT_TIME_ZONE);

            TimeZone actual = objectUnderTest.findExistingDefaultTZ();

            assertEquals(TimeZone.getDefault(), actual);
        }
    }

    @Nested
    @DisplayName("create TimeZones")
    class CreateTimeZones {
        @Test
        void createTimeZones_timeZonesIdsIsNull_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.createTimeZones(TEST_LOCALE);
                }
            });
        }

        @Test
        void createTimeZones_timeZonesIdsIsEmptyList_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_EMPTY);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOption = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));
            List<StringOption> expected = asList(expectedOption);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.createTimeZones(TEST_LOCALE);

            assertEquals(expected, actual);
        }

        @Test
        void createTimeZones_timeZonesIdIsSomeIds_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOptionForDefaultTimeZone = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));

            StringOption expectedOption1 = new StringOption(TEST_TIMEZONE_FROM_NAME.getID(), TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE));
            StringOption expectedOption2 = new StringOption(TEST_TIMEZONE_FROM_GMT.getID(), TEST_TIMEZONE_FROM_GMT.getDisplayName(TEST_LOCALE));

            List<StringOption> expected = asList(expectedOptionForDefaultTimeZone, expectedOption1, expectedOption2);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.createTimeZones(TEST_LOCALE);

            assertEquals(expected, actual);
        }

        @Test
        void createTimeZones_timeZonesIdContainsDefaultTZId_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_WITH_DEFAULT_TIME_ZONE);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOptionForDefaultTimeZone = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));

            StringOption expectedOption = new StringOption(TEST_TIMEZONE_FROM_NAME.getID(), TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE));

            List<StringOption> expected = asList(expectedOption, expectedOptionForDefaultTimeZone);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.createTimeZones(TEST_LOCALE);

            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("create option")
    class CreateOption {
        @Test
        void createOption_nullValues_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.createOption(null, null);
                }
            });
        }

        @Test
        void createOption_testLocaleAndDefaultTZ_option() {
            StringOption expected = new StringOption(TEST_TIMEZONE_FROM_NAME.getID(), TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE));

            StringOption actual = objectUnderTest.createOption(TEST_LOCALE, TEST_TIMEZONE_FROM_NAME);

            assertEquals(expected, actual);
        }
    }

    @Test
    void getTimeZoneDescription() {
        String expected = TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE);

        String result = objectUnderTest.getTimeZoneDescription(TEST_TIMEZONE_FROM_NAME, TEST_LOCALE);

        assertEquals(expected, result);
    }

    @Nested
    @DisplayName("get default TimeZone ID")
    class GetDefaultTimeZoneID {
        @Test
        void getDefaultTimeZoneID_timeZoneIdsIsNull_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.getDefaultTimeZoneID();
                }
            });
        }

        @Test
        void getDefaultTimeZoneID_timeZonesIdIsEmptyList_defaultTZid() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_EMPTY);

            String actual = objectUnderTest.getDefaultTimeZoneID();

            assertEquals(TimeZone.getDefault().getID(), actual);
        }

        @Test
        void getDefaultTimeZoneID_testTimeZonesIdIsSomeIds_defaultTZid() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS);

            String actual = objectUnderTest.getDefaultTimeZoneID();

            assertEquals(TimeZone.getDefault().getID(), actual);
        }

        @Test
        void getDefaultTimeZoneID_testTimeZonesContainsDefaultTZ_defaultTZid() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_WITH_DEFAULT_TIME_ZONE);

            String actual = objectUnderTest.getDefaultTimeZoneID();

            assertEquals(TimeZone.getDefault().getID(), actual);
        }
    }

    @Nested
    @DisplayName("get timeZones")
    class getTimeZones {
        @Test
        void getTimeZones_timeZonesIdsIsNull_throwsException() {
            Assertions.assertThrows(NullPointerException.class, new Executable() {
                @Override
                public void execute() {
                    objectUnderTest.getTimeZones(TEST_LOCALE);
                }
            });
        }

        @Test
        void getTimeZones_timeZonesIdsIsEmptyList_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_EMPTY);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOption = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));
            List<StringOption> expected = asList(expectedOption);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.getTimeZones(TEST_LOCALE);
            assertEquals(expected, actual);
        }

        @Test
        void getTimeZones_timeZonesIdsIsSomeIds_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOptionForDefaultTimeZone = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));

            StringOption expectedOption1 = new StringOption(TEST_TIMEZONE_FROM_NAME.getID(), TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE));
            StringOption expectedOption2 = new StringOption(TEST_TIMEZONE_FROM_GMT.getID(), TEST_TIMEZONE_FROM_GMT.getDisplayName(TEST_LOCALE));

            List<StringOption> expected = asList(expectedOptionForDefaultTimeZone, expectedOption1, expectedOption2);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.getTimeZones(TEST_LOCALE);
            assertEquals(expected, actual);
        }

        @Test
        void getTimeZones_timeZonesIdsContainsDefaultTZ_stringOptions() {
            objectUnderTest.setTimeZonesIds(TEST_TIMEZONE_IDS_WITH_DEFAULT_TIME_ZONE);

            TimeZone defaultTZ = TimeZone.getDefault();
            StringOption expectedOptionForDefaultTimeZone = new StringOption(defaultTZ.getID(), defaultTZ.getDisplayName(TEST_LOCALE));

            StringOption expectedOption = new StringOption(TEST_TIMEZONE_FROM_NAME.getID(), TEST_TIMEZONE_FROM_NAME.getDisplayName(TEST_LOCALE));

            List<StringOption> expected = asList(expectedOption, expectedOptionForDefaultTimeZone);

            List<StringOption> actual = (List<StringOption>)objectUnderTest.getTimeZones(TEST_LOCALE);

            assertEquals(expected, actual);
        }
    }

}