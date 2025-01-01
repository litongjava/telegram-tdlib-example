package com.litongjava.telegram;

public class Printer {

  public static void print(String str) {
    if (TelegramMobileClientCan.currentPrompt != null) {
      System.out.println("");
    }
    System.out.println(str);
    if (TelegramMobileClientCan.currentPrompt != null) {
      System.out.print(TelegramMobileClientCan.currentPrompt);
    }
  }
}
