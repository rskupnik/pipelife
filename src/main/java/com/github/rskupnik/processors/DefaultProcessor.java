package com.github.rskupnik.processors;

import com.evernote.edam.type.Note;

public final class DefaultProcessor extends AbstractProcessor {

    @Override
    public void process(Note note) {
        System.out.println("No processor defined so this note will be ignored: "+note.getTitle());
    }
}
