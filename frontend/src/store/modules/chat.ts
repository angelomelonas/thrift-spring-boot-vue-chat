import {
  Action,
  getModule,
  Module,
  Mutation,
  VuexModule
} from "vuex-module-decorators";
import store from "@/store";
import moment from "moment";

import {
  ChatService,
  Message,
  MessageRequest,
  MessagesRequest,
  SubscriptionRequest,
  UnsubscriptionRequest
} from "../../../codegen";
import {
  createHttpClient,
  createHttpConnection,
  HttpConnection,
  TBinaryProtocol,
  TBufferedTransport
} from "thrift";

@Module({
  namespaced: true,
  name: "chat",
  store,
  dynamic: true
})
class ChatModule extends VuexModule {
  connection!: HttpConnection;
  chatClient!: ChatService.Client;
  polling!: any;

  subscribed: boolean = false;
  username: string = "";
  messages: string = "";

  get getUsername(): string {
    return this.username;
  }

  get getMessages(): string {
    return this.messages;
  }

  get isSubscribed(): boolean {
    return this.subscribed;
  }

  @Mutation
  setUsername(username: string) {
    this.username = username;
  }

  @Mutation
  setSubscription(subscription: boolean) {
    this.subscribed = subscription;
  }

  @Mutation
  appendMessage(message: Message) {
    if (message) {
      this.messages +=
        "[" +
        moment(
          message.timestamp ? message.timestamp.valueOf() : moment()
        ).format("LTS") +
        "] " +
        message.username +
        ": " +
        message.message +
        "\n";
    } else {
      this.messages += "ERROR: Message not delivered." + "\n";
    }
  }

  @Action
  connectClient(payload: { hostname: string; port: number }) {
    const options = {
      transport: TBufferedTransport,
      protocol: TBinaryProtocol,
      https: false,
      path: "/chat"
    };

    this.connection = createHttpConnection(
      payload.hostname,
      payload.port,
      options
    );
    this.chatClient = createHttpClient(ChatService.Client, this.connection);

    console.log("Client connected.");
  }

  @Action
  startPolling() {
    this.polling = setInterval(() => {
      const messagesRequest = new MessageRequest();
      messagesRequest.username = this.username;

      this.chatClient
        .getMessages(messagesRequest)
        .then((messages: Message[]) => {
          messages.forEach(message => this.appendMessage(message));
        });
    }, 500);
  }

  @Action
  stopPolling() {
    clearInterval(this.polling);
  }

  @Action
  subscribe() {
    console.log("User has subscribed: " + this.username);

    const subscriptionRequest = new SubscriptionRequest();
    subscriptionRequest.username = this.username;

    this.chatClient.subscribe(subscriptionRequest).then((message: Message) => {
      if (message.message) {
        if (!message.message.includes("Error")) {
          this.setSubscription(true);
          this.startPolling();
        }
        this.appendMessage(message);
      }
    });
  }

  @Action
  unsubscribe() {
    console.log("User has unsubscribed: " + this.username);

    const unsubscriptionRequest = new UnsubscriptionRequest();
    unsubscriptionRequest.username = this.username;

    this.chatClient
      .unsubscribe(unsubscriptionRequest)
      .then((message: Message) => {
        if (message.message) {
          if (!message.message.includes("Error")) {
            this.setSubscription(false);
            this.stopPolling();
          }
          this.appendMessage(message);
        }
      });
  }

  @Action
  sendMessage(message: string) {
    console.log("Sending message: " + message + " from user: " + this.username);

    const messageRequest = new MessageRequest();
    messageRequest.username = this.username;
    messageRequest.message = message;

    this.chatClient.sendMessage(messageRequest).then((message: Message) => {
      console.log(message.message);
    });
  }
}

export default getModule(ChatModule);
