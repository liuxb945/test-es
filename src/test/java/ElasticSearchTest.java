

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
/**
 * http://www.tuicool.com/articles/R7RVJb
 * http://riching.iteye.com/blog/1921625
 * http://blog.csdn.net/column/details/elasticsearch.html
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-update-api.html
 * marp:
 * http://blog.csdn.net/lvhong84/article/details/23936697
 * 
 * java Mapping
 * http://www.csdn123.com/html/blogs/20130806/48812.htm
 * 
 * ElasticSearch java API--创建mapping
 * http://blog.csdn.net/an74520/article/details/8200551
 * 
 * 搜索基类
 * https://github.com/elastic/elasticsearch
 * 
 *  // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);
        //设置高亮显示
        searchRequestBuilder.addHighlightedField("title");
        searchRequestBuilder.setHighlighterPreTags("<span style=\"color:red\">");
        searchRequestBuilder.setHighlighterPostTags("</span>");     
        
        多字段查询
        http://blog.csdn.net/xiaohulunb/article/details/37877435
        
        elasticsearch-不停服务修改mapping
        http://m.blog.csdn.net/blog/jingkyks/41513063
 * 
 * @author hezhengjun
 */
public class ElasticSearchTest {
    
	private String clusterName ;
	private String address ;

    private Client client;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTest.class);
    
 
    
    
    public void init(){
    	 LOGGER.debug("clusterName:" + clusterName+" address:" + address);
//    	 client = createClient(clusterName, address);182.92.128.179:9030
//    	 client = createClient("code", "192.168.187.130:9300");
    	 client=this.createClient("code", "192.168.187.130:9300");
    }

    public  Client getClient() {
        return client;
    }
    
 
    /**
     * 删除
     * @param indexName
     * @param indexType
     * @param id
     * @return 是否删除成功，不成功可能是没有找到对应的数据或者索引
     */
	public boolean delField(String indexName, String indexType, int id) {
		if (StringUtils.isBlank(indexName) || StringUtils.isBlank(indexType)) {
			LOGGER.error("delField 必填字段为空:indexName=" + indexName
					+ " indexType=" + indexType);
			throw new RuntimeException("delField 必填字段为空:indexName=" + indexName
					+ " indexType=" + indexType);
		}
		if (id == 0) {
			LOGGER.error("delField id必填填写:indexName=" + indexName
					+ " indexType=" + indexType);
			throw new RuntimeException("delField id必填填写:indexName=" + indexName
					+ " indexType=" + indexType);
		}
		DeleteResponse d = client
				.prepareDelete(indexName, indexType, String.valueOf(id))
				.execute().actionGet();
		return d.isFound();
	}
    
    /**
     * 更新
     * @param indexName
     * @param indexType
     * @param id
     * @param map 如果你需要清除一个属性的值，传入null会导致ES认为你不需要更新这个属性，所以清除一个属性的值我们需要传入一个""
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
	public void updateField(String indexName, String indexType, int id,
			Map<String, Object> map) {
		if (StringUtils.isBlank(indexName) || StringUtils.isBlank(indexType)) {
			LOGGER.error("addField 必填字段为空:indexName=" + indexName
					+ " indexType=" + indexType);
			throw new RuntimeException("addField 必填字段为空:indexName=" + indexName
					+ " indexType=" + indexType);
		}
		if (id == 0) {
			LOGGER.error("addField id必填填写:indexName=" + indexName
					+ " indexType=" + indexType);
			throw new RuntimeException("addField id必填填写:indexName=" + indexName
					+ " indexType=" + indexType);
		}
		try {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(indexName);
			updateRequest.type(indexType);
			updateRequest.id(String.valueOf(id));

			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("serialVersionUID") ){
					continue;
				}
				builder.field(entry.getKey(), entry.getValue());
			}
			builder.endObject();
			updateRequest.doc(builder);
			client.update(updateRequest).get();
		} catch (Exception e) {
			LOGGER.error("updateField  fail!", e);
			throw new RuntimeException("updateField fail!", e);
		}

 
    }
    
    /**
     * 新加字段
     * @param indexName
     * @param indexType
     * @param id
     * @param beanJson  JSON.toJSONString(bean/List<bean<)
     */
    public  void addField(String indexName, String indexType, int id ,String beanJson) {
    	System.out.println("beanJson:" + beanJson);
    	  if(StringUtils.isBlank(indexName)||StringUtils.isBlank(indexType)){
    		  LOGGER.error("addField 必填字段为空:indexName="+indexName+" indexType="+indexType); 
    		  throw new RuntimeException("addField 必填字段为空:indexName="+indexName+" indexType="+indexType) ;
    	  }
    	  if(StringUtils.isBlank(beanJson)){
    		  LOGGER.error("addField beanJson为空:indexName="+indexName+" indexType="+indexType); 
    		  throw new RuntimeException("addField beanJson为空:indexName="+indexName+" indexType="+indexType) ;
    	  }
    	  
		Map<String, Object> map = JSON.parseObject(beanJson,
				new TypeReference<Map<String, Object>>() {
				});
    	  if(id==0){ 
    		  LOGGER.error("addField id必填填写:indexName="+indexName+" indexType="+indexType);
    		  throw new RuntimeException("addField id必填填写:indexName="+indexName+" indexType="+indexType) ;
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
//            LOGGER.debug(builder.toString());
            System.out.println("builder:" + builder.toString());
			IndexResponse response = client.prepareIndex(indexName, indexType,String.valueOf(id)) 
//					.prepareIndex(indexName, indexType)
					.setSource(builder).execute().actionGet();
			 
            
//            PutMappingRequest request = Requests.putMappingRequest(indexName).type(indexType).source(builder);
//            PutMappingResponse response = client.admin().indices().putMapping(request).actionGet();
            System.out.println("response:" + response.toString());
            LOGGER.debug("response id:" + response.getId() +" input id :"+id);
        } catch (IOException e) {
        	LOGGER.error("add filed fail!", e);
            throw new RuntimeException("add filed fail!", e);
        } 
    }
    
    
	/**
	 * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引
	 * ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * 
	 * @param indices
	 *            索引名称；
	 * @param mappingType
	 *            索引类型
	 * @throws Exception
	 */
	public void createQuestionMapping(String indices, String mappingType)
			throws Exception {
 
 XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")	 
 .startObject("labels").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()		 
 .startObject("id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("userId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("title").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("enable").field("type", "byte").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("usedAnswerId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("description").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("browseNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("topNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("commentNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("answerNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("businessScore").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("createTime").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("lastActiveTime").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("lastActiveUserId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("codeValidTime").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("codeId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 
 
 .endObject().endObject().endObject();
PutMappingRequest mapping = Requests.putMappingRequest(indices)
		.type(mappingType).source(builder);
 client.admin().indices().putMapping(mapping).actionGet();
 
	}
    
	
	/**
	 * 根据标签id 如：5,4 或5 来查询对应的问题或者存货,并根据指定字段排序
	 * @param index
	 * @param mappingType
	 * @param labels 标签id 如：5,4 或5 
	 * @param sortName 排序字段
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResponse labelQueInvQuery(String index, String mappingType,
			String labels,String sortName, int start, int end){
		QueryBuilder query = QueryBuilders.queryStringQuery(labels)
				.field("labels")  ;
		SearchResponse response = client.prepareSearch(index)
				.setTypes(mappingType).setQuery(query)
				.addSort(sortName, SortOrder.DESC).setFrom(start).setSize(end)
				.setExplain(true)
				//设置高亮显示
                .addHighlightedField("labels")
				.setHighlighterPreTags("<span style=\"color:red\">")
				.setHighlighterPostTags("</span>")
				//设置高亮结束
				.execute().actionGet();
		
		 
          
		
		
		return response ;
	}
	
	
	/**
	 * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引
	 * ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * 
	 * @param indices
	 *            索引名称；
	 * @param mappingType
	 *            索引类型
	 * @throws Exception
	 */
	public void createInventoryMapping(String indices, String mappingType)
			throws Exception {
 
 XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")
 .startObject("labels").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()		 
 .startObject("id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("userId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("title").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("enable").field("type", "byte").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("attachment").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("description").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("browseNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("topNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("commentNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("answerNum").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("businessScore").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("createTime").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("lastActiveTime").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("lastActiveUserId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 
 .endObject().endObject().endObject();
PutMappingRequest mapping = Requests.putMappingRequest(indices)
		.type(mappingType).source(builder);
 client.admin().indices().putMapping(mapping).actionGet();
 
	}
	
	
	public void createLabelyMapping(String indices, String mappingType)
			throws Exception {
 
 XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")
 .startObject("id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("name").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("ikname").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .endObject().endObject().endObject();
PutMappingRequest mapping = Requests.putMappingRequest(indices)
		.type(mappingType).source(builder);
 client.admin().indices().putMapping(mapping).actionGet();
 
	}
	
	/**  
	 * 把picture当用日期，格式为20150720 用来判断，是否一天以内邀请数超过2个
	 * @param indices
	 * @param mappingType
	 * @throws Exception
	 */
	public void createUserMapping(String indices, String mappingType)
			throws Exception {
 
 XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")
 .startObject("id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("picture").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("name").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 // label 标签名字(名字1,名字2)   question inventory  inviteCount  questionAnswerCount   inventoryAnswerCount
 .startObject("label").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("question").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("inventory").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
 .startObject("inviteCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("questionAnswerCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 .startObject("inventoryAnswerCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
 
 .endObject().endObject().endObject();
PutMappingRequest mapping = Requests.putMappingRequest(indices)
		.type(mappingType).source(builder);
 client.admin().indices().putMapping(mapping).actionGet();
 
	}
	
	
	public void delMapping(String indices, String mappingType)
			throws Exception {
//		DeleteMappingRequest d = Requests.deleteMappingRequest(indices).types(mappingType) ;
		//client.admin().indices().putMapping(mapping).actionGet();
//		client.admin().indices().deleteMapping(d).actionGet() ;
	}
	
    /**
     * 前缀查询
     * @param index
     * @param mappingType
     * @param queryWord
     * @param column 查询的字段列名
     * @param size 查询几条
     * @return
     */
    public SearchResponse prefixQuery(String index,String mappingType,String queryWord,String column,int size){
		QueryBuilder query = QueryBuilders.prefixQuery(column, queryWord) ;
	       SearchResponse response = client.prepareSearch(index).setTypes(mappingType) 
	                .setQuery(query).setFrom(0).setSize(size)
	                .execute().actionGet();
		return response;
    }
    
    /**
     * 简单查询
     * @param index
     * @param mappingType
     * @param queryWord
     * @param start 从0开始
     * @param end
     * @return
     *
     */
    public SearchResponse query(String index,String mappingType,String queryWord,List<String> fieldName,int start,int end){
    	
//		if (fieldName==null) {
//			LOGGER.error("query 必填字段fieldName");
//			throw new RuntimeException("query 必填字段fieldName");
//		}
//		if (StringUtils.isBlank(index)) {
//			LOGGER.error("query 必填字段index");
//			throw new RuntimeException("query 必填字段index");
//		}
//    	if(StringUtils.isBlank(queryWord)){
//    		  LOGGER.error("query 必填字段queryWord"); 
//    		  throw new RuntimeException("query 必填字段queryWord") ; 
//    	}
    	QueryBuilder query = QueryBuilders.queryStringQuery(queryWord).field("title").field("description");
    	//   	QueryBuilder query = QueryBuilders.queryStringQuery(queryWord).field("label");
    	// 	QueryBuilder query = QueryBuilders.termQuery("title", queryWord) ;
    	SearchResponse response ;
    	if(StringUtils.isBlank(mappingType)){
    		 response = client.prepareSearch(index)
     				.setQuery(query)
    				.setFrom(start).setSize(end).setExplain(true)
    			//设置高亮显示
                .addHighlightedField("title").addHighlightedField("description")
				.setHighlighterPreTags("<span style=\"color:red\">")
				.setHighlighterPostTags("</span>")
				//设置高亮结束
    				.execute().actionGet();
    	}else{
    		 response = client.prepareSearch(index).setTypes(mappingType) 
       				.setQuery(query)
     				.setFrom(start).setSize(end).setExplain(true)
     				    			//设置高亮显示
                .addHighlightedField("title").addHighlightedField("description")
				.setHighlighterPreTags("<span style=\"color:red\">")
				.setHighlighterPostTags("</span>")
				//设置高亮结束
     				.execute().actionGet();
    	}
		return response;
 
    	 
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
     * @param end
     * @return
     */
	public SearchResponse inviteQuery(String index, String mappingType,
			String queryWord, boolean isQuestion ,int businessId, int inviteCount,int start, int end) {
		QueryBuilder query = QueryBuilders.queryStringQuery(queryWord)
				.field("label")  ;
		

		RangeQueryBuilder ic = QueryBuilders.rangeQuery("inviteCount").from(0)
		.to(inviteCount).includeLower(true).includeUpper(false);
		
		QueryBuilder dayAndCount = QueryBuilders.queryStringQuery("2015-07-23")
				.field("picture")  ;
		 
		
		
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
				.addSort(sortName, SortOrder.DESC).setFrom(start).setSize(end)
				.setExplain(true).execute().actionGet();
	 
		return response;
	}
    
    
	 protected static QueryBuilder disMaxQuery() {  
	        return QueryBuilders.disMaxQuery()  
	                .add(QueryBuilders.termQuery("name", "kimchy"))          // Your queries  
	                .add(QueryBuilders.termQuery("name", "elasticsearch"))   // Your queries  
	                 ;  
	    }  
	
    
    
    /**
    * 创建索引名称
    * @param indexName 索引名称
    */
	public synchronized void  createCluterName(String indexName) {
        IndicesExistsResponse ier = client.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet();
        if (ier.isExists()) {
            LOGGER.error("create indexName=" + indexName + " is exist!!!");
            return;
        }
    
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
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
    
    /*private  Client createClient(String cluster, String addresses) {
        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).put("client", true).put("data", false).put("cluster.name", cluster).build();
        
        TransportClient client = new TransportClient(settings);
        String[] addressArray = addresses.split(",");
        for (String address : addressArray) {
            String[] addrArr = address.split(":");
            client.addTransportAddress(new InetSocketTransportAddress(addrArr[0], Integer.valueOf(addrArr[1])));
        }
        return client;
    }*/
    
    private  Client createClient(String cluster, String addresses) {
//		Settings settings = ImmutableSettings.settingsBuilder()
//				.put("client.transport.sniff", true).put("client", true)
//				.put("data", false).put("cluster.name", cluster).build();
		TransportClient client = TransportClient.builder().build();
		String[] addressArray = addresses.split(",");
		for (String address : addressArray) {
			String[] addrArr = address.split(":");
			byte[] bs=new byte[]{(byte)192,(byte)168,(byte)187,(byte)130};
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
