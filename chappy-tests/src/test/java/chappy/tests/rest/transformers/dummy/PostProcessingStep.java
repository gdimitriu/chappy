package chappy.tests.rest.transformers.dummy;

import java.io.IOException;

import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.transformers.AbstractStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is part of transformationsEngine project.
 * This is the PostProcessing step class, this in the test case is the third transformation done by digester.
 * @author Gabriel Dimitriu
 *
 */
public class PostProcessingStep extends AbstractStep {

	private String mode = null;
	
	/**
	 * 
	 */
	public PostProcessingStep() {
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
		String addedMode = "=>postProcessing:" + mode;
		output.write(addedMode.getBytes());
		holder.setOutputStream(output);
	}

}
