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
package chappy.interfaces.statisticslogs;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IStatistics {
	
	/**
	 * put a statistic information
	 * @param stepName step for statistic
	 * @param start time
	 * @param stop time
	 */
	void putStatistic(final String stepName, final LocalDateTime start, final LocalDateTime stop);
	
	/**
	 * put a statistic information
	 * @param data contained statistic/log
	 */
	void putStatistic(final StatisticLog data);
	
	/**
	 * get all statistics for a step
	 * @param stepName to get the statistics
	 * @return list of statistics
	 */
	List<StatisticLog> getStatisticsForStep(final String stepName);
	
	/**
	 * get all statistics for this transaction.
	 * @return list of statistics
	 */
	List<StatisticLog> getAllStatistics();
}
