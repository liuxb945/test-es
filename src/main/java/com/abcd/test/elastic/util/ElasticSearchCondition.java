package com.abcd.test.elastic.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ElasticSearchCondition {
	
	private BoolQueryBuilder query = null;
	
	public ElasticSearchCondition(){
		query = QueryBuilders.boolQuery();
	}
	
	public ElasticSearchCondition add(String key,String value){
		query = query.must(QueryBuilders.termQuery(key, value));
		return this;
	}
	
	public ElasticSearchCondition in(String key,String... values){
		query = query.must(QueryBuilders.termsQuery(key, values));
		return this;
	}
	
	public ElasticSearchCondition range(String key,Object from,Object to){
		query = query.must(QueryBuilders.rangeQuery(key).from(from).to(to));
		return this;
	}
	
	public ElasticSearchCondition from(String key,Object from){
		query = query.must(QueryBuilders.rangeQuery(key).from(from));
		return this;
	}
	
	public ElasticSearchCondition to(String key,Object to){
		query = query.must(QueryBuilders.rangeQuery(key).to(to));
		return this;
	}
	
	public ElasticSearchCondition like(String key,String value){
		query = query.must(QueryBuilders.wildcardQuery(key,"*" + value + "*"));
		return this;
	}
	
	public ElasticSearchCondition or(ElasticSearchCondition query){
		this.query = this.query.should(query.getQuery());
		return this;
	}
	
	public ElasticSearchCondition and(ElasticSearchCondition query){
		this.query = this.query.must(query.getQuery());
		return this;
	}
	
	public BoolQueryBuilder getQuery(){
		return query;
	}
}
