package bitcoinJava;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/***
 * Elasticsearch bolt
 * Prend les informations des 3 bolt :
 *          cours_bolt,
 *          transaction_cours_join_bolt
 *          block_cours_join_bolt
 * envoie toutes les informations dans 3 index elasticsearch
 */
public class ElasticBolt extends BaseRichBolt {

    private OutputCollector outputCollector;


    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
    }


    public void execute(Tuple input) {
        try {
            process(input);
        } catch (ParseException e) {
            e.printStackTrace();
            outputCollector.fail(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void process(Tuple input) throws ParseException , IOException {

        System.out.println("-------***** ElasticBolt **********" );

        String op = input.getStringByField("op");
        Long timestamp = input.getLongByField("timestamp");




        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));


        if ("cours".equals(op)){
            System.out.println("cours à envoyer dans elastic");
            Double cours_bitcoin= input.getDoubleByField("cours_bitcoin");
            Long id = input.getLongByField("id");

            Date date = new Date((Long)timestamp);
            DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateelastic = f.format(date);


            IndexRequest request = new IndexRequest("cours");
            request.id(id.toString());


            // Création d'objet String, en format json pour envoyer les info dans l'index cours d'elasticsearch
            String jsonString = "{" +
                    "\"op\":" + "\"" + op + "\"," +
                    "\"date\":" + "\"" + dateelastic + "\"," +
                    "\"cours_bitcoin\":" + "\"" + cours_bitcoin + "\"," +
                    "\"id\":" + "\"" + id + "\"" +
                    "}";


            System.out.println("op : "+ op+ "    date : "+ dateelastic+"    cours_bitcoin : "+ cours_bitcoin+"    id : "+ id  );

            request.source(jsonString, XContentType.JSON);
            IndexResponse indexResponse = client.index(request,RequestOptions.DEFAULT);
        }
        else if ("utx".equals(op)){
            System.out.println("transaction à envoyer dans elastic");
            String hash = input.getStringByField("hash");
            Double transaction_total_amount = input.getDoubleByField("transaction_total_amount");
            Double transaction_total_amount_euro = input.getDoubleByField("transaction_total_amount_euro");

            Date date = new Date(((Long)timestamp+3600)*1000);
            DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateelastic = f.format(date);

            IndexRequest request = new IndexRequest("transaction");
            request.id(hash);

            // Création d'objet String, en format json pour envoyer les info dans l'index transaction d'elasticsearch
            String jsonString = "{" +
                    "\"op\":" + "\"" + op + "\"," +
                    "\"date\":" + "\"" + dateelastic + "\"," +
                    "\"hash\":" + "\"" + hash + "\"," +
                    "\"transaction_total_amount\":" + "\"" + transaction_total_amount + "\"," +
                    "\"transaction_total_amount_euro\":" + "\"" + transaction_total_amount_euro + "\"" +
                    "}";

            System.out.println("op : "+ op+ "    date : "+ dateelastic+"    hash : "+ hash+"    transaction_total_amount : "+ transaction_total_amount+"    transaction_total_amount_euro : "+ transaction_total_amount_euro  );

            request.source(jsonString, XContentType.JSON);
            IndexResponse indexResponse = client.index(request,RequestOptions.DEFAULT);
        }
        else if ("block".equals(op)){
            System.out.println("block à envoyer dans elastic");
            String hash = input.getStringByField("hash");
            String block_found_by = input.getStringByField("block_found_by");
            Double block_reward = input.getDoubleByField("block_reward");
            Double block_reward_euro = input.getDoubleByField("block_reward_euro");

            Date date = new Date((Long)timestamp*1000);
            DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateelastic = f.format(date);


            IndexRequest request = new IndexRequest("block");
            request.id(hash);

            // Création d'objet String, en format json pour envoyer les info dans l'index block d'elasticsearch
            String jsonString = "{" +
                    "\"op\":" + "\"" + op + "\"," +
                    "\"date\":" + "\"" + dateelastic + "\"," +
                    "\"hash\":" + "\"" + hash + "\"," +
                    "\"block_found_by\":" + "\"" + block_found_by + "\"," +
                    "\"block_reward\":" + "\"" + block_reward + "\"," +
                    "\"block_reward_euro\":" + "\"" + block_reward_euro + "\"" +
                    "}";


            System.out.println("op : "+ op+ "    date : "+ dateelastic+"    hash : "+ hash+"    block_found_by : "+ block_found_by+"    block_reward : "+ block_reward +"    block_reward_euro : "+ block_reward_euro  );

            request.source(jsonString, XContentType.JSON);
            IndexResponse indexResponse = client.index(request,RequestOptions.DEFAULT);
        }
        else System.out.println("Aucun op trouvé");

        //je pousse ma requete, j'attends la reponse pour la traiter
        client.close();

    }



    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
