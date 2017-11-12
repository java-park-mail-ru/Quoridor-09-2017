package application;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.validation.constraints.NotNull;

@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @NotNull
    private WebSocketHandler socketHandler;

    public WebSocketConfig(@NotNull WebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/game")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
