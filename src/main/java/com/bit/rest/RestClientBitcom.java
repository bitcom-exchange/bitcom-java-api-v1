package com.bit.rest;

import static com.bit.utils.RestApiUtils.*;

import com.bit.utils.Common;
import com.bit.utils.EncryptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import okhttp3.OkHttpClient;

public class RestClientBitcom {

  // ws auth
  public static String V1_WS_AUTH = "/v1/ws/auth";

  // SPOT
  public static String V1_SPOT_INSTRUMENTS = "/spot/v1/instruments";
  public static String V1_SPOT_ACCOUNTS = "/spot/v1/accounts";
  public static String V1_SPOT_ORDERS = "/spot/v1/orders";
  public static String V1_SPOT_CANCEL_ORDERS = "/spot/v1/cancel_orders";
  public static String V1_SPOT_OPENORDERS = "/spot/v1/open_orders";
  public static String V1_SPOT_USER_TRADES = "/spot/v1/user/trades";
  public static String V1_SPOT_AMEND_ORDERS = "/spot/v1/amend_orders";
  public static String V1_SPOT_TRANSACTION_LOGS = "/spot/v1/transactions";
  public static String V1_SPOT_WS_AUTH = "/spot/v1/ws/auth";
  public static String V1_SPOT_BATCH_ORDERS = "/spot/v1/batchorders";
  public static String V1_SPOT_AMEND_BATCH_ORDERS =
      "/spot/v1/amend_batchorders";
  public static String V1_SPOT_MMP_STATE = "/spot/v1/mmp_state";
  public static String V1_SPOT_MMP_UPDATE_CONFIG = "/spot/v1/update_mmp_config";
  public static String V1_SPOT_RESET_MMP = "/spot/v1/reset_mmp";
  public static String V1_SPOT_ACCOUNT_CONFIGS_COD =
      "/spot/v1/account_configs/cod";
  public static String V1_SPOT_ACCOUNT_CONFIGS = "/spot/v1/account_configs";
  public static String V1_SPOT_AGG_TRADES = "/spot/v1/aggregated/trades";

  // UM
  public static String V1_UM_ACCOUNT_MODE = "/um/v1/account_mode";
  public static String V1_UM_ACCOUNTS = "/um/v1/accounts";
  public static String V1_UM_TRANSACTIONS = "/um/v1/transactions";
  public static String V1_UM_INTEREST_RECORDS = "/um/v1/interest_records";

  // LINEAR;
  public static String V1_LINEAR_POSITIONS = "/linear/v1/positions";
  public static String V1_LINEAR_ORDERS = "/linear/v1/orders";
  public static String V1_LINEAR_CANCEL_ORDERS = "/linear/v1/cancel_orders";
  public static String V1_LINEAR_OPENORDERS = "/linear/v1/open_orders";
  public static String V1_LINEAR_USER_TRADES = "/linear/v1/user/trades";
  public static String V1_LINEAR_AMEND_ORDERS = "/linear/v1/amend_orders";
  public static String V1_LINEAR_EST_MARGINS = "/linear/v1/margins";
  public static String V1_LINEAR_CLOSE_POS = "/linear/v1/close_positions";
  public static String V1_LINEAR_BATCH_ORDERS = "/linear/v1/batchorders";
  public static String V1_LINEAR_AMEND_BATCH_ORDERS =
      "/linear/v1/amend_batchorders";
  public static String V1_LINEAR_BLOCK_TRADES = "/linear/v1/blocktrades";
  public static String V1_LINEAR_USER_INFO = "/linear/v1/user/info";
  public static String V1_LINEAR_PLATFORM_BLOCK_TRADES =
      "/linear/v1/platform_blocktrades";
  public static String V1_LINEAR_ACCOUNT_CONFIGS = "/linear/v1/account_configs";
  public static String V1_LINEAR_LEVERAGE_RATIO = "/linear/v1/leverage_ratio";
  public static String V1_LINEAR_AGG_POSITIONS =
      "/linear/v1/aggregated/positions";
  public static String V1_LINEAR_MMP_STATE = "/linear/v1/mmp_state";
  public static String V1_LINEAR_MMP_UPDATE_CONFIG =
      "/linear/v1/update_mmp_config";
  public static String V1_LINEAR_RESET_MMP = "/linear/v1/reset_mmp";

  private static final ObjectMapper mapper = new ObjectMapper();

  private final OkHttpClient client = new OkHttpClient();
  private String baseUrl = "";
  private String apiKey = "";
  private String secretKey = "";

  public RestClientBitcom(String baseUrl, String apiKey, String secretKey) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.secretKey = secretKey;
  }

  public static String encodeList(ArrayList itemList) {
    var strList = new ArrayList<String>();
    for (var item : itemList) {
      var value = encodeObject((HashMap<String, Object>)item);
      strList.add(value);
    }
    return "[" + String.join("&", strList) + "]";
  }

  public static String encodeObject(HashMap<String, Object> params) {
    ArrayList<String> sortedKeys = new ArrayList<>(params.keySet());
    Collections.sort(sortedKeys);
    var valueList = new ArrayList<String>();
    for (var key : sortedKeys) {
      var obj = params.get(key);
      if (obj instanceof ArrayList) {
        var listVal = encodeList((ArrayList)obj);
        valueList.add(String.format("%s=%s", key, listVal));
      } else if (obj instanceof HashMap) {
        var mapVal = encodeObject((HashMap<String, Object>)obj);
        valueList.add(String.format("%s=%s", key, mapVal));
      } else {
        var value = String.valueOf(obj);
        valueList.add(String.format("%s=%s", key, value));
      }
    }
    Collections.sort(valueList);
    var output = String.join("&", valueList);
    return output;
  }

  public static String calcBitcomSignature(String secretKey, String apiPath,
                                           HashMap<String, Object> params) {
    String strToSign = apiPath + "&" + encodeObject(params);
    var sig = EncryptUtils.calcHmacWithBase64(secretKey, strToSign);
    System.out.println("strToSign = " + strToSign + ", signature = " + sig);
    return sig;
  }

  public String callApiBitcom(String method, String reqPath,
                              HashMap<String, Object> params) {

    if (params == null) {
      params = new HashMap<>();
    }

    var nonce = System.currentTimeMillis();
    params.put("timestamp", nonce);
    var sig = calcBitcomSignature(this.secretKey, reqPath, params);
    params.put("signature", sig);

    var headers = new HashMap<String, String>() {
      { put("X-Bit-Access-Key", apiKey); }
    };

    try {
      var url = this.baseUrl + reqPath;
      var resp = httpRequest(this.client, method, url, params, headers);
      var content = resp.body().string();
      System.out.printf("method=%s\n header=%s\n url=%s\n params=%s, resp=%s\n",
                        method, Common.objectToPrettyJson(headers), url,
                        Common.objectToPrettyJson(params),
                        Common.formatAsJson(content));
      return content;
    } catch (IOException e) {
      // TODO: send alarm
      e.printStackTrace();
      return e.getMessage();
    }
  }

  ////////////////////////////////////////////////////
  // rest api endpoints
  ////////////////////////////////////////////////////
  public String wsAuth(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_WS_AUTH, params);
  }

  ////////////////////////////////////////////////////
  // UM
  ////////////////////////////////////////////////////
  public String queryAccountMode(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_UM_ACCOUNT_MODE, params);
  }

  public String queryUmAccount(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_UM_ACCOUNTS, params);
  }

  public String queryUmTxLogs(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_UM_TRANSACTIONS, params);
  }

  ////////////////////////////////////////////////////
  // SPOT
  ////////////////////////////////////////////////////
  public String spotQueryAccountConfigs(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_ACCOUNT_CONFIGS, params);
  }

  public String spotWsAuth(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_WS_AUTH, params);
  }

  public String spotQueryClassicAccount(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_ACCOUNTS, params);
  }

  public String spotQueryClassicTxLogs(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_TRANSACTION_LOGS, params);
  }

  public String spotQueryOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_ORDERS, params);
  }

  public String spotQueryOpenOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_OPENORDERS, params);
  }

  public String spotQueryUserTrades(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_USER_TRADES, params);
  }

  public String spotNewOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_ORDERS, params);
  }

  public String spotAmendOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_AMEND_ORDERS, params);
  }

  public String spotCancelOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_CANCEL_ORDERS, params);
  }

  public String spotNewBatchNewOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_BATCH_ORDERS, params);
  }

  public String spotAmendBatchNewOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_AMEND_BATCH_ORDERS, params);
  }

  public String spotEnableCod(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_ACCOUNT_CONFIGS_COD, params);
  }

  public String spotQueryMmpState(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_SPOT_MMP_STATE, params);
  }

  public String spotUpdateMmpConfig(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_MMP_UPDATE_CONFIG, params);
  }

  public String spotResetMmpConfig(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_SPOT_RESET_MMP, params);
  }

  ////////////////////////////////////////////////////
  // LINEAR
  ////////////////////////////////////////////////////

  public String linearQueryAccountConfigs(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_LINEAR_ACCOUNT_CONFIGS, params);
  }

  /* linearQueryOrders
   * params = new HashMap<String, Object>() {
                           {
                             put("currency", "USD");
                             put("limit", 10);
                           }
                         };
   */
  public String linearQueryOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_LINEAR_ORDERS, params);
  }

  public String linearQueryOpenOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_LINEAR_OPENORDERS, params);
  }

  public String linearQueryUserTrades(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_LINEAR_USER_TRADES, params);
  }

  /* linearNewOrder
   var params = new HashMap<String, Object>() {
                           {
                             put("instrument_id", "BTC-PERPETUAL");
                             put("price", "39000");
                             put("qty", "1200");
                             put("side", "buy");
                             put("post_only", true);
                           }
                         };
   */
  public String linearNewOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_ORDERS, params);
  }

  public String linearAmendOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_AMEND_ORDERS, params);
  }

  public String linearCancelOrder(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_CANCEL_ORDERS, params);
  }

  /* linearNewBatchNewOrder
   var orderReqList = new ArrayList<>();
    orderReqList.add(new HashMap<String, Object>() {
      {
        put("instrument_id", "BTC-PERPETUAL");
        put("price", "38000");
        put("qty", "1500");
        put("side", "buy");
        put("post_only", true);
      }
    });
    orderReqList.add(new HashMap<String, Object>() {
      {
        put("instrument_id", "BTC-PERPETUAL");
        put("price", "39000");
        put("qty", "2000");
        put("side", "buy");
        put("post_only", true);
      }
    });
    var params = new HashMap<String, Object>() {
                           {
                             put("currency", "BTC");
                             put("orders_data", orderReqList);
                           }
                         };
   */
  public String linearNewBatchNewOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_BATCH_ORDERS, params);
  }

  public String linearAmendBatchNewOrders(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_AMEND_BATCH_ORDERS,
                         params);
  }

  public String linearQueryMmpState(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_GET, V1_LINEAR_MMP_STATE, params);
  }

  public String linearUpdateMmpConfig(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_MMP_UPDATE_CONFIG, params);
  }

  public String linearResetMmpConfig(HashMap<String, Object> params) {
    return callApiBitcom(HTTP_METHOD_POST, V1_LINEAR_RESET_MMP, params);
  }
}
