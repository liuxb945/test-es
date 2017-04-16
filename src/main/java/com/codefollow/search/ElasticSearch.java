package com.codefollow.search;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
//import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
//import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
//import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
//import org.elasticsearch.search.suggest.SuggestBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
/**
 * 搜索基类
 * https://github.com/elastic/elasticsearch
 * @author 
 */
public class ElasticSearch {
    
	private static final String style = "color:#f60" ;
	
	private String clusterName ;
	private String address ;
 
	
    private Client client;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearch.class);
    
 
    public void init(){
    	 LOGGER.debug("clusterName:" + clusterName+" address:" + address);
	    client = createClient(clusterName, address); 
    }

    public  Client getClient() {
        return client;
    }
    
 
    /**
     * 删除
     * @param indexName
     * @param mappingType
     * @param id
     * @return 是否删除成功，不成功可能是没有找到对应的数据或者索引
     */
	public boolean delField(String indexName, String mappingType, int id) {
		if (StringUtils.isBlank(indexName) || StringUtils.isBlank(mappingType)) {
			LOGGER.error("delField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("delField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		if (id == 0) {
			LOGGER.error("delField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("delField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		DeleteResponse d = client
				.prepareDelete(indexName, mappingType, String.valueOf(id))
				.execute().actionGet();
		return d.isFound();
	}
    
    /**
     * 更新
     * @param indexName
     * @param mappingType
     * @param id
     * @param map 如果你需要清除一个属性的值，传入null会导致ES认为你不需要更新这个属性，所以清除一个属性的值我们需要传入一个""
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
	public void updateField(String indexName, String mappingType, int id,
			Map<String, Object> map) {
		if (StringUtils.isBlank(indexName) || StringUtils.isBlank(mappingType)) {
			LOGGER.error("addField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("addField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		if (id == 0) {
			LOGGER.error("addField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("addField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		try {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(indexName);
			updateRequest.type(mappingType);
			updateRequest.id(String.valueOf(id));

			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("serialVersionUID") ){
					continue ;
				}
				if(entry.getValue() instanceof Date){ //保持和json里一样的格式
					Date temp = (Date)entry.getValue() ;
					builder.field(entry.getKey(),temp.getTime());
					continue ;
				}
				builder.field(entry.getKey(), entry.getValue());
			}
			builder.endObject();
			updateRequest.doc(builder);
			client.update(updateRequest).get();
		} catch (Exception e) {
			LOGGER.error("updateField  fail!", e);//忽略此异常，一个值刚创建，然后更新，会报错，但实际插入值是正确的
		}
    }
    
    /**
     * 新加类型索引
     * @param indexName 类似库名
     * @param indexType 表名
     * @param id  相同的id多次 add会替换掉旧的值
     * @param beanJson  JSON.toJSONString(bean/List<bean<)
     */
    public  void addField(String indexName, String mappingType, int id ,String beanJson) {
    	  if(StringUtils.isBlank(indexName)||StringUtils.isBlank(mappingType)){
    		  LOGGER.error("addField 必填字段为空:indexName="+indexName+" mappingType="+mappingType); 
    		  throw new RuntimeException("addField 必填字段为空:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
    	  if(StringUtils.isBlank(beanJson)){
    		  LOGGER.error("addField beanJson为空:indexName="+indexName+" mappingType="+mappingType); 
    		  throw new RuntimeException("addField beanJson为空:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
    	  
		Map<String, Object> map = JSON.parseObject(beanJson,
				new TypeReference<Map<String, Object>>() {
				});
    	  if(id==0){ 
    		  LOGGER.error("addField id必填填写:indexName="+indexName+" mappingType="+mappingType);
    		  throw new RuntimeException("addField id必填填写:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
        try {
        	XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("serialVersionUID") ){
					continue ;
				}
				builder.field(entry.getKey(), entry.getValue()) ;
			}
            builder.endObject();
           
			IndexResponse response = client
					.prepareIndex(indexName, mappingType, String.valueOf(id))
					.setSource(builder).execute().actionGet();
            LOGGER.debug("response:" + response.toString()+" response id:" + response.getId() +" input id :"+id);
        } catch (IOException e) {
        	LOGGER.error("add filed fail!", e);
            throw new RuntimeException("add filed fail!", e);
        } 
    }
    

	/**
	 * 简单搜索
	 * @param index 索引名
	 * @param mappingType mapping名，可为空，查整个index
	 * @param queryWord 查询关键词
	 * @param fieldNameList 查询具体的字段
	 * @param start 从0开始
	 * @param pageSize
	 * @return
	 */
    public SearchResponse query(String index,String mappingType,String queryWord,List<String> fieldNameList,int start,int pageSize){
    	
		if (fieldNameList==null||fieldNameList.isEmpty()) {
			LOGGER.error("query 必填字段fieldNameList");
			throw new RuntimeException("query 必填字段fieldNameList");
		}
		if (StringUtils.isBlank(index)) {
			LOGGER.error("query 必填字段index");
			throw new RuntimeException("query 必填字段index");
		}
    	if(StringUtils.isBlank(queryWord)){
    		  LOGGER.error("query 必填字段queryWord"); 
    		  throw new RuntimeException("query 必填字段queryWord") ;
    	}
    	QueryStringQueryBuilder  builder =	QueryBuilders.queryStringQuery(QueryParser.escape(queryWord));
    		for(String fieldName:fieldNameList)	{
    			builder.field(fieldName);
    		}
    	SearchResponse response ;

		if (StringUtils.isBlank(mappingType)) {
			SearchRequestBuilder srb = client.prepareSearch(index)
					.setQuery(builder).setFrom(start).setSize(pageSize)
					.setExplain(true);
			for (String fieldName : fieldNameList) {
				srb.addHighlightedField(fieldName);
			}
			// 设置高亮显示
			response = srb
					.setHighlighterPreTags("<span style=\"" + style + "\">")
					.setHighlighterPostTags("</span>")
					// 设置高亮结束
					.execute().actionGet();
		} else {

			SearchRequestBuilder srb = client.prepareSearch(index)
					.setTypes(mappingType).setQuery(builder).setFrom(start)
					.setSize(pageSize).setExplain(true);
			for (String fieldName : fieldNameList) {
				srb.addHighlightedField(fieldName);
			}
			// 设置高亮显示
			response = srb
					.setHighlighterPreTags("<span style=\"" + style + "\">")
					.setHighlighterPostTags("</span>")
					// 设置高亮结束
					.execute().actionGet();
		}
		return response;
    }
    
    /**
     * 前缀查询--不分词
     * @param index
     * @param mappingType 必须输入
     * @param queryWord
     * @param column 查询的字段列名
     * @param size 查询几条
     * @return
     */
    public SearchResponse prefixQuery(String index,String mappingType,String queryWord,String column,int size){
		QueryBuilder query = QueryBuilders.prefixQuery(column, QueryParser.escape(queryWord)) ;
	       SearchResponse response = client.prepareSearch(index).setTypes(mappingType) 
	                .setQuery(query).setFrom(0).setSize(size)
	                .execute().actionGet();
		return response;
    }
    
    /**
     * 根据id查询
     * @param index
     * @param mappingType
     * @param id
     * @return
     */
    public SearchHit queryById(String index,String mappingType,Integer id){
    	QueryStringQueryBuilder  builder =	QueryBuilders.queryStringQuery(String.valueOf(id)).field("id") ;
	       SearchResponse response = client.prepareSearch(index).setTypes(mappingType).setQuery(builder)
	                .execute().actionGet();
			SearchHits  hits  = response.getHits() ;
	    	if(hits.getTotalHits()==0 || hits.getTotalHits() > 1){
	    		LOGGER.error("index:"+index+"  mappingType :"+mappingType+" id:"+id+" 找到对应的数据异常，为:"+hits.getTotalHits() +" 个"); 
	    		return null ;
	    	}
	    	Iterator<SearchHit> it = hits.iterator();  
		  return  it.next() ;
    }
    
    private byte[] getIP(String addr){
    	String[] strs=addr.split(".");
    	byte[] bs=new byte[strs.length];
    	for(int i=0;i<bs.length;i++){
    		bs[i]=Byte.parseByte(strs[i]);
    	}
    	return bs;
    }
    
    //"client.transport.sniff", true  集群下用
    private  Client createClient(String cluster, String addresses) {
//		Settings settings = ImmutableSettings.settingsBuilder()
//				.put("client.transport.sniff", true).put("client", true)
//				.put("data", false).put("cluster.name", cluster).build();
		TransportClient client = TransportClient.builder().build();
		String[] addressArray = addresses.split(",");
		for (String address : addressArray) {
			String[] addrArr = address.split(":");
			byte[] bs=new byte[]{(byte)192,(byte)168,(byte)204,(byte)131};
			try {
				client.addTransportAddress(new InetSocketTransportAddress(
						InetAddress.getByAddress(bs), Integer.valueOf(addrArr[1])));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return client;
    }
    
    
    /**
     * 用户推荐查询
     * 
     * 查相关标签的用户任何一个     回答次数多的靠前，回答过此问题的不能回答，邀请过的不能在邀请    
		//每天同一个用户不能邀请超过X次 inventory_invitation  
		// label    question 为问题的id列表 inventory 为问题的id列表  inviteCount  questionAnswerCount   inventoryAnswerCount
     * @param index
     * @param mappingType
     * @param queryWord 标签 如:3,4
     * @param isQuestion 是否是问题模块(否则为存货)
     * @param businessId 问题/存货的id
     * @param inviteCount 每个用户最多邀请的次数
     * @param start
     * @param pageSize 每页多少个
     * @return
     */
	public SearchResponse inviteQuery(String index, String mappingType,
			String queryWord, boolean isQuestion ,int businessId, int inviteCount,int start, int pageSize) {
		QueryBuilder query = QueryBuilders.queryStringQuery(queryWord)
				.field("label")  ;
		
		RangeQueryBuilder ic = QueryBuilders.rangeQuery("inviteCount").from(0)
				.to(inviteCount).includeLower(true).includeUpper(false);
 
		String businssRow ="question" ;
		String sortName = "questionAnswerCount" ;
		if(!isQuestion){
			sortName = "inventoryAnswerCount" ;
			businssRow = "inventory" ;
		}
		//回答了或者邀请的问题不能在邀请
		QueryBuilder businss = QueryBuilders.queryStringQuery(String.valueOf(businessId))
				.field(businssRow)  ;
 
		BoolQueryBuilder bool =	QueryBuilders.boolQuery() ;
		bool.must(query) ;
 		bool.must(ic);
 		bool.mustNot(businss) ;
		
		SearchResponse response = client.prepareSearch(index)
				.setTypes(mappingType).setQuery(bool)
				.addSort(sortName, SortOrder.DESC).setFrom(start).setSize(pageSize)
				.setExplain(true).execute().actionGet();
	 
		return response;
	}
    
	/**
	 * 根据标签id 如：5,4 或5 来查询对应的问题或者存货,并根据指定字段排序
	 * @param index
	 * @param mappingType
	 * @param labels 标签id 如：5,4 或5 
	 * @param sortName 排序字段
	 * @param start
	 * @param pageSize 
	 * @return
	 */
	public SearchResponse labelQueInvQuery(String index, String mappingType,
			String labels,String sortName, int start, int pageSize){
		QueryBuilder query = QueryBuilders.queryStringQuery(labels)
				.field("labels")  ;
		SearchResponse response = client.prepareSearch(index)
				.setTypes(mappingType).setQuery(query)
				.addSort(sortName, SortOrder.DESC).setFrom(start).setSize(pageSize)
				.setExplain(true).execute().actionGet();
		
		return response ;
	}
	
	/**
	 * 得到高亮的词
	 * @param result
	 * @param fildName
	 * @return
	 */
    public String getHitField(SearchHit hit,String fildName){
    	HighlightField  hf = hit.getHighlightFields().get(fildName) ;
    	if(hf!=null){
            String description = "";  
            for(Text text : hf.getFragments()){     
          	  description += text;   
            }
            return description ;
    	}
    	return	(String)hit.getSource().get(fildName) ;
    }
	
	

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


 
}
