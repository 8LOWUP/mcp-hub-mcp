package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class TestController {

	private final ProducerService producer;

	@PostMapping("/send")
	public String send(@RequestParam String message) {
		producer.sendMessage("test-topic", message);
		return "Message sent: " + message;
	}
}
