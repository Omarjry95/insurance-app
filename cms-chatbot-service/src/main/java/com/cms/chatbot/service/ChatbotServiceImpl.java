package com.cms.chatbot.service;

import com.cms.chatbot.dao.ConversationDao;
import com.cms.chatbot.dto.ChatRequestModel;
import com.cms.chatbot.dto.ChatResponseModel;
import com.cms.chatbot.entity.Conversation;
import com.cms.chatbot.entity.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

  private final RestTemplate restTemplate;

  private final ObjectMapper objectMapper;

  private final ConversationDao conversationDao;

  @Override
  public List<Message> getMessages(String id, boolean display) {
    Optional<Conversation> conversation;

    try {
      conversation = conversationDao.findById(Integer.parseInt(id));
    } catch (RuntimeException ex) {
      conversation = Optional.empty();
    }

    return filterAndSortConversationMessages(conversation, display);
  }

  @Override
  public Conversation createOrAppendConversation(String id, List<Message> messages) {
    Conversation conversation;

    Optional<Conversation> existingConversation = conversationDao.findById(Integer.parseInt(id));

    conversation = existingConversation.orElse(
        new Conversation(Integer.parseInt(id))
    );

    conversation.addMessages(messages);

    try {
      return conversationDao.save(conversation);
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      return null;
    }
  }

  @Override
  public String ask(ChatRequestModel chatRequest, String apiUrl) {
    String responseAsString = restTemplate.postForObject(apiUrl, chatRequest, String.class);

    try {
      ChatResponseModel response = objectMapper.readValue(responseAsString, ChatResponseModel.class);

      return Optional.ofNullable(response)
          .map(ChatResponseModel::getChoices)
          .flatMap(choices -> choices.stream()
              .min(Comparator.comparing(ChatResponseModel.Choice::getIndex)))
          .map(ChatResponseModel.Choice::getMessage)
          .map(ChatRequestModel.Message::getContent)
          .orElse(null);
    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage());
      return null;
    }
  }
}
