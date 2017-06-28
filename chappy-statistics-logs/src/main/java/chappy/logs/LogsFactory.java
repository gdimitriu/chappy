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
package chappy.logs;

import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.policy.IUserPolicy;
import chappy.interfaces.statisticslogs.ILogs;
import chappy.policy.provider.SystemPolicyProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class LogsFactory {

	/** singleton entry point */
	private static LogsFactory singleton = new LogsFactory();
	
	/** private constructor for singleton */
	private LogsFactory() {
		
	}
	
	/**
	 * get the singleton
	 * @return logs factory
	 */
	public static LogsFactory getInstance() {
		return singleton;
	}
	
	/**
	 * create the new instance of the logs based on the user preferences
	 * @param cookie
	 * @return statistics instance
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ILogs newInstance(final CookieTransaction cookie) throws InstantiationException, IllegalAccessException {
		IUserPolicy policy = SystemPolicyProvider.getInstance().getAuthenticationHandler();
		return newInstance(policy.statisticsType(cookie.getUserName()));
	}
	
	/**
	 * get the new instance of the log base on type.
	 * @param type of the logs
	 * @return instance of the log.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ILogs  newInstance(final String type) throws InstantiationException, IllegalAccessException {
		Reflections reflection = new Reflections("chappy.logs");
		Set<Class<? extends ILogs>> availableImplementations = reflection.getSubTypesOf(ILogs.class);
		for (Class<? extends ILogs> data : availableImplementations) {
			if (data.getSimpleName().equals(type)) {
				return data.newInstance();
			}
		}
		return null;
	}
}
