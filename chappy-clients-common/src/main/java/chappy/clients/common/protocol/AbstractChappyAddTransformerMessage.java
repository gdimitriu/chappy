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
package chappy.clients.common.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.transformers.AbstractStep;

/**
 * @author Gabriel Dimitriu
 *
 */
public abstract class AbstractChappyAddTransformerMessage {

	/** transformer name as it will be in flow */
	private String transformerName = null;
	
	/** transformer class name as it will be in flow */
	private String transformerClassName = null;
	
	/** transformer package name as it will be in flow */
	private String transformerPackageName = null;
	
	/** transformer data as it will be in flow */
	private String transformerData = null;
	
	/** cookie for the transaction */
	private IChappyCookie cookie = null;
	
	/** exception received from chappy */
	private Exception exception = null;
	
	/** reply message received from chappy */
	private String replyMessage = null;
	
	/**
	 * there will be no default constructor.
	 */
	public AbstractChappyAddTransformerMessage(final String transformerName) {
		this.transformerName = transformerName;
	}

	
	/**
	 * @return cookie for chappy transaction.
	 */
	public IChappyCookie getCookie() {
		return cookie;
	}
	
	/**
	 * set the cookie for this transaction.
	 * @param cookie of the chappy transaction.
	 */
	public void setCookie(final IChappyCookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * @return true if the request has exception.
	 */
	public boolean hasException() {
		if (exception != null) {
			return true;
		}
		return false;
	}

	/**
	 * @return the reply message from chappy.
	 */
	public String getReplyMessage() {
		if (replyMessage != null) {
			return replyMessage;
		}
		return null;
	}

	
	/**
	 * @return exception received from chappy.
	 */
	public Exception getException() {
		return exception;
	}


	/**
	 * @return the transformerName
	 */
	public String getTransformerName() {
		return transformerName;
	}


	/**
	 * @return the transformerClassName
	 */
	public String getTransformerClassName() {
		return transformerClassName;
	}

	/**
	 * @return the transformerData
	 */
	public String getTransformerData() {
		return transformerData;
	}

	/**
	 * @param transformerClassName
	 * @throws IOException 
	 */
	public void setTransformerFromClassPath(final String transformerClassName, final String packageName) throws IOException {
		this.transformerClassName = transformerClassName;
		this.transformerPackageName = packageName;
		transformerData = getClassFromClassPathAsString(transformerClassName, transformerPackageName);
	}

	/**
	 * getClassAsString from ClassPath
	 * @param className
	 * @param packageName
	 * @return class definition as string
	 * @throws IOException 
	 */
	private String getClassFromClassPathAsString(final String className, final String packageName) throws IOException {
		String classPath = null;
		Reflections ref = new Reflections(packageName);
		Set<Class<? extends AbstractStep>> trasnformers = ref.getSubTypesOf(AbstractStep.class);
		for (Class<? extends AbstractStep> cl : trasnformers) {
			if (cl.getSimpleName().equals(className)){
				classPath = cl.getCanonicalName();
			}
		}
		if (classPath == null) {
			throw new FileNotFoundException("file not found on client side");
		}
		classPath = classPath.replace(".", "/");
		classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + classPath  + ".class";
		InputStream is = new FileInputStream(new File(classPath));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count =0;
		while((count = is.read(buffer,0,buffer.length))>0 ) {
			outputStream.write(buffer,0,count);
		}
		is.close();
		outputStream.close();
		return Base64.getEncoder().encodeToString(outputStream.toByteArray());
	}


	/**
	 * @return the transformerPackageName
	 */
	public String getTransformerPackageName() {
		return transformerPackageName;
	}


	/**
	 * @param exception the exception to set
	 */
	public void setException(final Exception exception) {
		this.exception = exception;
	}


	/**
	 * @param replyMessage the replyMessage to set
	 */
	public void setReplyMessage(final String replyMessage) {
		this.replyMessage = replyMessage;
	}

}
