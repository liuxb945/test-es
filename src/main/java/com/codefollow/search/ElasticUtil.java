package com.codefollow.search;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class ElasticUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticUtil.class);
	private Client client;
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	
	private byte[] getIP(String ipAddr){
		byte[] ret = new byte[4]; 
		  try { 
		   String[] ipArr = ipAddr.split("\\."); 
		   ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF); 
		   ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF); 
		   ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF); 
		   ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF); 
		   return ret; 
		  } catch (Exception e) { 
		   throw new IllegalArgumentException(ipAddr + " is invalid IP"); 
		  }
    }
	
	public  Client createClient(String cluster, String addresses) {
		TransportClient client = TransportClient.builder().build();
		String[] addressArray = addresses.split(",");
		for (String address : addressArray) {
			String[] addrArr = address.split(":");
			byte[] bs=getIP(addrArr[0]);
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
	// 创建索引
	public void createIndex(String indexName){
		CreateIndexRequestBuilder cirb = client
				.admin()
				.indices()
				.prepareCreate(indexName);//index名称
//				.setSource(mapping);

		CreateIndexResponse response = cirb.execute().actionGet();
		if (response.isAcknowledged()) {
			System.out.println("Index created.");
		} else {
			System.err.println("Index creation failed.");
		}
		}
	
	public void createBookMapping(String indices, String mappingType)
			throws Exception {
 
 XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")	 
 .startObject("author").field("type", "string").field("store", "yes").endObject()		 
 .startObject("available").field("type", "boolean").field("store", "yes").endObject()
 .startObject("characters").field("type", "string").field("store", "yes").endObject()
 .startObject("copies").field("type", "long").field("store", "yes").endObject()
 .startObject("description").field("type", "string").field("store", "yes").endObject()
 .startObject("otitle").field("type", "string").field("store", "yes").endObject()
 .startObject("section").field("type", "long").field("store", "yes").endObject()
 .startObject("tags").field("type", "string").field("store", "yes").endObject()
 .startObject("title").field("type", "string").field("store", "yes").endObject()
 .startObject("year").field("type", "long").field("store", "yes").endObject()
 .endObject().endObject().endObject();
PutMappingRequest mapping = Requests.putMappingRequest(indices)
		.type(mappingType).source(builder);
 client.admin().indices().putMapping(mapping).actionGet();
 
	}
	
	public void deleteIndex(String indexName) {
        IndicesExistsResponse indicesExistsResponse = client.admin().indices()
                .exists(new IndicesExistsRequest(new String[] { indexName }))
                .actionGet();
        if (indicesExistsResponse.isExists()) {
            client.admin().indices().delete(new DeleteIndexRequest(indexName))
                    .actionGet();
        }
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
    
 // 删除Index下的某个Type
    public void deleteType(String indexName,String typeName){
        client.prepareDelete().setIndex(indexName).setType(typeName).execute().actionGet();
    }
}
