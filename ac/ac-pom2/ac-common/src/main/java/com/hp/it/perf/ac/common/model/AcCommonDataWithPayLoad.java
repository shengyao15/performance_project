/**
 * 
 */
package com.hp.it.perf.ac.common.model;

/**
 * @author luday
 * 
 */
public class AcCommonDataWithPayLoad extends AcCommonData {

	private static final long serialVersionUID = -3680940581693561718L;

	protected Object payLoad;
	
	protected AcLocation location = new AcLocation();

	public AcCommonDataWithPayLoad() {
		super();
	}
	
	public AcCommonDataWithPayLoad(AcCommonData clone) {
		super(clone);
	}
	
	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	public AcLocation getLocation() {
		return location;
	}

	public void setLocation(AcLocation location) {
		this.location = location;
	}

}
