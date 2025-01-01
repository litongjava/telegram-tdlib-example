package com.litongjava.telegram.utils;

public class ChatUtils {

  public static int toInt(String arg) {
    int result = 0;
    try {
      result = Integer.parseInt(arg);
    } catch (NumberFormatException ignored) {
    }
    return result;
  }

  public static long getChatId(String arg) {
    long chatId = 0;
    try {
      chatId = Long.parseLong(arg);
    } catch (NumberFormatException ignored) {
    }
    return chatId;
  }
}
