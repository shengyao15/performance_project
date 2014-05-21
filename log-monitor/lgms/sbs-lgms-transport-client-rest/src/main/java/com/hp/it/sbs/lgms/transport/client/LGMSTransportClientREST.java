package com.hp.it.sbs.lgms.transport.client;

import com.hp.it.sbs.core.client.SBSHttpClientExecutor;
import com.hp.it.sbs.core.client.TransportClientImplementation;
import com.hp.it.sbs.core.client.beans.ServiceAuthentication;
import com.hp.it.sbs.core.client.beans.ServiceRequest;
import com.hp.it.sbs.core.client.beans.ServiceResponse;
import com.hp.it.sbs.core.client.enums.LifeCycle;
import com.hp.it.sbs.core.client.logging.ClientActivity;
import com.hp.it.sbs.core.client.logging.ClientActivityTracker;
import com.hp.it.sbs.core.shared.exceptions.CoreServiceException;
import com.hp.it.sbs.core.shared.serialization.ObjectMapperProvider;
import com.hp.it.sbs.lgms.interfaces.LGMS;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.http.auth.Credentials;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LGMS Transport Client
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
final class LGMSTransportClientREST extends TransportClientImplementation implements LGMS
{
	/** Transport Client Logger */
	private static final Logger SERVICELOG;

	/** Service Category */
	public static final String SERVICE_CATEGORY = "LGMS";
	
	/** Service Interface */
	public static final String SERVICE_INTERFACE = "LGMS";
	
	/** Service Version: Major */
	public static final String VERSION_MAJOR = "1";
	
	/** Service Version: Minor */
	public static final String VERSION_MINOR = "0";
	
	/** Service Version: Incremental */
	public static final String VERSION_INCREMENTAL = "0";
	
	/** Client Timeout: Connection Timeout(ms) */
	private static final int CLIENT_CONNECTION_TIMEOUT = 30000;
	
	/** Client Timeout: Read Timeout(ms) */
	private static final int CLIENT_READ_TIMEOUT = 60000;

	/** String Literal: SUPPRESS_UNCHECKED - string literal to suppress unchecked exceptions (Conditional) */
	@SuppressWarnings( "unused" )
	private static final String SUPPRESS_UNCHECKED = "unchecked";
	
	/** String Literal: JSON - specify the service produces JSON */
	private static final String JSON = "application/json";
	
	
	/** Parameter Literal: Value0 */
	private static final String VALUE0 = "Value0";


	/** Method Literal: getLGMSValue */
	private static final String METHOD_GETLGMSVALUE = "getLGMSValue";
	/** Method Literal: resetLGMSValue */
	private static final String METHOD_RESETLGMSVALUE = "resetLGMSValue";
	/** Method Literal: setLGMSValue */
	private static final String METHOD_SETLGMSVALUE = "setLGMSValue";
	/** Method Literal: testParseFile */
	private static final String METHOD_TESTPARSEFILE = "testParseFile";

	
	

	/** Client HTTP Executor - client that handles connections to the server implementation */
	private SBSHttpClientExecutor LGMSHttpExecutor;
	
	/** Server Resource Connection - provides connection to the server implementation */
	private LGMSServerResource LGMSResource;
	
	/** HTTP Client Connection Timeout */
	private Integer httpConnectionTimeout = CLIENT_CONNECTION_TIMEOUT;
	
	/** HTTP Client Socket Timeout */
	private Integer httpSocketTimeout = CLIENT_READ_TIMEOUT;
	
	/** HTTP Authentication Credentials */
	private Credentials httpCredentials;
	
	
	
	/** 
	 * This is here because we want to get the RestEasy setup done once, and only once per JVM.
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
			Thread.currentThread().setContextClassLoader( LGMSTransportClientREST.class.getClassLoader() );
			String newCLName = Thread.currentThread().getContextClassLoader().toString();
	        
			// Now instantiate the logger and make note of the modifications made to the class loader
			SERVICELOG = LoggerFactory.getLogger(LGMSTransportClientREST.class);
			SERVICELOG.info( "Context Class Loader changed from \"{}\" to \"{}\"", oldCLName, newCLName  );
		} else {
			// Otherwise only instantiate the logger
			SERVICELOG = LoggerFactory.getLogger(LGMSTransportClientREST.class);
		}
	
		// Register the RESTEasy Provider Factory
		registerProviderFactory();
	}
	
	/**
	 * <b>Register Provider Factory</b><br/>
	 * Registers the provider factory for RESTEasy<br/>
	 * This will be provided to the client for handling conversions<br/>
	 */
	private static final void registerProviderFactory( )
	{
		// Acquire an instance of the RESTEasy Provider
		ResteasyProviderFactory resteasyProvider = ResteasyProviderFactory.getInstance();
		
		// Register the provider
		RegisterBuiltin.register(resteasyProvider);
		
		// Register any providers
		registerProviders(resteasyProvider);
		
		// Register any readers
		registerMessageBodyReaders(resteasyProvider);
		
		// Register any providers
		registerMessageBodyWriters(resteasyProvider);
	}
	
	/**
	 * <b>Register Providers</b><br/>
	 * Registers individual providers needed by the RESTEasy factory<br/>
	 * Each is responsible for a different Java type<br/>
	 * @param factory RESTEasy Provider Factory
	 */
	private static final void registerProviders( final ResteasyProviderFactory factory )
	{
		// Provider to handle converting SBS Objects
		factory.registerProvider(ObjectMapperProvider.class);
		
		// Provider to handle converting JSON
		factory.registerProvider( org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider.class);
		
		// Providers to handle converting multipart-forms and their parts
		factory.registerProvider( org.jboss.resteasy.plugins.providers.multipart.OutputPart.class );
		factory.registerProvider( org.jboss.resteasy.plugins.providers.multipart.MultipartOutput.class );
		factory.registerProvider( org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput.class );
		
		// Provider to handle converting byte arrays
		factory.registerProvider( org.jboss.resteasy.plugins.providers.ByteArrayProvider.class );
		
		// Provider to handle converting strings
		factory.registerProvider( org.jboss.resteasy.plugins.providers.StringTextStar.class );
		
		// Providers to handle content encoding
		factory.registerProvider( org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPInterceptor.class );
		factory.registerProvider( org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor.class );
	}
	
	/**
	 * <b>Register Message Body Readers</b><br/>
	 * Registers individual message body readers needed by the RESTEasy factory<br/>
	 * Each is responsible for a different Java type<br/>
	 * @param factory RESTEasy Provider Factory
	 */
	private static final void registerMessageBodyReaders( final ResteasyProviderFactory factory )
	{
		// Register message body readers for RESTEasy
		factory.addMessageBodyReader(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class);
		factory.addMessageBodyReader(org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider.class);
		factory.addMessageBodyReader(org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider.class);
		factory.addMessageBodyReader(org.jboss.resteasy.plugins.providers.multipart.MultipartReader.class);
		factory.addMessageBodyReader(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader.class);
	}
	
	/**
	 * <b>Register Message Body Writers</b><br/>
	 * Registers individual message body writers needed by the RESTEasy factory<br/>
	 * Each is responsible for a different Java type<br/>
	 * @param factory RESTEasy Provider Factory
	 */
	private static final void registerMessageBodyWriters( final ResteasyProviderFactory factory )
	{
		// Register message body writers for RESTEasy
		factory.addMessageBodyWriter(org.jboss.resteasy.plugins.providers.multipart.MultipartWriter.class);
		factory.addMessageBodyWriter(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter.class);
	}

	/** JSON SECTION for the REST interface to the LGMS Service. **/
	interface LGMSServerResource
	{
		/**
		 * getLGMSValue
		 * @param request ServiceRequest Object
		 * @return returns value of type String
		 */
		@POST
		@Path(METHOD_GETLGMSVALUE)
		@Produces(JSON)
		ClientResponse<ServiceResponse<String>> getLGMSValue(
				@Form final ServiceRequest request);

		/**
		 * setLGMSValue
		 * @param request ServiceRequest Object
		 * @param lgmsValue param0
		 * @return returns value of type Integer
		 */
		@POST
		@Path(METHOD_SETLGMSVALUE)
		@Produces(JSON)
		ClientResponse<ServiceResponse<Integer>> setLGMSValue(
				@Form final ServiceRequest request,
				@FormParam(VALUE0) final String lgmsValue);

		/**
		 * resetLGMSValue
		 * @param request ServiceRequest Object
		 * @param lgmsValue param0
		 * @return returns value of type Integer
		 */
		@POST
		@Path(METHOD_RESETLGMSVALUE)
		@Produces(JSON)
		ClientResponse<ServiceResponse<Integer>> resetLGMSValue(
				@Form final ServiceRequest request,
				@FormParam(VALUE0) final String lgmsValue);

		/**
		 * testParseFile
		 * @param request ServiceRequest Object
		 * @param fileName param0
		 * @return returns value of type String
		 */
		@POST
		@Path(METHOD_TESTPARSEFILE)
		@Produces(JSON)
		ClientResponse<ServiceResponse<String>> testParseFile(
				@Form final ServiceRequest request,
				@FormParam(VALUE0) final String fileName);

	}

	/**
	 * Authenticated Constructor
	 * Configures the RESTEasy provider
	 * Caches the authentication information
	 * Acquires the current logging level
	 * @param authentication Service Authentication Information
	 */
	public LGMSTransportClientREST( final ServiceAuthentication authentication )
	{
		// Client Implementation Constructor
		super();
		
		// Set the authentication information
		setCachedAuthentication( authentication );
		
		// Set any URL Patterns based on annotations
		
		// Generate the service url
		serviceURL = generateURL(
					serviceLifecycle,
					SERVICE_CATEGORY,
					SERVICE_INTERFACE,
					VERSION_MAJOR,
					VERSION_MINOR,
					VERSION_INCREMENTAL );
					
		// Initialize the client
		initialize();
	}
	
	/**
	 * Get LGMS
	 * @return LGMSResource
	 */
	protected LGMSServerResource getLGMSResource( ) 
	{
		return this.LGMSResource;
	}

	/**
	 * Set LGMS
	 * @param serverResource Server Resource Object
	 */
	protected void setLGMSResource( final LGMSServerResource serverResource )
	{
		this.LGMSResource = serverResource;
	}
	
	/**
	 * Initialize
	 * Determines the life cycle
	 * Acquires the LGMSResource
	 */
	private synchronized void initialize( )
	{
		// Check if the service resource needs to be created
		if ( this.LGMSResource == null )
		{
			// Start tracking the activity
			ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_INITIALIZE);
			
			this.LGMSHttpExecutor = null;
			try {
				// Attempt to break the Service URL into parts
				URL sbsURL = new URL( serviceURL );
				
				// Create an HTTP executor for calling services
				this.LGMSHttpExecutor = new SBSHttpClientExecutor(
					sbsURL.getHost(),
					sbsURL.getPort(),
					sbsURL.getProtocol(),
					this.httpConnectionTimeout,
					this.httpSocketTimeout
				);
			} catch ( MalformedURLException e ) {
				// Track the error
				SERVICELOG.error( e.getMessage(), e );
				
				// Create an HTTP executor for calling services
				this.LGMSHttpExecutor = new SBSHttpClientExecutor(
					this.httpConnectionTimeout,
					this.httpSocketTimeout
				);
			}
			
			// Create a server resource with the executor
			LGMSServerResource LGMSResourceTemp =
					ProxyFactory.create(
						LGMSServerResource.class,
						this.serviceURL,
						this.LGMSHttpExecutor
					);
			
			// Save the resource for later usage
			setLGMSResource( LGMSResourceTemp );
			
			// Stop tracking the activity
			ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_INITIALIZE);
		}
	}
	
	/**
	 * Get Credentials
	 * @return Credentials Client Credentials for Authentication Scheme
	 */
	public Credentials getCredentials( )
	{
		return this.httpCredentials;
	}
	
	/**
	 * Set Credentials
	 * @param credentials Client Credentials for Authentication Scheme
	 */
	public void setCredentials( final Credentials credentials )
	{
		SERVICELOG.info( "Client Credentials: " + credentials.getUserPrincipal().getName() );
		this.httpCredentials = credentials;
		
		// Update the HTTP Executor's credentials
		this.LGMSHttpExecutor.setCredentials( this.httpCredentials );
	}
	
	/**
	 * <b>Get HTTP Client Connection Timeout</b><br/>
	 * The timeout until a connection is established<br/>
	 * A value of zero means the timeout is not used<br/>
	 * @return duration Milliseconds (ms)
	 */
	public Integer getConnectionTimeout( )
	{
		return this.httpConnectionTimeout;
	}
	
	/**
	 * <b>Get HTTP Client Socket Timeout</b><br/>
	 * Sets the socket timeout (SO_TIMEOUT) in milliseconds to be used when executing the method<br/>
	 * A timeout value of zero is interpreted as an infinite timeout<br/>
	 * @return duration Milliseconds (ms)
	 */
	public Integer getSocketTimeout( )
	{
		return this.httpSocketTimeout;
	}
	
	/**
	 * <b>Set HTTP Client Connection Timeout</b><br/>
	 * The timeout until a connection is established<br/>
	 * A value of zero means the timeout is not used<br/>
	 * @param duration Milliseconds (ms)
	 */
	public void setConnectionTimeout( final Integer duration )
	{
		// Must be positive
		if( duration >= 0 ) {
			SERVICELOG.info( "Client Connection Timeout: " + duration );
			this.httpConnectionTimeout = duration;
			
			// Update the HTTP Executor
			this.LGMSHttpExecutor.setConnectionTimeout( this.httpConnectionTimeout );
		}
	}
	
	/**
	 * <b>Set HTTP Client Socket Timeout</b><br/>
	 * Sets the socket timeout (SO_TIMEOUT) in milliseconds to be used when executing the method<br/>
	 * A timeout value of zero is interpreted as an infinite timeout<br/>
	 * @param duration Milliseconds (ms)
	 */
	public void setSocketTimeout( final Integer duration )
	{
		// Must be positive
		if( duration >= 0 ) {
			SERVICELOG.info( "Client Socket Timeout: " + duration );
			this.httpSocketTimeout = duration;
			
			// Update the HTTP Executor
			this.LGMSHttpExecutor.setSocketTimeout( this.httpSocketTimeout );
		}
	}

	/**
	 * Initialize ServiceRequest
	 * @return Service Request
	 */
	private ServiceRequest generateServiceRequest( ) {
		// Start tracking the activity
		ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_CREATE_REQUEST);
		
		// Create a new service request
		ServiceRequest request = new ServiceRequest();
		
		// Initialize the service request
		initializeRequest(request);
		
		// Stop tracking the activity
		ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_CREATE_REQUEST);
		
		// Return the service request
		return request;
	}

	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#getLGMSValue LGMS
	 */
	public String getLGMSValue(  )
	{
		// Setup the return object
		String result = null;
		
		// Initialize the service request
		ServiceRequest request = generateServiceRequest( );

		// Start tracking the execution of getLGMSValue()
		ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Client Response Object
		ClientResponse<?> clientResponse = null;
		
		try {
			// Execute the service call
			clientResponse = getLGMSResource().getLGMSValue(
					request
			);
		} catch ( RuntimeException sbsServiceException ) {
			if( sbsServiceException.getCause() instanceof SocketTimeoutException ) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( SOCKET_TIMEOUT, serviceURL, sbsServiceException.getMessage());
			} else if ( sbsServiceException.getCause() instanceof ConnectException) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( CONNECTION_REFUSED, serviceURL, sbsServiceException.getMessage());
			} else {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( RUNTIME_ERROR, sbsServiceException.getMessage());
			}
			
			// Capture the exception to be thrown after logging & processing
			throw new CoreServiceException( sbsServiceException );
		}
		
		// Stop tracking the execution of getLGMSValue()
		ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Retrieve the return object from the service response
		result = this.<String>processResponse( clientResponse );

		// Return the response to getLGMSValue()
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#setLGMSValue LGMS
	 */
	public Integer setLGMSValue( 
			final String lgmsValue )
	{
		// Setup the return object
		Integer result = null;
		
		// Initialize the service request
		ServiceRequest request = generateServiceRequest( );

		// Start tracking the execution of setLGMSValue()
		ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Client Response Object
		ClientResponse<?> clientResponse = null;
		
		try {
			// Execute the service call
			clientResponse = getLGMSResource().setLGMSValue(
					request,
				lgmsValue
			);
		} catch ( RuntimeException sbsServiceException ) {
			if( sbsServiceException.getCause() instanceof SocketTimeoutException ) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( SOCKET_TIMEOUT, serviceURL, sbsServiceException.getMessage());
			} else if ( sbsServiceException.getCause() instanceof ConnectException) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( CONNECTION_REFUSED, serviceURL, sbsServiceException.getMessage());
			} else {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( RUNTIME_ERROR, sbsServiceException.getMessage());
			}
			
			// Capture the exception to be thrown after logging & processing
			throw new CoreServiceException( sbsServiceException );
		}
		
		// Stop tracking the execution of setLGMSValue()
		ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Retrieve the return object from the service response
		result = this.<Integer>processResponse( clientResponse );

		// Return the response to setLGMSValue()
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#resetLGMSValue LGMS
	 */
	public Integer resetLGMSValue( 
			final String lgmsValue )
	{
		// Setup the return object
		Integer result = null;
		
		// Initialize the service request
		ServiceRequest request = generateServiceRequest( );

		// Start tracking the execution of resetLGMSValue()
		ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Client Response Object
		ClientResponse<?> clientResponse = null;
		
		try {
			// Execute the service call
			clientResponse = getLGMSResource().resetLGMSValue(
					request,
				lgmsValue
			);
		} catch ( RuntimeException sbsServiceException ) {
			if( sbsServiceException.getCause() instanceof SocketTimeoutException ) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( SOCKET_TIMEOUT, serviceURL, sbsServiceException.getMessage());
			} else if ( sbsServiceException.getCause() instanceof ConnectException) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( CONNECTION_REFUSED, serviceURL, sbsServiceException.getMessage());
			} else {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( RUNTIME_ERROR, sbsServiceException.getMessage());
			}
			
			// Capture the exception to be thrown after logging & processing
			throw new CoreServiceException( sbsServiceException );
		}
		
		// Stop tracking the execution of resetLGMSValue()
		ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Retrieve the return object from the service response
		result = this.<Integer>processResponse( clientResponse );

		// Return the response to resetLGMSValue()
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.hp.it.sbs.lgms.interfaces.LGMS#testParseFile LGMS
	 */
	public String testParseFile( 
			final String fileName )
	{
		// Setup the return object
		String result = null;
		
		// Initialize the service request
		ServiceRequest request = generateServiceRequest( );

		// Start tracking the execution of testParseFile()
		ClientActivityTracker.startFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Client Response Object
		ClientResponse<?> clientResponse = null;
		
		try {
			// Execute the service call
			clientResponse = getLGMSResource().testParseFile(
					request,
				fileName
			);
		} catch ( RuntimeException sbsServiceException ) {
			if( sbsServiceException.getCause() instanceof SocketTimeoutException ) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( SOCKET_TIMEOUT, serviceURL, sbsServiceException.getMessage());
			} else if ( sbsServiceException.getCause() instanceof ConnectException) {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( CONNECTION_REFUSED, serviceURL, sbsServiceException.getMessage());
			} else {
				// Set a unsuccessful status code since an exception was thrown
				SBSLOGGER.error( RUNTIME_ERROR, sbsServiceException.getMessage());
			}
			
			// Capture the exception to be thrown after logging & processing
			throw new CoreServiceException( sbsServiceException );
		}
		
		// Stop tracking the execution of testParseFile()
		ClientActivityTracker.endFrameworkActivity(ClientActivity.CLIENT_ACTIVITY.CLIENT_SEND_REQUEST);
		
		// Retrieve the return object from the service response
		result = this.<String>processResponse( clientResponse );

		// Return the response to testParseFile()
		return result;
	}
	
}
