package com.github.rskupnik;

import com.evernote.edam.type.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.model.Action;
import com.github.rskupnik.model.Config;
import com.github.rskupnik.model.Handler;
import com.github.rskupnik.processors.DefaultProcessor;
import com.github.rskupnik.processors.Processor;
import com.github.rskupnik.processors.ProcessorFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Pipelife {

    private static final String CONFIG_EVERNOTE_TOKEN = "EVERNOTE-TOKEN";
    private static final String CONFIG_EVERNOTE_INPUT_NOTEBOOK = "EVERNOTE-INPUT-NOTEBOOK";

    private Map<String, Action> actions = new HashMap<>();
    private Map<String, Handler> handlers = new HashMap<>();
    private Map<Action, Processor> processors = new HashMap<>();

    private Pipelife() {

        try {
            readConfig();

            EvernoteManager.getInstance().init(
                    ConfigManager.getInstance().getConfigEntry(CONFIG_EVERNOTE_TOKEN).orElseThrow(IllegalStateException::new)
            );

            List<Note> notes = EvernoteManager.getInstance()
                    .getNotes(
                            ConfigManager.getInstance().getConfigEntry(CONFIG_EVERNOTE_INPUT_NOTEBOOK).orElseThrow(IllegalStateException::new)
                    );

            for (Note note : notes) {
                parseNote(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void parseNote(Note note) throws Exception {
        Handler handler = handlers.get(note.getTitle());
        if (handler == null)
            return;

        Action action = actions.get(handler.getAction());
        if (action == null)
            return;

        Processor processor = processors.get(action);
        if (processor == null)
            return;

        switch (action.getScope()) {
            default:
            case BUILT_IN:
                processor.process(note);
                break;
            case PROVIDED:
                break;
        }
    }

    private void readConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Config configRaw = objectMapper.readValue(new File("config.json"), Config.class);
        ConfigManager.getInstance().init(configRaw.getConfig());
        actions = configRaw.getActions().stream()
                .collect(Collectors.toMap(action -> action.getId(), action -> action));
        handlers = configRaw.getHandlers().stream()
                .collect(Collectors.toMap(handler -> handler.getPattern(), handler -> handler));

        for (Map.Entry<String, Action> entry : actions.entrySet()) {
            if (entry.getValue().getScope() == Action.ActionScope.BUILT_IN) {
                processors.put(entry.getValue(), ProcessorFactory.fromAction(entry.getValue()).orElse(new DefaultProcessor()));
            }
        }

        for (Map.Entry<String, Action> entry : actions.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue().getId());
        }
        for (Map.Entry<String, Handler> entry : handlers.entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
        for (Map.Entry<Action, Processor> entry : processors.entrySet()) {
            System.out.println(entry.getKey().getId()+": "+entry.getValue());
        }
    }

    public static void main(String[] args) {
        new Pipelife();
    }
}
