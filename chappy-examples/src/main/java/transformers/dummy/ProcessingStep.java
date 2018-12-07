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
package transformers.dummy;
import java.io.IOException;

import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.transformers.AbstractStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is part of Chappy project.
 * This is the Processing step class.
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingStep extends AbstractStep {

	/** mapping or processing */
	private String mappingName = null;
	/** factory for the engine */
	private String factoryEngine = null;
	
	public ProcessingStep() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the processing mapping
	 * @return mapping
	 */
	public String getMappingName() {
		return mappingName;
	}

	/**
	 * set the processing mapping
	 * @param mapping
	 */
	public void setMappingName(final String mapping) {
		this.mappingName = mapping;
	}

	/**
	 * get the factory for the mapping.
	 * @return factory
	 */
	public String getFactoryEngine() {
		return factoryEngine;
	}

	/**
	 * set the factory for the mapping.
	 * @param tfFactoryEngine
	 */
	public void setFactoryEngine(final String tfFactoryEngine) {
		this.factoryEngine = tfFactoryEngine;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.AbstractStep#execute(chappy.utils.streams.wrappers.StreamHolder, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void execute(final StreamHolder holder, final MultiDataQueryHolder dataHolder)
			throws IOException {
		ByteArrayInputStreamWrapper input = holder.getInputStream();
		ByteArrayOutputStreamWrapper output = new ByteArrayOutputStreamWrapper();
		byte[] buffer = new byte[1024];
		int len;
		len = input.read(buffer);
		while (len != -1) {
			output.write(buffer, 0, len);
		    len = input.read(buffer);
		}
		String addedMode = "=>processing:" + mappingName + " factory =" + factoryEngine;
		output.write(addedMode.getBytes());
		holder.setOutputStream(output);
	}

}
