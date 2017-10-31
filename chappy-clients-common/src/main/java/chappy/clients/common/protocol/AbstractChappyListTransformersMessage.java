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
package chappy.clients.common.protocol;

import java.util.List;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyListTransformersMessage extends AbstractChappyProtocolMessage {

	/** list of the transformers */
	private List<String> transformersName;
	/**
	 * 
	 */
	public AbstractChappyListTransformersMessage() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the transformers
	 */
	public List<String> getTransformersName() {
		return transformersName;
	}
	
	/**
	 * @param transformers the transformers to set
	 */
	public void setTransformersName(final List<String> transformers) {
		this.transformersName = transformers;
	}

}
