package com.abcd.test.elastic.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import net.sf.json.JSONObject;

/**
 * ES操作工具类
 * @author 
 *
 */
public class ElasticSearchUtil {
	//ES客户端
	private static Client client = null;
	//ES所在服务器地址
	private static String ip = PropsUtil.readProps("elasticsearch.ip");
	//连接使用端口
	private static int port = Integer.valueOf(PropsUtil.readProps("elasticsearch.port"));
	
	/**
	 * 初始化ES客户端
	 */
	static{
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));

		} catch (Exception e) {
			throw new RuntimeException("初始化ES客户端出错" + e.getMessage());
		}
	}
	
	/**
	 * 添加一条数据
	 * @param object 要添加的数据 json格式
	 * @param index 索引，类似数据库
	 * @param type 类型，类似表
	 * @param id 主键 要求类型当中唯一
	 */
	public static void addDocument(Map map, String index, String type, String id) {
		client.prepareIndex(index, type, id).setSource(map).get();
	}
	
	public static void addDocument(JSONObject jsonObject, String index, String type, String id) {
		client.prepareIndex(index, type, id).setSource(jsonObject).get();
	}
	
	/**
	 * 跟据指定ID获取一条数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static Map<String, Object> getDocument(String index, String type, String id) {
		GetResponse response = client.prepareGet(index, type, id).get();
		Map<String, Object> map = response.getSource();
		return map;
	}

	/**
	 * 删除一条数据
	 * @param index 索引，类似数据库
	 * @param type 类型，类似表
	 * @param id 要删除数据的主键
	 */
	public static void delDocument(String index, String type, String id){
		client.prepareDelete(index, type, id).get();
	}
	
	/**
	 * 使用添加方法进行数据更新
	 */
	@Deprecated
	public static void updDocument(){
		
	}
	
	/**
	 * 根据条件进行分页查询
	 * @param index 相当于库
	 * @param type 相当于表
	 * @param from 从第几条开始
	 * @param size 查询几条
	 * @param index
	 * @param type
	 * @param from
	 * @param size
	 * @return
	 */
	public static Map<String,Object> queryDocuments(
    		String index, String type, 
    		int from, int size){
		return ElasticSearchUtil.queryDocuments(index, 
				type, from, size, null, null, null, null);
	}
	
	/**
	 * 根据条件进行分页查询
	 * @param index 相当于库
	 * @param type 相当于表
	 * @param from 从第几条开始
	 * @param size 查询几条
	 * @param queryMaps 精确查找字段
	 * @param fullTextQueryMaps 模糊查找字段
	 * @param rangeList 范围 参数 key为 field,from,to
	 * @param sortMaps 排序参数  key为要排序的字段 value传大写的 ASC , DESC
	 * @return
	 */
    public static Map<String,Object> queryDocuments(
    		String index, 
    		String type, 
    		int from, 
    		int size, 
    		Map<Object, Object> queryMap, 
    		Map<Object, Object> likeMap,
    		List<Map<String, Object>> rangeList,
    		Map<Object, Object> sortMaps
    		) {
        try {
        	Map<String,Object> map = new HashMap<String,Object>();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
            		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            //模糊查询
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            if (likeMap != null && !likeMap.isEmpty()) {
                for (Object key : likeMap.keySet()) {
                	query = query.must(QueryBuilders.wildcardQuery((String) key, "*" + likeMap.get(key).toString()+"*"));
                }
            }
            
            //构造精确的并且查询
            if (queryMap != null && !queryMap.isEmpty()) {
                for (Object key : queryMap.keySet()) {
                	query = query.must(QueryBuilders.termQuery((String) key, queryMap.get(key)));
                }
            }
            
            //范围查询
	        if (rangeList != null && rangeList.size() > 0) {
	            for (Map<String, Object> range : rangeList) {
	                if (range != null && (!range.isEmpty())) {
	                	query = query.must(QueryBuilders.rangeQuery(range.get("field").toString()).from(range.get("from")).to(range.get("to")));
	                }
	            }
	        }
            
            searchRequestBuilder.setQuery(query);
            
            //构造排序参数
            SortBuilder sortBuilder = null;
            if (sortMaps != null && !sortMaps.isEmpty()) {
                for (Object key : sortMaps.keySet()) {
                    sortBuilder = SortBuilders.fieldSort((String) key)
                    		.order(sortMaps.get(key).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
                }
                searchRequestBuilder.addSort(sortBuilder);
            }
            searchRequestBuilder.setFrom(from).setSize(size);
            //查询
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits hits = response.getHits();
            map.put("total", hits.getTotalHits());
            for (SearchHit hit : hits) {
                list.add(hit.getSource());
            }
            map.put("data", list);
            return map;
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException("条件查询时出错" + e.getMessage());
        }
    }
    @SuppressWarnings("rawtypes")
    public static void main(String args[]){
    	//添加1000条数据
//    	long l = System.currentTimeMillis();
//    	DateTimeFormatter defaultDatePrinter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
//    	for(int i = 0;i<100;i++){
//    		Map<String,Object> order = new HashMap<String,Object>();
//    		order.put("orderId", 999+i);
//    		order.put("createUser", "zuotest");
//    		order.put("address", "北京市朝阳区");
//    		order.put("ordersum", 100 + i);
//    		order.put("createtime",DateUtil.addDate(new Date(), i*-1).getTime());
//    		ElasticSearchUtil.addDocument(order, "zuotest", "orderTest", UUID.randomUUID().toString());
//    	}
//    	System.out.println(System.currentTimeMillis() - l);
    	//获取一条数据
//    	Map map = ElasticSearchUtil.getDocument("zuotest", "orderTest", "bc6d5499-8412-4576-8d96-740d9117dbd9");
//    	//删除一条数据
//    	long l = System.currentTimeMillis();
//    	ElasticSearchUtil.delDocument("zuotest", "orderTest", "802733fd-3ee2-42bc-850c-573416a3251a");
//    	System.out.println(System.currentTimeMillis() - l);
    	//修改一条数据
    	
//    	Map<String,Object> order = new HashMap<String,Object>();*
//		order.put("orderId", 73);
//		order.put("createUser", "zuoguodong");
//		order.put("address", "北京市朝阳区aa"); 
//		order.put("ordersum", 173);
//		long l = System.currentTimeMillis();
//		ElasticSearchUtil.addDocument(order, "zuotest", "orderTest", "36b4ee2b-2b30-4234-8632-fc94d6b4e90f");
//		System.out.println(System.currentTimeMillis() - l);
		//获取一条数据
//    	Map map = ElasticSearchUtil.getDocument("zuotest", "orderTest", "f81edb91-f4ae-44b9-8419-dd997e70864c");
//    	System.out.println(map.get("address") + ":" + map.get("createUser"));
    	
    	//条件检索
		Map queryMaps = new HashMap();
//    	queryMaps.put("createUser", "zuo");
//    	queryMaps.put("orderId", 82);
    	
    	List<Map<String, Object>> rangeList = new ArrayList<Map<String, Object>>();
    	Map<String, Object> range = new HashMap<String,Object>();
    	range.put("field", "createtime");
    	range.put("from", DateUtil.addDate(new Date(), -2).getTime());
    	range.put("to", DateUtil.addDate(new Date(), -1).getTime());
    	rangeList.add(range);
    	Map<String, Object> range2 = new HashMap<String,Object>();
    	range2.put("field", "createtime");
    	range2.put("from", DateUtil.addDate(new Date(), -8).getTime());
    	range2.put("to", DateUtil.addDate(new Date(), -7).getTime());
    	rangeList.add(range2);
    	
    	Map fullTextQueryMaps = new HashMap();
//    	fullTextQueryMaps.put("createUser", "d");
    	
    	Map sortMaps = new HashMap();
    	Map map = ElasticSearchUtil.queryDocuments("zuotest", "orderTest", 0, 10, queryMaps, fullTextQueryMaps,rangeList, sortMaps);
    	System.out.println("total:" + map.get("total"));
    	List<Map<String, Object>> data = (List<Map<String, Object>>)map.get("data");
    	for(Map<String, Object> order : data){
    		System.out.println("orderId" + ":" + order.get("orderId"));
    	}
    }
    
    public static QueryBuilder getQueryBuilder(String index,String type){
    	return new QueryBuilder(index,type);
    }
    
    public static class QueryBuilder{
    	private SearchRequestBuilder searchRequestBuilder = null;
    	public QueryBuilder(String index,String type){
    		searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
    	}
    	
    	public QueryBuilder sort(String field,String order){
            searchRequestBuilder.addSort(SortBuilders.fieldSort(field).order(
            		order.toUpperCase().equals("ASC") ? SortOrder.ASC : SortOrder.DESC));
    		return this;
    	}
    	
    	public QueryBuilder setPage(int from, int size){
    		searchRequestBuilder.setFrom(from).setSize(size);
    		return this;
    	}
    	
    	public Map<String,Object> execute(){
    		return this.execute(null);
    	}
    	
    	public Map<String,Object> execute(ElasticSearchCondition condition){
    		Map<String,Object> map = new HashMap<String,Object>();
    		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    		if(condition != null){
    			searchRequestBuilder.setQuery(condition.getQuery());
    		}
    		SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits hits = response.getHits();
            map.put("total", hits.getTotalHits());
            for (SearchHit hit : hits) {
                list.add(hit.getSource());
            }
            map.put("data", list);
            return map;
    	}
    	
    }
    
}
