package application.websocket;

import application.game.messages.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(JoinGame.class),
        @JsonSubTypes.Type(InitGame.class),
        @JsonSubTypes.Type(Coordinates.class),
        @JsonSubTypes.Type(FinishGame.class),
        @JsonSubTypes.Type(InfoMessage.class)
})
public abstract class Message {
}
