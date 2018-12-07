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
 * This is the PreProcessing step class.
 * @author Gabriel Dimitriu
 *
 */
public class PreProcessingStep extends AbstractStep {

	/** mode ex: xml2json, json2xml */
	private String mode = null;
	/**
	 * 
	 */
	public PreProcessingStep() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(final String mode) {
		this.mode = mode;
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
		String addedMode = "=>preProcessing:" + mode;
		output.write(addedMode.getBytes());
		holder.setOutputStream(output);
	}

}
