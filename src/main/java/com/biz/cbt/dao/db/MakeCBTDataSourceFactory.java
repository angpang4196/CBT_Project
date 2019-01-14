package com.biz.cbt.dao.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

public class MakeCBTDataSourceFactory implements DataSourceFactory {

	Properties ps;

	public void setProperties(Properties props) {
		this.ps = props;
	}

	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		PooledDataSource pds = new PooledDataSource();

		pds.setDriver(ps.getProperty("DRIVER"));
		pds.setUrl(ps.getProperty("URL"));
		pds.setUsername(ps.getProperty("USER"));
		pds.setPassword(ps.getProperty("PASSWORD"));

		return pds;
	}

}
