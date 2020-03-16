package bitcoinJava;

import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;


/***
 * jointure entre Block et Cours bolt
 * Prend les informations des 2 bolt : cours_bolt et block_bolt
 * fait la multiplication entre le cours actuel et le block créé
 */
public class BlockCoursJoinBolt extends BaseRichBolt {
    private OutputCollector outputCollector;
    Double coursbtceuro;


    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        coursbtceuro = Double.valueOf(0.0);
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            process(tuple);
        } catch (ParseException e) {
            e.printStackTrace();
            outputCollector.fail(tuple);
        }
    }


    public void process(Tuple input) throws ParseException {

        System.out.println("-------***** BlockCoursJointBolt **********" );

        String op = input.getStringByField("op");


        if ("cours".equals(op)) {
            coursbtceuro = input.getDoubleByField("cours_bitcoin");
        }
        else if ("block".equals(op) && coursbtceuro != 0){
            Long timestamp = input.getLongByField("timestamp");
            String hash = input.getStringByField("hash");
            String block_found_by = input.getStringByField("block_found_by");
            Double block_reward = input.getDoubleByField("block_reward");


            Double block_reward_euro = block_reward  * coursbtceuro;
            System.out.println("============= block_reward_euro ===========  block_reward  * coursbtceuro = " + block_reward +" * "+ coursbtceuro+" = "+ block_reward_euro );
            outputCollector.emit(new Values(op, timestamp, hash, block_found_by, block_reward, block_reward_euro));
            outputCollector.ack(input);
        }
        else System.out.println("xxxxxxxxxx  pas de cours ni block avec cours euro !=0 xxxxxxxx coursbtceuro = " + coursbtceuro );
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("op", "timestamp", "hash","block_found_by","block_reward","block_reward_euro"));
    }
}