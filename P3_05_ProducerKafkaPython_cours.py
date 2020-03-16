#! /usr/bin/env python

# Connect to a websocket powered by blockchain.info and print events in
# the terminal in real time.

import json
import time
import urllib.request
import websocket  # install this with the following command: pip install websocket-client

# Run `pip install kafka-python` to install this package
from kafka import KafkaProducer




def main():

    url = "http://api.coindesk.com/v1/bpi/currentprice.json"

    producer = KafkaProducer(bootstrap_servers="localhost:9092")
    producerid = 0;
    while True:
        producerid = producerid + 1
        response = urllib.request.urlopen(url)
        coursbitcoin = json.loads(response.read().decode())
        updatedtime = coursbitcoin["time"]["updated"]
        courseuro = coursbitcoin["bpi"]["EUR"]["rate_float"]
        print("date: ", updatedtime)
        print("cours actuelle en euro: ",courseuro)

        a = json.dumps(
            {"op": "cours", "cours_bitcoin": courseuro, "updated_time": updatedtime, "id":producerid}).encode('utf-8')
        print(a)
        #print(producerid.to_bytes(producerid, byteorder='big'))
        #key = producerid.to_bytes(producerid, byteorder='big'),
        producer.send(topic="topcours",  value=a)
        #
        time.sleep(1)
        #time.sleep(20)






if __name__ == "__main__":
    main()
