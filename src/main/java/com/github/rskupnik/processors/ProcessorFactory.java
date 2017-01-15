package com.github.rskupnik.processors;

import com.github.rskupnik.model.Action;

import java.util.Optional;

public final class ProcessorFactory {

    public static Optional<Processor> fromAction(Action action) {
        switch (action.getId()) {
            case "trello-todo":
                return Optional.of(new TrelloTodoProcessor());
            default:
                return Optional.empty();
        }
    }
}
