package com.cms.chatbot.controller;

import com.cms.chatbot.dto.ChatBotAskRequestDto;
import com.cms.chatbot.dto.ChatbotConversationDto;
import com.cms.chatbot.dto.ChatbotResponseDto;
import com.cms.chatbot.dto.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cms.chatbot.resource.ChatbotResource;

@RestController
@RequestMapping("/api/chatbot/")
@RequiredArgsConstructor
public class ChatbotController {

  private final ChatbotResource chatbotResource;

  @GetMapping("/{id}")
  public ResponseEntity<ChatbotConversationDto> getConversation(@PathVariable String id) {
    return chatbotResource.getConversation(id);
  }

  @PostMapping("/init/{id}")
  public ResponseEntity<CommonApiResponse> init(@PathVariable String id) {
    return chatbotResource.initConversation(id);
  }

  @PostMapping("/ask/{id}")
  public ResponseEntity<ChatbotResponseDto> ask(@PathVariable String id,
                                                @RequestBody ChatBotAskRequestDto askRequest) {
    return chatbotResource.askChatbot(id, askRequest);
  }
}
