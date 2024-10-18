package org.eclipse.digitaltwin.basyx.common.mongocore;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class MongoDBCrudRepository<T> extends SimpleMongoRepository<T, String> implements BaSyxCrudRepository<T>  {
	
	private MongoTemplate mongoTemplate;
	private Class<T> clazz;
	private MongoEntityInformation<T, String> metadata;

	public MongoDBCrudRepository(MongoEntityInformation<T, String> metadata, MongoTemplate mongoTemplate, Class<T> clazz) {
		super(metadata, mongoTemplate);
		
		this.metadata = metadata;
		this.mongoTemplate = mongoTemplate;
		this.clazz = clazz;
	}
	
	@Override
	public List<T> findAll(PaginationInfo paginationInfo) {
		
		List<AggregationOperation> allAggregations = new LinkedList<>();
		
		MongoDBUtilities.applySorting(allAggregations);
		MongoDBUtilities.applyPagination(paginationInfo, allAggregations);
		
		AggregationResults<T> results = mongoTemplate.aggregate(Aggregation.newAggregation(allAggregations), metadata.getCollectionName(), clazz);
		
		return results.getMappedResults();
	}

}
