package com.hp.it.sbs.lgms.service.stub;

import com.hp.it.sbs.core.shared.serialization.ObjectMapperProvider;
import com.hp.it.sbs.core.shared.stub.StubDataManager;
import com.hp.it.sbs.lgms.interfaces.LGMS;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LGMS Stub
 * Service Category:      LGMS
 * Generated By:          sbs-generator
 * Service Version:       1.0.0-SNAPSHOT
 * Core Version:          1.1.2
 * I-Processor Version:   2.1.5
 *
 * This class is generated based upon the service's interface(s).
 * The versions listed above were utilized in the process.
 * These may be snapshots instead of actual releases during development.
 */
public final class LGMSStub implements LGMS
{
	/** Service Category */
	private static final String SERVICE_CATEGORY = "LGMS";

	/** Service Version: Major */
	private static final String VERSION_MAJOR = "1";
	
	/** Service Version: Minor */
	private static final String VERSION_MINOR = "0";
	
	/** Service Version: Incremental */
	private static final String VERSION_INCREMENTAL = "0";

	/** Logger */
	private static final Logger SERVICELOG;

	/** Stub Data Manager to control flow of data */
	private StubDataManager dataManager;
	
	/** List of Method Names */
	private List<String> methodNames;
	
	/**
	 * String Literals: Processing
	 * JSON - specify the service produces JSON
	 * LOG_SEPARATOR - string literal to divide values in logging
	 * SUPPRESS_UNCHECKED - string literal to suppress unchecked exceptions (Conditional)
	 * SUPPRESS_RAWTYPES - string literal to suppress raw type exceptions (Conditional)
	 */
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	
	/** Method Literal: getLGMSValue */
	private static final String METHOD_GETLGMSVALUE = "getLGMSValue";
	/** Method Literal: resetLGMSValue */
	private static final String METHOD_RESETLGMSVALUE = "resetLGMSValue";
	/** Method Literal: setLGMSValue */
	private static final String METHOD_SETLGMSVALUE = "setLGMSValue";
	/** Method Literal: testParseFile */
	private static final String METHOD_TESTPARSEFILE = "testParseFile";

	
	/** 
	 * This is here because we want to get the RestEasy setup done once, and only once per JVM
	 * This prevents concurrency conflicts when multiple clients are created (either dynamically by thread, or 
	 * as part of a normal construction)
	 */
	static
	{
		// Only swap the class loader when running in IKVM's .NET JVM
		if( System.getProperty("java.runtime.name").equals("IKVM.NET") ) {
			// Setup the contextual class loader for the thread before anything from RESTEasy is instantiated
			// Inner classes utilize Class.forName() which causes an issue if the wrong context loader is present
			// Note: This is included for supporting .NET Clients converted through IKVM
			String oldCLName = Thread.currentThread().getContextClassLoader().toString();
			Thread.currentThread().setContextClassLoader( LGMSStub.class.getClassLoader() );
			String newCLName = Thread.currentThread().getContextClassLoader().toString();
        
			// Now instantiate the logger and make note of the modifications made to the class loader
			SERVICELOG = LoggerFactory.getLogger(LGMSStub.class);
			SERVICELOG.info( "Context Class Loader changed from \"{}\" to \"{}\"", oldCLName, newCLName  );
		} else {
			// Otherwise only instantiate the logger
			SERVICELOG = LoggerFactory.getLogger(LGMSStub.class);
		}
	}

	/**
	 * Default Constructor
	 */
    public LGMSStub( )
    {
    	//Construct the Stub Data Manager
    	this.dataManager = new StubDataManager();
    	
    	// Acquire a list of methods
    	Method[] methods = LGMS.class.getDeclaredMethods();
    	
    	// Construct a list of method names
		this.methodNames = new ArrayList<String>();
		for( Method m : methods ) {
			this.methodNames.add( m.getName() );
		}
    }
	
	
	/**
	 * Get Service Version Number
	 * @return Version
	 */
	public String getVersion( ) {
		return VERSION_MAJOR + '.' + VERSION_MINOR + '.' + VERSION_INCREMENTAL;
	}
	
	/**
	 * Get Service Major Version Number
	 * @return Major Version
	 */
	public String getMajorVersion( ) {
		return VERSION_MAJOR;
	}
	
	/**
	 * Get Service Minor Version Number
	 * @return Minor Version
	 */
	public String getMinorVersion( ) {
		return VERSION_MINOR;
	}
	
	/**
	 * Get Service Incremental Version Number
	 * @return IncrementalVersion
	 */
	public String getIncrementalVersion( ) {
		return VERSION_INCREMENTAL;
	}
	
	/**
	 * Get Service URL
	 * @return URL
	 */
	public String getServiceURL( ) {
		// Generate the Service URL
		StringBuilder url = new StringBuilder();
		url.append( "http://localhost:8080/" );
		url.append( SERVICE_CATEGORY.toLowerCase(Locale.US) );
		url.append( '-' );
		url.append( getMajorVersion() );
		url.append( '.' );
		url.append( getMinorVersion() );
		url.append( '/' );
	
		// Return the Service URL
		return url.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#getLGMSValue LGMS
	 */
	public String getLGMSValue(   )
	{

		//Acquire an instance of the object mapper
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();

		//Construct the necessary return type
		JavaType returnType = om.getTypeFactory().constructFromCanonical( "java.lang.String" );

		//Add a JavaType for each of the method parameters to a list
		List<JavaType> parameterTypes = new ArrayList<JavaType>();

		//Add all of the method parameters to an object list
		List<Object> parameterObjects = new ArrayList<Object>();

		//Acquire the object based on the stored JSON value
		String result = (String) dataManager.simulateServiceMethod(
			SERVICE_CATEGORY,
			this.methodNames,
			METHOD_GETLGMSVALUE,
			returnType,
			parameterTypes,
			parameterObjects
		);
		
		//Return the result
		return result;
	}
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#setLGMSValue LGMS
	 */
	public Integer setLGMSValue(  
			final String lgmsValue )
	{

		//Acquire an instance of the object mapper
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();

		//Construct the necessary return type
		JavaType returnType = om.getTypeFactory().constructFromCanonical( "java.lang.Integer" );

		//Add a JavaType for each of the method parameters to a list
		List<JavaType> parameterTypes = new ArrayList<JavaType>();
		parameterTypes.add(om.getTypeFactory().constructFromCanonical( "java.lang.String" ));

		//Add all of the method parameters to an object list
		List<Object> parameterObjects = new ArrayList<Object>();
		parameterObjects.add( lgmsValue );

		//Acquire the object based on the stored JSON value
		Integer result = (Integer) dataManager.simulateServiceMethod(
			SERVICE_CATEGORY,
			this.methodNames,
			METHOD_SETLGMSVALUE,
			returnType,
			parameterTypes,
			parameterObjects
		);
		
		//Return the result
		return result;
	}
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#resetLGMSValue LGMS
	 */
	public Integer resetLGMSValue(  
			final String lgmsValue )
	{

		//Acquire an instance of the object mapper
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();

		//Construct the necessary return type
		JavaType returnType = om.getTypeFactory().constructFromCanonical( "java.lang.Integer" );

		//Add a JavaType for each of the method parameters to a list
		List<JavaType> parameterTypes = new ArrayList<JavaType>();
		parameterTypes.add(om.getTypeFactory().constructFromCanonical( "java.lang.String" ));

		//Add all of the method parameters to an object list
		List<Object> parameterObjects = new ArrayList<Object>();
		parameterObjects.add( lgmsValue );

		//Acquire the object based on the stored JSON value
		Integer result = (Integer) dataManager.simulateServiceMethod(
			SERVICE_CATEGORY,
			this.methodNames,
			METHOD_RESETLGMSVALUE,
			returnType,
			parameterTypes,
			parameterObjects
		);
		
		//Return the result
		return result;
	}
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#testParseFile LGMS
	 */
	public String testParseFile(  
			final String fileName )
	{

		//Acquire an instance of the object mapper
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();

		//Construct the necessary return type
		JavaType returnType = om.getTypeFactory().constructFromCanonical( "java.lang.String" );

		//Add a JavaType for each of the method parameters to a list
		List<JavaType> parameterTypes = new ArrayList<JavaType>();
		parameterTypes.add(om.getTypeFactory().constructFromCanonical( "java.lang.String" ));

		//Add all of the method parameters to an object list
		List<Object> parameterObjects = new ArrayList<Object>();
		parameterObjects.add( fileName );

		//Acquire the object based on the stored JSON value
		String result = (String) dataManager.simulateServiceMethod(
			SERVICE_CATEGORY,
			this.methodNames,
			METHOD_TESTPARSEFILE,
			returnType,
			parameterTypes,
			parameterObjects
		);
		
		//Return the result
		return result;
	}

}
