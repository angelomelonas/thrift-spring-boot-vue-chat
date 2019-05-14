package com.angelomelonas.thriftwebchat;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ChatApplication.class)
public class ChatTest {

    @Autowired
    protected TProtocolFactory protocolFactory;

    @Value("${local.server.port}")
    protected int port;

    protected ChatService.Client chatClient;

    @Before
    public void setUp() throws Exception {
        TTransport transport = new THttpClient("http://localhost:" + port + "/chat");

        TProtocol protocol = protocolFactory.getProtocol(transport);

        chatClient = new ChatService.Client(protocol);
    }

    @Test
    public void testSendMessage() throws Exception {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message");
        messageRequest.setUsername("test_username");

        Message message = chatClient.sendMessage(messageRequest);

        assertEquals(message.getMessage(), messageRequest.getMessage());
    }
}
