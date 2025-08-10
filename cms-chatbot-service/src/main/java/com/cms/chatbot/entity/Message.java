package com.cms.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

  @Id
  private String id;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String content;

  private LocalDateTime sentAt;

  private Status status;

  public enum Status {
    AUTO, INIT_SENT, INIT_RECEIVED, SENT, RECEIVED;
  }

  @JsonIgnore
  public boolean isSent() {
    return Arrays.asList(Status.INIT_SENT, Status.SENT).contains(status);
  }

  @JsonIgnore
  public boolean isReceived() {
    return Arrays.asList(Status.INIT_RECEIVED, Status.RECEIVED).contains(status);
  }

  @JsonIgnore
  public boolean isChat() {
    return status != Status.AUTO;
  }

  @JsonIgnore
  public boolean isDisplay() {
    return !Arrays.asList(Status.INIT_SENT, Status.INIT_RECEIVED).contains(status);
  }
}
