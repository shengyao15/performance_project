package com.hp.it.sbs.lgms.interfaces;

import com.hp.it.sbs.core.shared.annotations.DisableSBSAuthScheme;
import com.hp.it.sbs.core.shared.annotations.ParameterName;
import com.hp.it.sbs.core.shared.annotations.ReadOnly;

/**
 * <b>LGMS</b><br/>
 * This is an example of a service interface<br/>
 * Replace these method based on the Service API<br/>
 * 
 * @author SBS Archetype
 * @since 1.0.0
 */
@DisableSBSAuthScheme
public interface LGMS {
	/** Parameter Name Literal */
	String PARAMETER_VALUE = "lgmsValue";

	/**
	 * <b>Get LGMS Value</b><br/>
	 * This is an example service method<br/>
	 * <b>DELETE FROM YOUR SERVICE INTERFACE</b><br/>
	 * 
	 * @return String Example value for this service
	 */
	@ReadOnly
	String getLGMSValue();

	/**
	 * <b>Set LGMS Value</b><br/>
	 * This is an example service method<br/>
	 * <b>DELETE FROM YOUR SERVICE INTERFACE</b><br/>
	 * 
	 * @param value
	 *            String Value used for this example
	 * @return Integer Status
	 */
	Integer setLGMSValue(@ParameterName(PARAMETER_VALUE) final String lgmsValue);

	/**
	 * <b>Reset LGMS Value</b><br/>
	 * This is an example service method<br/>
	 * <b>DELETE FROM YOUR SERVICE INTERFACE</b><br/>
	 * 
	 * @param value
	 *            String Value used for this example
	 * @return Integer Status
	 */
	Integer resetLGMSValue(
			@ParameterName(PARAMETER_VALUE) final String lgmsValue);

	String testParseFile(@ParameterName("fileName") String fileName);

}
