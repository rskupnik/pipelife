package com.github.rskupnik;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.github.rskupnik.parrot.Parrot;

import java.util.List;

public final class Pipelife {

    private static final String CONFIG_TRELLO_KEY = "trello_key";
    private static final String CONFIG_TRELLO_TOKEN = "trello_token";
    private static final String CONFIG_EVERNOTE_TOKEN = "evernote_token";
    private static final String CONFIG_INPUT_NOTEBOOK = "input_notebook";

    private final String TRELLO_KEY;
    private final String TRELLO_TOKEN;
    private final String EVERNOTE_TOKEN;
    private final String INPUT_NOTEBOOK;

    private final Parrot config;
    private UserStoreClient userStore;
    private NoteStoreClient noteStore;

    private Pipelife() {
        config = new Parrot();
        if (!isConfigValid()) {
            System.err.println("Invalid config");
            System.exit(-1);
        }

        TRELLO_KEY = config.get(CONFIG_TRELLO_KEY).orElseThrow(IllegalStateException::new);
        TRELLO_TOKEN = config.get(CONFIG_TRELLO_TOKEN).orElseThrow(IllegalStateException::new);
        EVERNOTE_TOKEN = config.get(CONFIG_EVERNOTE_TOKEN).orElseThrow(IllegalStateException::new);
        INPUT_NOTEBOOK = config.get(CONFIG_INPUT_NOTEBOOK).orElseThrow(IllegalStateException::new);

        try {
            initializeEvernote();
            List<Note> notes = getNotes();
            for (Note note : notes) {
                System.out.println(note.getTitle() + ": "+note.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Initializes connection to Evernote
     */
    private void initializeEvernote() throws Exception {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, EVERNOTE_TOKEN);
        ClientFactory clientFactory = new ClientFactory(evernoteAuth);
        userStore = clientFactory.createUserStoreClient();

        boolean versionOk = userStore.checkVersion("Pipelife",
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            System.err.println("Incompatible Evernote client protocol version");
            System.exit(1);
        }

        noteStore = clientFactory.createNoteStoreClient();
    }

    /**
     * Receives all notes from Evernote from the specified input notebook
     */
    private List<Note> getNotes() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {
        Notebook inputNotebook = null;
        for (Notebook notebook : noteStore.listNotebooks()) {
            if (notebook.getName().equals(INPUT_NOTEBOOK)) {
                inputNotebook = notebook;
                break;
            }
        }

        if (inputNotebook == null)
            throw new IllegalStateException("Input notebook not found: "+INPUT_NOTEBOOK);

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(inputNotebook.getGuid());
        filter.setOrder(NoteSortOrder.CREATED.getValue());
        filter.setAscending(true);

        return noteStore.findNotes(filter, 0, 100).getNotes();
    }

    private boolean isConfigValid() {
        if (config == null)
            return false;

        String[] configEntries = new String[] {CONFIG_TRELLO_KEY, CONFIG_TRELLO_TOKEN, CONFIG_EVERNOTE_TOKEN, CONFIG_INPUT_NOTEBOOK};
        for (String configEntry : configEntries) {
            if (!config.get(configEntry).isPresent() || config.get(configEntry).equals("")) {
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
