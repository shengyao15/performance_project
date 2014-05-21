package com.hp.hpsc.logview.service;

import java.util.Locale;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.factory.JSSESocketFactory;

import com.hp.hpsc.logview.po.CoreTimeBasedMap;

public class LDAPService {

	private static final int CACHE_DURATION = 1000 * 60 * 60 * 4;
	private static CoreTimeBasedMap authenticationCache = new CoreTimeBasedMap(
			CACHE_DURATION);

	private static String groups = "";
	private static final String LDAP_SEARCH_UID = "uid=%s,%s";
	private static final String LDAP_SEARCH_CN = "cn=%s,%s";
	private static final String LDAP_PEOPLE = "ou=People,o=hp.com";
	private static final String LDAP_GROUP = "ou=Groups,o=hp.com";
	private static final String LDAP_APPLICATIONS = "ou=Applications,o=hp.com";

	private static String host = "ldap.hp.com";
	private static int port = 636;
	private static final int CONNECTION_TIMEOUT = 1800;
	private static final int LDAP_VERSION = 3;
	private static final String LDAP_ATTRIBUTE_MEMBER = "member";
	private static JSSESocketFactory jsseFactory;

	static {
		jsseFactory = new JSSESocketFactory(null);
		try {
			jsseFactory.makeSocket(host, port);
		} catch (Exception e) {
			throw new RuntimeException("LDAP Exception", e);
		}
		
		String ldapGroup = System.getProperty("ldap.group");
		if(ldapGroup == null || "".equals(ldapGroup)){
			groups = "hpsc-log-view";
		}else{
			groups = ldapGroup;
		}
		
	}

	public static boolean verifyApp(final String id, final String password){
		boolean authorized = false;

		String uppercaseID = id.toUpperCase(Locale.US);
		String accountDN = String.format(LDAP_SEARCH_CN, uppercaseID,
				LDAP_APPLICATIONS);

		boolean alreadyInCache = false;
		alreadyInCache = authenticateAgainstCache(accountDN, password);
		if(alreadyInCache){
			return true;
		}

		LDAPConnection connection = null;
		try {
			connection = getLdapConnection(host, port);

			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			LDAPSearchConstraints constr = new LDAPSearchConstraints();
			constr.setMaxResults(0);
			constr.setServerTimeLimit(0);

			connection.connect(host, port);
			connection.bind(LDAP_VERSION, accountDN, password);
			connection.connect(LDAP_VERSION, host, port, accountDN, password);

			String uppercaseGroupID = groups.toUpperCase(Locale.US);

			String groupDN = String.format(LDAP_SEARCH_CN, uppercaseGroupID,
					LDAP_GROUP);

			authorized = authorizeAgainstEnterpriseDirectory(connection,
					accountDN, groupDN);
			
			if(authorized){
				authenticationCache.put( accountDN, password );
			}
			
		} catch (Exception e) {
			throw new RuntimeException("LDAP Exception", e);
		} finally {
			try {
				closeLdapConnection(connection);
			} catch (Exception e) {
				throw new RuntimeException("LDAP Exception", e);
			}
		}

		return authorized;
	}

	public static boolean verifyUser(final String id, final String password)
			throws Exception {
		boolean authorized = false;

		String uppercaseID = id.toUpperCase(Locale.US);

		String accountDN = String.format(LDAP_SEARCH_UID, uppercaseID,
				LDAP_PEOPLE);

		LDAPConnection connection = null;
		try {
			connection = getLdapConnection(host, port);

			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			LDAPSearchConstraints constr = new LDAPSearchConstraints();
			constr.setMaxResults(0);
			constr.setServerTimeLimit(0);

			connection.connect(host, port);
			connection.bind(LDAP_VERSION, accountDN, password);
			connection.connect(LDAP_VERSION, host, port, accountDN, password);

			String uppercaseGroupID = groups.toUpperCase(Locale.US);

			String groupDN = String.format(LDAP_SEARCH_CN, uppercaseGroupID,
					LDAP_GROUP);

			authorized = authorizeAgainstEnterpriseDirectory(connection,
					accountDN, groupDN);
		} catch (Exception e) {
		} finally {
			try {
				closeLdapConnection(connection);
			} catch (Exception e) {
				throw new RuntimeException("LDAP Exception", e);
			}
		}

		return authorized;
	}

	private static boolean authenticateAgainstCache(String dn, String password) {
		// Ensure non null values were provided before processing occurs
		if (dn == null || password == null) {
			return false;
		}

		if (authenticationCache.get(dn) != null) {
			if (authenticationCache.get(dn).equals(password)) {
				return true;
			}
			authenticationCache.remove(dn);
		}
		return false;
	}

	private static void closeLdapConnection(final LDAPConnection connection)
			throws LDAPException {
		if (connection != null) {
			connection.disconnect();
		}
	}

	private static boolean authorizeAgainstEnterpriseDirectory(
			final LDAPConnection connection, final String memberDN,
			final String groupDN) throws Exception {
		boolean authFlag = authorize(connection, memberDN, groupDN);
		if (authFlag) {
			return true;
		}

		return false;
	}

	private static boolean authorize(final LDAPConnection connection,
			final String memberDN, final String groupDN) throws Exception {
		boolean isMember = false;
		String[] members = null;

		LDAPEntry groupEntry = connection.read(groupDN);
		if (groupEntry != null) {
			LDAPAttribute membersEntry = groupEntry
					.getAttribute(LDAP_ATTRIBUTE_MEMBER);
			if (membersEntry != null) {
				members = membersEntry.getStringValueArray();
			}
		}

		for (int x = 0; members != null && x < members.length && !isMember; x++) {
			if (members[x].equalsIgnoreCase(memberDN)) {
				isMember = true;
			}
		}

		return isMember;
	}

	private static LDAPConnection getLdapConnection(final String ldaphost,
			final int ldapport) throws Exception {

		LDAPConnection connection = new LDAPConnection(jsseFactory);
		return connection;
	}
}
