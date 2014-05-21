package com.hp.it.sbs.lgms.transport.server;

import ch.qos.logback.core.util.Loader;

import com.hp.it.sbs.core.server.TransportServerImplementation;
import com.hp.it.sbs.core.server.beans.ServiceRequest;
import com.hp.it.sbs.core.server.beans.ServiceResponse;
import com.hp.it.sbs.core.server.logging.LogInitializer;
import com.hp.it.sbs.core.server.logging.ServerActivity;
import com.hp.it.sbs.core.server.logging.ServerActivityTracker;
import com.hp.it.sbs.core.server.logging.ServiceActivityTracker;
import com.hp.it.sbs.core.server.security.TrustStoreManager;
import com.hp.it.sbs.core.shared.exceptions.CoreServiceException;
import com.hp.it.sbs.core.shared.exceptions.ServiceStandardErrorCodes;
import com.hp.it.sbs.lgms.interfaces.LGMS;
import com.hp.it.sbs.lgms.service.LGMSImpl;
import com.hp.it.sbs.lgms.service.stub.LGMSStub;

import java.net.URL;

import java.util.ArrayList;
import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LGMS Transport Server
 * Service Category:      LGMS
 * Generated By:          sbs-generator
 * Service Version:       1.0.0-SNAPSHOT
 * Core Version:          1.1.2
 * I-Processor Version:   2.1.5
 *
 * This class is generated based upon one of the service's interface(s).
 * The versions listed above were utilized in the process.
 * These may be snapshots instead of actual releases during development.
 */
@Path("/")
public class LGMSTransportServerREST extends TransportServerImplementation  
{
	/** Service Category */
	private static final String SERVICE_CATEGORY = "LGMS";

	/** Service Logger */
	private static final Logger SERVICELOG = LoggerFactory.getLogger(LGMSTransportServerREST.class);

	/** Service Version: Major */
	private static final String VERSION_MAJOR = "1";
	
	/** Service Version: Minor */
	private static final String VERSION_MINOR = "0";
	
	/** Service Version: Incremental */
	private static final String VERSION_INCREMENTAL = "0";
	
	/** Stub Implementation of the Service */
	private LGMS stub;
	
	/** Mock Implementation of the Service */
	private LGMS mock;
	
	/** Real Implementation of the Service */
	private LGMSImpl service;
	
	/** Whether the Service's Logger was initialized */
	private static boolean serviceLogInitialized;
	
	
	/** System Property: Service Version */
	private static final String PROPERTY_SERVICE_VERSION = "sbs.LGMS.version";
	
	
	
	/** String Literals: EXCEPTION_INVOCATION - message response for exception thrown during method execution */
	private static final String EXCEPTION_INVOCATION =
			"***** The following exception : %s was thrown during invocation of method : %s.";
	
	/** String Literals: LOGFILE_NAME - naming convention for the log configuration file */
	private static final String LOGFILE_NAME = "logback-LIFECYCLE.xml";
	
	/** String Literals: LOGFILE_NAME - naming convention for the log configuration file */
	private static final String LOGFILE_LIFECYCLE = "LIFECYCLE";
	
	
	/** Access Control Group: SBS-LGMS-ACCESS0 */
	private static final String ACG_SBS_LGMS_ACCESS0 = "SBS-LGMS-ACCESS0";

	/** Parameter Literal: Value0 */
	private static final String VALUE0 = "Value0";

	/** Type Literal: Integer */
	private static final String TYPE_INTEGER = "Integer";
	/** Type Literal: String */
	private static final String TYPE_STRING = "String";

	/** MIME Type Literal: application/json */
	private static final String MIME_APPLICATION_JSON = "application/json";

	/** Method Literal: getLGMSValue */
	private static final String METHOD_GETLGMSVALUE = "getLGMSValue";
	/** Method Literal: resetLGMSValue */
	private static final String METHOD_RESETLGMSVALUE = "resetLGMSValue";
	/** Method Literal: setLGMSValue */
	private static final String METHOD_SETLGMSVALUE = "setLGMSValue";
	/** Method Literal: testParseFile */
	private static final String METHOD_TESTPARSEFILE = "testParseFile";



	/**
	 * Static Logger Block
	 * Acquires the correct location to the logback.xml configuration file
	 * and triggers the class logger initialization with this location.
	 * Prevents the configuration from loading from another file in the classpath
	 */
	static
	{	
		// Acquire the file location for logback.xml
		ClassLoader classLoader = Loader.getClassLoaderOfObject( new LGMSTransportServerREST( true ) );
		
		// Determine the file name for the logback.xml configuration file
		String lc = TransportServerImplementation.determineLifeCycle().toString().toLowerCase(Locale.US);
		String configurationFileName = LOGFILE_NAME.replace( LOGFILE_LIFECYCLE, lc );
		
		// Acquire a url to the resource from the class loader
		URL configurationURL = classLoader.getResource(configurationFileName);
		
		// Initialize the logger
		if( configurationURL != null ) {
			// Construct the version for the logging property
			StringBuilder version = new StringBuilder();
			version.append( VERSION_MAJOR );
			version.append( '.' );
			version.append( VERSION_MINOR );
			
			System.setProperty(PROPERTY_SERVICE_VERSION, version.toString() );
			LogInitializer.getInstance().initializeLogger(
				LGMSTransportServerREST.class,
				configurationURL,
				configurationFileName.toString()
			);
		}
		
		// Initialize the Trust Store for SSL
		TrustStoreManager.initializeTrustStore();
	}
	
	/**
	 * Private Constructor
	 * @param initialized Whether logger is properly initialized
	 */
	private LGMSTransportServerREST( final Boolean initialized )
	{
		setServiceLogInitialized( initialized );
	}
	
	
	/**
	 * Default Constructor
	 */
	public LGMSTransportServerREST( )
	{
		// Transport Server Implementation Constructor
		super();
		
		// Initialize the service which will also utilize the logback.configurationFile property
		service = new LGMSImpl(
				TransportServerImplementation.determineDataSourceType(),
				TransportServerImplementation.determineLifeCycle() );
				
		// Set the service version
		service.setMajorVersion(VERSION_MAJOR);
		service.setMinorVersion(VERSION_MINOR);
		service.setIncrementalVersion(VERSION_INCREMENTAL);
		
		// Construct the service url
		StringBuilder serviceURL = new StringBuilder();
		serviceURL.append( "https://" );
		serviceURL.append( HOST_NAME );
		serviceURL.append( '-' );
		serviceURL.append( VERSION_MAJOR );
		serviceURL.append( '-' );
		serviceURL.append( VERSION_MINOR );
		serviceURL.append( '/' );
		
		// Set the service url
		service.setServiceURL( serviceURL.toString() );
		
		// Note if the service log was not properly initialized before this constructor
		// This could lead to the wrong log file being utilized by the service
		if(!serviceLogInitialized) {
			SERVICELOG.error("Service Log was not properly initialized before constructor was executed.");
		}
	}
	
	/**
	 * Set Service Log Initialized
	 * @param initialized Whether the service log is properly initialized
	 */
	private static void setServiceLogInitialized( final Boolean initialized ) {
		// Set the service log as being initialized
		serviceLogInitialized = initialized;
	}

	
	/**
	 * Get Service Implementation
	 * Returns the implementation to execute the request against
	 * depending on whether the stub, mock, or service implementation should be used
	 * @param mockRequest Whether a mock implementation should be utilized
	 * @param stubRequest Whether a stub implementation should be utilized
	 * @return Service Implementation
	 */
	private LGMS getServiceImplementation( final Boolean mockRequest, final Boolean stubRequest )
	{
		// If request desires stub implementation, return the stub service
		if( stubRequest != null && stubRequest ) {
			// Instantiate a stub service implementation if necessary
			if( stub == null ) {
				stub = new LGMSStub( );
			}
			
			// Return the stub implementation
			return stub;
		}
		
		// If request desires mock execution, return the mock service
		if( mockRequest != null && mockRequest ) {
			// Instantiate a mock service implementation if necessary
			//if( mock == null) {
				//mock = new MockLGMSImpl( );
			//}
			
			// Return the mock implementation
			return mock;
		}
		
		// Otherwise return the real service implementation
		return service;
	}

	
	/**
	 * Validate the Incoming Request
	 * @param <T> Response Object Type
	 * @param request Service Request
	 * @param acgs Access Control Groups
	 * @return Service Response <Object Type>
	 */
	private <T> ServiceResponse<T> validateRequest(
			final ServiceRequest request,
			final String[] acgs )
	{
		try 
		{
			// Set log values based on the incoming request
			setLogParameters(request.getUnitOfWorkId(), request.getApplicationId());
		
			// Validate the incoming request to createReport
			validateRequest(
				request,
				false,
				acgs
			);
			
			// Copy values from the incoming request
			service.copyValues( request );
		}
		catch ( CoreServiceException cse )
		{
			// Handle any exceptions such as an invalid request
			ServiceResponse<T> response = new ServiceResponse<T>( request );
			response.setStatusCode( cse.getCode() );
			response.setStatusDescription( cse.getDescription() );
			
			// Clear thread locals
			clearLogParameters();
			service.clearThreadLocalVariables();
			ServiceActivityTracker.clearThreadLocalVariables();
			ServerActivityTracker.clearThreadLocalVariables();
			
			// Return a validation error as a response
			return response;
		}
		
		// Otherwise no response
		return null;
	}

	/**
	 * getLGMSValue
	 * @param request ServiceRequest Object
	 * @return String
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#getLGMSValue LGMS
	 */
	@POST
	@Path(METHOD_GETLGMSVALUE)
	@Produces({MIME_APPLICATION_JSON})
	public final ServiceResponse<String> getLGMSValue(
			@Form final ServiceRequest request )
	{
		// Start tracking the time required to respond to getLGMSValue()
		ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Validate the incoming request
		ServiceResponse<String> validationResponse = this.<String>validateRequest(
				request,
				new String[] {ACG_SBS_LGMS_ACCESS0}
		);
		
		// Check if an issue was encountered
		if ( validationResponse != null ) {
			return validationResponse;
		}
		
		// Prepare a successful response object for getLGMSValue()
		ServiceResponse<String> response = new ServiceResponse<String>( request );
		response.setStatusCode( ServiceStandardErrorCodes.SC_200.getCode() );
		response.setStatusDescription( ServiceStandardErrorCodes.SC_200.getMsg() );
		String value = null;

		try {
			// Start tracking the execution of getLGMSValue()
			ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			// Execute the service method
			value = getServiceImplementation(
				request.isMockRequest(),
				request.isStubRequest()
				).getLGMSValue( );
			
			// Stop tracking the execution of getLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);

		} catch ( Exception e ) {
			//Stop tracking the execution of getLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			//Process the service exception
			processServiceExceptions(
					response,
					EXCEPTION_INVOCATION,
					METHOD_GETLGMSVALUE,
					e
			);
		}
		
		// Update the payload of the response
		this.<String>updateResponsePayload( response, value, METHOD_GETLGMSVALUE);
		
		// Stop tracking the time required to respond to getLGMSValue()
		ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Provide a list of parameter types
		ArrayList<String> parameterTypes = new ArrayList<String>();
			
		// Generate the log output
		generateTransportLog(
				ServiceActivityTracker.getActivityList(),
				SERVICE_CATEGORY,
				METHOD_GETLGMSVALUE,
				request.getApplicationId(),
				String.valueOf(request.isTestRequest()),
				request.getUnitOfWorkId(),
				String.valueOf(response.getStatusCode()),
				TYPE_STRING,
				value,
				parameterTypes
		);

		// Cleanup and return the response to getLGMSValue()
		clearLogParameters();
		service.clearThreadLocalVariables();
		ServiceActivityTracker.clearThreadLocalVariables();
		ServerActivityTracker.clearThreadLocalVariables();
		return response;
	}

	/**
	 * setLGMSValue
	 * @param request ServiceRequest Object
	 * @param lgmsValue param0
	 * @return Integer
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#setLGMSValue LGMS
	 */
	@POST
	@Path(METHOD_SETLGMSVALUE)
	@Produces({MIME_APPLICATION_JSON})
	public final ServiceResponse<Integer> setLGMSValue(
			@Form final ServiceRequest request,
			@FormParam(VALUE0) final String lgmsValue )
	{
		// Start tracking the time required to respond to setLGMSValue()
		ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Validate the incoming request
		ServiceResponse<Integer> validationResponse = this.<Integer>validateRequest(
				request,
				new String[] {ACG_SBS_LGMS_ACCESS0}
		);
		
		// Check if an issue was encountered
		if ( validationResponse != null ) {
			return validationResponse;
		}
		
		// Prepare a successful response object for setLGMSValue()
		ServiceResponse<Integer> response = new ServiceResponse<Integer>( request );
		response.setStatusCode( ServiceStandardErrorCodes.SC_200.getCode() );
		response.setStatusDescription( ServiceStandardErrorCodes.SC_200.getMsg() );
		Integer value = null;

		try {
			// Start tracking the execution of setLGMSValue()
			ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			// Execute the service method
			value = getServiceImplementation(
				request.isMockRequest(),
				request.isStubRequest()
				).setLGMSValue(
				lgmsValue );
			
			// Stop tracking the execution of setLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);

		} catch ( Exception e ) {
			//Stop tracking the execution of setLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			//Process the service exception
			processServiceExceptions(
					response,
					EXCEPTION_INVOCATION,
					METHOD_SETLGMSVALUE,
					e
			);
		}
		
		// Update the payload of the response
		this.<Integer>updateResponsePayload( response, value, METHOD_SETLGMSVALUE);
		
		// Stop tracking the time required to respond to setLGMSValue()
		ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Provide a list of parameter types
		ArrayList<String> parameterTypes = new ArrayList<String>();
		parameterTypes.add(TYPE_STRING);
			
		// Generate the log output
		generateTransportLog(
				ServiceActivityTracker.getActivityList(),
				SERVICE_CATEGORY,
				METHOD_SETLGMSVALUE,
				request.getApplicationId(),
				String.valueOf(request.isTestRequest()),
				request.getUnitOfWorkId(),
				String.valueOf(response.getStatusCode()),
				TYPE_INTEGER,
				value,
				parameterTypes,
			lgmsValue
		);

		// Cleanup and return the response to setLGMSValue()
		clearLogParameters();
		service.clearThreadLocalVariables();
		ServiceActivityTracker.clearThreadLocalVariables();
		ServerActivityTracker.clearThreadLocalVariables();
		return response;
	}

	/**
	 * resetLGMSValue
	 * @param request ServiceRequest Object
	 * @param lgmsValue param0
	 * @return Integer
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#resetLGMSValue LGMS
	 */
	@POST
	@Path(METHOD_RESETLGMSVALUE)
	@Produces({MIME_APPLICATION_JSON})
	public final ServiceResponse<Integer> resetLGMSValue(
			@Form final ServiceRequest request,
			@FormParam(VALUE0) final String lgmsValue )
	{
		// Start tracking the time required to respond to resetLGMSValue()
		ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Validate the incoming request
		ServiceResponse<Integer> validationResponse = this.<Integer>validateRequest(
				request,
				new String[] {ACG_SBS_LGMS_ACCESS0}
		);
		
		// Check if an issue was encountered
		if ( validationResponse != null ) {
			return validationResponse;
		}
		
		// Prepare a successful response object for resetLGMSValue()
		ServiceResponse<Integer> response = new ServiceResponse<Integer>( request );
		response.setStatusCode( ServiceStandardErrorCodes.SC_200.getCode() );
		response.setStatusDescription( ServiceStandardErrorCodes.SC_200.getMsg() );
		Integer value = null;

		try {
			// Start tracking the execution of resetLGMSValue()
			ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			// Execute the service method
			value = getServiceImplementation(
				request.isMockRequest(),
				request.isStubRequest()
				).resetLGMSValue(
				lgmsValue );
			
			// Stop tracking the execution of resetLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);

		} catch ( Exception e ) {
			//Stop tracking the execution of resetLGMSValue()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			//Process the service exception
			processServiceExceptions(
					response,
					EXCEPTION_INVOCATION,
					METHOD_RESETLGMSVALUE,
					e
			);
		}
		
		// Update the payload of the response
		this.<Integer>updateResponsePayload( response, value, METHOD_RESETLGMSVALUE);
		
		// Stop tracking the time required to respond to resetLGMSValue()
		ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Provide a list of parameter types
		ArrayList<String> parameterTypes = new ArrayList<String>();
		parameterTypes.add(TYPE_STRING);
			
		// Generate the log output
		generateTransportLog(
				ServiceActivityTracker.getActivityList(),
				SERVICE_CATEGORY,
				METHOD_RESETLGMSVALUE,
				request.getApplicationId(),
				String.valueOf(request.isTestRequest()),
				request.getUnitOfWorkId(),
				String.valueOf(response.getStatusCode()),
				TYPE_INTEGER,
				value,
				parameterTypes,
			lgmsValue
		);

		// Cleanup and return the response to resetLGMSValue()
		clearLogParameters();
		service.clearThreadLocalVariables();
		ServiceActivityTracker.clearThreadLocalVariables();
		ServerActivityTracker.clearThreadLocalVariables();
		return response;
	}

	/**
	 * testParseFile
	 * @param request ServiceRequest Object
	 * @param fileName param0
	 * @return String
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#testParseFile LGMS
	 */
	@POST
	@Path(METHOD_TESTPARSEFILE)
	@Produces({MIME_APPLICATION_JSON})
	public final ServiceResponse<String> testParseFile(
			@Form final ServiceRequest request,
			@FormParam(VALUE0) final String fileName )
	{
		// Start tracking the time required to respond to testParseFile()
		ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Validate the incoming request
		ServiceResponse<String> validationResponse = this.<String>validateRequest(
				request,
				new String[] {ACG_SBS_LGMS_ACCESS0}
		);
		
		// Check if an issue was encountered
		if ( validationResponse != null ) {
			return validationResponse;
		}
		
		// Prepare a successful response object for testParseFile()
		ServiceResponse<String> response = new ServiceResponse<String>( request );
		response.setStatusCode( ServiceStandardErrorCodes.SC_200.getCode() );
		response.setStatusDescription( ServiceStandardErrorCodes.SC_200.getMsg() );
		String value = null;

		try {
			// Start tracking the execution of testParseFile()
			ServerActivityTracker.startFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			// Execute the service method
			value = getServiceImplementation(
				request.isMockRequest(),
				request.isStubRequest()
				).testParseFile(
				fileName );
			
			// Stop tracking the execution of testParseFile()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);

		} catch ( Exception e ) {
			//Stop tracking the execution of testParseFile()
			ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTION);
			
			//Process the service exception
			processServiceExceptions(
					response,
					EXCEPTION_INVOCATION,
					METHOD_TESTPARSEFILE,
					e
			);
		}
		
		// Update the payload of the response
		this.<String>updateResponsePayload( response, value, METHOD_TESTPARSEFILE);
		
		// Stop tracking the time required to respond to testParseFile()
		ServerActivityTracker.endFrameworkActivity(ServerActivity.SERVER_ACTIVITY.SERVER_EXECUTE_METHOD);
		
		// Provide a list of parameter types
		ArrayList<String> parameterTypes = new ArrayList<String>();
		parameterTypes.add(TYPE_STRING);
			
		// Generate the log output
		generateTransportLog(
				ServiceActivityTracker.getActivityList(),
				SERVICE_CATEGORY,
				METHOD_TESTPARSEFILE,
				request.getApplicationId(),
				String.valueOf(request.isTestRequest()),
				request.getUnitOfWorkId(),
				String.valueOf(response.getStatusCode()),
				TYPE_STRING,
				value,
				parameterTypes,
			fileName
		);

		// Cleanup and return the response to testParseFile()
		clearLogParameters();
		service.clearThreadLocalVariables();
		ServiceActivityTracker.clearThreadLocalVariables();
		ServerActivityTracker.clearThreadLocalVariables();
		return response;
	}

}
