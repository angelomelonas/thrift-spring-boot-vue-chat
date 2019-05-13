namespace java com.angelomelonas.thriftwebchat

service Chat {
    Message sendMessage(1: MessageRequest messageRequest);

    list<Message> getMessages(1: list<Message> message);
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
