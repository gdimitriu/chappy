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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;


/**
 * @author Gabriel Dimitriu
 *
 */
public class MultiDataQueryHolder {
	
	/** holder for REST FormDataMultiPart */
	private MultiValuedMap<String, Object> bodyValues = null;
	
	/** holder for MultivaluedMap<String,String> from REST queries*/
	private MultiValuedMap<String, String> queries = null;
	
	public MultiDataQueryHolder() {
		queries = new HashSetValuedHashMap<String, String>();
		bodyValues = new HashSetValuedHashMap<String, Object>();
	}
	
	
	/**
	 * get the body values multimap.
	 * @return multimap of the body values
	 */
	public MultiValuedMap<String, Object> getBodyValues() {
		return bodyValues;
	}
	
	/**
	 * get the queries multimap.
	 * @return multimap of the queries
	 */
	public MultiValuedMap<String, String> getQueries() {
		return queries;
	}
	
	/**
	 * set the multimap of the body values.
	 * @param bodies
	 */
	public void setBodyValues(final MultiValuedMap<String, Object> bodies) {
		this.bodyValues = bodies;
	}
	
	/**
	 * set the multimap of the queries
	 * @param queries as multimap
	 */
	public void setQueries(final MultiValuedMap<String, String> queries) {
		this.queries = queries;
	}
	
	/**
	 * set the queries map
	 * @param key query
	 * @param values of query
	 */
	public void setQuery(final String key, final List<String> values) {
		values.stream().forEach(val -> queries.put(key, val));
	}
	
	/**
	 * set the value map
	 * @param key name
	 * @param values to add
	 */
	public void setValue(final String key, final List<Object> values) {
		values.stream().forEach(val -> bodyValues.put(key, val));
	}
	
	/**
	 * get the collection of value from key element corresponding to body value.
	 * @param key to search
	 * @return collection of values
	 */
	public Collection<Object> getBodyValue(final String key) {
		return (Collection<Object>) bodyValues.get(key);
	}
	
	/**
	 * get the collection of value from key element corresponding to a query.
	 * @param key to search
	 * @param T type of element
	 * @return collection of type element.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getQueryValueAs(final String key, final Class<?> T) {
		return (Collection<T>) queries.get(key);
	}
	
	/**
	 * get the collection of value from key element corresponding to a query.
	 * @param key to search
	 * @return collection of values
	 */
	public Collection<String> getQueryValue(final String key) {
		return (Collection<String>) queries.get(key);
	}
	
	/**
	 * get the collection of value from key element.
	 * @param key to search
	 * @param T type of element
	 * @return collection of type element.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getBodyValueAs(final String key, final Class<?> T) {
		return (Collection<T>) bodyValues.get(key);
	}
	
	
	/**
	 * get the set of string key values.
	 * @return a set containing all keys.
	 */
	public Set<String> getQueriesSet() {
		return queries.keySet();
	}
	
	/**
	 * get the first query.
	 * @param key the query key
	 * @return value of the query
	 */
	@SuppressWarnings("rawtypes" )
	public Object getFirstQuery(final String key) {
		Object col = queries.get(key);
		if (col != null) {
			return ((Collection) col).iterator().next();
		} else {
			return null;
		}
	}
}
