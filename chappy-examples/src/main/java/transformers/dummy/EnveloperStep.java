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

import java.util.List;

import chappy.configurations.transformers.ConfigurationProperties;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.transformers.AbstractEnveloperStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * Dummy Enveloper Step.
 * @author Gabriel Dimitriu
 *
 */
public class EnveloperStep extends AbstractEnveloperStep {

	/**
	 * 
	 */
	public EnveloperStep() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.AbstractEnveloperStep#execute(java.util.List, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void execute(final List<StreamHolder> holders, final MultiDataQueryHolder dataHolder) throws Exception {
		StreamHolder outputHolder = new StreamHolder();
		if (holders.size() == 1) {
			return;
		}
		ByteArrayOutputStreamWrapper os = new ByteArrayOutputStreamWrapper();
		outputHolder.setOutputStream(os);
		for (int i = 0; i < holders.size(); i++ ) {
			StreamHolder sh = holders.get(i);
			ByteArrayInputStreamWrapper input = sh.getInputStream();
			byte[] buffer = new byte[1024];
			int len;
			len = input.read(buffer);
			while (len != -1) {
				os.write(buffer, 0, len);
			    len = input.read(buffer);
			}
			if (i != (holders.size() -1)) {
				os.write(getSeparator().getBytes());
			}
		}
		holders.clear();
		holders.add(outputHolder);
	}
	
	@Override
	public String getSeparator() {
		ConfigurationProperties[] configs = getConfigProperties();
		for (ConfigurationProperties conf : configs) {
			if ("separator".equals(conf.getProperty())) {
				return conf.getValue();
			}
		}
		return super.getSeparator();
	}
}
