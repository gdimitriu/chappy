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
package chappy.providers.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import chappy.interfaces.transformers.IEnveloperStep;
import chappy.interfaces.transformers.ISplitterStep;
import chappy.interfaces.transformers.ITransformerStep;

/**
 * This hold the singleton for the default step providers.
 * This hold the classes description for all default steps.
 * @author Gabriel Dimitriu
 *
 */
public class DefaultStepProvider {

	private static final DefaultStepProvider singleton = new DefaultStepProvider();
	
	private List<String> defaultStepsList = null;
	
	private DefaultStepProvider() {
		defaultStepsList = getDefaultSteps();
	}
	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	public static DefaultStepProvider getInstance() {
		return singleton;
	}
	
	/**
	 * get the default steps.
	 * @return list of steps names.
	 */
	public List<String> getDefaultSteps() {
		Reflections reflections = new Reflections("chappy.interfaces");
		List<String> steps = new ArrayList<String>();
		steps.add("ITransformerStep");
		Set<Class<? extends ITransformerStep>> stepResources = reflections.getSubTypesOf(ITransformerStep.class);
		for (Class<? extends ITransformerStep> current : stepResources) {
			steps.add(current.getSimpleName());
		}
		steps.add("IEnveloperStep");
		Set<Class<? extends IEnveloperStep>> eveloperResources = reflections.getSubTypesOf(IEnveloperStep.class);
		for (Class<? extends IEnveloperStep> current : eveloperResources) {
			steps.add(current.getSimpleName());
		}
		steps.add("ISplitterStep");
		Set<Class<? extends ISplitterStep>> splitterResources = reflections.getSubTypesOf(ISplitterStep.class);
		for (Class<? extends ISplitterStep> current : splitterResources) {
			steps.add(current.getSimpleName());
		}
		steps = steps.stream().distinct().collect(Collectors.toList());
		return steps;
	}
	
	/**
	 * test if a name is a default step provided by installation.
	 * @param name of the step
	 * @return true if is provided by installation.
	 */
	public boolean isDefaultStep(final String name) {
		for (String defaultName : defaultStepsList) {
			if (name.contains(defaultName)) {
				return true;
			}
		}
		return false;
	}
}
