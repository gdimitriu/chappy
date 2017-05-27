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

import chappy.configurations.transformers.StaxonConfiguration;
import chappy.utils.streams.wrappers.StreamHolder;
import chappy.utils.streams.wrappers.WrapperUtils;



/**
 * This is the interface which every step should implement.
 * @author Gabriel Dimitriu
 *
 */
public interface ITransformerStep {

	/**
	 * set disabled state for the step.
	 * @param disabled if the step will be disabled.
	 */
	public default void setDisabled(String disabled) {
		
	}

	/**
	 * return disabled state of step.
	 * @return true if the step is disabled
	 */
	public default boolean isDisabled() {
		return false;
	}
	
	/**
	 * set the number corresponding input stream 
	 * @param order corresponding to the input stream
	 */
	public default void setOrder(final int order) {
		
	}
	
	/**
	 * get the order corresponding to the input stream.
	 * @return order corresponding to the input stream.
	 */
	public default int getOrder() {
		return 0;
	}

	/**
	 * This is the execute method, this is used to execute the actual transformation.
	 * The input is holder in which will be also the return and intermediary values. 
	 * @param holder StreamHolder.
	 * @param multipart multipart from rest request
	 * @throws Exception 
	 */
	public default void execute(StreamHolder holder,
			FormDataMultiPart multipart,
			MultivaluedMap<String, String> queryParams) throws Exception {
		holder.setOutputStream(
				WrapperUtils.fromInputStreamToOutputWrapper(holder.getInputStream()));
	}
	
	/**
	 * This is the execute method, this is used to execute the actual transformation.
	 * The input is a list of holders in which will be also the return and intermediary values. 
	 * @param holders List of StreamHolders.
	 * @param multipart multipart from rest request
	 * @throws Exception 
	 */
	public default void execute(List<StreamHolder> holders,
			FormDataMultiPart multipart,
			MultivaluedMap<String, String> queryParams) throws Exception {
		
		if(holders.size() == 1) {
			execute(holders.get(0), multipart, queryParams);
			return;
		}
		holders.get(0).setOutputStream(
				WrapperUtils.fromInputStreamToOutputWrapper(holders.get(0).getInputStream()));
	}
	
	/**
	 * get number of input streams. 
	 * @return number of input streams.
	 */
	public default int getNumberOfInputs() {
		return 1;
	}
	
	/**
	 * get number of output streams.
	 * @return number of output streams.
	 */
	public default int getNumberOfOutputs() {
		return 1;
	}

	/** 
	 * set the staxon configuration
	 * @param config configuration for staxon.
	 */
	public default void setConfiguration(final StaxonConfiguration config) {
		
	}
	
	/**
	 * This will set the client serialized configuration.
	 * This will unmarshall the configration using jaxb.
	 * @param config serialized configuration
	 */
	public default void setSerializedConfiguration(final String config) {
		
	}
}