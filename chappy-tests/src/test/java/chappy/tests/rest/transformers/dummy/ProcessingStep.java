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
 * This is the Processing step class, this in the test case is the second transformation done by digester.
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
		String addedMode = "=>processing:" + mappingName + " factory =" + factoryEngine;
		output.write(addedMode.getBytes());
		holder.setOutputStream(output);
	}

}
