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

import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.policy.IUserPolicy;
import chappy.interfaces.statisticslogs.IStatistics;
import chappy.policy.provider.SystemPolicyProvider;

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
	 * create the new instance of the statistics based on the user preferences
	 * @param cookie
	 * @return statistics instance
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public IStatistics newInstance(final IChappyCookie cookie) throws InstantiationException, IllegalAccessException {
		IUserPolicy policy = SystemPolicyProvider.getInstance().getAuthenticationHandler();
		return newInstance(policy.statisticsType(cookie.getUserName()));
	}
	
	/**
	 * create a new instance of the statistics based on the type.
	 * @param type of the statistics
	 * @return instance of statistics
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private IStatistics newInstance(final String type) throws InstantiationException, IllegalAccessException {
		Reflections reflection = new Reflections("chappy.statistics");
		Set<Class<? extends IStatistics>> availableImplementations = reflection.getSubTypesOf(IStatistics.class);
		for (Class<? extends IStatistics> data : availableImplementations) {
			if (data.getSimpleName().equals(type)) {
				return data.newInstance();
			}
		}
		return null;
	}
}
