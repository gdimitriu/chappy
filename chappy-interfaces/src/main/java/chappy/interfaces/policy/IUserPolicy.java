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
package chappy.interfaces.policy;

import chappy.interfaces.statisticslogs.IStatisticsLogsConstants;

/**
 * User authentication on the system.
 * @author Gabriel Dimitriu
 *
 */
public interface IUserPolicy {

	/**
	 * is the user Authenticated
	 * @param userName name of the user
	 * @param password for the user
	 * @return true if is authenticate correct
	 */
	default boolean isAuthenticate(final String userName, final String password) {
		return true;
	}
	
	/**
	 * is the user is allowed to persist
	 * @param userName name of the user
	 * @return true if the user has requested persistence
	 */
	default boolean isAllowedPersistence(final String userName) {
		return true;
	}
	
	
	/**
	 * get the statistics type for this user.
	 * @param userName
	 * @return statistics/log type
	 */
	default String statisticsType(final String userName) {
		return IStatisticsLogsConstants.MEMORY_VOLATILE;
	}
	
	/**
	 * get the persistence type for this user.
	 * @param userName
	 * @return persistence type.
	 */
	default String persistenceType(final String userName) {
		return "default/datanucleus/ODF";
	}
}
