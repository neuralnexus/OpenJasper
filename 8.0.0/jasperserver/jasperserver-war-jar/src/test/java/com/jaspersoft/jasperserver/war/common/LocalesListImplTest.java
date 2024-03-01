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

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
class LocalesListImplTest {

    private final LocaleHelper LOCALE_HELPER = LocaleHelper.getInstance();

    private static final Locale TEST_LOCALE = Locale.CANADA;
    private static final Locale TEST_DISPLAY_LOCALE = Locale.CHINA;
    private static final Locale TEST_LOCALE_WITHOUT_LANGUAGE = new Locale("", TEST_LOCALE.getCountry());
    private static final Locale TEST_LOCALE_WITHOUT_COUNTRY = new Locale(TEST_LOCALE.getLanguage(), "");
    private static final Locale TEST_DISPLAY_LOCALE_WITHOUT_COUNTRY = new Locale(TEST_DISPLAY_LOCALE.getLanguage(), "");
    private static final List<Locale> TEST_LOCALES = asList(
            TEST_LOCALE,
            TEST_DISPLAY_LOCALE,
            TEST_LOCALE_WITHOUT_LANGUAGE,
            TEST_LOCALE_WITHOUT_COUNTRY,
            TEST_DISPLAY_LOCALE_WITHOUT_COUNTRY
    );
    private LocalesListImpl objectUnderTest = new LocalesListImpl();

    @Nested
    @DisplayName("accessors and mutators")
    class GetAndSet {
        @Test
        void getAndSet_instanceWithDefaultValues() {
            final LocalesListImpl instance = new LocalesListImpl();

            assertAll("an instance with default values",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertNull(instance.getLocales());
                        }
                    });
        }

        @Test
        void getAndSet_fullyConfiguredInstance() {
            final LocalesListImpl instance = new LocalesListImpl();
            instance.setLocales(TEST_LOCALES);

            assertAll("a fully configured instance",
                    new Executable() {
                        @Override
                        public void execute() {
                            assertEquals(TEST_LOCALES, instance.getLocales());
                        }
                    });
        }
    }

    @Nested
    @DisplayName("get user Locale")
    class GetUserLocale {
        @Test
        void getUserLocale_localeIsNullAndDisplayLocaleIsSomeValue_throwsException() {
            assertThrows(
                    NullPointerException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            objectUnderTest.getUserLocale(null, TEST_DISPLAY_LOCALE);
                        }
                    }
            );
        }

        @Test
        void getUserLocale_localeIsSomeValeAndDisplayLocaleIsNull_throwsException() {
            assertThrows(
                    NullPointerException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            objectUnderTest.getUserLocale(TEST_LOCALE, null);
                        }
                    }
            );
        }

        @Test
        void getUserLocale_localeIsSomeValueAndDisplayLocaleIsSomeValue_UserLocalInstance() {
            UserLocale expected = new UserLocale(
                    LOCALE_HELPER.getCode(TEST_LOCALE),
                    TEST_LOCALE.getDisplayName(TEST_DISPLAY_LOCALE)
            );

            UserLocale actual = objectUnderTest.getUserLocale(TEST_LOCALE, TEST_DISPLAY_LOCALE);

            assertUserLocaleEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("get user Locales")
    class getUserLocales {
        @Test
        void getUserLocales_localesIsNull_displayLocaleIsNull_throwsException() {
            assertThrows(
                    NullPointerException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            objectUnderTest.getUserLocales(null);
                        }
                    }
            );
        }

        @Test
        void getUserLocales_localesIsSomeValue_displayLocaleIsNull_throwsException() {
            objectUnderTest.setLocales(TEST_LOCALES);

            assertThrows(
                    NullPointerException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            objectUnderTest.getUserLocales(null);
                        }
                    }
            );
        }

        @Test
        void getUserLocales_localesIsSomeValue_displayLocaleIsSomeValue_UserLocaleInstances() {
            UserLocale[] expected = new UserLocale[] {
                    new UserLocale(
                            LOCALE_HELPER.getCode(TEST_DISPLAY_LOCALE),
                            TEST_DISPLAY_LOCALE.getDisplayName(TEST_DISPLAY_LOCALE)
                    )
            };

            UserLocale[] actual = objectUnderTest.getUserLocales(TEST_DISPLAY_LOCALE);

            assertUserLocalesEquals(asList(expected), asList(actual));
        }

        @Test
        void getUserLocales_localesIsSomeValue_displayLocaleHasCountry_UserLocaleInstances() {
            objectUnderTest.setLocales(TEST_LOCALES);

            UserLocale[] expected = userLocalesForDisplayLocale(TEST_DISPLAY_LOCALE);

            UserLocale[] actual = objectUnderTest.getUserLocales(TEST_DISPLAY_LOCALE);

            assertUserLocalesEquals(asList(expected), asList(actual));
        }

        @Test
        void getUserLocales_localesIsSomeValue_displayLocaleHasNotCountry_UserLocaleInstances() {
            objectUnderTest.setLocales(TEST_LOCALES);

            UserLocale[] expected = userLocalesForDisplayLocale(TEST_DISPLAY_LOCALE_WITHOUT_COUNTRY);

            UserLocale[] actual = objectUnderTest.getUserLocales(TEST_DISPLAY_LOCALE_WITHOUT_COUNTRY);

            assertUserLocalesEquals(asList(expected), asList(actual));
        }
    }

    /*
     * Helpers
     */

    private void assertUserLocalesEquals(List<UserLocale> expected, List<UserLocale> actual) {
        assertEquals(expected.size(), actual.size());

        for(int i = 0; i < expected.size(); i++) {
            UserLocale expectedUserLocale = expected.get(i);
            UserLocale actualUserLocale = expected.get(i);
            assertUserLocaleEquals(expectedUserLocale, actualUserLocale);
        }
    }

    private void assertUserLocaleEquals(UserLocale expected, UserLocale actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private UserLocale[] userLocalesForDisplayLocale(Locale locale) {
        return new UserLocale[] {
                new UserLocale(
                        LOCALE_HELPER.getCode(locale),
                        locale.getDisplayName(TEST_LOCALE)
                ),
                new UserLocale(
                        LOCALE_HELPER.getCode(locale),
                        locale.getDisplayName(TEST_DISPLAY_LOCALE)
                ),
                new UserLocale(
                        LOCALE_HELPER.getCode(locale),
                        locale.getDisplayName(TEST_LOCALE_WITHOUT_LANGUAGE)
                ),
                new UserLocale(
                        LOCALE_HELPER.getCode(locale),
                        locale.getDisplayName(TEST_LOCALE_WITHOUT_COUNTRY)
                ),
                new UserLocale(
                        LOCALE_HELPER.getCode(locale),
                        locale.getDisplayName(TEST_DISPLAY_LOCALE_WITHOUT_COUNTRY)
                )
        };
    }

}