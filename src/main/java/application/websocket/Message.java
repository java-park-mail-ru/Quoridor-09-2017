package application.websocket;

import application.game.models.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(JoinGame.class),
        @JsonSubTypes.Type(InitGame.class),
        @JsonSubTypes.Type(Coordinates.class)
})
public abstract class Message {
}
