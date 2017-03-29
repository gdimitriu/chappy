package chappy.tests.rest.transformers.dummy;

import java.io.IOException;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.interfaces.transformers.AbstractStep;
import chappy.utils.streams.wrappers.ByteArrayInputStreamWrapper;
import chappy.utils.streams.wrappers.ByteArrayOutputStreamWrapper;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This is part of transformationsEngine project.
 * This is the PreProcessing step class, this in the test case is the first transformation done by digester.
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
	 * @see transformationsEngine.digester.steps.AbstractStep#execute(transformationsEngine.wrappers.StreamHolder)
	 */
	@Override
	public void execute(final StreamHolder holder, final FormDataMultiPart multipart, final MultivaluedMap<String, String> queryParams)
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
