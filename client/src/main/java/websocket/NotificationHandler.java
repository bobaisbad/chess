package websocket;

import chess.ChessMove;
import websocket.messages.ServerMessage;

import java.io.PrintStream;

public interface NotificationHandler {
    void notify(ServerMessage msg);

    void printBoardBlack(ChessMove[] moves);

    void printBoardWhite(ChessMove[] moves);
}
