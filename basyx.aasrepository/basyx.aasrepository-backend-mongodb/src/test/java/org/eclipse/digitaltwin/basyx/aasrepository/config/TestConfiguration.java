package org.eclipse.digitaltwin.basyx.aasrepository.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

@Configuration
public class TestConfiguration {
	
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	@Bean
	public MongoDatabaseFactory databaseFactory(MongoClient client) {
		return new SimpleMongoClientDatabaseFactory(client, database);
	}

}
