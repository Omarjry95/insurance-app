package com.cms.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonApiResponse {

  private String responseMessage;

  private HttpStatus status;

  private boolean isSuccess;

}