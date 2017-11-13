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
package chappy.interfaces.jms.protocol;

/**
 * Keys for the protocol aggregates.
 * @author Gabriel Dimitriu
 *
 */
public interface IJMSProtocolKeys {

	/** cookie key for the protocol */
	String COOKIE_KEY = "cookie";
	
	/** reply message key for the protocol */
	String REPLY_MESSAGE_KEY = "reply_message";

	/** exception occurred in chappy*/
	String REPLY_EXCEPTION_KEY = "exception";
	
	/** status property */
	String REPLY_STATUS_PROPERTY = "status";
	
	/** transformer name */
	String TRANSFORMER_NAME = "transformer_name";
	
	/** transformer bytecode to be instantiated */
	String TRANSFORMER_BYTECODE = "transformer_bytecode";

	/** transformer flow configuration */
	String FLOW_CONFIGURATION = "flow_configuration";
	
	/** number of input data */
	String DATA_FLOW_NR = "data_nr";
	
	/** prefix for input data for the flow */
	String DATA_FLOW = "data_";

	/** flow queries data */
	String FLOW_QUERIES = "flow_queries";
}
