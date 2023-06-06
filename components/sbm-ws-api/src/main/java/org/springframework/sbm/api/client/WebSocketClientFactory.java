/*
 * Copyright 2021 - 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sbm.api.client;


import org.springframework.stereotype.Component;


/**
 * @author Fabian Krüger
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class WebSocketClientFactory {

    public void setPort(long port) {
        this.port = port;
    }

    @Value("${server.port}")
    private long port;

    public WebSocketStompClient createWebSocketStompClient() {
        try {
            WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
            StompSession session = webSocketStompClient.connect(getHandshakeEndpoint(), new StompSessionHandlerAdapter() {
            }).get(1, SECONDS);
            webSocketStompClient.setMessageConverter(new StringMessageConverter());
            return webSocketStompClient;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getHandshakeEndpoint() {
        return String.format("ws://localhost:%d/ws-endpoint", port);
    }
}
