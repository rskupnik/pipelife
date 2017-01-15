package com.github.rskupnik.processors;

import com.evernote.edam.type.Note;

public interface Processor {
    void process(Note note) throws Exception;
}
