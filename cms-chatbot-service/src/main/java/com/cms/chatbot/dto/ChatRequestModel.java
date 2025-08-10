package com.cms.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatRequestModel {

  private String model;
  private List<Message> messages = new ArrayList<>();

  public ChatRequestModel(String model, String messageContent) {
    this.model = model;
    messages.add(new Message(messageContent));
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  public static class Message {

    private String role;
    private String content;

    public Message(String content) {
      this.role = "user";
      this.content = content;
    }

    public Message(boolean isUser, String content) {
      this.role = isUser ? "user" : "assistant";
      this.content = content;
    }
  }
}
