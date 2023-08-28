/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.basyx.common.mongocore.CustomIdentifiableMappingMongoConverter;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.MongoDBConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.MongoDBConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core.ConceptDescriptionRepositorySuite;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the {@link MongoDBConceptDescriptionRepository}
 * 
 * @author danish, kammognie
 *
 */
public class TestMongoDBConceptDescriptionRepository extends ConceptDescriptionRepositorySuite {
	private static final String CONFIGURED_CD_REPO_NAME = "configured-cd-repo-name";
	private final String COLLECTION = "conceptDescTestCollection";
	
	@Override
	protected ConceptDescriptionRepository getConceptDescriptionRepository() {
		MongoTemplate template = createMongoTemplate();

		clearDatabase(template);

		return new MongoDBConceptDescriptionRepositoryFactory(template, COLLECTION).create();
	}

	@Override
	protected ConceptDescriptionRepository getConceptDescriptionRepository(
			Collection<ConceptDescription> conceptDescriptions) {
		MongoTemplate template = createMongoTemplate();
		
		clearDatabase(template);
		
		ConceptDescriptionRepository conceptDescriptionRepository = new MongoDBConceptDescriptionRepositoryFactory(template, COLLECTION).create();
		
		conceptDescriptions.forEach(conceptDescriptionRepository::createConceptDescription);
		
		return conceptDescriptionRepository;
	}
	
	@Test
	public void testConfiguredMongoDBConceptDescriptionRepositoryName() {
        MongoTemplate template = createMongoTemplate();
		
		clearDatabase(template);
		
		ConceptDescriptionRepository repo = new MongoDBConceptDescriptionRepository(template, COLLECTION, CONFIGURED_CD_REPO_NAME);
		
		assertEquals(CONFIGURED_CD_REPO_NAME, repo.getName());
	}

	@Test
	public void conceptDescriptionIsPersisted() {
		ConceptDescriptionRepository conceptDescriptionRepository = getConceptDescriptionRepository();
		ConceptDescription expectedConceptDescription = createDummyConceptDescriptionOnRepo(conceptDescriptionRepository);
		ConceptDescription retrievedConceptDescription = getConceptDescriptionFromNewBackendInstance(conceptDescriptionRepository, expectedConceptDescription.getId());

		assertEquals(expectedConceptDescription, retrievedConceptDescription);
	}
	
	@Test
	public void updatedConceptDescriptionIsPersisted() {
		ConceptDescriptionRepository mongoDBConceptDescriptionRepository = getConceptDescriptionRepository();
		
		ConceptDescription expectedConceptDescription = createDummyConceptDescriptionOnRepo(mongoDBConceptDescriptionRepository);
		
		addDescriptionToConceptDescription(expectedConceptDescription);
		
		mongoDBConceptDescriptionRepository.updateConceptDescription(expectedConceptDescription.getId(), expectedConceptDescription);
		
		ConceptDescription retrievedConceptDescription = getConceptDescriptionFromNewBackendInstance(mongoDBConceptDescriptionRepository, expectedConceptDescription.getId());

		assertEquals(expectedConceptDescription, retrievedConceptDescription);
	}
	
	@Test
	public void retrieveRawJson() throws FileNotFoundException, IOException {
		MongoTemplate template = createMongoTemplate();
		
		createDummyConceptDescriptionOnRepo(getConceptDescriptionRepository());
		
		String expectedCDJson = getDummyCDJSONString();
		
		Document cdDocument = template.findOne(new Query().addCriteria(Criteria.where("id").is("dummy")),
				Document.class, COLLECTION);
		
		assertSameJSONContent(expectedCDJson, cdDocument.toJson());
	}
	
	private void assertSameJSONContent(String expectedCDJson, String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		assertEquals(mapper.readTree(expectedCDJson), mapper.readTree(json));
	}
	
	private void addDescriptionToConceptDescription(ConceptDescription expectedConceptDescription) {
		expectedConceptDescription.setDescription(Arrays.asList(new DefaultLangStringTextType.Builder().text("description").language("en").build()));
	}

	private ConceptDescription getConceptDescriptionFromNewBackendInstance(ConceptDescriptionRepository conceptDescriptionRepository, String conceptDescriptionId) {
		ConceptDescription retrievedConceptDescription = conceptDescriptionRepository.getConceptDescription(conceptDescriptionId);
		
		return retrievedConceptDescription;
	}

	private ConceptDescription createDummyConceptDescriptionOnRepo(ConceptDescriptionRepository conceptDescriptionRepository) {
		ConceptDescription expectedConceptDescription = new DefaultConceptDescription.Builder().id("dummy").build();
		
		conceptDescriptionRepository.createConceptDescription(expectedConceptDescription);
		
		return expectedConceptDescription;
	}
	
	private void clearDatabase(MongoTemplate template) {
		template.remove(new Query(), COLLECTION);
	}
	
	private MongoTemplate createMongoTemplate() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());
		
		ObjectMapper mapper = new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
		
		MongoDatabaseFactory databaseFactory = createDatabaseFactory();
		
		return new MongoTemplate(databaseFactory, new CustomIdentifiableMappingMongoConverter(databaseFactory, new MongoMappingContext(), mapper));
	}
	
	private MongoDatabaseFactory createDatabaseFactory() {
		String connectionString = createConnectionString();

		MongoClient client = MongoClients.create(connectionString);

		return new SimpleMongoClientDatabaseFactory(client, "BaSyxTestDb");
	}

	private String createConnectionString() {
		return String.format("mongodb://%s:%s@%s:%s", "mongoAdmin", "mongoPassword", "127.0.0.1", "27017");
	}
	
	private String getDummyCDJSONString() throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource("DummyCD.json");
		InputStream in = classPathResource.getInputStream();
		
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}

}
