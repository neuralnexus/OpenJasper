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
package com.jaspersoft.jasperserver.rest.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carbiv
 * Date: 11/7/11
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="entityResource")
public class JAXBList<T>
{
    @XmlElement(name="Item")
    List<T> list=new ArrayList<T>();
    public JAXBList (){}
    public JAXBList (List<T> lst){
        list.addAll(lst);
    }

    public JAXBList (T[] arr){
        list = Arrays.asList(arr);
    }

    public void add(T element){
       list.add(element);
    }

    public int size(){
        return list.size();
    }

    public T get(int index){
        return list.get(index);
    }
}

