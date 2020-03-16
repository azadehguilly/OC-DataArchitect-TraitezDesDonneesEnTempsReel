package bitcoinJava;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/***
 * Classe principal
 * Création de 3 spout et 6 bolt
 * Création de topology
 * Lancement de projet
 */
public class App {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        TopologyBuilder builder = new TopologyBuilder();
        // Création d'un objet KafkaSpoutConfigBuilder
        // On passe au constructeur l'adresse d'un broker Kafka ainsi que
        // le nom d'un topic KafkaSpoutConfig.Builder
        //9092 est l'adresse de mon serveur kafka
        KafkaSpoutConfig.Builder<String, String> spoutConfigBuilderTransaction = KafkaSpoutConfig.builder("localhost:9092", "toptransaction");
        KafkaSpoutConfig.Builder<String, String> spoutConfigBuilderBlock = KafkaSpoutConfig.builder("localhost:9092", "topblock");
        KafkaSpoutConfig.Builder<String, String> spoutConfigBuilderCours = KafkaSpoutConfig.builder("localhost:9092", "topcours");


        // On définit ici le groupe Kafka auquel va appartenir les spout
        spoutConfigBuilderTransaction.setGroupId("mygroup");
        spoutConfigBuilderBlock.setGroupId("mygroup");
        spoutConfigBuilderCours.setGroupId("mygroup");


        KafkaSpoutConfig<String, String> spoutConfigTransaction = spoutConfigBuilderTransaction.build();
        KafkaSpoutConfig<String, String> spoutConfigBlock = spoutConfigBuilderBlock.build();
        KafkaSpoutConfig<String, String> spoutConfigCours = spoutConfigBuilderCours.build();

        // Création des objets KafkaSpout
        builder.setSpout("bitcoinTransaction", new KafkaSpout<String, String>(spoutConfigTransaction));
        builder.setSpout("bitcoinBlock", new KafkaSpout<String, String>(spoutConfigBlock));
        builder.setSpout("bitcoinCours", new KafkaSpout<String, String>(spoutConfigCours));


        // Création des bolt transaction_bolt
        builder.setBolt("transaction_bolt", new TransactionBolt())
                .shuffleGrouping("bitcoinTransaction");

        // Création des bolt block_bolt
        builder.setBolt("block_bolt", new BlockBolt())
                .shuffleGrouping("bitcoinBlock");

        // Création des bolt cours_bolt
        builder.setBolt("cours_bolt", new CoursBolt())
                .shuffleGrouping("bitcoinCours");


        // Création des bolt de jointure enntre transaction et cours
        builder.setBolt("transaction_cours_join_bolt", new TransactionCoursJoinBolt())
                .fieldsGrouping("transaction_bolt", new Fields("timestamp"))
                .globalGrouping("cours_bolt");

        // Création des bolt de jointure enntre block et cours
        builder.setBolt("block_cours_join_bolt", new BlockCoursJoinBolt())
                .fieldsGrouping("block_bolt", new Fields("timestamp"))
                .globalGrouping("cours_bolt");


        // Création des bolt pour envoie dans elasticsearch
        builder.setBolt("elastic_bolt", new ElasticBolt())
                .shuffleGrouping("transaction_cours_join_bolt")
                .shuffleGrouping("block_cours_join_bolt")
                .shuffleGrouping("cours_bolt");

        // Création de topology
        StormTopology topology = builder.createTopology();

        Config config = new Config();
        config.setMessageTimeoutSecs(60 * 30);
        String topologyName = "bitcoin_topology";

        // lancement de storm en local
        if (args.length > 0 && args[0].equals("remote")) {
            System.setProperty("storm.jar", "/Users/Axa/dev/OC/projet3/bitcoin/bitcoinJava/bitcoinJava/target/bitcoin-jar-with-dependencies.jar");
            StormSubmitter.submitTopology(topologyName, config, topology);
        }
        // lancement de storm sur le cluster
        else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(topologyName, config, topology);
        }
    }
}