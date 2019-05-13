package com.angelomelonas.thriftwebchat.thrift;

import com.angelomelonas.thriftwebchat.ChatService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftServerConfig {

    @Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }


    @Bean
    public ServletRegistrationBean thriftChatServlet(TProtocolFactory protocolFactory, ChatServiceImpl chatService) {
        TServlet tServlet = new TServlet(new ChatService.Processor<>(chatService), protocolFactory);

        return new ServletRegistrationBean(tServlet, "/chat");
    }
}
