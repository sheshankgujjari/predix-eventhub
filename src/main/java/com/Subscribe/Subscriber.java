package com.Subscribe;

import com.Utility.Util;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.Message;
import com.ge.predix.eventhub.client.Client;
import org.springframework.stereotype.Component;

import java.util.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Subscriber {
    private static Client eventHub;

    public static void main(String[] args) throws EventHubClientException {
        eventHub = Util.createClient();
        run();

    }

    public static void run() {

        Client.SubscribeCallback subcribeCallBack = new Client.SubscribeCallback() {
            private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
            private List<String> errors = Collections.synchronizedList(new ArrayList<String>());
            private AtomicInteger errorCount = new AtomicInteger();

            public void onMessage(Message message) {
                messages.add(message);
            }

            public void onFailure(Throwable throwable) {
                errors.add(throwable.getMessage());
                errorCount.incrementAndGet();
            }

            public List<Message> getMessage() {
                return messages;
            }

            public List<String> getError() {
                return errors;
            }

            public int getErrorCount() {
                return errorCount.get();
            }
        };
        eventHub.subscribe(subcribeCallBack);
    }

}
