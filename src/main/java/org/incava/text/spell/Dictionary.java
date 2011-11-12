package org.incava.text.spell;

import java.io.Reader;
import java.io.InputStream;
import java.util.EnumSet;
import org.incava.ijdk.io.FileExt;
import static org.incava.ijdk.util.IUtil.*;

public class Dictionary extends WordList {
    public Dictionary(SpellingCaseType caseType, String dictName) {
        this(caseType, FileExt.readLines(dictName, EnumSet.of(FileExt.ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, Reader dictReader) {
        this(caseType, FileExt.readLines(dictReader, EnumSet.of(FileExt.ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, InputStream dictStream) {
        this(caseType, FileExt.readLines(dictStream, EnumSet.of(FileExt.ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, String[] words) {
        super(caseType);
        addAllWords(words);
    }
}
