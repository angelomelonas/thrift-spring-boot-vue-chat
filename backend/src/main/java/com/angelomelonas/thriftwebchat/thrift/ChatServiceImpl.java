package com.angelomelonas.thriftwebchat.thrift;

import com.angelomelonas.thriftwebchat.ChatService;
import com.angelomelonas.thriftwebchat.Message;
import com.angelomelonas.thriftwebchat.MessageRequest;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl implements ChatService.Iface {

    private ConcurrentLinkedQueue<Message> messagesQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Message sendMessage(MessageRequest messageRequest) throws TException {
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
