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

import chappy.configurations.transformers.ConfigurationProperties;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is the abstract class for Splitter operations.
 * @author Gabriel Dimitriu
 *
 */
/**
 * @author gdimitriu
 *
 */
public abstract class AbstractSplitterStep extends AbstractStep implements ISplitterStep{

	private ConfigurationProperties[] configProperties = null;
	
	/**
	 * 
	 */
	public AbstractSplitterStep() {
		// TODO Auto-generated constructor stub
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.ITransformerStep#execute(java.util.List, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public abstract void execute(final List<StreamHolder> holders, final MultiDataQueryHolder dataHolder)
			throws Exception;
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.AbstractStep#execute(chappy.utils.streams.wrappers.StreamHolder, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void execute(final StreamHolder holder, final MultiDataQueryHolder dataHolder)
			throws Exception {
		
	}

	public ConfigurationProperties[] getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(ConfigurationProperties[] configProperties) {
		this.configProperties = configProperties;
	}
}
