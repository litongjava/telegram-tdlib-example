package com.litongjava.telegram.handler;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultHandler implements Client.ResultHandler {
  @Override
  public void onResult(TdApi.Object object) {
    log.info(object.toString());
  }
}