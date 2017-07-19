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
package chappy.statistics.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.statisticslogs.IStatistics;
import chappy.interfaces.statisticslogs.StatisticLog;

/**
 * @author Gabriel Dimitriu
 *
 */
public class MemoryVolatile implements IStatistics {
	
	/** list that hold the statistics */
	protected List<StatisticLog> statisticHolder = new ArrayList<StatisticLog>();

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#putStatistic(java.lang.String, java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public StatisticLog putStatistic(final String stepName, final LocalDateTime start, final LocalDateTime stop) {
		StatisticLog stat = new StatisticLog(stepName, start, stop);
		statisticHolder.add(stat);
		return stat;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#putStatistic(chappy.interfaces.statisticslogs.StatisticLog)
	 */
	@Override
	public void putStatistic(final StatisticLog data) {
		statisticHolder.add(data);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#getStatisticsForStep(java.lang.String)
	 */
	@Override
	public List<StatisticLog> getStatisticsForStep(final String stepName) {
		List<StatisticLog> ret = new ArrayList<StatisticLog>();
		for (StatisticLog stat : statisticHolder) {
			if (stat.getStepName().equals(stepName)) {
				ret.add(stat);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.statisticslogs.IStatistics#getAllStatistics()
	 */
	@Override
	public List<StatisticLog> getAllStatistics() {
		return this.statisticHolder;
	}

	@Override
	public void persist(IPersistence persistence) {
		// TODO Auto-generated method stub
		
	}

}
