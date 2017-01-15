package com.github.rskupnik.processors;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.github.rskupnik.ConfigManager;
import com.github.rskupnik.EvernoteManager;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;

public final class TrelloTodoProcessor extends AbstractProcessor {

    private static final String TRELLO_LIST = "TRELLO-LIST";
    private static final String TRELLO_KEY = "TRELLO-KEY";
    private static final String TRELLO_TOKEN = "TRELLO-TOKEN";

    private Trello trello = null;

    @Override
    public void process(Note note) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        // First get actual contents of the note
        note = enrichNote(note);
        String content = parseENML(note.getContent());

        // Put a new to-do item in the designated trello board
        org.trello4j.model.List list = getTrello().getList(ConfigManager.getInstance().getConfigEntry(TRELLO_LIST).orElseThrow(IllegalStateException::new));
        getTrello().createCard(list.getId(), "Todo: "+content, null);

        // Delete the note
        EvernoteManager.getInstance().getNoteStore().deleteNote(note.getGuid());

        // Log the action
        System.out.println("Created new Trello TODO: "+(content.length() > 32 ? content.substring(0, 32) : content));
    }

    private Trello getTrello() {
        if (trello == null)
            trello = new TrelloImpl(ConfigManager.getInstance().getConfigEntry(TRELLO_KEY).orElseThrow(IllegalStateException::new),
                    ConfigManager.getInstance().getConfigEntry(TRELLO_TOKEN).orElseThrow(IllegalStateException::new));

        return trello;
    }
}
