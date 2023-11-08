package com.example.standalonepushservice

import com.example.standalonepushservice.model.ChannelType
import com.example.standalonepushservice.model.PushModel
import com.example.standalonepushservice.service.PushService
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*

class PushServiceTest {

    @Mock
    private lateinit var firebaseMessaging: FirebaseMessaging

    @InjectMocks
    private lateinit var pushNotificationService: PushService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testPushNotificationWithTopic() {
        val emptyTokenList: List<String> = listOf()

        val request = PushModel("Test Title", "Test Body", emptyTokenList, "test-topic", ChannelType.FIREBASE)
        val expectedMessage = Message.builder().setNotification(
            Notification.builder().setTitle(request.title).setBody(request.body).build(),
        ).setTopic(request.topic).build()

        val captor: ArgumentCaptor<Message> = ArgumentCaptor.forClass(Message::class.java)

        pushNotificationService.pushNotificationWithTopic(request)

        Mockito.verify(firebaseMessaging).sendAsync(Mockito.any())
    }

    @Test
    fun testPushNotificationWithToken() {
        val tokens = listOf("token1", "token2", "token3")
        val request = PushModel("Test Title", "Test Body", tokens, null, ChannelType.FIREBASE)
        val expectedMessage = MulticastMessage.builder().addAllTokens(tokens).setNotification(
            Notification.builder().setTitle(request.title).setBody(request.body).build(),
        ).build()

        pushNotificationService.pushNotificationWithToken(request)

        Mockito.verify(firebaseMessaging).sendMulticastAsync(Mockito.any())
    }
}
