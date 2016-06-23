import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;

import com.codefollow.search.ElasticUtil;

public class ElasticUtilTest {
	private ElasticUtil eu;
	private Client client;
	
	@Before
	public void setUp(){
		eu=new ElasticUtil();
		client=eu.createClient("code", "192.168.187.130:9300");
		eu.setClient(client);
	}
	
	@Test
	public void testCreateIndex(){
		eu.createIndex("test3");
	}
}
