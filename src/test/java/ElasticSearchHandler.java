
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ElasticSearchHandler {
    public static void main(String[] args) {
        try {
            /* 创建客户端 */
            // client startup
            Client client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.204.131"), 9300));

            XContentBuilder builder = jsonBuilder().startObject().field("user", "kimchy").field("postDate", new Date())
                    .field("message", "trying out Elasticsearch").endObject();


            List<String> jsonData = DataFactory.getInitJsonData();

            for (int i = 0; i < jsonData.size(); i++) {
                IndexResponse response = client.prepareIndex("blog", "article").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                   System.out.println("创建成功!");
                }
            }
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}