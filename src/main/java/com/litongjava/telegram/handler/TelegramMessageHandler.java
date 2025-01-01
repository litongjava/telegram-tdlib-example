package com.litongjava.telegram.handler;

import org.drinkless.tdlib.TdApi.UpdateChatLastMessage;

public interface TelegramMessageHandler {
  public void consume(UpdateChatLastMessage updateChat);
}
