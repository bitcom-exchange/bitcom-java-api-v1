# bit.com Java Api demo

## version

java 17

## Run rest api demo

```bash
mvnd package

# testnet host: https://betaapi.bitexch.dev
# api-key and secret-key can be obtained from bit.com website
java -cp target/bit-java-api-demo-jar-with-dependencies.jar com.bit.demo.rest_app.App <host> <api-key> <secret-key>

```