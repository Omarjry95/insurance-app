package com.cms.user.external;

import com.cms.user.dto.CommonApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(name = "cms-chatbot-service", url = "http://localhost:9000/api/chatbot")
public interface ChatBotService {

  @PostMapping("/init/{id}")
  CommonApiResponse init(@PathVariable String id);
}
