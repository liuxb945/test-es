package com.codefollow.search;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class ElasticUtil {
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
}
