package com.bit.demo.ws_public;

import com.bit.utils.Common;
import com.bit.ws.WsClientBitcom;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class WsPublicApp {
  public static String wsHost = "wss://betaws.bitexch.dev";

  public static void onOpen(WsClientBitcom wsClient) {
    var req = new HashMap<String, Object>() {
      {
        put("type", "subscribe");
        put("instruments", List.of("BTC-USD-PERPETUAL"));
        put("channels", List.of("order_book.10.10"));
        put("interval", "raw");
      }
    };
    var reqJson = Common.objectToJson(req);
    System.out.println("sending ws request: " + reqJson);
    wsClient.send(reqJson);
  }

  public static void onMsg(WsClientBitcom wsClient, String msg) {
    System.out.println(msg);
  }

  public static void main(String[] args)
      throws URISyntaxException, InterruptedException {
    WsClientBitcom wsClient = new WsClientBitcom(
        new URI(wsHost), WsPublicApp::onOpen, WsPublicApp::onMsg);
    wsClient.connect();

    Thread.currentThread().join();
  }
}
