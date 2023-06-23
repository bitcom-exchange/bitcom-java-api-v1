# bit.com Java Api demo


### API request/response format
https://www.bit.com/docs/en-us/spot.html#order

### Guidelines for account mode
https://www.bit.com/docs/en-us/spot.html#guidelines-for-account-mode

### API Host:
https://www.bit.com/docs/en-us/spot.html#spot-api-hosts-production

## Java version

java 17

## Run rest api demo


* testnet host: https://betaapi.bitexch.dev
* api-key and secret-key can be obtained from bit.com website


```bash
mvnd package

java -cp target/bit-java-api-demo-jar-with-dependencies.jar com.bit.demo.rest_app.App <host> <api-key> <secret-key>

```


## Run public ws demo

```bash
java -cp target/bit-java-api-demo-jar-with-dependencies.jar com.bit.demo.ws_public.WsPublicApp
```

## Run private ws demo

```bash
java -cp target/bit-java-api-demo-jar-with-dependencies.jar com.bit.demo.ws_private.WsPrivateApp <host> <api-key> <secret-key>
```