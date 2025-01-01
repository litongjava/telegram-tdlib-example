package com.litongjava.telegram.handler;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import com.litongjava.telegram.TelegramMobileClient;
import com.litongjava.telegram.TelegramMobileClientCan;

public class UpdateHandler implements Client.ResultHandler {

  private TelegramMessageHandler messageHandler;

  public UpdateHandler(TelegramMessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }

  @Override
  public void onResult(TdApi.Object object) {
    switch (object.getConstructor()) {
    case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
      TelegramMobileClient.onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
      break;

    case TdApi.UpdateUser.CONSTRUCTOR:
      TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
      TelegramMobileClientCan.users.put(updateUser.user.id, updateUser.user);
      break;
    case TdApi.UpdateUserStatus.CONSTRUCTOR: {
      TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) object;
      TdApi.User user = TelegramMobileClientCan.users.get(updateUserStatus.userId);
      synchronized (user) {
        user.status = updateUserStatus.status;
      }
      break;
    }
    case TdApi.UpdateBasicGroup.CONSTRUCTOR:
      TdApi.UpdateBasicGroup updateBasicGroup = (TdApi.UpdateBasicGroup) object;
      TelegramMobileClientCan.basicGroups.put(updateBasicGroup.basicGroup.id, updateBasicGroup.basicGroup);
      break;
    case TdApi.UpdateSupergroup.CONSTRUCTOR:
      TdApi.UpdateSupergroup updateSupergroup = (TdApi.UpdateSupergroup) object;
      TelegramMobileClientCan.supergroups.put(updateSupergroup.supergroup.id, updateSupergroup.supergroup);
      break;
    case TdApi.UpdateSecretChat.CONSTRUCTOR:
      TdApi.UpdateSecretChat updateSecretChat = (TdApi.UpdateSecretChat) object;
      TelegramMobileClientCan.secretChats.put(updateSecretChat.secretChat.id, updateSecretChat.secretChat);
      break;

    case TdApi.UpdateNewChat.CONSTRUCTOR: {
      TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) object;
      TdApi.Chat chat = updateNewChat.chat;
      synchronized (chat) {
        TelegramMobileClientCan.chats.put(chat.id, chat);

        TdApi.ChatPosition[] positions = chat.positions;
        chat.positions = new TdApi.ChatPosition[0];
        TelegramMobileClient.setChatPositions(chat, positions);
      }
      break;
    }
    case TdApi.UpdateChatTitle.CONSTRUCTOR: {
      TdApi.UpdateChatTitle updateChat = (TdApi.UpdateChatTitle) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.title = updateChat.title;
      }
      break;
    }
    case TdApi.UpdateChatPhoto.CONSTRUCTOR: {
      TdApi.UpdateChatPhoto updateChat = (TdApi.UpdateChatPhoto) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.photo = updateChat.photo;
      }
      break;
    }
    case TdApi.UpdateChatPermissions.CONSTRUCTOR: {
      TdApi.UpdateChatPermissions update = (TdApi.UpdateChatPermissions) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.permissions = update.permissions;
      }
      break;
    }
    case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
      TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.lastMessage = updateChat.lastMessage;
        TelegramMobileClient.setChatPositions(chat, updateChat.positions);
      }
      if (updateChat.lastMessage != null && messageHandler!=null) {
        messageHandler.consume(updateChat);
      }
      break;
    }
    case TdApi.UpdateChatPosition.CONSTRUCTOR: {
      TdApi.UpdateChatPosition updateChat = (TdApi.UpdateChatPosition) object;
      if (updateChat.position.list.getConstructor() != TdApi.ChatListMain.CONSTRUCTOR) {
        break;
      }

      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        int i;
        for (i = 0; i < chat.positions.length; i++) {
          if (chat.positions[i].list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
            break;
          }
        }
        TdApi.ChatPosition[] new_positions = new TdApi.ChatPosition[chat.positions.length + (updateChat.position.order == 0 ? 0 : 1) - (i < chat.positions.length ? 1 : 0)];
        int pos = 0;
        if (updateChat.position.order != 0) {
          new_positions[pos++] = updateChat.position;
        }
        for (int j = 0; j < chat.positions.length; j++) {
          if (j != i) {
            new_positions[pos++] = chat.positions[j];
          }
        }
        assert pos == new_positions.length;

        TelegramMobileClient.setChatPositions(chat, new_positions);
      }
      break;
    }
    case TdApi.UpdateChatReadInbox.CONSTRUCTOR: {
      TdApi.UpdateChatReadInbox updateChat = (TdApi.UpdateChatReadInbox) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId;
        chat.unreadCount = updateChat.unreadCount;
      }
      break;
    }
    case TdApi.UpdateChatReadOutbox.CONSTRUCTOR: {
      TdApi.UpdateChatReadOutbox updateChat = (TdApi.UpdateChatReadOutbox) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId;
      }
      break;
    }
    case TdApi.UpdateChatActionBar.CONSTRUCTOR: {
      TdApi.UpdateChatActionBar updateChat = (TdApi.UpdateChatActionBar) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.actionBar = updateChat.actionBar;
      }
      break;
    }
    case TdApi.UpdateChatAvailableReactions.CONSTRUCTOR: {
      TdApi.UpdateChatAvailableReactions updateChat = (TdApi.UpdateChatAvailableReactions) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.availableReactions = updateChat.availableReactions;
      }
      break;
    }
    case TdApi.UpdateChatDraftMessage.CONSTRUCTOR: {
      TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.draftMessage = updateChat.draftMessage;
        TelegramMobileClient.setChatPositions(chat, updateChat.positions);
      }
      break;
    }
    case TdApi.UpdateChatMessageSender.CONSTRUCTOR: {
      TdApi.UpdateChatMessageSender updateChat = (TdApi.UpdateChatMessageSender) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.messageSenderId = updateChat.messageSenderId;
      }
      break;
    }
    case TdApi.UpdateChatMessageAutoDeleteTime.CONSTRUCTOR: {
      TdApi.UpdateChatMessageAutoDeleteTime updateChat = (TdApi.UpdateChatMessageAutoDeleteTime) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.messageAutoDeleteTime = updateChat.messageAutoDeleteTime;
      }
      break;
    }
    case TdApi.UpdateChatNotificationSettings.CONSTRUCTOR: {
      TdApi.UpdateChatNotificationSettings update = (TdApi.UpdateChatNotificationSettings) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.notificationSettings = update.notificationSettings;
      }
      break;
    }
    case TdApi.UpdateChatPendingJoinRequests.CONSTRUCTOR: {
      TdApi.UpdateChatPendingJoinRequests update = (TdApi.UpdateChatPendingJoinRequests) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.pendingJoinRequests = update.pendingJoinRequests;
      }
      break;
    }
    case TdApi.UpdateChatReplyMarkup.CONSTRUCTOR: {
      TdApi.UpdateChatReplyMarkup updateChat = (TdApi.UpdateChatReplyMarkup) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.replyMarkupMessageId = updateChat.replyMarkupMessageId;
      }
      break;
    }
    case TdApi.UpdateChatBackground.CONSTRUCTOR: {
      TdApi.UpdateChatBackground updateChat = (TdApi.UpdateChatBackground) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.background = updateChat.background;
      }
      break;
    }
    case TdApi.UpdateChatTheme.CONSTRUCTOR: {
      TdApi.UpdateChatTheme updateChat = (TdApi.UpdateChatTheme) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.themeName = updateChat.themeName;
      }
      break;
    }
    case TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR: {
      TdApi.UpdateChatUnreadMentionCount updateChat = (TdApi.UpdateChatUnreadMentionCount) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.unreadMentionCount = updateChat.unreadMentionCount;
      }
      break;
    }
    case TdApi.UpdateChatUnreadReactionCount.CONSTRUCTOR: {
      TdApi.UpdateChatUnreadReactionCount updateChat = (TdApi.UpdateChatUnreadReactionCount) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.unreadReactionCount = updateChat.unreadReactionCount;
      }
      break;
    }
    case TdApi.UpdateChatVideoChat.CONSTRUCTOR: {
      TdApi.UpdateChatVideoChat updateChat = (TdApi.UpdateChatVideoChat) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.videoChat = updateChat.videoChat;
      }
      break;
    }
    case TdApi.UpdateChatDefaultDisableNotification.CONSTRUCTOR: {
      TdApi.UpdateChatDefaultDisableNotification update = (TdApi.UpdateChatDefaultDisableNotification) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.defaultDisableNotification = update.defaultDisableNotification;
      }
      break;
    }
    case TdApi.UpdateChatHasProtectedContent.CONSTRUCTOR: {
      TdApi.UpdateChatHasProtectedContent updateChat = (TdApi.UpdateChatHasProtectedContent) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.hasProtectedContent = updateChat.hasProtectedContent;
      }
      break;
    }
    case TdApi.UpdateChatIsTranslatable.CONSTRUCTOR: {
      TdApi.UpdateChatIsTranslatable update = (TdApi.UpdateChatIsTranslatable) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.isTranslatable = update.isTranslatable;
      }
      break;
    }
    case TdApi.UpdateChatIsMarkedAsUnread.CONSTRUCTOR: {
      TdApi.UpdateChatIsMarkedAsUnread update = (TdApi.UpdateChatIsMarkedAsUnread) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.isMarkedAsUnread = update.isMarkedAsUnread;
      }
      break;
    }
    case TdApi.UpdateChatBlockList.CONSTRUCTOR: {
      TdApi.UpdateChatBlockList update = (TdApi.UpdateChatBlockList) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.blockList = update.blockList;
      }
      break;
    }
    case TdApi.UpdateChatHasScheduledMessages.CONSTRUCTOR: {
      TdApi.UpdateChatHasScheduledMessages update = (TdApi.UpdateChatHasScheduledMessages) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(update.chatId);
      synchronized (chat) {
        chat.hasScheduledMessages = update.hasScheduledMessages;
      }
      break;
    }

    case TdApi.UpdateMessageMentionRead.CONSTRUCTOR: {
      TdApi.UpdateMessageMentionRead updateChat = (TdApi.UpdateMessageMentionRead) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.unreadMentionCount = updateChat.unreadMentionCount;
      }
      break;
    }
    case TdApi.UpdateMessageUnreadReactions.CONSTRUCTOR: {
      TdApi.UpdateMessageUnreadReactions updateChat = (TdApi.UpdateMessageUnreadReactions) object;
      TdApi.Chat chat = TelegramMobileClientCan.chats.get(updateChat.chatId);
      synchronized (chat) {
        chat.unreadReactionCount = updateChat.unreadReactionCount;
      }
      break;
    }

    case TdApi.UpdateUserFullInfo.CONSTRUCTOR:
      TdApi.UpdateUserFullInfo updateUserFullInfo = (TdApi.UpdateUserFullInfo) object;
      TelegramMobileClientCan.usersFullInfo.put(updateUserFullInfo.userId, updateUserFullInfo.userFullInfo);
      break;
    case TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR:
      TdApi.UpdateBasicGroupFullInfo updateBasicGroupFullInfo = (TdApi.UpdateBasicGroupFullInfo) object;
      TelegramMobileClientCan.basicGroupsFullInfo.put(updateBasicGroupFullInfo.basicGroupId, updateBasicGroupFullInfo.basicGroupFullInfo);
      break;
    case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR:
      TdApi.UpdateSupergroupFullInfo updateSupergroupFullInfo = (TdApi.UpdateSupergroupFullInfo) object;
      TelegramMobileClientCan.supergroupsFullInfo.put(updateSupergroupFullInfo.supergroupId, updateSupergroupFullInfo.supergroupFullInfo);
      break;
    default:
      // print("Unsupported update:" + newLine + object);
    }
  }
}