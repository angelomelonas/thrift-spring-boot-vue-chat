namespace java com.angelomelonas.thriftwebchat

service ChatService {
    // Clients first subscribe to be able to send messages.
    Message subscribe(1: SubscriptionRequest subscriptionRequest)

    Message unsubscribe(1: UnsubscriptionRequest unsubscriptionRequest)

    Message sendMessage(1: MessageRequest messageRequest);

    list<Message> getMessages();
}

struct Message {
    1: string message;
    2: string username;
    3: i64 timestamp;
}

struct MessageRequest {
    1: string username;
    2: string message;
}

struct SubscriptionRequest{
    1: string username;
}

struct UnsubscriptionRequest{
    1: string username;
}
