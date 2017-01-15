package com.github.rskupnik.processors;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.github.rskupnik.EvernoteManager;

public abstract class AbstractProcessor implements Processor {

    protected String parseENML(String enml) {
        if (!enml.startsWith("<?xml"))
            return enml;

        int start = enml.indexOf("<div>") + 5;
        int end = enml.indexOf("</div>");
        return enml.substring(start, end).replace("<br/>", " ");
    }

    protected Note enrichNote(Note note) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        return EvernoteManager.getInstance().getNoteStore().getNote(note.getGuid(), true, false, false, false);
    }
}
