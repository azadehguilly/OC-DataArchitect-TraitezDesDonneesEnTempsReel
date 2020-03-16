# Open Classrooms, formation Data Architect
# Projet : Traitez des données en temps réel


**Mission:**

Faire un projet avec l'eco système de bitcoin 


**Contexte :**

* Bitcoin: une monnaie virtuelle dont l’unité de compte est le bitcoin
* Le minage: le procédé par lequel les transactions Bitcoin sont sécurisées. 
* Les Mineurs: effectuent avec leur matériel informatique des calculs mathématiques pour le réseau Bitcoin
* Transaction : une transfère de bitcoin, payer ou recevoir de bitcoin


## inclus :

**P3_01_kibana.pptx :** Tableau de bord, resultat de projet 
**P3_02_schémaarchitecture.pptx :** Dossier d'architecture
**P3_03_ProducerKafkaPython_TransactionBlock.py:** Producer kafka qui consome une API de transaction et de block de bitcoin, ecrit en python
**P3_04_ProducerKafkaPython_cours.py :** Producer kafka qui consome une API de cours de bitcoin, ecrit en python
**P3_05_DevStormJava_bitcoinJava.zip :** Code source de la partie Storm en java
**P3_06_mapingElasticsearch_transaction.json :** Mapping de création d'index "transaction" d'elasticsearch
**P3_07_mapingElasticsearch_block.json :** Mapping de création d'index "block" d'elasticsearch
**P3_08_mapingElasticsearch_cours.json :** Mapping de création d'index "cours" d'elasticsearch


## note et usage
Les outils suivant doivent être bien configuré et être lancé:
	
  ```
  1) Zookeeper:
	cd dev/OC/projet3/kafka_2.12-2.3.1
	./bin/zookeeper-server-start.sh ./config/zookeeper.properties

	2) Kafka:
	cd dev/OC/projet3/kafka_2.12-2.3.1
	 ./bin/kafka-server-start.sh ./config/server.properties
	
	3) Kafka manager:
	cd  /Users/Axa/dev/OC/projet3/kafka_2.12-2.3.1/kafka-manager/target/universal/kafka-manager-2.0.0.2
	ZK_HOSTS=localhost:2181 ./bin/kafka-manager
	http://localhost:9000/clusters/bitcoin/topics
	
	4) Storm nimbus:
	cd dev/OC/projet3/apache-storm-1.2.3
	./bin/storm nimbus
	
	5) Storm supervisor:
	cd dev/OC/projet3/apache-storm-1.2.3
	./bin/storm supervisor
	
	6) Storm UI
	cd /Users/Axa/dev/OC/projet3/apache-storm-1.2.3
	./bin/storm ui
	http://localhost:8080/index.html
	
	7) Elasticsearch
	cd  /Users/Axa/dev/OC/projet3/elasticsearch-7.5.2/bin
	./elasticsearch
	http://localhost:9200/
	
	8) Kibana
	cd /Users/Axa/dev/OC/projet3/kibana-7.5.2-darwin-x86_64/
	./bin/kibana
   http://localhost:5601
  
  ```


## Prérequis

* python  2
* java version "1.8"
* kafka 2.12-2.3.1
* storm 1.2.3
* elasticsearch 7.5.2
* kibana 7.5.2


## Le résultat : 

Le cours du bitcoin en euros.
Le volume de bitcoins échangés par heure, en bitcoins et en euros.
La valeur maximale des transactions réalisées par heure, en bitcoins et en euros.
Les mineurs qui se sont le plus enrichis, par jour et par mois, en bitcoins et en euros.
voir : P3_01_kibana.pptx


## Todo :

Enregistrer les données dans HDFS avant d'envoyer dans kafka et dans elasticsearch.

