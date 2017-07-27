/**
 * 
 */
package chappy.persistence.datanucleus.loaders;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.datanucleus.ClassLoaderResolverImpl;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyDatanucleusClassResolver extends ClassLoaderResolverImpl {

	private ClassLoader customLoading = null;
	/**
	 * 
	 */
	public ChappyDatanucleusClassResolver(final ClassLoader classLoader) {
		customLoading = classLoader;
	}

	@Override
	public Class<?> classForName(String name, ClassLoader primary) {
		
		return null;
	}

	@Override
	public Class<?> classForName(String name, ClassLoader primary, boolean initialize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> classForName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> classForName(String name, boolean initialize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAssignableFrom(String class_name_1, Class<?> class_2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAssignableFrom(Class<?> class_1, String class_name_2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAssignableFrom(String class_name_1, String class_name_2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Enumeration<URL> getResources(String resourceName, ClassLoader primary) throws IOException {
		return primary.getResources(resourceName);
	}

	@Override
	public URL getResource(String resourceName, ClassLoader primary) {
		return primary.getResource(resourceName);
	}

	@Override
	public void setRuntimeClassLoader(ClassLoader loader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerUserClassLoader(ClassLoader loader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPrimary(ClassLoader primary) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsetPrimary() {
		// TODO Auto-generated method stub
		
	}

}
