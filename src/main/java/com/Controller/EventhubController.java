package com.Controller;

import com.Utility.Util;
import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.AckStatus;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.Message;
import com.ge.predix.eventhub.client.Client;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@RestController
@EnableAutoConfiguration
public class EventhubController {
  private static final Logger logger = Logger.getLogger(EventhubController.class.getName());
  public Client eventHub;

  @PostConstruct
  public void makeClient() throws EventHubClientException {
    eventHub = Util.createClient();
  }


  @RequestMapping("/")
  String home() {
    return "Hello Sheshank POC Event Hub!";
  }

  @RequestMapping(value = "/publish", method = RequestMethod.POST)
  ResponseEntity<?> publish(@RequestBody String input, @RequestParam(value = "id", required = false) String id) throws EventHubClientException {
    id = id != null ? id : String.valueOf(new Random().nextInt(100));

    List<Ack> acks = eventHub.addMessage(id, input, null).flush();

    String response = "";
    HttpStatus status;
    if (acks.size() == 1 && id.equals(acks.get(0).getId())) {
      response += "Received ack with id: " + acks.get(0).getId() + " and message with body: " + input;
      status = HttpStatus.ACCEPTED;

      if (!acks.get(0).getStatusCode().equals(AckStatus.ACCEPTED)){
        response += " Received unexpected status code. Expected status was" +
                AckStatus.ACCEPTED + " but got " + acks.get(0).getStatusCode() + " instead";

      }
    } else {
      response = "Did not receive ack for message sent.";
      if (!acks.isEmpty()) {
        response += "Id was " + acks.get(0).getId() + " but should have been " + id;
      }
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
      logger.warning(response);
    } else {
      logger.info(response);
    }

    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<String>(response, responseHeaders, status);
  }

  @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
  ResponseEntity<?> subscribe(@RequestParam(value = "timeout", required = true) long timeout) throws EventHubClientException {
    String response = "";

    class SubCallback implements Client.SubscribeCallback {
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
    }

    SubCallback callback = new SubCallback();
    eventHub.subscribe(callback);
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    eventHub.unsubscribe();

    response += "Total messages received: " + callback.getMessage().size() + " \n";
    response += "Total errors received: " + callback.getErrorCount() + " \n";

    Iterator<Message> msgIterator = callback.getMessage().iterator();
    while (msgIterator.hasNext()) {
      Message message = msgIterator.next();
      response += "Id: " + message.getId() + "\t\t\t Body: " + message.getBody().toStringUtf8() + "\n";
    }

    Iterator<String> errIterator = callback.getError().iterator();
    while (errIterator.hasNext()) {
      String error = errIterator.next();
      response += "Error: " + error + "\n";
    }

    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<String>(response, responseHeaders, HttpStatus.ACCEPTED);
  }

}

