package io.github.dlopezgarsco.matchmaking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MatchmakingDashboard

fun main(args: Array<String>) {
    runApplication<MatchmakingDashboard>(*args)
}

