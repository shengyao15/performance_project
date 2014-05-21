	
package com.hp.it.sbs.lgms.beans;

import javax.xml.bind.annotation.XmlRootElement;

import com.hp.it.sbs.core.shared.serialization.CoreUtil;

/**
 * <b>Sample Payload</b><br/>
 * This is an example POJO Bean for a sevice<br/>
 * <b>DELETE FROM YOUR SERVICE</b><br/>
 * @author SBS Archetype
 * @since 1.0.0
 */
@XmlRootElement( name = "samplePayload" )
@org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
public class SamplePayload 
{
	/** String value */
	private String lgms;

	/**
	 * Default Constructor
	 */
	public SamplePayload()
	{
		//Call the super constructor
		super();
	}
	
	/**
	 * Get LGMS
	 * @return String the example value
	 */
	public final String getLGMS() {
		return lgms;
	}

	/**
	 * Set LGMS
	 * @param lgmsNew Parameter
	 */
	public final void setLGMS( final String lgmsNew ) {
		this.lgms = lgmsNew;
	}
	
	/**
	 * Value Of
	 * @param str Serialized String
	 * @return Instance of SamplePayload
	 */
	public static SamplePayload valueOf( final String str )
	{
		SamplePayload result = CoreUtil.valueOf( SamplePayload.class, str );
		return result;
	}
	
	/**
	 * To String
	 * @return Serialized String
	 */
	@Override
	public final String toString( )
    {
		String result = CoreUtil.toStringJSON( SamplePayload.class, this );
		return result;
    }
}
