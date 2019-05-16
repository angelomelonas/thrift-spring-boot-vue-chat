package com.angelomelonas.thriftwebchat.thrift;

import com.angelomelonas.thriftwebchat.ChatService;
import com.angelomelonas.thriftwebchat.Message;
import com.angelomelonas.thriftwebchat.MessageRequest;
import com.angelomelonas.thriftwebchat.MessagesRequest;
import com.angelomelonas.thriftwebchat.SubscriptionRequest;
import com.angelomelonas.thriftwebchat.UnsubscriptionRequest;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class ChatServiceImpl implements ChatService.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> clientMessageQueues = new ConcurrentHashMap<>();

    @Override
    public Message subscribe(SubscriptionRequest subscriptionRequest) throws TException {
        String username = subscriptionRequest.getUsername();
        Long timestamp = Instant.now().toEpochMilli();

        if (!clientMessageQueues.containsKey(username)) {
            Message serverSuccessMessage = new Message();
            serverSuccessMessage.setTimestamp(timestamp);
            serverSuccessMessage.setUsername("Server");
            serverSuccessMessage.setMessage("Welcome to Thrift Chat, " + username + "!");

            // TODO: User a token instead of the username.
            // Create a new message queue for the client.
            ConcurrentLinkedQueue<Message> clientQueue = new ConcurrentLinkedQueue<>();
            // Add the client with its queue to our list.
            clientMessageQueues.put(username, clientQueue);

            LOGGER.info("Client with Username {} has subscribed.", username);

            return serverSuccessMessage;
        } else {
            // TODO: Introduce an Error protocol buffer.
            Message serverErrorMessage = new Message();
            serverErrorMessage.setTimestamp(timestamp);
            serverErrorMessage.setUsername("Server");
            serverErrorMessage.setMessage("Error: Username '" + username + "' has already been taken.");

            LOGGER.warn("Error: Username '{}' has already been taken.", username);

            return serverErrorMessage;
        }
    }

    @Override
    public Message unsubscribe(UnsubscriptionRequest unsubscriptionRequest) throws TException {
        String username = unsubscriptionRequest.getUsername();
        Long timestamp = Instant.now().toEpochMilli();

        if (clientMessageQueues.containsKey(username)) {

            Message serverSuccessMessage = new Message();
            serverSuccessMessage.setTimestamp(timestamp);
            serverSuccessMessage.setUsername("Server");
            serverSuccessMessage.setMessage("You have unsubscribed from Thrift Chat.");

            // Remove the client and its message queue.
            clientMessageQueues.remove(username);

            LOGGER.info("Client with username {} has been unsubscribed.", username);
            return serverSuccessMessage;
        } else {
            Message serverErrorMessage = new Message();
            serverErrorMessage.setTimestamp(timestamp);
            serverErrorMessage.setUsername("Server");
            serverErrorMessage.setMessage("Error: Client with username " + username + " does not exist or has already unsubscribed.");

            LOGGER.warn("Client with username {} does not exist or has already unsubscribed.", username);

            return serverErrorMessage;
        }
    }

    @CrossOrigin
    @Override
    public Message sendMessage(MessageRequest messageRequest) throws TException {
        String username = messageRequest.getUsername();
        String message = messageRequest.getMessage();
        Long timestamp = Instant.now().toEpochMilli();

        if (clientMessageQueues.containsKey(username)) {
            Message newMessage = new Message();
            newMessage.setMessage(messageRequest.getMessage());
            newMessage.setUsername(messageRequest.getUsername());
            newMessage.setTimestamp(timestamp);

            // Add the message to each of the client's message queues.
            clientMessageQueues.forEach((client, messageQueue) -> messageQueue.add(newMessage));

            LOGGER.info("Received message: {} from user: {}", message, username);

            // Return the message to the client.
            return newMessage;
        } else {
            Message serverErrorMessage = new Message();
            serverErrorMessage.setTimestamp(timestamp);
            serverErrorMessage.setUsername("Server");
            serverErrorMessage.setMessage("Error: Client with username " + username + " does not exist or has already unsubscribed.");

            LOGGER.info("User with username: {} is not subscribed.", username);

            return serverErrorMessage;
        }
    }

    @Override
    public List<Message> getMessages(MessagesRequest messagesRequest) throws TException {
        String username = messagesRequest.getUsername();

        if (clientMessageQueues.containsKey(username)) {
            ArrayList<Message> messages = new ArrayList<>(clientMessageQueues.get(username));
            // Clear the queue for the user.
            clientMessageQueues.get(messagesRequest.getUsername()).clear();
            return new ArrayList<>(messages);
        }
        return new ArrayList<>();
    }
}
