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
import com.evernote.thrift.transport.TTransportException;

import java.util.List;

public final class EvernoteManager {

    private static EvernoteManager INSTANCE;

    public static EvernoteManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EvernoteManager();

        return INSTANCE;
    }

    private UserStoreClient userStore;
    private NoteStoreClient noteStore;

    public void init(String token) throws TException, EDAMSystemException, EDAMUserException {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, token);
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

    public List<Note> getNotes(String notebookName) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {
        Notebook inputNotebook = null;
        for (Notebook notebook : noteStore.listNotebooks()) {
            if (notebook.getName().equals(notebookName)) {
                inputNotebook = notebook;
                break;
            }
        }

        if (inputNotebook == null)
            throw new IllegalStateException("Input notebook not found: "+notebookName);

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(inputNotebook.getGuid());
        filter.setOrder(NoteSortOrder.CREATED.getValue());
        filter.setAscending(true);

        return noteStore.findNotes(filter, 0, 100).getNotes();
    }

    public UserStoreClient getUserStore() {
        return userStore;
    }

    public NoteStoreClient getNoteStore() {
        return noteStore;
    }
}
