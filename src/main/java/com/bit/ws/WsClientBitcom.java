package com.bit.ws;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WsClientBitcom extends WebSocketClient {
  private Consumer<WsClientBitcom> handleOpen;
  private BiConsumer<WsClientBitcom, String> handleMsg;

  public WsClientBitcom(URI serverUri, Consumer<WsClientBitcom> handleOpen,
                        BiConsumer<WsClientBitcom, String> handleMsg) {
    super(serverUri);
    this.handleOpen = handleOpen;
    this.handleMsg = handleMsg;
  }

  @Override
  public void onClose(int arg0, String arg1, boolean arg2) {
    System.out.println("WsClientBitcom.onClose");
  }

  @Override
  public void onError(Exception arg0) {
    System.out.println("WsClientBitcom.onError");
  }

  @Override
  public void onMessage(String arg0) {
    this.handleMsg.accept(this, arg0);
  }

  @Override
  public void onOpen(ServerHandshake arg0) {
    this.handleOpen.accept(this);
  }
}
