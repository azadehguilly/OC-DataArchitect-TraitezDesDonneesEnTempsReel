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
 * Block bolt
 * Prend les informations de kafkaspout : bitcoinBlock en format json
 */
public class BlockBolt extends BaseRichBolt{
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
        String block_found_by="unknown";
        Double block_reward= Double.valueOf(0.0);

        System.out.println("-------***** BlockBolt **********" + input.getStringByField("value"));


        JSONParser jsonParser = new JSONParser();
        // le contenue de tuple est dans le champs "value"
        JSONObject obj = (JSONObject) jsonParser.parse(input.getStringByField("value"));

        String op = (String)obj.get("op");

        timestamp = (Long) obj.get("timestamp");
        hash = (String) obj.get("hash");
        block_found_by = (String) obj.get("block_found_by");
        block_reward = (Double) obj.get("block_reward");


        System.out.println(op);
        System.out.println(timestamp);
        System.out.println(hash);
        System.out.println(block_found_by);
        System.out.println(block_reward);

        outputCollector.emit(new Values(op, timestamp, hash, block_found_by, block_reward));
        outputCollector.ack(input);

    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("op", "timestamp", "hash","block_found_by","block_reward"));
    }
}