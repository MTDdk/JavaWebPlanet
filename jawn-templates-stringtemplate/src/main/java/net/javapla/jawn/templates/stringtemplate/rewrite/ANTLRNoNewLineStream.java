package net.javapla.jawn.templates.stringtemplate.rewrite;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.antlr.runtime.ANTLRStringStream;

import net.javapla.jawn.core.util.AsyncBufferedReader;

/**
 * This is purposefully not optimised for speed, as it only ought to be
 * for reading files, and it should be up to other classes to cache the
 * results from this.
 * 
 * @author MTD
 */
public class ANTLRNoNewLineStream extends ANTLRStringStream {
    
    protected final static int EXPECTED_LINE_LENGTH = 180;
    
    public ANTLRNoNewLineStream(URL f) throws IOException {
        this(f.openStream(), null);
    }
    
    public ANTLRNoNewLineStream(URL f, String encoding) throws IOException {
        this(f.openStream(), encoding);
    }
    
    public ANTLRNoNewLineStream(InputStream input, String encoding) throws IOException {
        
        final InputStreamReader isr;
        if ( encoding!=null ) {
            isr = new InputStreamReader(input, encoding);
        } else {
            isr = new InputStreamReader(input);
        }
        
        // load the file
        try (AsyncBufferedReader reader = new AsyncBufferedReader(isr)) {
            final StringBuilder bob = new StringBuilder(EXPECTED_LINE_LENGTH);
            
            reader.lines(true).forEach(line -> trim(bob, line));
            
            this.data = bob.toString().toCharArray();
            this.n = bob.length();
        }
    }
    
    
    /**
     * Trim the string but keep a single space in the end of the string if one or more is present.
     * The original String.trim() removes all characters such as spaces and newlines: (character &lt;= ' ').
     * 
     * Keeping a single space is needed as the removal of all newline characters AND spaces results in
     * strings suddenly missing correct spacing in the words.
     * However, skip the extra space if the line starts with a tag start '&lt;', as the previous line
     * probably ended anyway.
     * 
     * At this point in the execution we know that \r and \n are removed completely (due to {@linkplain AsyncBufferedReader}),
     * so we only need to look at the spaces surrounding the line.
     * 
     * @see {@linkplain String#trim()}
     * @param bob
     * @param line
     */
    protected static final void trim(StringBuilder bob, String line) {
        int length = line.length();
        final int end = trimEndOfLine(line, length);
        if (end > 0) {
            final int start = trimStartOfLine(line, length);
            if (start > 0 && line.charAt(start) != '<') {
                bob.append(' ');
            }
            bob.append(line, start, end);
        }
    }
    
    
    protected static final int trimStartOfLine(CharSequence s, final int len) {
        int st = 0;

        while ((st < len) && (s.charAt(st) <= ' ')) {
            st++;
        }
        return st;
    }
    protected static final int trimEndOfLine(CharSequence s, int len) {
        while ((0 < len) && (s.charAt(len - 1) <= ' ')) {
            len--;
        }
        return (len > 0) ? len : 0;
    }
}
