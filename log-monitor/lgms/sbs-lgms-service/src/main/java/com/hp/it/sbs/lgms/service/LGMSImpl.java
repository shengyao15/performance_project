package com.hp.it.sbs.lgms.service;

import java.io.IOException;
import java.net.URL;

import com.hp.hpsc.logservice.parser.ParserTestMain;
import com.hp.it.sbs.core.server.ServiceImplementation;
import com.hp.it.sbs.core.server.enums.DataSourceType;
import com.hp.it.sbs.core.server.enums.LifeCycle;
import com.hp.it.sbs.lgms.interfaces.LGMS;

/**
 * LGMS Service Implementation
 * 
 * @author SBS Archetype
 * @since 1.0.0
 */
public class LGMSImpl extends ServiceImplementation implements LGMS {
	/** Value */
	private String lgmsValue;

	/**
	 * Constructor with datasource and lifecycle
	 * 
	 * @param dst
	 *            Data Source Type
	 * @param lc
	 *            Life Cycle
	 */
	public LGMSImpl(final DataSourceType dst, final LifeCycle lc) {
		super(dst, lc);
		// Do initialization here.
		setLGMSValue("Earl");
	}

	/**
	 * Get LGMS Value
	 * 
	 * @return String Example Value
	 */
	public final String getLGMSValue() {
		return this.lgmsValue;
	}

	/**
	 * Set LGMS
	 * 
	 * @param lgmsValue
	 *            Example Value
	 * @return Integer Status
	 */
	public final Integer setLGMSValue(final String lgmsValue) {
		this.lgmsValue = lgmsValue;
		return 1;
	}

	/**
	 * Reset LGMS Value
	 * 
	 * @param lgmsValue
	 *            Example Value
	 * @return Integer Status
	 */
	public final Integer resetLGMSValue(final String lgmsValue) {
		this.lgmsValue = lgmsValue;
		return 1;
	}

	public String testParseFile(String fileName) {
		URL fileLocation = ParserTestMain.class.getClassLoader().getResource(
				fileName);
		try {
			return ParserTestMain.parsePortalErrorLogs(
					fileLocation.openStream(), fileLocation.toString(), null,
					null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.toString();
		}
	}
}
