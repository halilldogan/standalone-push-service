package com.example.standalonepushservice.service

import com.eatthepath.pushy.apns.ApnsClient
import com.eatthepath.pushy.apns.PushNotificationResponse
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification
import com.example.standalonepushservice.config.ApnsConfig
import com.example.standalonepushservice.model.PushModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ExecutionException

@Service
class PushService(
    private val firebaseMessaging: FirebaseMessaging,
    private val apnsConfig: ApnsConfig,
    private val apnsClient: ApnsClient,
) {

    fun pushNotificationWithTopic(request: PushModel): ResponseEntity<String> {
        val msg: Message = Message.builder().setNotification(
            Notification.builder().setTitle(request.title)
                .setBody(request.body).build(),
        )
            .setTopic(request.topic).build()
        return try {
            firebaseMessaging.sendAsync(msg)
            ResponseEntity.ok("Successfully sent with Topic\n")
        } catch (e: FirebaseMessagingException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    fun pushNotificationWithToken(request: PushModel): ResponseEntity<String> {
        val msg: MulticastMessage = MulticastMessage.builder()
            .addAllTokens(request.tokenList).setNotification(
                Notification.builder()
                    .setTitle(request.title).setBody(request.body).build(),
            ).build()
        return try {
            firebaseMessaging.sendMulticastAsync(msg)
            ResponseEntity.ok("Successfully sent with token")
        } catch (e: FirebaseMessagingException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    fun pushNotificationWithApns(request: PushModel): ResponseEntity<String> {
        val payloadBuilder = SimpleApnsPayloadBuilder()
        payloadBuilder.setAlertTitle(request.title)
        payloadBuilder.setAlertBody(request.body)
        val payload: String = payloadBuilder.build()

        request.tokenList.forEach {
            val pushNotification = SimpleApnsPushNotification(it, apnsConfig.topic, payload)
            val sendNotificationFuture = apnsClient.sendNotification(pushNotification)
            try {
                val pushNotificationResponse: PushNotificationResponse<SimpleApnsPushNotification> =
                    sendNotificationFuture.get()
                if (pushNotificationResponse.isAccepted) {
                    println("Push notification accepted by APNs gateway.")
                } else {
                    println(
                        "Notification rejected by the APNs gateway: " +
                            pushNotificationResponse.rejectionReason,
                    )
                    pushNotificationResponse.tokenInvalidationTimestamp.ifPresent { timestamp: Instant ->
                        println("\t…and the token is invalid as of $timestamp")
                    }
                }
            } catch (e: ExecutionException) {
                System.err.println("Failed to send push notification.")
                e.printStackTrace()
            }
        }
        return ResponseEntity.ok("Push notification sent to device tokens.")
    }
}
