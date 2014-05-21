package com.hp.it.sbs.lgms.sample;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.sbs.core.client.beans.ServiceAuthentication;
import com.hp.it.sbs.lgms.transport.client.LGMSService;
import com.hp.it.sbs.lgms.transport.client.LGMSServiceClient;

/**
 * <b>LGMS Sample Client</b><br/>
 * Provides an example for how to execute this service's client<br/>
 * Also, utilized to perform quick verification tests by developers<br/><br/>
 * <b>ENSURE DEFAULT SETTINGS PERFORM READ-ONLY TESTS AGAINST DEV LIFECYCLE</b><br/>
 * <b>ENSURE APPLICATION ACCOUNTS ARE NOT HARD CODED IN THIS FILE</b><br/><br/>
 * Other people may check out this example and run it without thinking<br/>
 * @author SBS Archetype
 * @since 1.0.0
 */
public class LGMSSampleClient 
{
	/** Sample Client Logger -- Behavior is controlled by the logback.xml in /src/main/resources/ */
	private static final Logger logger = LoggerFactory.getLogger(LGMSSampleClient.class);
	
	/** Read Only -- Controls access to write method of the service */
	private static final Boolean READ_ONLY = true;
	
	/** Life Cycle -- Controls which environment the client will utilize */
	private static final String LIFECYCLE = "LOCAL";
	
	/**
	 * <b>Main Execution of the Sample Client</b><br/>
	 * The values for the application id and password must be provided<br/>
	 * These may be specified as VM Arguments or Command Line Arguments<br/>
	 * In Eclipse:<br/>
	 * 1) Go to "Run Configurations...<br/>
	 * 2) Go to the "(x)= Arguments" tab<br/>
	 * 3) Provide either "Program Arguments" or "VM Arguments"<br/>
	 * 
	 * @param args Application Id & Password (Optional)
	 * @throws IOException Exception
	 */
	public static void main(String[] args) throws IOException
	{
		// Capture the application id and password from system properties
		String application = System.getProperty( "SBS-ApplicationId" , "applicationId");
		String password = System.getProperty( "SBS-ApplicationKey", "applicationKey");
		
		// Otherwise, attempt to read from the command line arguments
		if ( ( application == null || password == null ) && args.length >= 2 ) {
			application = args[0];
			password = args[1];
		}

		// Ensure the necessary parameters were provided
		if ( application == null || password == null || application.trim().length() < 1 || password.trim().length() < 1 ) {
			System.out.println( "Application Id and Password were not provided.  Specify these as command line or virtual machine arguments." );
			System.exit( -1 );
		}
		
		// Validate the Application Id is valid
		if ( application.contains(",") ) {
			System.out.println( "Accounts in the Enterprise Directory should not contain commas" );
			System.exit( -1 );
		}
		
		// Execute the service's client with the provided account
		executeClient( application, password );
	}
	
	/**
	 * Execute Service Client
	 * @param application Application Id
	 * @param password Application Password
	 */
	private static void executeClient( final String application, final String password )
	{
		// Set the life cycle for the service
		System.setProperty("sbs.lifecycle", LIFECYCLE);
		
		// Setup the Service Authentication object for the service requests
		ServiceAuthentication authInfo = new ServiceAuthentication( application, password );
		
		// Request a new service client instance from the service factory
		LGMSService service = new LGMSServiceClient( authInfo );

		// Call a read only method on the service
		String result = service.getLGMSValue();
		logger.info( "LGMS value is " + result );
		
		// Default behavior should not execute writes
		if( !READ_ONLY ) {
			// Call a write method on the service
			service.setLGMSValue( "VALUE1" );
		}
		
		// Retrieve the new value
		result = service.getLGMSValue();
		logger.info( "LGMS value is " + result );
		
		// Default behavior should not execute writes
		if( !READ_ONLY ) {
			// Call a write method on the service
			service.setLGMSValue( "VALUE2" );
		}
			
		// Retrieve the new value
		result = service.getLGMSValue();
		logger.info( "LGMS value is " + result );
		
		// TEST Parse Method
		System.out.println(service.testParseFile("sample_portal.txt"));
	}
}
