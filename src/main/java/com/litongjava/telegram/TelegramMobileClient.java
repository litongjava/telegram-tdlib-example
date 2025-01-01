package com.litongjava.telegram;

import java.io.IOError;
import java.io.IOException;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import com.litongjava.telegram.handler.LogMessageHandler;
import com.litongjava.telegram.handler.TelegramMessageHandler;
import com.litongjava.telegram.handler.UpdateHandler;

/**
 * Example class for TDLib usage from Java.
 */
public final class TelegramMobileClient {

  public static void setChatPositions(TdApi.Chat chat, TdApi.ChatPosition[] positions) {
    synchronized (TelegramMobileClientCan.mainChatList) {
      synchronized (chat) {
        for (TdApi.ChatPosition position : chat.positions) {
          if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
            boolean isRemoved = TelegramMobileClientCan.mainChatList.remove(new OrderedChat(chat.id, position));
            assert isRemoved;
          }
        }

        chat.positions = positions;

        for (TdApi.ChatPosition position : chat.positions) {
          if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
            boolean isAdded = TelegramMobileClientCan.mainChatList.add(new OrderedChat(chat.id, position));
            assert isAdded;
          }
        }
      }
    }
  }

  public static void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
    if (authorizationState != null) {
      TelegramMobileClientCan.authorizationState = authorizationState;
    }
    switch (TelegramMobileClientCan.authorizationState.getConstructor()) {
    case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
      TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters();
      request.databaseDirectory = "tdlib";
      request.useMessageDatabase = true;
      request.useSecretChats = true;
      request.apiId = 22378785;
      request.apiHash = "f2dd9e59b2f40da8b9dd330306ca53c6";
      request.systemLanguageCode = "en";
      request.deviceModel = "Desktop";
      request.applicationVersion = "1.0";

      TdClient.client.send(request, new AuthorizationRequestHandler());
      break;
    case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
      String phoneNumber = PromptConsole.promptString("Please enter phone number: ");
      TdClient.client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
      String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) TelegramMobileClientCan.authorizationState).link;
      System.out.println("Please confirm this login link on another device: " + link);
      break;
    }
    case TdApi.AuthorizationStateWaitEmailAddress.CONSTRUCTOR: {
      String emailAddress = PromptConsole.promptString("Please enter email address: ");
      TdClient.client.send(new TdApi.SetAuthenticationEmailAddress(emailAddress), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateWaitEmailCode.CONSTRUCTOR: {
      String code = PromptConsole.promptString("Please enter email authentication code: ");
      TdClient.client.send(new TdApi.CheckAuthenticationEmailCode(new TdApi.EmailAddressAuthenticationCode(code)), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
      String code = PromptConsole.promptString("Please enter authentication code: ");
      TdClient.client.send(new TdApi.CheckAuthenticationCode(code), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR: {
      String firstName = PromptConsole.promptString("Please enter your first name: ");
      String lastName = PromptConsole.promptString("Please enter your last name: ");
      TdClient.client.send(new TdApi.RegisterUser(firstName, lastName, false), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
      String password = PromptConsole.promptString("Please enter password: ");
      TdClient.client.send(new TdApi.CheckAuthenticationPassword(password), new AuthorizationRequestHandler());
      break;
    }
    case TdApi.AuthorizationStateReady.CONSTRUCTOR:
      TelegramMobileClientCan.haveAuthorization = true;
      TelegramMobileClientCan.authorizationLock.lock();
      try {
        TelegramMobileClientCan.gotAuthorization.signal();
      } finally {
        TelegramMobileClientCan.authorizationLock.unlock();
      }
      break;
    case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
      TelegramMobileClientCan.haveAuthorization = false;
      Printer.print("Logging out");
      break;
    case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
      TelegramMobileClientCan.haveAuthorization = false;
      Printer.print("Closing");
      break;
    case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
      Printer.print("Closed");
      if (!TelegramMobileClientCan.needQuit) {
        TdClient.client = Client.create(new UpdateHandler(TelegramMobileClientCan.messageHandler), null, null); // recreate client after previous has closed
      } else {
        TelegramMobileClientCan.canQuit = true;
      }
      break;
    default:
      System.err.println("Unsupported authorization state:" + TelegramMobileClientCan.newLine + TelegramMobileClientCan.authorizationState);
    }
  }

  public static void start(TelegramMessageHandler messageHandler) {
    // set log message handler to handle only fatal errors (0) and plain log messages (-1)
    Client.setLogMessageHandler(0, new LogMessageHandler());

    // disable TDLib log and redirect fatal errors and plain log messages to a file
    try {
      Client.execute(new TdApi.SetLogVerbosityLevel(0));
      Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
    } catch (Client.ExecutionException error) {
      throw new IOError(new IOException("Write access to the current directory is required"));
    }

    // create client
    TelegramMobileClientCan.messageHandler = messageHandler;
    TdClient.client = Client.create(new UpdateHandler(messageHandler), null, null);
  }

  private static class AuthorizationRequestHandler implements Client.ResultHandler {
    @Override
    public void onResult(TdApi.Object object) {
      switch (object.getConstructor()) {
      case TdApi.Error.CONSTRUCTOR:
        System.err.println("Receive an error:" + TelegramMobileClientCan.newLine + object);
        onAuthorizationStateUpdated(null); // repeat last action
        break;
      case TdApi.Ok.CONSTRUCTOR:
        // result is already received through UpdateAuthorizationState, nothing to do
        break;
      default:
        System.err.println("Receive wrong response from TDLib:" + TelegramMobileClientCan.newLine + object);
      }
    }
  }

}
