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
 * rest path constants.
 * @author Gabriel Dimitriu
 *
 */
public interface IRestPathConstants {

	
	String PATH_TO_TRANSFORM_FLOW = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_TRANSFORM + "/" + IRestResourcesConstants.REST_FLOW;
	String PATH_TO_TRANSFORM = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_TRANSFORM;
	String PATH_TO_TRANSFORM_STAXON = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_TRANSFORM + "/" + IRestResourcesConstants.REST_TRANSFORMER_STAXON;
	String PATH_TO_TRANSFORM_DIGESTER = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_TRANSFORM + "/" + IRestResourcesConstants.REST_DIGESTER_FLOW;
	
	/*
	 * path for upgrade and update and delete of injection.
	 */
	String PATH_TO_ADD_TRANSFORMER_TO_FLOW = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_ADD + "/" + IRestResourcesConstants.REST_FLOW;
	String PATH_TO_DELETE_TRANSFORMER_TO_FLOW = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_DELETE + "/" + IRestResourcesConstants.REST_FLOW;
	String PATH_TO_TRANSACTION = IRestResourcesConstants.BASE_REST + "/" 
			+ IRestResourcesConstants.REST_TRANSACTION;
	
	/*
	 * path for integration broker.
	 */
	String PATH_TO_INTEGRATION = IRestResourcesConstants.BASE_REST + "/"
			+ IRestResourcesConstants.REST_TRANSACTION + "/"
			+ IRestResourcesConstants.REST_INTEGRATION;
	String PATH_TO_RUN_INTEGRATION = PATH_TO_INTEGRATION + "/" + IRestResourcesConstants.REST_RUN;
}
