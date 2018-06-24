package com.Utility;

import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.PublishSyncConfiguration;
import com.ge.predix.eventhub.configuration.SubscribeConfiguration;
import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClientImpl;

import javax.json.Json;
import java.util.logging.Logger;

public class Util {

    private static final Logger logger = Logger.getLogger(Util.class.getName());
    public static Client eventHub;

    public static EventHubConfiguration setEventHubConfiguration() throws EventHubClientException {
        EventHubConfiguration configuration = new EventHubConfiguration.Builder()
                .host("event-hub-aws-usw02.data-services.predix.io")
                .port(443)
                .zoneID("89a86586-dafb-4cff-9c70-60d9fe4aa209")
                .clientID("change client id")
                .clientSecret("change client secret")
                .authURL("https://4d10605b-c10f-46km-bfo0-a73c03cc927d.predix-uaa.run.aws-usw02-pr.ice.predix.io")
                .subscribeConfiguration(new SubscribeConfiguration.Builder()
                        .subscriberName("sample-app").subscriberInstance("some-id").build())
                .publishConfiguration(new PublishSyncConfiguration.Builder()
                        .maxAddedMessages(20000).timeout(2000)
                        .build())
                .build();
        return configuration;
    }

    public static byte[] createJsonMessage() {
        return Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("id", "123")
                        .add("body", "test1")
                        .add("tags", "sheshank-tag")
                        .build())
                .add(Json.createObjectBuilder()
                        .add("id", "234")
                        .add("body", "test2")
                        .add("tags", "sheshank-tag")
                        .build())
             .build().toString().getBytes();
    }

    public static Client explicitConfiguration() throws EventHubClientException {
        try {
            EventHubConfiguration configuration = setEventHubConfiguration();
            eventHub = new Client(configuration);
            eventHub.forceRenewToken();
            return eventHub;
        } catch (EventHubClientException.InvalidConfigurationException e){
            logger.info(e.getMessage());
            System.out.println("Could not create client");
            throw e;
        }
    }

    public static Client implicitConfiguration() throws EventHubClientException {
        try {
            EventHubConfiguration configuration = new EventHubConfiguration.Builder()
                    .fromEnvironmentVariables()
                    .publishConfiguration(
                            new PublishSyncConfiguration.Builder()
                                    .timeout(2000)
                                    .build())
                    .subscribeConfiguration(
                            new SubscribeConfiguration.Builder()
                                    .subscriberName("sample-app")
                                    .subscriberInstance("some-id")
                                    .build())
                    .build();

            eventHub = new Client(configuration);
            eventHub.forceRenewToken();
            return eventHub;
        } catch (EventHubClientException.InvalidConfigurationException e){
            logger.info(e.getMessage());
            System.out.println("Could not create client");
            throw e;
        }
    }

    public static Client createClient() throws EventHubClientException {

        if(System.getenv("sheshank-event-hub") != null && System.getenv("sheshank-uaa") != null) {
            eventHub = implicitConfiguration();
            return eventHub;
        } else {
            eventHub = Util.explicitConfiguration();
            return eventHub;
        }
    }

    public static RestClientImpl createRestClient()  {

        RestClientImpl restClient = new RestClientImpl();
        DefaultOauthRestConfig config =new DefaultOauthRestConfig();
        config.setOauthClientId("clientId:clientsecret");
        config.setOauthIssuerId("https://4d10605b-c10f-467c-bfea-a73c03cc927d.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token");

        restClient.overrideRestConfig(config);
        return restClient;
    }
}
