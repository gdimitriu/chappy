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
import java.util.Scanner;

import chappy.configurations.transformers.ConfigurationProperties;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.transformers.AbstractSplitterStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is part of Chappy project.
 * Dummy splitter step.
 * @author Gabriel Dimitriu
 *
 */
public class SplitterStep extends AbstractSplitterStep {

	/**
	 * 
	 */
	public SplitterStep() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transformers.AbstractSplitterStep#execute(java.util.List, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void execute(final List<StreamHolder> holders, final MultiDataQueryHolder dataHolder) throws Exception {
		if (holders.size() != 1) {
			return;
		}
		ByteArrayInputStreamWrapper input = holders.get(0).getInputStream();
		holders.clear();
		Scanner scan = new Scanner(input);
		scan.useDelimiter(getSeparator());
		while(scan.hasNext()) {
			ByteArrayOutputStreamWrapper output = new ByteArrayOutputStreamWrapper();
			output.write(scan.next().getBytes());
			StreamHolder holder = new StreamHolder();
			holder.setOutputStream(output);
			holders.add(holder);
			output.close();
		}
		scan.close();
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
