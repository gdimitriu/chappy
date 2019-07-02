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
package chappy.flows.transformers.staticflows;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import chappy.configurations.transformers.StaxonConfiguration;
import chappy.interfaces.transformers.ITransformerStep;

/**
 * This hold the step configuration JAXB.
 * This corresponds to step in digester.
 * @author Gabriel Dimitriu
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StepConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "class")
	private String name = null;
	
	@XmlElement(name = "disabled")
	private boolean disabled = false;
	
	@XmlElement(name = "parameters")
	private StepParameters parameters = null;
	
	@XmlElement(name = "configuration")
	private StaxonConfiguration configuration = null;
	
	@XmlElement(name = "order")
	private int order = 0;
	/**
	 * 
	 */
	public StepConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * @return the param
	 */
	public StepParameters getParameters() {
		return parameters;
	}
	
	/**
	 * 
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * set the parameters to the step using reflection.
	 * @param step
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public void setStageParameters(final ITransformerStep step) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		setStageParameters(step, parameters);
		if (configuration == null) {
			return;
		}
		Field target = getInheritedPrivateField(step.getClass(), "configuration");
		if (target != null && target.getType().equals(configuration.getClass())) {
			target.setAccessible(true);
			target.set(step, configuration);
		}
		
	}
	
	/**
	 * set the stage parameters using reflection.
	 * @param step
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void setStageParameters(final ITransformerStep step, final Object objSource) throws IllegalArgumentException, IllegalAccessException {
		if (objSource == null) {
			return;
		}
		List<Field> targetFields = getInheritedPrivateFields(step.getClass());
		List<Field> sourceFields = getInheritedPrivateFields(objSource.getClass());
		for (Field source : sourceFields) {
			for (Field target : targetFields) {
				if (target.getName().equals(source.getName())) {
					target.setAccessible(true);
					source.setAccessible(true);
					target.set(step, source.get(objSource));
				}
			}
		}
	}
	
	/**
	 * get all the declared field from the entire hierarchy of the classes.
	 * @param type master class
	 * @return list of fields
	 */
	private List<Field> getInheritedPrivateFields(Class<?> type) {
	    List<Field> result = new ArrayList<Field>();

	    Class<?> i = type;
	    while (i != null && i != Object.class) {
	        Collections.addAll(result, i.getDeclaredFields());
	        i = i.getSuperclass();
	    }

	    return result;
	}
	
	/**
	 * get the field with a specific name from the class hierarchy
	 * @param type of the master class
	 * @param name of the field
	 * @return field
	 */
	private Field getInheritedPrivateField(Class<?> type, final String name) {

	    Class<?> i = type;
	    while (i != null && i != Object.class) {
	        try {
				return i.getDeclaredField(name);
			} catch (NoSuchFieldException | SecurityException e) {
				i = i.getSuperclass();
			}
	    }
	    return  null;
	}
}
