package com.example.standalonepushservice.config

import com.eatthepath.pushy.apns.ApnsClient
import com.eatthepath.pushy.apns.ApnsClientBuilder
import com.eatthepath.pushy.apns.auth.ApnsSigningKey
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class PushConfig(private val apnsConfig: ApnsConfig) {

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }

    @Bean
    fun firebaseApp(): FirebaseApp {
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun apnsClient(): ApnsClient {
        return ApnsClientBuilder()
            .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
            .setSigningKey(
                ApnsSigningKey.loadFromPkcs8File(
                    File(apnsConfig.authKeyPath),
                    apnsConfig.teamId,
                    apnsConfig.keyId,
                ),
            )
            .build()
    }
}
