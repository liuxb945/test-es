package com.codefollow.search;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

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
}
