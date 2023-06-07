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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.sbm.api.model.ScanResult;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;


import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * @author Fabian Krüger
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SbmClientWsImpl implements SbmClient {

    private final WebSocketClientFactory webSocketClientFactory;

    private WebSocketStompClient stompClient;

    private static final String SCAN_DESTINATION = "/sbm/scan";
    private static final String APPLY_DESTINATION = "/sbm/apply";
    private final String HANDSHAKE_URL = "ws://127.0.0.1:8080/endpoint";

    private StompSession stompSession;



    private WebSocketStompClient getWebSocketStompClient() throws Exception {
        if (stompClient == null)
            this.stompClient = webSocketClientFactory.createWebSocketStompClient();
        return stompClient;
    }

    private StompSession getStompSession() {
        try {

            StompSession session = getWebSocketStompClient()
                    .connect(webSocketClientFactory.getHandshakeEndpoint(), new StompSessionHandlerAdapter() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return super.getPayloadType(headers);
                        }
                    }).get();
            return session;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<ScanResult> scan(Path projectRoot) {
        StompSession session = getStompSession();
        session.send(SCAN_DESTINATION, projectRoot.toString());

        CompletableFuture<ScanResult> future = new CompletableFuture();
        return future;
    }



}
