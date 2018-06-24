package com;

import com.Utility.Util;
import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.AckStatus;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.Message;

import java.util.List;

public class Main {

    public static void main(String [] args)  throws Exception {
        publish();
        subscribe();
    }

    public static void subscribe() throws EventHubClientException {
        Client eventHubClient  = Util.createClient();
        eventHubClient.subscribe(new MessageSubscribeCallback());
    }

    public static void publish() throws EventHubClientException {
        Client eventHub = Util.createClient();
        String id = "12345";
        String body = "{ \"data_value_here\": \"value1\", \"more_text\": \"value2\" }, \"zone\": \"89a86586-dafb-4cff-9c70-60d9fe4aa209\", \"tags\": { \"tag1\": \"tag_value_1\" } }";
        List<Ack> acks = eventHub.addMessage(id, body, null).flush();

        String response = "";
        if (acks.size() == 1 && id.equals(acks.get(0).getId())) {
            response += "Received acknowledgement with id: " + acks.get(0).getId() + " and message with body: " + body;

            if (!acks.get(0).getStatusCode().equals(AckStatus.ACCEPTED)){
                response += " Received unexpected status code. Expected status was" +
                        AckStatus.ACCEPTED + " but got " + acks.get(0).getStatusCode() + " instead";

            }
        } else {
            response = "Did not receive acknowledgement for message sent.";
            if(!acks.isEmpty()) {
                response += "Id was " + acks.get(0).getId() + " but should have been " + id;
            }
        }
    }

    static class MessageSubscribeCallback implements Client.SubscribeCallback {

        @Override
        public void onFailure(Throwable arg0) {
            System.out.println("Failure occurred");
        }

        @Override
        public void onMessage(Message msg) {
            try {
                System.out.println("Callback Message :" + msg.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

