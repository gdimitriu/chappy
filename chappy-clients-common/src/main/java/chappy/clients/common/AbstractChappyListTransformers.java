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
package chappy.clients.common;

import java.util.List;

import chappy.clients.common.protocol.AbstractChappyListTransformersMessage;

/**
 * Abstract chappy client for list the transformers from a transaction.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyListTransformers extends AbstractChappyClient {

	/**
	 * 
	 */
	public AbstractChappyListTransformers() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.clients.common.AbstractChappyClient#getProtocol()
	 */
	@Override
	public AbstractChappyListTransformersMessage getProtocol() {
		return (AbstractChappyListTransformersMessage) super.getProtocol();
	}
	
	/**
	 * set the list of transformers.
	 * @param transformers that exist in chappy transaction.
	 */
	public void setTransformersNameList(final List<String> transformers) {
		getProtocol().setTransformersName(transformers);
	}

	/**
	 * @return list of transformers names that exist in chappy transaction.
	 */
	public List<String> getTransformersNameList() {
		return getProtocol().getTransformersName();
	}
}
