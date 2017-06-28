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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statistic or log holder.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
public class StatisticLog {

	/** step name */
	private String stepName;
	/** start time */
	private LocalDateTime startTime;
	/** end time */
	private LocalDateTime stopTime;
	/** log message */
	private String message;
	
	public StatisticLog() {
		
	}
	
	/** create a statistic data */
	public StatisticLog(final String stepName, final LocalDateTime start, final LocalDateTime stop) {
		this.stepName = stepName;
		this.startTime = start;
		this.stopTime = stop;
		this.message = null;
	}
	
	/** create a log data */
	public StatisticLog(final String stepName, final LocalDateTime time, final String message) {
		this(stepName, time, time);
		this.message = message;
	}

	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * @param stepName the stepName to set
	 */
	public void setStepName(final String stepName) {
		this.stepName = stepName;
	}

	/**
	 * @return the startTime
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(final LocalDateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the stopTime
	 */
	public LocalDateTime getStopTime() {
		return stopTime;
	}

	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(final LocalDateTime stopTime) {
		this.stopTime = stopTime;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
}
