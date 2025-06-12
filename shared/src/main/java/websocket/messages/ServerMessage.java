package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String serverMessage;
    ChessGame game;
    boolean gameOver = false;
    String winner;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.serverMessage = message;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getServerMessage() {
        return this.serverMessage;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public void setGameOver(boolean bool) {
        this.gameOver = bool;
    }

    public boolean getGameOver() {
        return this.gameOver;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return this.winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
