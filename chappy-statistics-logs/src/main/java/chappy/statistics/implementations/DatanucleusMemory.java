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
import chappy.interfaces.statisticslogs.StatisticLog;
import chappy.logs.implementations.PersistenceDatanucleusStatisticsLog;

/**
 * @author Gabriel Dimitriu
 *
 */
public class DatanucleusMemory extends MemoryVolatile {

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
	public StatisticLog putStatistic(final String stepName, final LocalDateTime start, final LocalDateTime stop) {
		StatisticLog stat = new PersistenceDatanucleusStatisticsLog(stepName, start, stop);
		super.putStatistic(stat);
		return stat;
	}
}
