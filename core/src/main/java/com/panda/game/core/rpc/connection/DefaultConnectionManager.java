package com.panda.game.core.rpc.connection;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: 支持Connection池管理
 * @author wenyuan
 */
public class DefaultConnectionManager implements ConnectionManager {
	
	private final static Logger log = LoggerFactory.getLogger(ConnectionManager.class);
	
	/** host:port -- connection */
	private ConcurrentHashMap<String, Connection> connectionPool = new ConcurrentHashMap<>();

	protected ConnectionFactory connectionFactory;

	public DefaultConnectionManager(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	@Override
	public void init() {
		connectionFactory.init();
	}
	
	@Override
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public void add(Connection connection, String addr) {
		connectionPool.put(addr, connection);
	}

	@Override
	public Connection get(String host, int port) {
		String address = host + ":" + port;

		Connection connection = connectionPool.get(address);
		if (connection == null) {
			try {
				connection = this.create(host, port, 2000);
				Connection oldConnection = connectionPool.putIfAbsent(address, connection);

				return oldConnection != null ? oldConnection : connection;
			} catch (Exception e) {
				log.error("create connection error", e);
			}
		}
		return connection;
	}

	@Override
	public List<Connection> getAll(String addr) {
		Connection connection = connectionPool.get(addr);
		if (connection == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(connection);
	}

	@Override
	public Map<String, List<Connection>> getAll() {
		Map<String, List<Connection>> connections = new HashMap<>(connectionPool.size());
		for (Map.Entry<String, Connection> entry : connectionPool.entrySet()) {
			List<Connection> list = new ArrayList<>();
			list.add(entry.getValue());
			connections.put(entry.getKey(), list);
		}

		return connections;
	}

	@Override
	public void remove(Connection connection) {
		String address = null;
		for (Map.Entry<String, Connection> entry : connectionPool.entrySet()) {
			if (entry.getValue() == connection) {
				address = entry.getKey();
				break;
			}
		}

		if (address != null) {
			remove(connection, address);
		}
	}

	@Override
	public void remove(Connection connection, String addr) {
		Connection connection1 = connectionPool.get(addr);
		if (connection == connection1) {
			Connection connection2 = connectionPool.remove(addr);
			if (connection2 == null) {
				connection2.close();
			}
		}
	}

	@Override
	public void remove(String addr) {
		Connection connection = connectionPool.remove(addr);
		if (connection != null) {
			connection.close();
		}
	}

	@Override
	public void removeAll() {
		List<Connection> list = new ArrayList<>(connectionPool.values());
		connectionPool.clear();

		for (Connection connection : list) {
			connection.close();
		}
	}

	@Override
	public void check(Connection connection) throws Exception {
		if (!connection.checkActive()) {
			throw new RuntimeException("not active connection");
		}
	}

	@Override
	public int count(String address) {
		return connectionPool.contains(address) ? 1 : 0;
	}

	@Override
	public Connection create(String address, int connectTimeout) throws Exception {
		String[] hostPort = address.split(":");
		return create(hostPort[0], Integer.parseInt(hostPort[1]), connectTimeout);
	}

	@Override
	public Connection create(String ip, int port, int connectTimeout) throws Exception {
		return connectionFactory.createConnection(ip, port, connectTimeout);
	}

	@Override
	public Connection getOrCreateConnectionIfAbsent(String address) {
		String[] hostPort = address.split(":");
		return get(hostPort[0], Integer.parseInt(hostPort[1]));
	}

	@Override
	public Connection getOrCreateConnectionIfAbsent(String ip, int port) {
		return get(ip, port);
	}
	
}
