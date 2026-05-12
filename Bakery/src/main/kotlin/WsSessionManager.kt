package org.example

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import java.util.concurrent.ConcurrentHashMap

object WsSessionManager {

    private val sessions = ConcurrentHashMap<Int, MutableSet<DefaultWebSocketServerSession>>()

    fun registerWsSession(userId: Int, session: DefaultWebSocketServerSession) {
        sessions.getOrPut(userId) { mutableSetOf() }.add(session)
    }

    fun removeWsSession(userId: Int, session: DefaultWebSocketServerSession
    ) {
        sessions[userId]?.remove(session)
        if (sessions[userId].isNullOrEmpty()) {
            sessions.remove(userId)
        }
    }

    suspend fun notifyUser(userId: Int, message: String) {
        sessions[userId]?.forEach {
            it.send(Frame.Text(message))
        }
    }
}