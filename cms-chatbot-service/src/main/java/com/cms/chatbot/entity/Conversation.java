package com.cms.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

  @Id
  private int id;

  @OneToMany(cascade = { CascadeType.ALL })
  private List<Message> messages;

  public Conversation(int id) {
    this.id = id;
    messages = new ArrayList<>();
  }

  public void addMessages(List<Message> messages) {
    this.messages.addAll(messages);
  }
}
