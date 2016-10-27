
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class ElasticSearchGet {

    public static void main(String[] args) {
        // client startup
        try {
            Client client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.204.131"), 9300));

            QueryBuilder qb1 = termQuery("title", "hibernate");
            QueryBuilder qb2= QueryBuilders.multiMatchQuery("git", "title","content");


            SearchResponse response = client.prepareSearch("blog").setTypes("article").setQuery(qb2).execute()
                    .actionGet();

            SearchHits hits = response.getHits();
            if (hits.totalHits() > 0) {
                for (SearchHit hit : hits) {
                    System.out.println("score:"+hit.getScore()+":\t"+hit.getSource());// .get("title")
                }
            } else {
                System.out.println("搜到0条结果");
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
