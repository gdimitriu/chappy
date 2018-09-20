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
package chappy.utils.files;

import java.io.File;

/**
 * @author Gabriel Dimitriu
 *
 */
public final class DeleteUtils {
	
	/* private constructor because this is a helper class */
	private DeleteUtils() {
		
	}
	
	
	/**
	 * Recursive delete a directory and its content.
	 * @param dir the directory or file to be deleted
	 * @return true if succeeded. 
	 */
	public static boolean recursiveDeleteDir(File dir) {
	    if (dir.isDirectory()) {
	      String[] children = dir.list();
	      for (int i = 0; i < children.length; i++) {
	        boolean success = recursiveDeleteDir(new File(dir, children[i]));
	        if (!success) {
	          return false;
	        }
	      }
	    }
	    return dir.delete();
	  }

}
