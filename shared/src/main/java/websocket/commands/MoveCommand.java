package websocket.commands;

public record MoveCommand(String start, String end, String color, boolean check, boolean mate, boolean stale) {}
