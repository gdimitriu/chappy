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
package chappy.interfaces.transformers;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.statisticslogs.ILogs;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is the abstract step class, the base of classes uses as transformation step by digester.
 * @author Gabriel Dimitriu
 *
 */

public abstract class AbstractStep implements ITransformerStep {

	/** disable the step */
	private String disabled = "false";
	
	/** the order of input stream */
	private int order = 0;
	
	/** logProvider */
	private ILogs logProvider = null;
	
	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#setDisabled(java.lang.String)
	 */
	@Override
	public void setDisabled(final String disabled) {
		this.disabled = disabled;
	}
	
	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#isDisabled()
	 */
	@Override
	public boolean isDisabled() {
		return "true".equals(disabled);
	}
	
	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#setOrder(java.lang.int)
	 */
	@Override
	public void setOrder(final int order) {
		this.order = order;
	}
	
	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * 
	 */
	public AbstractStep() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#execute(transformationsEngine.wrappers.StreamHolder, org.glassfish.jersey.media.multipart.FormDataMultiPart, javax.ws.rs.core.MultivaluedMap)
	 */
	@Override
	public abstract void execute(final StreamHolder holder, final FormDataMultiPart multipart, final MultivaluedMap<String, String> queryParams)
			throws Exception;
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.ITransformerStep#setLogProvider(chappy.interfaces.statisticslogs.ILogs)
	 */
	@Override
	public void setLogProvider(final ILogs logProvider) {
		this.logProvider = logProvider;
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.ITransformerStep#getLog()
	 */
	@Override
	public ILogs getLog() {
		return this.logProvider;
	}
}
