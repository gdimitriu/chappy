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
package chappy.interfaces.flows;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import chappy.utils.streams.StreamUtils;


/**
 * @author Gabriel Dimitriu
 *
 */
public class MultiDataQueryHolder implements Serializable{
	
	/**
	 * serial version.
	 */
	private static final long serialVersionUID = 1L;

	/** holder for REST FormDataMultiPart */
	private List<String> bodyNames = null;
	
	private List<List<String>> bodyValues = null;
	
	/** holder for MultivaluedMap<String,String> from REST queries*/
	private List<String> queriesNames = null;
	private List<List<String>> queriesValues = null;
	
	public MultiDataQueryHolder() {
		bodyNames = new ArrayList<>();
		bodyValues = new ArrayList<>();
		queriesNames = new ArrayList<>();
		queriesValues = new ArrayList<>();
	}
	
	/**
	 * get the queries multimap.
	 * @return multimap of the queries
	 */
	public MultiValuedMap<String, String> getQueries() {
		 MultiValuedMap<String, String> queries = new HashSetValuedHashMap<>();
		 for (int i = 0 ; i < queriesNames.size(); i++) {
			 queries.putAll(queriesNames.get(i), queriesValues.get(i));
		 }
		return queries;
	}
	
	/**
	 * set the multimap of the queries
	 * @param queries as multimap
	 */
	public void setQueries(final MultiValuedMap<String, String> queries) {
		for (String key : queries.keySet()) {
			queriesNames.add(key);
			queriesValues.add((List<String>) queries.get(key));
		}
	}
	
	/**
	 * set the queries map
	 * @param key query
	 * @param values of query
	 */
	public void setQuery(final String key, final List<String> values) {
		this.queriesNames.add(key);
		this.queriesValues.add(values);
	}
	
	/**
	 * set the value map
	 * @param key name
	 * @param values to add
	 */
	public void setValue(final String key, final List<Object> values) {
		this.bodyNames.add(key);
		if (values.get(0) instanceof InputStream) {
			List<String> vals = new ArrayList<>();
			values.stream().forEach(a -> vals.add(StreamUtils.toStringFromStream((InputStream)a)));
			this.bodyValues.add(vals);
			return;
		}
		if (values.get(0) instanceof String) {
			List<String> vals = new ArrayList<>();
			values.stream().forEach(a -> vals.add((String) a));
			this.bodyValues.add(vals);
			return;
		}
	}
	
	/**
	 * get the collection of value from key element.
	 * @param key to search
	 * @param T type of element
	 * @return collection of type element.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getBodyValueAs(final String key, final Class<?> T) {
		int index = bodyNames.indexOf(key);
		if (index > -1 && T == InputStream.class) {
			List<InputStream> vals = new ArrayList<>();
			bodyValues.get(index).stream().forEach(a -> vals.add(StreamUtils.toInputStreamFromString(a)));
			return (Collection<T>) vals;
		}
		if (index > -1 && T == String.class) {
			return (Collection<T>) bodyValues;
		}
		return new ArrayList<>();
	}
	
	/**
	 * get the collection of value from key element corresponding to a query.
	 * @param key to search
	 * @return collection of values
	 */
	public Collection<String> getQueryValue(final String key) {
		int index = queriesNames.indexOf(key);
		if (index > -1) {
			return queriesValues.get(index);
		}
		return new ArrayList<>();
	}
	
	/**
	 * get the list of string key values.
	 * @return a set containing all keys.
	 */
	public List<String> getQueriesList() {		
		return queriesNames;
	}
	
	/**
	 * get the first query.
	 * @param key the query key
	 * @return value of the query
	 */
	public String getFirstQuery(final String key) {
		int index = queriesNames.indexOf(key);
		if (index > -1) {
			if (queriesValues.get(index).isEmpty()) {
				return null;
			}
			return queriesValues.get(index).get(0);
		}
		return null;
	}
}
