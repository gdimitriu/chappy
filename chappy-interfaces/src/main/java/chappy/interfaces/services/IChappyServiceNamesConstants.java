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
package chappy.interfaces.services;

/**
 * This holds the constants for the request services (REST or JMS).
 * @author Gabriel Dimitriu
 *
 */
public interface IChappyServiceNamesConstants {

	/** login user query */
	public String LOGIN_USER = "user";
	
	/** login password for the login user query*/
	public String LOGIN_PASSWORD = "password";
	
	/** user data for the cookie*/
	public String COOKIE_USER_DATA = "userData";
	
	/** name of the transformer */
	public String TRANSFORMER_NAME = "name";
	
	/** transformer data */
	public String TRANSFORMER_DATA = "data";
	
	/** transformer query for delete*/
	public String TRANSFORMER = "transformer";

	/**configuration */
	public String CONFIGURATION = "configuration";

	/** input data to be processed */
	public String INPUT_DATA = "data";

	/** persist query required */
	public String PERSIST = "persist";

	/** get the statistic query request */
	public String GET_STATISTICS = "getStatistics";
	
	/** mode for staxon (deprecated) */
	public String MODE = "mode";
	
	/* ---------------------------------------------------------
	 *  runner for flows to be run
	 *  -------------------------------------------------------- 
	 */
	/** staxon simple flow */
	public String STAXON_SIMPLE_FLOW = "StaxonSimpleFlow";
	
	/** static flow request to be run */
	public String STATIC_FLOW = "StaticFlow";

	/** digester flow runner */
	public String DIGESTER_FLOW = "DigesterFlow";
}
