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
package chappy.interfaces.rest.resources;

/**
 * Constants for base rest paths.
 * 
 * @author Gabriel Dimitriu
 *
 */
public interface IRestResourcesConstants {
	
	String BASE_REST = "rest";

	String REST_TRANSACTION = "transaction";

	String REST_ADD = "add";

	String REST_UPGRADE = "upgrade";

	String REST_FLOW = "flow";

	String REST_DIGESTER_FLOW = "digesterFlow";

	String REST_TRANSFORM = "transform";

	String REST_TRANSFORMER_STAXON = "staxon";

	String REST_TRANSFORMER = "transformer";

	String REST_TRANSFORMER_BY_USER = "transformerByUser";
	
	String REST_AUTHENTICATE = "authenticate";
}
