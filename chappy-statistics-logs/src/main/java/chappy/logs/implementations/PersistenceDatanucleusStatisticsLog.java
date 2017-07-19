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

import javax.jdo.annotations.PersistenceCapable;

import chappy.interfaces.markers.ISystemLogsPersistence;
import chappy.interfaces.statisticslogs.StatisticLog;

/**
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable
public class PersistenceDatanucleusStatisticsLog extends StatisticLog implements ISystemLogsPersistence {

	/**
	 * 
	 */
	public PersistenceDatanucleusStatisticsLog() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param stepName
	 * @param start
	 * @param stop
	 */
	public PersistenceDatanucleusStatisticsLog(String stepName, LocalDateTime start, LocalDateTime stop) {
		super(stepName, start, stop);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param stepName
	 * @param time
	 * @param message
	 */
	public PersistenceDatanucleusStatisticsLog(String stepName, LocalDateTime time, String message) {
		super(stepName, time, message);
		// TODO Auto-generated constructor stub
	}

}
