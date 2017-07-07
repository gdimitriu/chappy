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
package chappy.logs.implementations;

import java.time.LocalDateTime;
import java.util.List;

import chappy.interfaces.statisticslogs.IStatistics;
import chappy.interfaces.statisticslogs.StatisticLog;

/**
 * @author Gabriel Dimitriu
 *
 */
public class DatanucleusMemory implements IStatistics {

	/**
	 * 
	 */
	public DatanucleusMemory() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#putStatistic(java.lang.String, java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public void putStatistic(String stepName, LocalDateTime start, LocalDateTime stop) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#putStatistic(chappy.interfaces.statisticslogs.StatisticLog)
	 */
	@Override
	public void putStatistic(StatisticLog data) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#getStatisticsForStep(java.lang.String)
	 */
	@Override
	public List<StatisticLog> getStatisticsForStep(String stepName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#getAllStatistics()
	 */
	@Override
	public List<StatisticLog> getAllStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
