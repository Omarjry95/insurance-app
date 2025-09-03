package com.cms.chatbot.resource;

import com.cms.chatbot.dto.*;
import com.cms.chatbot.entity.Conversation;
import com.cms.chatbot.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.cms.chatbot.service.ChatbotService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatbotResource {

  private final ChatbotService chatbotService;

  @Value("${openai.model}")
  private String model;

  @Value("${openai.api.url}")
  private String apiUrl;

  public ResponseEntity<ChatbotConversationDto> getConversation(String id) {
    if (id == null) {
      return ResponseEntity.badRequest()
          .body(ChatbotConversationDto.builder()
              .isSuccess(false)
              .status(HttpStatus.BAD_REQUEST)
              .responseMessage("Conversation id is null !")
              .build());
    }

    List<Message> messages = chatbotService.getMessages(id, true);

    return ResponseEntity.ok(ChatbotConversationDto.builder()
        .isSuccess(true)
        .status(HttpStatus.OK)
        .responseMessage("Conversation fetched successfully !")
        .messages(messages)
        .build());
  }

  public ResponseEntity<CommonApiResponse> initConversation(String id) {
    if (id == null) {
      return ResponseEntity.badRequest()
          .body(CommonApiResponse.builder()
              .isSuccess(false)
              .status(HttpStatus.BAD_REQUEST)
              .responseMessage("Chatbot init conversation request is invalid !")
              .build());
    }

    if (model == null || model.isEmpty()) {
      return ResponseEntity.internalServerError()
          .body(CommonApiResponse.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot model is not specified !")
              .build());
    }

    if (apiUrl == null || apiUrl.isEmpty()) {
      return ResponseEntity.internalServerError()
          .body(CommonApiResponse.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot API URL is not specified !")
              .build());
    }

    String initMessageContent = "Hello ChatGPT ! For this conversation, you are an insurance expert who answers only " +
        "questions related to the insurance field of expertise. For every other question that you find not related" +
        " to the subject, you need to answer by this response: I can't answer this question since it is not related" +
        " to the insurance world !";

    ChatRequestModel chatRequest = new ChatRequestModel(model, initMessageContent);

    String initMessageResponse = chatbotService.ask(chatRequest, apiUrl);

    if (initMessageResponse == null) {
      return ResponseEntity.internalServerError()
          .body(CommonApiResponse.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot failed to answer !")
              .build());
    }

    String autoMessageContent = "Here you can talk with our chatbot and ask it anything about the insurance world ! " +
        "Please keep your questions respectful and in the scope of our field of expertise." +
        "Have a nice experience !";

    List<Message> messages = Arrays.asList(
        new Message(UUID.randomUUID().toString(), autoMessageContent, LocalDateTime.now(), Message.Status.AUTO),
        new Message(UUID.randomUUID().toString(), initMessageContent, LocalDateTime.now(), Message.Status.INIT_SENT),
        new Message(UUID.randomUUID().toString(), initMessageResponse, LocalDateTime.now(), Message.Status.INIT_RECEIVED)
    );

    Conversation conversation = chatbotService.createOrAppendConversation(id, messages);

    if (conversation == null) {
      return ResponseEntity.internalServerError()
          .body(CommonApiResponse.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot conversation failed to init !")
              .build());
    }

    return ResponseEntity.ok(CommonApiResponse.builder()
        .isSuccess(true)
        .status(HttpStatus.OK)
        .responseMessage("Chatbot conversation has been initiated !")
        .build());
  }

  public ResponseEntity<ChatbotResponseDto> askChatbot(String id, ChatBotAskRequestDto askRequest) {
    if (askRequest == null || askRequest.getMessage() == null || askRequest.getMessage().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.BAD_REQUEST)
              .responseMessage("Chatbot ask request is invalid !")
              .build());
    }

    if (id == null || id.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.BAD_REQUEST)
              .responseMessage("Cannot find user conversation !")
              .build());
    }

    if (model == null || model.isEmpty()) {
      return ResponseEntity.internalServerError()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot model is not specified !")
              .build());
    }

    if (apiUrl == null || apiUrl.isEmpty()) {
      return ResponseEntity.internalServerError()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot API URL is not specified !")
              .build());
    }

    List<Message> conversationMessages = chatbotService.getMessages(id, false);

    Message askedMessage = new Message(UUID.randomUUID().toString(), askRequest.getMessage(), LocalDateTime.now(),
        Message.Status.SENT);

    conversationMessages.add(askedMessage);

    List<ChatRequestModel.Message> completionMessages = conversationMessages.stream()
        .filter(message -> message.isSent() || message.isReceived())
        .sorted(Comparator.comparing(Message::getSentAt))
        .map(message -> new ChatRequestModel.Message(message.isSent(), message.getContent()))
        .collect(Collectors.toList());

    ChatRequestModel chatRequest = new ChatRequestModel(model, completionMessages);

    String response = chatbotService.ask(chatRequest, apiUrl);

    if (response == null) {
      return ResponseEntity.internalServerError()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot failed to answer !")
              .build());
    }

    List<Message> messages = Arrays.asList(
        askedMessage,
        new Message(UUID.randomUUID().toString(), response, LocalDateTime.now(), Message.Status.RECEIVED)
    );

    Conversation conversation = chatbotService.createOrAppendConversation(id, messages);

    if (conversation == null) {
      return ResponseEntity.internalServerError()
          .body(ChatbotResponseDto.builder()
              .isSuccess(false)
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .responseMessage("Chatbot conversation failed to be modified !")
              .build());
    }

    return ResponseEntity.ok(ChatbotResponseDto.builder()
        .isSuccess(true)
        .status(HttpStatus.OK)
        .responseMessage("Chatbot has answered successfully !")
        .messages(chatbotService.filterAndSortConversationMessages(Optional.of(conversation), true))
        .build());
  }
}
