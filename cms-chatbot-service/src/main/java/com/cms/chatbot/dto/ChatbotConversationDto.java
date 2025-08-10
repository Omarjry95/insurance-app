package com.cms.chatbot.dto;

import com.cms.chatbot.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class ChatbotConversationDto extends CommonApiResponse {

  private List<Message> messages;
}
