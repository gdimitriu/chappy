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

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.configurations.transformers.ConfigurationProperties;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is the abstract class for Enveloper operations.
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractEnveloperStep extends AbstractStep implements IEnveloperStep{
	
	/** config properties for enveloper */
	private ConfigurationProperties[] configProperties = null;

	/**
	 * 
	 */
	public AbstractEnveloperStep() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#execute(java.util.List<transformationsEngine.wrappers.StreamHolder>, org.glassfish.jersey.media.multipart.FormDataMultiPart, javax.ws.rs.core.MultivaluedMap)
	 */
	@Override
	public abstract void execute(final List<StreamHolder> holders, final FormDataMultiPart multipart, final MultivaluedMap<String, String> queryParams)
			throws Exception;

	/* (non-Javadoc)
	 * @see transformationsEngine.digester.steps.ITransformerStep#execute(transformationsEngine.wrappers.StreamHolder, org.glassfish.jersey.media.multipart.FormDataMultiPart, javax.ws.rs.core.MultivaluedMap)
	 */
	@Override
	public void execute(final StreamHolder holder, final FormDataMultiPart multipart, final MultivaluedMap<String, String> queryParams)
			throws Exception {
		
	}

	public ConfigurationProperties[] getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(ConfigurationProperties[] configProperties) {
		this.configProperties = configProperties;
	}
}
