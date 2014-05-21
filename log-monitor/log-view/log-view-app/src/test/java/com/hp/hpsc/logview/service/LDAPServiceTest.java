package com.hp.hpsc.logview.service;

import org.junit.Assert;

import org.junit.Test;

public class LDAPServiceTest {
	
	static{
		System.setProperty("javax.net.ssl.trustStore", "C:\\Java2\\jdk1.6.0_45\\java6_cacerts");
	}
	
	//@Test
	public void testVerifyUser() throws Exception {
		String id = "rao.sheng@hp.com";
		String password = "xxxxxx";
		Assert.assertTrue(LDAPService.verifyUser(id, password));
	}
	
	@Test
	public void testVerifyApp() throws Exception {
		String id = "hpsc-log-view-client";
		String password = "asdfQWER654321";
		Assert.assertTrue(LDAPService.verifyApp(id, password));
	}
}
