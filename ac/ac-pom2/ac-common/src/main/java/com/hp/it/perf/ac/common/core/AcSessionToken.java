package com.hp.it.perf.ac.common.core;

import java.io.Serializable;

public class AcSessionToken implements Serializable {

	private static final long serialVersionUID = -7391521450542639661L;

	private int sessionId;

	private int profileId;

	private String token;

	public int getSessionId() {
		return sessionId;
	}

	public int getProfileId() {
		return profileId;
	}

	public String getToken() {
		return token;
	}

	public AcSessionToken(int profileId, int sessionId) {
		super();
		this.profileId = profileId;
		this.sessionId = sessionId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return String.format(
				"AcSessionToken [profileId=%s, sessionId=%s, token=%s]",
				profileId, sessionId, token);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + profileId;
		result = prime * result + sessionId;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AcSessionToken))
			return false;
		AcSessionToken other = (AcSessionToken) obj;
		if (profileId != other.profileId)
			return false;
		if (sessionId != other.sessionId)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

}
