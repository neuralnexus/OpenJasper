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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * 
 * @author tkavanagh
 * @version $Id$
 */
public class ExecutionContextImpl implements ExecutionContext, Serializable {
    public static final String EXECUTE_OVERRIDE = "execute";
    static final String RESTRICTED_CONTEXT_ATTR = "restricted-context";
    static final PermissionOverride EXECUTE_OVERRIDE_ATTR = new PermissionOverride(EXECUTE_OVERRIDE);
	private Locale locale;
	private TimeZone timeZone;
    private List attributes = new ArrayList();

    public ExecutionContextImpl() {
    }

    public ExecutionContextImpl(Locale locale) {
        this.locale = locale;
    }

	/**
	 * Given an existing execution context, return one that has the PermissionOverride object
	 * for execute-only perms in its attributes. Return the same object if it's already there.
	 *
	 * @param originalContext original context
	 * @return runtime execution context
	 */
	public static ExecutionContext getRuntimeExecutionContext(ExecutionContext originalContext) {
		return createContextAndAddAttrsIfAtLeastOneAttrNotPresent(originalContext, EXECUTE_OVERRIDE_ATTR);
	}

	public static ExecutionContext getRuntimeExecutionContext() {
		return getRuntimeExecutionContext(null);
	}

	/**
	 * Given an existing execution context, return one that has the PermissionOverride object
	 * for execute-only perms and RESTRICTED_CONTEXT_ATTR in its attributes. Return the same object if it's already there.
	 *
	 * This restricted execution context is introduced because in some cases client is charged of execution of some
	 * executable resources(like dashboard and domain), so it requires resource metadata for execution. That's why,
	 * original runtime execution context(getRuntimeExecutionContext) allows client to return any resource if client
	 * now resource URI. The idea of this context deny access for some secure sensitive resources.
	 *
	 * @param originalContext original context
	 * @return restricted runtime execution context
	 */
	public static ExecutionContext getRestrictedRuntimeExecutionContext(ExecutionContext originalContext) {
		return createContextAndAddAttrsIfAtLeastOneAttrNotPresent(originalContext, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
	}

	public static ExecutionContext getRestrictedRuntimeExecutionContext() {
		return getRestrictedRuntimeExecutionContext(null);
	}

	public static boolean isRestrictedRuntimeExecutionContext(ExecutionContext context) {
		return checkIfAllAttributesExist(context, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
	}

	public static ExecutionContext create(Locale locale, TimeZone timeZone, Object...attrs) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.setLocale(locale);
		context.setTimeZone(timeZone);
		if (attrs != null) {
			context.setAttributes(Stream.of(attrs).collect(toList()));
		}
		return context;
	}

	static boolean checkIfAllAttributesExist(ExecutionContext context, Object... attrs) {
		if (isEmpty(attrs)) {
			return true;
		}
		if (context == null) {
			return false;
		}
		return context.getAttributes().containsAll(nullableArrayToList(attrs));
	}

	/**
	 * Clones original context and adds missing  attributes, if at least one attribute from attrs array is not presented
	 * in original context, otherwise just return original context.
	 * @param originalContext original context
	 * @param attrs attributes, that should be present in returned execution context.
	 *
	 * @return execution context with all attributes from <attrs>
	 */
	@SuppressWarnings("unchecked")
	private static ExecutionContext createContextAndAddAttrsIfAtLeastOneAttrNotPresent(ExecutionContext originalContext, Object...attrs) {
		ExecutionContext context;
		//Convert array to list attrs or just return empty list.
		List attrsToAdd = nullableArrayToList(attrs);
		if (originalContext == null) {
			context = create(null, null, attrs);
		} else if (originalContext.getAttributes().containsAll(attrsToAdd)) {
			//Do you already have all attributes. Then you don't need to do anything
			context = originalContext;
		} else {
			context = create(originalContext.getLocale(), originalContext.getTimeZone(), originalContext.getAttributes().toArray());
			List contextAttrs = context.getAttributes();
			//Add required attribute if not present.
			attrsToAdd.stream().filter(attr -> !contextAttrs.contains(attr)).forEach(contextAttrs::add);
		}
		return context;
	}

	private static List nullableArrayToList(Object...attrs) {
		return Optional.ofNullable(attrs).map(Arrays::stream).orElseGet(Stream::empty).collect(toList());
	}

	/**
	 * @return List of Attributes for the object
	 */
	public List getAttributes() {
	    return attributes;
	}

    public void setAttributes(List attrs) {
	    attributes = attrs;
    }

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExecutionContextImpl that = (ExecutionContextImpl) o;

		if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
		if (timeZone != null ? !timeZone.equals(that.timeZone) : that.timeZone != null) return false;
		return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;
	}

	@Override
	public int hashCode() {
		int result = locale != null ? locale.hashCode() : 0;
		result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
		result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
		return result;
	}
}
