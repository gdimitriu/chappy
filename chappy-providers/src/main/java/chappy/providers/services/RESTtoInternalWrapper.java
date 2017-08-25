/**
    Copyright (c) 2017 Gabriel Dimitriu All rights reserved.
	DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

    This file is part of chappy project.

    Chappy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Chappy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chappy.  If not, see <http://www.gnu.org/licenses/>.
 */
package chappy.providers.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.flows.MultiDataQueryHolder;

/**
 * @author Gabriel Dimitriu
 *
 */
public class RESTtoInternalWrapper {

	static public MultiDataQueryHolder RESTtoInternal(final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) {
		MultiDataQueryHolder retData = new MultiDataQueryHolder();
		
		Map<String,List<FormDataBodyPart>> fields = multipart.getFields();
		fields.entrySet().stream().forEach(entry -> convertValues(retData, entry));
		
		queryParams.entrySet().stream().forEach(entry -> retData.setQuery(entry.getKey(), entry.getValue()));
		
		return retData;
	}
	
	private static void convertValues(final MultiDataQueryHolder holder, Entry<String, List<FormDataBodyPart>> originalList) {
		List<FormDataBodyPart> list = originalList.getValue();
		List<Object> objects = new ArrayList<Object>();
		list.stream().forEach(obj -> objects.add(obj.getEntityAs(InputStream.class)));
		holder.setValue(originalList.getKey(), objects);
	}
}
