import {Action, getModule, Module, Mutation, VuexModule} from "vuex-module-decorators";
import store from '@/store';

import {ChatService, Message, MessageRequest} from "../../../codegen";
import {createHttpClient, createHttpConnection, HttpConnection, TBinaryProtocol, TBufferedTransport,} from 'thrift'

@Module({
    namespaced: true,
    name: 'chat',
    store,
    dynamic: true,
})
class ChatModule extends VuexModule {
    connection!: HttpConnection;
    chatClient!: ChatService.Client;


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

    // @Mutation
    // appendMessage(data: Message) {
    //     if (data) {
    //         this.messages += "[" + new Date(data.getTimestamp()).toISOString() + "] " + data.getUsername() + ": " + data.getMessage() + "\n";
    //     } else {
    //         this.messages += "ERROR: Message not delivered" + "\n";
    //     }
    // }

    @Action
    async connectClient(hostname: string, port: number) {

        const options = {
            transport: TBufferedTransport,
            protocol: TBinaryProtocol,
            https: false,
            path: "/chat"
        };

        this.connection = await createHttpConnection(hostname, port, options);
        this.chatClient = await createHttpClient(ChatService.Client, this.connection);


        // this.chatClient = createHttpClient(ChatService.Client, );
        // console.log(this.chatClient.getMessages());
        console.log("Client connected.");
    }

    // @Action
    // async subscribe() {
    //     console.log("User has subscribed: " + this.username);
    //
    //     const subscriptionRequest = new SubscriptionRequest();
    //     subscriptionRequest.setUsername(this.username);
    //
    //     this.chatClient.subscribe(subscriptionRequest).on("data", data => {
    //         if (!data.getMessage().includes("Error")) {
    //             this.setSubscription(true);
    //         }
    //         this.appendMessage(data);
    //     });
    // }
    //
    // @Action
    // unsubscribe() {
    //     console.log("User has unsubscribed: " + this.username);
    //
    //     const unsubscriptionRequest = new UnsubscriptionRequest();
    //     unsubscriptionRequest.setUsername(this.username);
    //
    //     this.chatClient.unsubscribe(unsubscriptionRequest, {}, (err: grpcWeb.Error, message: Message) => {
    //         this.setSubscription(false);
    //     });
    // }

    @Action
    sendMessage(message: string) {
        console.log("Sending message: " + message);
        const messageRequest = new MessageRequest

        messageRequest.username = this.username;
        messageRequest.message = message;

        this.chatClient.sendMessage(messageRequest).then((message: Message) => {
            console.log(message.message)
        });
    }
}

export default getModule(ChatModule);
