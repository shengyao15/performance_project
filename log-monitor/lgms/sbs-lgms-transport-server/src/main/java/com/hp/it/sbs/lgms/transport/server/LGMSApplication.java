package com.hp.it.sbs.lgms.transport.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * LGMS Application
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
public class LGMSApplication extends Application
{
	/** Singleton instances */
	private Set<Object> singletons = new HashSet<Object>();

	/**
	 * Constructor
	 */
	public LGMSApplication() 
	{
		// Add an instance of the service server for LGMS
		((HashSet<Object>) singletons).add(new LGMSServiceServer());

		// Add an instance of the transport server for LGMS
		((HashSet<Object>) singletons).add(new LGMSTransportServerREST());
	}

	/**
	 * Get Classes
	 * @return Set of Classes
	 */
	@Override
	public final Set<Class<?>> getClasses()
	{
		return new HashSet<Class<?>>();
	}

	/**
	 * Get Singletons
	 * @return Set of Objects
	 */
	@Override
	public final Set<Object> getSingletons()
	{
		return singletons;
	}

}
