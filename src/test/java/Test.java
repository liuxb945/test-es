import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

 
 

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

import com.alibaba.fastjson.JSON;
 
 


public class Test {
//http://www.cnblogs.com/bigfanofcpp/archive/2013/01/22/2871852.html  搜索
	public static void main(String[] args) throws Exception {

    	ElasticSearchTest e = new ElasticSearchTest();
    	e.init();
    	
    	String CluterName ="testasv" ;
     	 String type ="user" ;
//    	 	 	  		String type ="inv" ;
//     	 	  	String type ="lab" ;
//         	e.createCluterName(CluterName);
//   	
//                       	e.delMapping(CluterName, "user");  
//                      	e.delMapping(CluterName, "inv"); 
//                    	e.delMapping(CluterName, "que"); 
//     	      	    e.createQuestionMapping(CluterName, "que"); 
//    	
//          	e.createLabelyMapping(CluterName, "lab"); 
//      	e.createInventoryMapping(CluterName, "inv"); 
//              	e.createUserMapping(CluterName, "user");
    	
     	Question q = new Question() ;
    	q.setId(3);
    	q.setAnswerNum(1);
    	q.setBrowseNum(5);
    	q.setBusinessScore(3);
    	q.setCommentNum(2);
    	q.setCreateTime(new Date());
    	q.setDescription("我是测试问题的内容");
    	byte b = 1;
    	q.setEnable(b);
    	q.setLastActiveTime(new Date());
    	q.setLastActiveUserId(400);
    	q.setTitle("测试title");
    	q.setTopNum(2);
    	q.setUsedAnswerId(300);
    	q.setUserId(307);
    	System.out.println(JSON.toJSONString(q));
        e.addField(CluterName,type, 3, JSON.toJSONString(q)); 
      	 
//      	 .startObject("id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
//      	 .startObject("picture").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
//      	 .startObject("name").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
//      	 // label 标签名字(名字1,名字2)   question inventory  inviteCount  questionAnswerCount   inventoryAnswerCount
//      	 .startObject("label").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
//      	 .startObject("question").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
//      	 .startObject("inventory").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
//      	 .startObject("inviteCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
//      	 .startObject("questionAnswerCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
//      	 .startObject("inventoryAnswerCount").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()	 
      	 
// 		int id = 8;
// 		String type = "user" ;
// 		Map<String, Object> map = new HashMap<String, Object>() ;
// 		map.put("label", "13,3");
//		UserSearchVO user = new UserSearchVO();
//		user.setId(id);
//		user.setName("400");
//		user.setLabel("12,1");
//		user.setQuestion("9257,9528");
//		user.setInviteCount(0);
//		user.setInventoryAnswerCount(0);
//		user.setQuestionAnswerCount(0); 
//		 e.addField(CluterName,type, id, JSON.toJSONString(user)); 
//		 e.updateField(CluterName,type, id, map);
		 e.delField(CluterName,"user", 8) ;
//	    	SearchHit  hit = e.queryById(CluterName,type, id) ;
//	 
//    	    System.out.println(hit.getSource().get("label") +"   "+hit.getSource().get("id") ); 
    	    
 
// 	
//			int id2 = 2;
//			UserSearchVO user2 = new UserSearchVO();
//			user2.setId(id2);
//			user2.setName("4002");
//			user2.setLabel("12,14");
//			user2.setQuestion("9384,9438");
//			user2.setInviteCount(0);
//			user2.setInventoryAnswerCount(0);
//			user2.setQuestionAnswerCount(0); 
//			 e.addField(CluterName,type, id2, JSON.toJSONString(user2)); 
		 
		 
//  			 SearchResponse  sr = e.inviteQuery(CluterName, type, "12,15", true, 9257, 2, 0, 5) ;
//      	     
//				Iterator<SearchHit> it = sr.getHits().iterator();
//				
//		    	while(it.hasNext()) {  
//	    		SearchHit hit = it.next() ;
//	    	 
//	    	    System.out.println(hit.getSource().get("name") +"   "+hit.getSource().get("question") ); 
//	    	    
//  
//	    	} 
				
				
//       	  
// 
//    	SearchHits  hits = e.query(CluterName,type,"测试", null ,0, 4).getHits() ;
//     	System.out.println("all hits:"+hits.getTotalHits()); 
//    	Iterator<SearchHit> it = hits.iterator();  
//    	while(it.hasNext()) {  
//    		SearchHit hit = it.next() ;
//    		
//    		   //获取对应的高亮域
//            Map<String, HighlightField> result = hit.highlightFields();    
//            //从设定的高亮域中取得指定域
//            HighlightField titleField = result.get("title");  
////            description
//            //取得定义的高亮标签
//             
//            Text[] titleTexts =  titleField.fragments();    
//            //为title串值增加自定义的高亮标签
//            String title = "";  
//            for(Text text : titleTexts){     
//                  title += text;  
//            }
//    		System.out.println("title1:"+title);
//    		
//            //为title串值增加自定义的高亮标签
//            String description = "";  
//            for(Text text : result.get("description").getFragments()){     
//            	description += text;  
//            }
//    		System.out.println("description:"+description);
//    		
//    		long time = (Long)hit.getSource().get("createTime") ;
//    		
//    	    System.out.println(hit.getSource().get("title")+"  "+DateUtils.friendlyTime(new Date(time)));  
//    	   
//    	} 
//    int id = 4 ;
//    	TestBean tb = new TestBean();
//    	tb.setId(id);
//    	tb.setName("何正军");
//    	e.addField(CluterName,type, id, JSON.toJSONString(tb)); 
//    	Map<String,Object> map = new HashMap<String,Object>() ;
//    	map.put("name", "500") ;
//    	e.updateField(CluterName, type, id, map); 
//    	
//    	SearchHit  hit  =  	e.queryById(CluterName, type, id) ;
//    	if(hit==null){
//    		System.out.println(111); 
//    	}else{
//    		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~"+hit.getSource().get("name")); 
//    	}
//    	
//    	
//    	TestBean tb2 = new TestBean();
//    	tb2.setId(2);
//    	tb2.setLabel("3,4,5");
//    	tb2.setName("hezhengjun");
//    	e.addField(CluterName,type, 2, JSON.toJSONString(tb2)); 
//    	
//    	TestBean tb3 = new TestBean();
//    	tb3.setId(3);
//    	tb3.setName("hcv1");
//    	tb3.setLabel("4,5,6");
//    	e.addField(CluterName,type, 3, JSON.toJSONString(tb3)); 
//    	
//    	TestBean tb4 = new TestBean();
//    	tb4.setId(4);
//    	tb4.setName("hez4");
//    	tb4.setLabel("5,6,7");
//    	e.addField(CluterName,type, 4, JSON.toJSONString(tb4)); 
//    	
//    	List<String> fieldName = new ArrayList<String>();
//    	fieldName.add("label") ;
//    	SearchHits  hits =  e.query(CluterName, type, "5", fieldName, 0, 5).getHits() ;
////
//// 
//    	System.out.println("all hits:"+hits.getTotalHits()); 
//    	Iterator<SearchHit> it = hits.iterator();  
//    	while(it.hasNext()) {  
//    		SearchHit hit = it.next() ;
//    	    System.out.println(hit.getSource().get("label"));  
//    	   
//    	} 
    	
    	
//     	 	  clusterName:code address:182.92.128.179:9030
    	
//     	 	  SearchResponse  s =  e.prefixQuery(CluterName, type, "java", "ikname", 2) ;
//     			SearchHits  hits  = s.getHits() ;
//     			if(hits.getTotalHits()==0){
//     				 System.out.println("null");
//     			}
//     	     
//     	    	Iterator<SearchHit> it = hits.iterator();  
//     	    	while(it.hasNext()) {  
//     	    		SearchHit hit = it.next() ;
//     	    		System.out.println(hit.getSource().get("name")); 
//     	    	}
     	    	
     	    	
    	
//    	 	SearchHits  hits =  e.labelQueInvQuery(CluterName, type,
//    				"4","lastActiveTime", 0,5).getHits() ;
//      	System.out.println("all hits:"+hits.getTotalHits()); 
//      	Iterator<SearchHit> it = hits.iterator();  
//      	while(it.hasNext()) {  
//      		SearchHit hit = it.next() ;
//      	    System.out.println(hit.getSource().get("label"));  
//      	   
//      	} 
    	
    	
    	
    	
	}

}
