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
package chappy.providers.transaction;

import java.util.HashMap;
import java.util.Map;

import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.statisticslogs.ILogs;
import chappy.interfaces.statisticslogs.IStatistics;
import chappy.logs.LogsFactory;
import chappy.statistics.StatisticsFactory;

/**
 * provider for statistics and log
 * @author Gabriel Dimitriu
 *
 */
public class StatisticsLogsProvider {

	/** singleton provider */
	private static StatisticsLogsProvider singleton = new StatisticsLogsProvider();
	
	/** statistics map */
	private Map<String, IStatistics> statisticsMap = new HashMap<String, IStatistics>();
	
	/** logs map */
	private Map<String, ILogs> logsMap = new HashMap<String, ILogs>();
	
	/**
	 * private constructor for singleton.
	 */
	private StatisticsLogsProvider() {
		
	}
	
	/**
	 * get the singleton instance.
	 * @return singleton instance.
	 */
	public static StatisticsLogsProvider getInstance() {
		return singleton;
	}
	
	/**
	 * get the statistics holder for this transaction
	 * @param cookie
	 * @return statistics for this transaction.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public IStatistics getStatistics(final CookieTransaction cookie) throws InstantiationException, IllegalAccessException {
		if (cookie == null) {
			return null;
		}
		if (statisticsMap.containsKey(cookie.getTransactionId())) {
			return statisticsMap.get(cookie.getTransactionId());
		}
		return StatisticsFactory.getInstance().newInstance(cookie);
	}
	
	/**
	 * get the logs holder for this transaction
	 * @param cookie
	 * @return logs for this transaction
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ILogs getLogs(final CookieTransaction cookie) throws InstantiationException, IllegalAccessException {
		if (cookie == null) {
			return null;
		}
		if (logsMap.containsKey(cookie.getTransactionId())) {
			return logsMap.get(cookie.getTransactionId());
		}
		return LogsFactory.getInstance().newInstance(cookie);
	}
}
