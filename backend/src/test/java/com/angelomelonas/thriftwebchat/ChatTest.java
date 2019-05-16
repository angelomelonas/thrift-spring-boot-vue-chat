package com.angelomelonas.thriftwebchat;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

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
    public void subscribeTest() throws TException {
        String username = "Test username 1";
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setUsername(username);

        // Subscribe
        Message responseMessage = chatClient.subscribe(subscriptionRequest);

        // Check that the subscription was successful.
        assertEquals("Welcome to Thrift Chat, " + username + "!", responseMessage.getMessage());

        // Subscribe again.
        Message errorResponse = chatClient.subscribe(subscriptionRequest);

        // Check that the same user cannot subscribe again.
        assertEquals("Error: Username '" + username + "' has already been taken.", errorResponse.getMessage());

        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setUsername(username);

        // Unsubscribe the client again.
        chatClient.unsubscribe(unsubscriptionRequest);
    }

    @Test
    public void unsubscribeTest() throws TException {
        String username = "Test username 1";
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setUsername(username);

        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setUsername(username);

        // Subscribe
        Message responseSubscriptionMessage = chatClient.subscribe(subscriptionRequest);

        // Check that the subscription was successful.
        assertEquals("Welcome to Thrift Chat, " + username + "!", responseSubscriptionMessage.getMessage());

        // Unsubscribe
        Message responseUnsubscriptionMessage = chatClient.unsubscribe(unsubscriptionRequest);

        // Check that the unsubscription was successful.
        assertEquals("You have unsubscribed from Thrift Chat.", responseUnsubscriptionMessage.getMessage());
    }

    @Test
    public void testSendMessageSubscribed() throws Exception {
        String username = "Test username 1";
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setUsername(username);

        // Subscribe
        Message responseMessage = chatClient.subscribe(subscriptionRequest);

        // Check that the subscription was successful.
        assertEquals("Welcome to Thrift Chat, " + username + "!", responseMessage.getMessage());

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message");
        messageRequest.setUsername(username);

        // Send the message.
        Message message = chatClient.sendMessage(messageRequest);

        assertEquals(messageRequest.getMessage(), message.getMessage());

        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setUsername(username);

        // Unsubscribe
        chatClient.unsubscribe(unsubscriptionRequest);
    }

    @Test
    public void testSendMessageUnsubscribed() throws Exception {
        String username = "Test username 1";
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message");
        messageRequest.setUsername(username);

        // Send the message.
        Message message = chatClient.sendMessage(messageRequest);

        assertEquals(message.getMessage(), "Error: Client with username " + username + " does not exist or has already unsubscribed.");
    }

    @Test
    public void getMessagesTest() throws TException {
        SubscriptionRequest subscriptionRequest1 = generateSubscriptionRequest(1);
        SubscriptionRequest subscriptionRequest2 = generateSubscriptionRequest(2);
        SubscriptionRequest subscriptionRequest3 = generateSubscriptionRequest(3);

        // Subscribe three clients
        chatClient.subscribe(subscriptionRequest1);
        chatClient.subscribe(subscriptionRequest2);
        chatClient.subscribe(subscriptionRequest3);

        String message1 = "Test message 1";
        String message2 = "Test message 2";
        String message3 = "Test message 3";

        // Each client sends three messages.
        MessageRequest messageRequest1 = new MessageRequest();
        messageRequest1.setMessage(message1);
        messageRequest1.setUsername(subscriptionRequest1.getUsername());

        MessageRequest messageRequest2 = new MessageRequest();
        messageRequest2.setMessage(message2);
        messageRequest2.setUsername(subscriptionRequest2.getUsername());

        MessageRequest messageRequest3 = new MessageRequest();
        messageRequest3.setMessage(message3);
        messageRequest3.setUsername(subscriptionRequest3.getUsername());

        MessagesRequest messagesRequest = new MessagesRequest();
        messagesRequest.setUsername(subscriptionRequest1.getUsername());

        List<Message> messages = chatClient.getMessages(messagesRequest);

        List<String> expected = Arrays.asList(message1, message2, message3);

        Assert.assertTrue(messages.stream().allMatch(message -> expected.contains(message.getMessage())));
    }

    private SubscriptionRequest generateSubscriptionRequest(int suffixIncrement) {
        return new SubscriptionRequest().setUsername("Test Username" + suffixIncrement);
    }
}
