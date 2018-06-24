##Change the below in Utility class
```
zoneID("89a86586-dafb-4cff-9c70-60d9fe4aa209")
clientID("change client id")
clientSecret("change client secret")
authURL("https://4d10605b-c10f-46km-bfo0-a73c03cc927d.predix-uaa.run.aws-usw02-pr.ice.predix.io")
```

##Change client id, secret and issuerId in createRestClientMethod
```

config.setOauthClientId("clientid:clientsecret");
config.setOauthIssuerId("https://4d10605b-c10f-46km-bfo0-a73c03cc927d.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token");
```

##Internal GE network should use the below repo in pom.xml
  ```
         <repository>
            <id>artifactory.snapshots</id>
            <name>artifactory.snapshots</name>
            <url>https://devcloud.swcoe.ge.com/artifactory/PREDIX-SNAPSHOT</url>
        </repository>
  ```
        
##External GE network should use the below repo in pom.xml
 ```
        <repository>
            <id>artifactory.external</id>
            <name>GE external repository</name>
            <url>https://artifactory.predix.io/artifactory/PREDIX-EXT</url>
        </repository>
        
        ```
        
##External GE network users should add username and password in mvn_settings.xml file
    ```        
            <username></username>
            <password></password>
    ```
            
            
#Change manifest.yml file

```
 env:
     EVENTHUB_URI: uri
     EVENTHUB_PORT: 443
     ZONE_ID: id
     AUTH_URL: url
     CLIENT_ID: clientid
     CLIENT_SECRET: clientsecret
            
 ```


