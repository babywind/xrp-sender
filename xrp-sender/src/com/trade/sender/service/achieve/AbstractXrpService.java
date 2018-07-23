package com.trade.sender.service.achieve;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.quqian.framework.config.SystemDefine;
import com.quqian.framework.data.sql.SQLConnectionProvider;
import com.quqian.framework.message.MessageProvider;
import com.quqian.framework.resource.ResourceNotFoundException;
import com.quqian.framework.service.AbstractService;
import com.quqian.framework.service.Service;
import com.quqian.framework.service.ServiceResource;
import com.quqian.framework.service.query.ItemParser;
import com.quqian.p2p.variables.P2PConst;
import com.trade.sender.entity.HttpEntity;

public abstract class AbstractXrpService extends AbstractService implements Service {
	public AbstractXrpService(ServiceResource serviceResource) {
		super(serviceResource);
	}

	protected SQLConnectionProvider getConnectionProvider() {
		SystemDefine systemDefine = serviceResource.getSystemDefine();
		return (SQLConnectionProvider) serviceResource.getDataConnectionProvider(SQLConnectionProvider.class,
				systemDefine.getDataProvider(MessageProvider.class));
	}

	protected Connection getConnection() throws ResourceNotFoundException, SQLException {
		SystemDefine systemDefine = serviceResource.getSystemDefine();
		return ((SQLConnectionProvider) serviceResource.getDataConnectionProvider(SQLConnectionProvider.class,
				systemDefine.getDataProvider(MessageProvider.class))).getConnection("" + P2PConst.DB_USER + "");
	}

	protected Connection getConnection(String db) throws ResourceNotFoundException, SQLException {
		SystemDefine systemDefine = serviceResource.getSystemDefine();
		return ((SQLConnectionProvider) serviceResource.getDataConnectionProvider(SQLConnectionProvider.class,
				systemDefine.getDataProvider(MessageProvider.class))).getConnection(db);
	}

	protected String selectString(Connection connection, String sql, Object... paramters) throws SQLException {
		final String decimal = "";
		return select(connection, new ItemParser<String>() {
			@Override
			public String parse(ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
				return decimal;
			}
		}, sql, paramters);
	}

	protected Integer selectInt(Connection connection, String sql, Object... paramters) throws SQLException {
		final Integer decimal = 0;
		return select(connection, new ItemParser<Integer>() {
			@Override
			public Integer parse(ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return decimal;
			}
		}, sql, paramters);
	}

	protected BigDecimal selectBigDecimal(Connection connection, String sql, Object... paramters) throws SQLException {
		final BigDecimal decimal = new BigDecimal(0);
		return select(connection, new ItemParser<BigDecimal>() {
			@Override
			public BigDecimal parse(ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getBigDecimal(1);
				}
				return decimal;
			}
		}, sql, paramters);
	}

	public int selectId(String bjc) {
		int bid = 0;
		try {
			bid = selectInt(P2PConst.DB_USER, "SELECT F01 FROM T6013 WHERE F03=?", bjc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bid;
	}

	protected String post(HttpEntity py) throws Exception {
		String result = "";
		try {
			URL postUrl = new URL(py.url);
			URLConnection connection = postUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
			httpURLConnection.setRequestProperty("accept", "*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("charset", "UTF-8");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			try (OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream())) {
				out.write(py.data);
				out.flush();
			}
			try (BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
				String line = "";
				while ((line = in.readLine()) != null) {
					result += line;
				}
			}
			if (httpURLConnection.getResponseCode() != 200) {
				throw new Exception("数据接口请求失败");
			}
		} catch (Exception e) {
			result = "{'success': false, 'result': 'trade error'}";
			/* throw new Exception(e.getMessage()); */
		}
		return result;
	}
}