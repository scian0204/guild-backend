package com.daelim.guildbackend.webSocket.service;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/chat")
@Service
public class WebSocketChatService {
    private static Set<Session> CLIENTS = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.toString());

        if (CLIENTS.contains(session)) {
            System.out.println("이미 연결된 세션 : " + session);
        } else {
            CLIENTS.add(session);
            System.out.println("새로운 세션 : " + session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        CLIENTS.remove(session);
        System.out.println("세션 종료 : " + session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("입력된 메시지 : " + message);

        for (Session client : CLIENTS) {
            System.out.println("메시지 전달 : " + message);
            client.getBasicRemote().sendText(message);
        }
    }
}
