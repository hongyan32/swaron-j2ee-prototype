package org.app.domain.grid.service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.app.domain.grid.vo.ColumnMetaData;
import org.app.domain.grid.vo.TableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ExternalDbService {
	Logger logger = LoggerFactory.getLogger(getClass());

	private HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();

	public DataSource getDataSource(String key) {
		return dataSources.get(key);
	}

	public synchronized void syncDataSource(HashMap<String, Properties> infos) {
		Collection<String> toAdd = CollectionUtils.subtract(infos.keySet(), dataSources.keySet());
		Collection<String> toRemove = CollectionUtils.subtract(dataSources.keySet(), infos.keySet());
		for (String key : toRemove) {
			DataSource dataSource = dataSources.remove(key);
			if (dataSource instanceof BasicDataSource) {
				try {
					((BasicDataSource) dataSource).close();
				} catch (SQLException e) {
					logger.warn("unable to close datasource", e);
				}
			}
		}
		for (String key : toAdd) {
			buildDataSource(key, infos.get(key));
		}

	}

	public synchronized DataSource buildDataSource(String key, Properties prop) {
		try {
			DataSource dataSource = BasicDataSourceFactory.createDataSource(prop);
			dataSources.put(key, dataSource);
			return dataSource;
		} catch (Exception e) {
			logger.info("unable to create datasource with properties: {}", prop);
			return null;
		}
	}

	public List<ColumnMetaData> getColumnMetaDatas(DataSource dataSource, final String tableName) {
		if (dataSource == null) {
			return null;
		}
		try {
			DatabaseMetaDataCallback action = new DatabaseMetaDataCallback() {
				@Override
				public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
					ResultSet tables = dbmd.getColumns(null, null, tableName, "");
					RowMapper<ColumnMetaData> rowMapper = BeanPropertyRowMapper.newInstance(ColumnMetaData.class);
					RowMapperResultSetExtractor<ColumnMetaData> resultSetExtractor = new RowMapperResultSetExtractor<ColumnMetaData>(
							rowMapper);
					List<ColumnMetaData> list = resultSetExtractor.extractData(tables);
					tables.close();
					return list;
				}
			};
			return (List<ColumnMetaData>) JdbcUtils.extractDatabaseMetaData(dataSource, action);
		} catch (MetaDataAccessException e) {
			logger.warn("Error while accessing table meta data results", e);
			return null;
		}

	}

	public List<TableMetaData> getTableMetaDatas(DataSource dataSource) {
		return getTableMetaDatas(dataSource, "");
	}

	public List<TableMetaData> getTableMetaDatas(DataSource dataSource, final String tableName) {
		if (dataSource == null) {
			return null;
		}
		try {
			DatabaseMetaDataCallback action = new DatabaseMetaDataCallback() {
				@Override
				public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
					ResultSet tables = dbmd.getTables(null, null, tableName, new String[] { "TABLE" });
					RowMapper<TableMetaData> rowMapper = BeanPropertyRowMapper.newInstance(TableMetaData.class);
					RowMapperResultSetExtractor<TableMetaData> resultSetExtractor = new RowMapperResultSetExtractor<TableMetaData>(
							rowMapper);
					List<TableMetaData> list = resultSetExtractor.extractData(tables);
					tables.close();
					return list;
				}
			};
			return (List<TableMetaData>) JdbcUtils.extractDatabaseMetaData(dataSource, action);
		} catch (MetaDataAccessException e) {
			logger.warn("Error while accessing table meta data results", e);
			return null;
		}
	}

	public TableMetaData getTableMetaData(DataSource dataSource, String tableName) {
		if (dataSource == null) {
			return null;
		}
		List<TableMetaData> list = getTableMetaDatas(dataSource, tableName);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@PreDestroy
	public void destroy() throws SQLException {
		for (Entry<String, DataSource> e : dataSources.entrySet()) {
			DataSource ds = e.getValue();
			if (ds instanceof BasicDataSource) {
				((BasicDataSource) ds).close();
			}
		}
	}

}