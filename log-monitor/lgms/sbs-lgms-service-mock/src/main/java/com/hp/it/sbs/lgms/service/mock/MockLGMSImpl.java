package com.hp.it.sbs.lgms.service.mock;

import com.hp.it.sbs.core.server.ServiceImplementation;
import com.hp.it.sbs.core.server.beans.ServiceAuthentication;
import com.hp.it.sbs.core.server.enums.DataSourceType;
import com.hp.it.sbs.core.server.enums.LifeCycle;
import com.hp.it.sbs.lgms.interfaces.LGMS;

public class MockLGMSImpl extends ServiceImplementation implements LGMS
{

	private String lgmsValue;
	
	public MockLGMSImpl(ServiceAuthentication sa)
	{
		super( DataSourceType.MOCK_ACCESS_LAYER, LifeCycle.DEV );
		setLGMSValue( "Mock Authentication Earl" );
	}

	
	public MockLGMSImpl( DataSourceType dst, LifeCycle lc )
	{
    	super( DataSourceType.MOCK_ACCESS_LAYER, LifeCycle.DEV );
		setLGMSValue( "Mock Earl" );
	}

	public String getLGMSValue() 
	{
		return this.lgmsValue;
	}

	public Integer setLGMSValue(final String lgmsValue )
	{
		this.lgmsValue = lgmsValue;
		return 1;
	}
	
	public Integer resetLGMSValue( final String lgmsValue )
	{
		this.lgmsValue = lgmsValue;
		return 1;
	}


	public String testParseFile(String fileName) {
		return fileName;
	}

}
