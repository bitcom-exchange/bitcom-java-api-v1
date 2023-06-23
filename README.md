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

```bash
mvnd package

# testnet host: https://betaapi.bitexch.dev
# api-key and secret-key can be obtained from bit.com website
java -cp target/bit-java-api-demo-jar-with-dependencies.jar com.bit.demo.rest_app.App <host> <api-key> <secret-key>

```