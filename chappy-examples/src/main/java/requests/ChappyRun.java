/**
    Copyright (c) 2018 Gabriel Dimitriu All rights reserved.
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
package requests;

/**
 * Main class for all test to run.
 * @author Gabriel Dimitriu
 *
 */
public class ChappyRun {

	/**
	 * 
	 */
	public ChappyRun() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("should be called with arguments: host RESTPort JMSPort");
			System.exit(-1);
		}
		new RestClientTrasactionFlowTransformations(args[0], Integer.parseInt(args[1])).runAllTests();
		new JMSClientTransactionFlowTransformations(args[0], Integer.parseInt(args[2])).runAllTests();
		new MixedJMSClientRESTClientTransaction(args[0], Integer.parseInt(args[1]),Integer.parseInt(args[2])).runAllTests();
	}

}
