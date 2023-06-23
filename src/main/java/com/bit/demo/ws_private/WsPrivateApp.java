package com.bit.demo.ws_private;

import com.bit.rest.RestClientBitcom;
import com.bit.utils.Common;
import com.bit.ws.WsClientBitcom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class WsPrivateApp {
  public static String wsHost = "wss://betaws.bitexch.dev";

  private static String currentWsToken;

  public static String makeUmAccountEventReq(String token) {
    var req = new HashMap<String, Object>() {
      {
        put("type", "subscribe");
        put("channels", List.of("um_account"));
        put("interval", "100ms");
        put("token", token);
      }
    };
    return Common.objectToJson(req);
  }

  public static String makeOrderEventReq(String token) {
    var req = new HashMap<String, Object>() {
      {
        put("type", "subscribe");
        put("channels", List.of("order"));
        put("pair", List.of("BTC-USD", "ETH-USD"));
        put("categories", List.of("future", "option"));
        put("interval", "raw");
        put("token", token);
      }
    };
    return Common.objectToJson(req);
  }

  public static String makeUserTradeEventReq(String token) {
    var req = new HashMap<String, Object>() {
      {
        put("type", "subscribe");
        put("channels", List.of("user_trade"));
        put("pair", List.of("BTC-USD", "ETH-USD"));
        put("categories", List.of("future", "option"));
        put("interval", "100ms");
        put("token", token);
      }
    };
    return Common.objectToJson(req);
  }

  public static void onOpen(WsClientBitcom wsClient) {
    var reqJson = makeUmAccountEventReq(currentWsToken);
    System.out.println("sending ws request: " + reqJson);
    wsClient.send(reqJson);
  }

  public static void onMsg(WsClientBitcom wsClient, String msg) {
    System.out.println(msg);
  }

  public static void main(String[] args)
      throws URISyntaxException, InterruptedException, JsonMappingException,
             JsonProcessingException {
    if (args.length != 3) {
      System.out.println("require arguments: <host> <api-key> <secret-key>");
    }
    var bitClient = new RestClientBitcom(args[0], args[1], args[2]);
    var authRespText = bitClient.wsAuth(null);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(authRespText);
    currentWsToken = root.path("data").path("token").asText();

    WsClientBitcom wsClient = new WsClientBitcom(
        new URI(wsHost), WsPrivateApp::onOpen, WsPrivateApp::onMsg);
    wsClient.connect();

    Thread.currentThread().join();
  }
}
