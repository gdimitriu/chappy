/**
    Copyright (c) 2018 Gabriel Dimitriu All rights reserved.
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
package chappy.utils.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * This holds the utils necessary for runtime information for clases.
 * @author Gabriel Dimitriu
 *
 */
public final class RTIClassUtils {

	public static List<Class<?>> getAllInterfaces(final Class<?> targetClass) {
		List<Class<?>> allInterfaces = new ArrayList<>();
		Class<?>[] interfaces = targetClass.getInterfaces();
		for (Class<?> cl : interfaces) {
			allInterfaces.add(cl);
			if(cl.getInterfaces().length != 0) {
				allInterfaces.addAll(getAllInterfaces(cl));
			}
		}
		return allInterfaces;
	}
}
