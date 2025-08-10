package com.cms.chatbot.service;

import com.cms.chatbot.dto.ChatRequestModel;
import com.cms.chatbot.entity.Conversation;
import com.cms.chatbot.entity.Message;

import java.util.*;
import java.util.stream.Collectors;

public interface ChatbotService {

  List<Message> getMessages(String id, boolean display);

  Conversation createOrAppendConversation(String id, List<Message> messages);

  String ask(ChatRequestModel chatRequest, String apiUrl);

  default List<Message> filterAndSortConversationMessages(Optional<Conversation> conversation,
                                                          boolean isForDisplay) {
    return conversation.map(Conversation::getMessages)
        .orElse(new ArrayList<>())
        .stream()
        .filter(message -> isForDisplay ? message.isDisplay() : message.isChat())
        .sorted(Comparator.comparing(Message::getSentAt))
        .collect(Collectors.toList());
  }
}
