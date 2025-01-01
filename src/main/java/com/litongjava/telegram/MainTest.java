package com.litongjava.telegram;

import org.drinkless.tdlib.TdApi;

import com.litongjava.telegram.utils.ChatUtils;

public class MainTest {
  public static void main(String[] args) {
    TelegramMobileClient.start(null);
    // 主循环
    while (!TelegramMobileClientCan.needQuit) {
      // 等待授权
      TelegramMobileClientCan.authorizationLock.lock();
      try {
        while (!TelegramMobileClientCan.haveAuthorization) {
          try {
            TelegramMobileClientCan.gotAuthorization.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } finally {
        TelegramMobileClientCan.authorizationLock.unlock();
      }

      while (TelegramMobileClientCan.haveAuthorization) {
        getCommand();
      }
    }
    while (!TelegramMobileClientCan.canQuit) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static void getCommand() {
    String command = PromptConsole.promptString(TelegramMobileClientCan.commandsLine);
    String[] commands = command.split(" ", 2);
    try {
      switch (commands[0]) {
      case "gcs": {
        int limit = 20;
        if (commands.length > 1) {
          limit = ChatUtils.toInt(commands[1]);
        }
        ChatCan.getMainChatList(limit);
        break;
      }
      case "gc":
        TdClient.client.send(new TdApi.GetChat(ChatUtils.getChatId(commands[1])), TelegramMobileClientCan.defaultHandler);
        break;
      case "me":
        TdClient.client.send(new TdApi.GetMe(), TelegramMobileClientCan.defaultHandler);
        break;
      case "sm": {
        String[] args = commands[1].split(" ", 2);
        sendMessage(ChatUtils.getChatId(args[0]), args[1]);
        break;
      }
      case "lo":
        TelegramMobileClientCan.haveAuthorization = false;
        TdClient.client.send(new TdApi.LogOut(), TelegramMobileClientCan.defaultHandler);
        break;
      case "q":
        TelegramMobileClientCan.needQuit = true;
        TelegramMobileClientCan.haveAuthorization = false;
        TdClient.client.send(new TdApi.Close(), TelegramMobileClientCan.defaultHandler);
        break;
      default:
        System.err.println("Unsupported command: " + command);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      Printer.print("Not enough arguments");
    }
  }

  private static void sendMessage(long chatId, String message) {
    // initialize reply markup just for testing
    TdApi.InlineKeyboardButton[] row = { new TdApi.InlineKeyboardButton("https://telegram.org?1", new TdApi.InlineKeyboardButtonTypeUrl()),
        new TdApi.InlineKeyboardButton("https://telegram.org?2", new TdApi.InlineKeyboardButtonTypeUrl()),
        new TdApi.InlineKeyboardButton("https://telegram.org?3", new TdApi.InlineKeyboardButtonTypeUrl()) };
    TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][] { row, row, row });

    TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), null, true);
    TdClient.client.send(new TdApi.SendMessage(chatId, 0, null, null, replyMarkup, content), TelegramMobileClientCan.defaultHandler);
  }
}
