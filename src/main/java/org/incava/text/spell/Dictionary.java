package org.incava.text.spell;

import java.io.InputStream;
import java.io.Reader;
import java.util.EnumSet;
import java.util.List;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.io.IO;
import org.incava.ijdk.io.InputStreamExt;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
import static org.incava.ijdk.util.IUtil.*;

public class Dictionary extends WordList {
    public Dictionary(SpellingCaseType caseType, String dictName) {
        this(caseType, IO.readLines(dictName, EnumSet.of(ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, Reader dictReader) {
        this(caseType, ReaderExt.readLines(dictReader, EnumSet.of(ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, InputStream dictStream) {
        this(caseType, InputStreamExt.readLines(dictStream, EnumSet.of(ReadOptionType.NONEMPTY)));
    }

    public Dictionary(SpellingCaseType caseType, List<String> words) {
        super(caseType);
        addAllWords(words);
    }
}
