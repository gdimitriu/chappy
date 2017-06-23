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
package chappy.statistics;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.statisticslogs.IStatistics;

/**
 * @author Gabriel Dimitriu
 *
 */
public class StatisticsFactory {
	
	/** singleton for factory */
	private static StatisticsFactory singleton = new StatisticsFactory();
	
	/** private constructor for singleton */
	private StatisticsFactory() {
		
	}
	
	/**
	 * get the factory singleton.
	 * @return statistics factory.
	 */
	public static StatisticsFactory getInstance() {
		return singleton;
	}

	/**
	 * create the new instance of the statistics based on the user perferences
	 * @param cookie
	 * @return statistics instance
	 */
	public IStatistics newInstance(final CookieTransaction cookie) {
		SystemPolicyProvider policy = null;
		return null;
	}
}
