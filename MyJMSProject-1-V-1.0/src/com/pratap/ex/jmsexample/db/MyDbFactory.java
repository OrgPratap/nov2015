package com.pratap.ex.jmsexample.db;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

public class MyDbFactory 
{
	private OracleDataSource oracleDataSource;
	
	public void createOracleDataSource(String url, String user, String password) throws SQLException{
		oracleDataSource = new OracleDataSource();
		oracleDataSource.setURL(url);
		oracleDataSource.setUser(user);
		oracleDataSource.setPassword(password);
		System.out.println("oracle-data-source-created-successfully...");
	}

	public OracleDataSource getOracleDataSource() {
		return oracleDataSource;
	}

	public void setOracleDataSource(OracleDataSource oracleDataSource) {
		this.oracleDataSource = oracleDataSource;
	}
	
}
