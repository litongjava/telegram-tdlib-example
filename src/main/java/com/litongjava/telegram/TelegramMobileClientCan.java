package com.litongjava.telegram;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import com.litongjava.telegram.handler.DefaultHandler;
import com.litongjava.telegram.handler.TelegramMessageHandler;

public class TelegramMobileClientCan {

  public static TdApi.AuthorizationState authorizationState = null;
  public static volatile boolean haveAuthorization = false;
  public static volatile boolean needQuit = false;
  public static volatile boolean canQuit = false;

  public static final Client.ResultHandler defaultHandler = new DefaultHandler();

  public static final Lock authorizationLock = new ReentrantLock();
  public static final Condition gotAuthorization = authorizationLock.newCondition();

  public static final ConcurrentMap<Long, TdApi.User> users = new ConcurrentHashMap<Long, TdApi.User>();
  public static final ConcurrentMap<Long, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<Long, TdApi.BasicGroup>();
  public static final ConcurrentMap<Long, TdApi.Supergroup> supergroups = new ConcurrentHashMap<Long, TdApi.Supergroup>();
  public static final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<Integer, TdApi.SecretChat>();

  public static final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
  public static final NavigableSet<OrderedChat> mainChatList = new TreeSet<OrderedChat>();
  public static boolean haveFullMainChatList = false;

  public static final ConcurrentMap<Long, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Long, TdApi.UserFullInfo>();
  public static final ConcurrentMap<Long, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<Long, TdApi.BasicGroupFullInfo>();
  public static final ConcurrentMap<Long, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<Long, TdApi.SupergroupFullInfo>();

  public static final String newLine = System.getProperty("line.separator");
  public static final String commandsLine = "Enter command (gcs - GetChats, gc <chatId> - GetChat, me - GetMe, sm <chatId> <message> - SendMessage, lo - LogOut, q - Quit): ";

  public static volatile String currentPrompt = null;

  public static TelegramMessageHandler messageHandler;

}
