package com.example.standalonepushservice.controller

import com.example.standalonepushservice.model.ChannelType
import com.example.standalonepushservice.model.PushModel
import com.example.standalonepushservice.service.PushService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PushController(private val pushNotificationService: PushService) {

    @PostMapping("/push/send")
    fun pushNotification(@RequestBody request: PushModel): ResponseEntity<String> {
        if (request.topic == null && request.tokenList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both tokenList and topic cannot be empty.")
        }
        var message: String = ""
        when (request.channel) {
            ChannelType.FIREBASE -> {
                if (request.topic != null) {
                    message = message.plus(pushNotificationService.pushNotificationWithTopic(request))
                }
                if (request.tokenList.isNotEmpty()) {
                    message = message.plus(pushNotificationService.pushNotificationWithToken(request))
                }
                return ResponseEntity.ok(message)
            }
            ChannelType.APNS -> {
                if (request.tokenList.isNotEmpty()) {
                    message = message.plus(pushNotificationService.pushNotificationWithApns(request))
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("TokenList cannot be empty.")
                }
                return ResponseEntity.ok(message)
            }
        }
    }
}
