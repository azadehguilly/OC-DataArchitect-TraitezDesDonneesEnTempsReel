package bitcoinJava;

import java.util.Date;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;

/***
 * Cours bolt
 * Prend les informations de kafkaspout : bitcoinCours en format json
 */
public class CoursBolt extends BaseRichBolt {
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


        Double cours_bitcoin= Double.valueOf(0.0);
        String updated_time = "unknown";
        Long producerid = Long.valueOf(0);

        System.out.println("-------******** CoursBolt *******" + input.getStringByField("value"));


        JSONParser jsonParser = new JSONParser();
        //le contenue de tuple est dans le champs "value"
        JSONObject obj = (JSONObject) jsonParser.parse(input.getStringByField("value"));
        //les champs en json

        String op = (String)obj.get("op");
        cours_bitcoin = (Double) obj.get("cours_bitcoin");
        updated_time = (String) obj.get("updated_time");
        producerid = (Long) obj.get("id");

        Date updated_time2 = new Date(updated_time);
        Long updated_time_timestamp = updated_time2.getTime();


        System.out.println("op:" + op);
        System.out.println("cours_bitcoin:" + cours_bitcoin);
        System.out.println("updated_time:" + updated_time);
        System.out.println("timestamp:" + updated_time_timestamp);
        System.out.println("id:" + producerid);


        outputCollector.emit(new Values(op, cours_bitcoin, updated_time_timestamp, producerid));
        outputCollector.ack(input);

    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("op", "cours_bitcoin", "timestamp", "id"));
    }
}