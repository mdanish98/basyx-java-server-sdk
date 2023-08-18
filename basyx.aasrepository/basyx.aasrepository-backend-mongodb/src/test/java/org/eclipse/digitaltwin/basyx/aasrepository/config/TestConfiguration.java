package org.eclipse.digitaltwin.basyx.aasrepository.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class TestConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private int port;

	@Value("${spring.data.mongodb.username}")
	private String userName;

	@Value("${spring.data.mongodb.password}")
	private String password;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Bean
	public MongoDatabaseFactory databaseFactory() {
		String connectionString = createConnectionString();

		MongoClient client = MongoClients.create(connectionString.toString());

		return new SimpleMongoClientDatabaseFactory(client, database);
	}

	private String createConnectionString() {
		return String.format("mongodb://%s:%s@%s:%s", userName, password, host, port);
	}

}
