package com.example.standalonepushservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StandalonePushServiceApplication

fun main(args: Array<String>) {
	runApplication<StandalonePushServiceApplication>(*args)
}
