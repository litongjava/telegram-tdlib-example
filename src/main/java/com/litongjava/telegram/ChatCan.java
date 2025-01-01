package com.litongjava.telegram;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class ChatCan {
  public static void getMainChatList(final int limit) {
    synchronized (TelegramMobileClientCan.mainChatList) {
      if (!TelegramMobileClientCan.haveFullMainChatList && limit > TelegramMobileClientCan.mainChatList.size()) {
        // send LoadChats request if there are some unknown chats and have not enough known chats
        TdClient.client.send(new TdApi.LoadChats(new TdApi.ChatListMain(), limit - TelegramMobileClientCan.mainChatList.size()), new Client.ResultHandler() {
          @Override
          public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
            case TdApi.Error.CONSTRUCTOR:
              if (((TdApi.Error) object).code == 404) {
                synchronized (TelegramMobileClientCan.mainChatList) {
                  TelegramMobileClientCan.haveFullMainChatList = true;
                }
              } else {
                System.err.println("Receive an error for LoadChats:" + TelegramMobileClientCan.newLine + object);
              }
              break;
            case TdApi.Ok.CONSTRUCTOR:
              // chats had already been received through updates, let's retry request
              getMainChatList(limit);
              break;
            default:
              System.err.println("Receive wrong response from TDLib:" + TelegramMobileClientCan.newLine + object);
            }
          }
        });
        return;
      }

      java.util.Iterator<OrderedChat> iter = TelegramMobileClientCan.mainChatList.iterator();
      System.out.println();
      System.out.println("First " + limit + " chat(s) out of " + TelegramMobileClientCan.mainChatList.size() + " known chat(s):");
      for (int i = 0; i < limit && i < TelegramMobileClientCan.mainChatList.size(); i++) {
        long chatId = iter.next().chatId;
        TdApi.Chat chat = TelegramMobileClientCan.chats.get(chatId);
        synchronized (chat) {
          System.out.println(chatId + ": " + chat.title);
        }
      }
      Printer.print("");
    }
  }


}
