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
package org.springframework.sbm.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.sbm.api.client.SbmClientWsImpl;
import org.springframework.sbm.api.client.WebSocketClientFactory;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Fabian Krüger
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SbmClientTest {

    private static final String PROJECT_ROOT = "/users/user1/source/aproject";

    @Autowired
    private WebSocketClientFactory stompClientFactory;

    @Autowired
    SbmClientWsImpl clientService;

    @Autowired
    private ApiServerController controller;

    @Autowired
    private ApiServerController apiServerController;


    @LocalServerPort
    private Integer port;
    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    void setup() throws Exception {
        stompClientFactory.setPort(port);
        this.webSocketStompClient = stompClientFactory.createWebSocketStompClient();
    }

    @Test
    void assertFactoryNotNull() {
        assertNotNull(stompClientFactory);
        assertNotNull(clientService) ;
    }

    @Test
    void sendScanRequest() throws Exception {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
        controller.setBlockingQueue(blockingQueue);

        webSocketStompClient.setMessageConverter(new StringMessageConverter());
        Path p = Path.of(PROJECT_ROOT);
        clientService.scan(p);
        System.out.println(String.format("Before await queue size %d", blockingQueue.size()));
        Thread.sleep(2000);
        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(PROJECT_ROOT, blockingQueue.poll()));
    }

    public String getWsPath() {
        return String.format("ws://localhost:%d/ws-endpoint", port);
    }


}
