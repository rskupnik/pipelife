package com.github.rskupnik;

import com.github.rskupnik.parrot.Parrot;

public final class Pipelife {

    private static final String CONFIG_TRELLO_KEY = "trello_key";
    private static final String CONFIG_TRELLO_TOKEN = "trello_token";
    private static final String CONFIG_EVERNOTE_TOKEN = "evernote_token";

    private final Parrot config;

    private Pipelife() {
        config = new Parrot();
        if (!isConfigValid()) {
            System.err.println("Invalid config");
            System.exit(0);
        }
    }

    private boolean isConfigValid() {
        if (config == null)
            return false;

        String[] configEntries = new String[] {CONFIG_TRELLO_KEY, CONFIG_TRELLO_TOKEN, CONFIG_EVERNOTE_TOKEN};
        for (String configEntry : configEntries) {
            if (!config.get(configEntry).isPresent()) {
                System.err.println("Missing config entry: "+configEntry);
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        new Pipelife();
    }
}
