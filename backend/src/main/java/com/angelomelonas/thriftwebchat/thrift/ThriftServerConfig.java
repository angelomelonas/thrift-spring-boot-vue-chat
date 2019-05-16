package com.angelomelonas.thriftwebchat.thrift;

import com.angelomelonas.thriftwebchat.ChatService.Processor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class ThriftServerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftServerConfig.class);

    @Bean
    public TProtocolFactory tProtocolFactory() {
        LOGGER.info("Protocol Factory created.");
        return new TBinaryProtocol.Factory();
    }

    @Bean
    public ServletRegistrationBean thriftChatServlet(TProtocolFactory protocolFactory, ChatServiceImpl chatService) {
        Processor<ChatServiceImpl> chatServiceProcessor = new Processor<>(chatService);
        TServlet tServlet = new TServlet(chatServiceProcessor, protocolFactory);

        LOGGER.info("Thrift Server started.");
        return new ServletRegistrationBean(tServlet, "/chat");
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        // Enable a CORS filter and allow all origins. This can be set to only allow a specific origin.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new CorsFilter(source));
        bean.setOrder(0);

        LOGGER.info("CORS Filter enabled.");
        return bean;
    }
}
