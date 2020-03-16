package bitcoinJava;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;

/***
 * Transaction bolt
 * Prend les informations de kafkaspout : bitcoinTransaction en format json
 */
public class TransactionBolt extends BaseRichBolt{
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
        }
    }

    public void process(Tuple input) throws ParseException {

        Long timestamp = Long.valueOf(0);
        String hash = "unknown";
        Double transaction_total_amount = Double.valueOf(0.0);

        System.out.println("-------******* TransactionBolt ********" + input.getStringByField("value"));


        JSONParser jsonParser = new JSONParser();
        //le contenue de tuple est dans le champs "value"
        JSONObject obj = (JSONObject) jsonParser.parse(input.getStringByField("value"));
        //les champs en json

        String op = (String)obj.get("op");
        timestamp = (Long) obj.get("timestamp");
        hash = (String) obj.get("hash");
        transaction_total_amount = (Double) obj.get("transaction_total_amount");


        System.out.println(op);
        System.out.println(timestamp);
        System.out.println(hash);
        System.out.println(transaction_total_amount);


        outputCollector.emit(new Values(op, timestamp, hash,transaction_total_amount));
        outputCollector.ack(input);

    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("op", "timestamp", "hash", "transaction_total_amount"));
    }
}