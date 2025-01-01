package com.litongjava.telegram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PromptConsole {

  public static String promptString(String prompt) {
    System.out.print(prompt);
    TelegramMobileClientCan.currentPrompt = prompt;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String str = "";
    try {
      str = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    TelegramMobileClientCan.currentPrompt = null;
    return str;
  }
}
