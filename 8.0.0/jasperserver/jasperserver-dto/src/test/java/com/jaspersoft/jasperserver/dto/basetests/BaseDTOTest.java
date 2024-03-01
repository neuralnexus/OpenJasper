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

package com.jaspersoft.jasperserver.dto.basetests;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @author Olexandr Dahno <odahno@tibco.com>
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDTOTest<T extends DeepCloneable> {

    protected abstract List<T> prepareInstancesWithAlternativeParameters();
    protected abstract T createFullyConfiguredInstance();
    protected abstract T createInstanceWithDefaultParameters();
    protected abstract T createInstanceFromOther(T other);

    private static Object TEST_OBJECT = new Object();

    protected T fullyConfiguredTestInstance;
    protected T testInstanceWithDefaultParameters;

    @BeforeEach
    public void setUp() {
        fullyConfiguredTestInstance = createFullyConfiguredInstance();
        testInstanceWithDefaultParameters = createInstanceWithDefaultParameters();
    }

    /*
     * Constructors
     */

    @Test
    public void copiedInstanceEqualsToOriginalInstanceWithDefaultParameters() {
        T copied = createInstanceFromOther(testInstanceWithDefaultParameters);

        assertEquals(copied, testInstanceWithDefaultParameters);
    }

    @Test
    public void copiedInstanceEqualsToFullyConfiguredOriginalInstance() {
        T copied = createInstanceFromOther(fullyConfiguredTestInstance);

        assertEquals(copied, fullyConfiguredTestInstance);
    }

    @Test
    public void copiedInstanceIsNotSameAsOriginal() {
        T copied = createInstanceFromOther(fullyConfiguredTestInstance);

        assertFieldsHaveUniqueReferences(copied, fullyConfiguredTestInstance);
    }

    @Test
    public void nullInstanceCanNotBeCopied() {
        Assertions.assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                createInstanceFromOther(null);
            }
        });
    }

    protected void assertFieldsHaveUniqueReferences(T expected, T actual) {}

    /*
     * DeepCloneable
     */

    @Test
    public void deepClonedInstanceEqualsToOriginalInstanceWithDefaultParameters() {
        T copied = (T) testInstanceWithDefaultParameters.deepClone();

        assertEquals(copied, testInstanceWithDefaultParameters);
    }


    @Test
    public void deepClonedInstanceEqualsToOriginalInstanceWithAllParameters() {
        T copied = (T) fullyConfiguredTestInstance.deepClone();

        assertEquals(copied, fullyConfiguredTestInstance);
    }

    @Test
    public void deepClonedInstanceIsNotSameAsOriginalInstance() {
        T copied = (T) fullyConfiguredTestInstance.deepClone();

        assertFieldsHaveUniqueReferences(copied, fullyConfiguredTestInstance);
    }

    /*
     * Equals
     */

    @Test
    public void instancesWithDifferentParametersAreNotEqual() {
        assertNotEquals(fullyConfiguredTestInstance, testInstanceWithDefaultParameters);
        assertNotEquals(testInstanceWithDefaultParameters, fullyConfiguredTestInstance);
    }

    @ParameterizedTest
    @MethodSource(value = "prepareInstancesWithAlternativeParameters")
    public void instancesWithDifferentParametersAreNotEqual(T instance) {
        assertNotEquals(instance, fullyConfiguredTestInstance);
        assertNotEquals(fullyConfiguredTestInstance, instance);
    }

    @ParameterizedTest
    @MethodSource(value = "prepareInstancesWithAlternativeParameters")
    public void instancesWithSameParametersAreEquals(T instance) {
        T second = (T) instance.deepClone();

        assertEquals(instance, second);
    }

    @Test
    public void instancesWithDefaultParametersAreEquals() {
        T clone = createInstanceWithDefaultParameters();

        assertEquals(testInstanceWithDefaultParameters, clone);
    }

    @Test
    public void instanceIsEqualsToItself() {
        assertEquals(fullyConfiguredTestInstance, fullyConfiguredTestInstance);
    }

    @Test
    public void instanceIsNotEqualsToNull() {
        assertNotEquals(null, createFullyConfiguredInstance());
    }

    @Test
    public void instanceIsNotEqualsToObject() {
        assertNotEquals(fullyConfiguredTestInstance, TEST_OBJECT);
    }

    /*
     * Hash Code
     */

    @Test
    public void equalInstancesWithDefaultParametersHaveEqualHashCodes() {
        T first = createInstanceWithDefaultParameters();
        T second = createInstanceWithDefaultParameters();

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void twoFullyConfiguredInstancesHaveEqualHashCodes() {
        T first = createFullyConfiguredInstance();
        T second = createFullyConfiguredInstance();

        assertEquals(first.hashCode(), second.hashCode());
    }

    @ParameterizedTest
    @MethodSource(value = "prepareInstancesWithAlternativeParameters")
    public void instancesWithSameParametersHaveSameHashCodes(T instance) {
        T second = (T) instance.deepClone();

        assertEquals(instance.hashCode(), second.hashCode());
    }

    /*
     * toString
     */

    @Test
    public void generatedStringBeginsWithClassName() {
        assertThat(fullyConfiguredTestInstance.toString(), startsWith(fullyConfiguredTestInstance.getClass().getSimpleName()));
    }
}
