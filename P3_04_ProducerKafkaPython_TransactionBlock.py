#! /usr/bin/env python

# Connect to a websocket powered by blockchain.info and print events in
# the terminal in real time.

import json
import time
import websocket  # install this with the following command: pip install websocket-client
import requests
from bs4 import BeautifulSoup

# Run `pip install kafka-python` to install this package
from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers="localhost:9092")


def main():
    ws = open_websocket_to_blockchain()

    last_ping_time = time.time()

    while True:
        # Receive event
        data = json.loads(ws.recv())
        #print("------------------", data)

        # envoyer dans producer
        # producer.send("topbitcoin", json.dumps(data).encode('utf-8'))

        # We ping the server every 10s to show we are alive
        #if time.time() - last_ping_time >= 10:
            #ws.send(json.dumps({"op": "ping"}))
            #last_ping_time = time.time()

        # Response to "ping" events
        if data["op"] == "pong":
            pass

        # New unconfirmed transactions
        elif data["op"] == "utx":
            transaction_timestamp = data["x"]["time"]
            transaction_hash = data['x']['hash']  # this uniquely identifies the transaction
            transaction_total_amount = 0

            for recipient in data["x"]["out"]:
                # Every transaction may in fact have multiple recipients
                # Note that the total amount is in hundredth of microbitcoin; you need to
                # divide by 10**8 to obtain the value in bitcoins.
                transaction_total_amount += recipient["value"] / 100000000.

            # envoyer dans producer
            a= json.dumps({"op": "utx" ,"timestamp": transaction_timestamp,"hash": transaction_hash,
                           "transaction_total_amount": transaction_total_amount}).encode('utf-8')
            print(a)
            producer.send(topic="toptransaction", key=transaction_hash.encode(), value=a)
            producer.flush()
            time.sleep(1)
        #topbitcoin
        # New block
        elif data["op"] == "block":
            block_hash = data['x']['hash']
            block_timestamp = data["x"]["time"]
            block_found_by = data["x"]["foundBy"]["description"]
            block_reward = 12.5  # blocks mined in 2016 have an associated reward of 12.5 BTC
            url = 'https://www.blockchain.com/btc/block/' + block_hash
            r = requests.get(url)
            responseText = r.text
            soup = BeautifulSoup(responseText, 'html.parser')
            soup.prettify()
            i=0
            j=0
            for div in soup.find_all('div'):
                i += 1
                content = div.get_text()
                if (content == "Miner" or content == "miner"):
                    #print("********************************")
                    #print(i)
                    break
            for divSecond in soup.find_all('div'):
                j += 1
                if j == (i + 2):
                    if (divSecond.get_text() != "Miner" or divSecond.get_text() != 'miner'):
                        minerName = divSecond.get_text()
                        #print("||||||||||||||||||||||||||||")
                        #print(minerName)
                    else:
                        continue
            block_found_by = minerName


            b = json.dumps(
                {"op": "block", "timestamp": block_timestamp, "hash": block_hash,
                 "block_found_by": block_found_by, "block_reward":block_reward}).encode('utf-8')
            print(b)
            # envoyer dans producer
            producer.send(topic="topblock", key=block_hash.encode(),value=b)
            time.sleep(1)


        # This really should never happen
        else:
            print(---------"Unknown op: {}".format(data["op"]))

            # envoyer dans producer
            #producer.send("topbitcoin", json.dumps(
            #    {"op": "Unknown op", "data": data.encode('utf-8')}))
            pass


def open_websocket_to_blockchain():
    # Open a websocket
    ws = websocket.WebSocket()
    ws.connect("wss://ws.blockchain.info/inv")
    # Register to unconfirmed transaction events
    ws.send(json.dumps({"op": "unconfirmed_sub"}))
    # Register to block creation events
    ws.send(json.dumps({"op": "blocks_sub"}))

    return ws


if __name__ == "__main__":
    main()
