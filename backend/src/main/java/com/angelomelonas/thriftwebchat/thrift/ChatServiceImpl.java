package com.angelomelonas.thriftwebchat.thrift;

import com.angelomelonas.thriftwebchat.ChatService;
import com.angelomelonas.thriftwebchat.Message;
import com.angelomelonas.thriftwebchat.MessageRequest;
import com.angelomelonas.thriftwebchat.SubscriptionRequest;
import com.angelomelonas.thriftwebchat.UnsubscriptionRequest;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl implements ChatService.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
    private ConcurrentLinkedQueue<Message> messagesQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Message subscribe(SubscriptionRequest subscriptionRequest) throws TException {
        // TODO
        return null;
    }

    @Override
    public Message unsubscribe(UnsubscriptionRequest unsubscriptionRequest) throws TException {
        // TODO
        return null;
    }

    @CrossOrigin
    @Override
    public Message sendMessage(MessageRequest messageRequest) throws TException {
        LOGGER.info("Received client message: {} from user: {}", messageRequest.getMessage(), messageRequest.getUsername());

        Message newMessage = new Message();

        newMessage.setMessage(messageRequest.getMessage());
        newMessage.setUsername(messageRequest.getUsername());
        newMessage.setTimestamp(Instant.now().getEpochSecond());

        // Store the message on the server.
        messagesQueue.add(newMessage);

        // Return the message to the client.
        return newMessage;
    }

    @Override
    public List<Message> getMessages() throws TException {
        return messagesQueue.stream().collect(Collectors.toList());
    }
}
