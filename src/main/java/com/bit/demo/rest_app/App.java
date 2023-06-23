package com.bit.demo.rest_app;

import com.bit.rest.RestClientBitcom;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("require arguments: <host> <api-key> <secret-key>");
    }
    var bitClient = new RestClientBitcom(args[0], args[1], args[2]);

    try {
      RunRestApi(bitClient);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void RunRestApi(RestClientBitcom bitClient)
      throws InterruptedException {

    bitClient.queryAccountMode(null);

    Thread.sleep(1000);

    bitClient.queryUmAccount(null);

    Thread.sleep(1000);

    bitClient.linearNewOrder(new HashMap<String, Object>() {
      {
        put("instrument_id", "ETH-USD-PERPETUAL");
        put("price", "1800");
        put("qty", "30");
        put("side", "buy");
        put("post_only", true);
      }
    });

    Thread.sleep(1000);

    // linear batch order
    var orderReqList = new ArrayList<>();
    orderReqList.add(new HashMap<String, Object>() {
      {
        put("instrument_id", "BTC-USD-PERPETUAL");
        put("price", "21000");
        put("qty", "0.1");
        put("side", "sell");
      }
    });
    orderReqList.add(new HashMap<String, Object>() {
      {
        put("instrument_id", "ETH-USD-PERPETUAL");
        put("price", "18000");
        put("qty", "3");
        put("side", "buy");
        put("post_only", true);
      }
    });
    var params = new HashMap<String, Object>() {
      {
        put("currency", "USD");
        put("orders_data", orderReqList);
      }
    };
    bitClient.linearNewBatchNewOrders(params);

    Thread.sleep(1000);

    bitClient.linearQueryOpenOrders(new HashMap<>() {
      { put("currency", "USD"); }
    });

    Thread.sleep(1000);

        bitClient.linearQueryUserTrades(new HashMap<>() {
      { put("currency", "USD"); }
    });


  }
}
