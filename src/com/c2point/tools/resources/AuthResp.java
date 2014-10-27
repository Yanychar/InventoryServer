package com.c2point.tools.resources;

import org.joda.time.DateTime;

import com.c2point.tools.entity.authentication.Account;

public class AuthResp {
	
	Account account;
	DateTime date = DateTime.now();

	public AuthResp( Account account ) {
		this.account = account;
	}

	public Account getAccount() { return account; }
	public DateTime getDate() { return date; }
}
