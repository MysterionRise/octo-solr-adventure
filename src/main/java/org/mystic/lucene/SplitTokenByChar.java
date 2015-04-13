package org.mystic.lucene;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
* http://stackoverflow.com/q/29595502/2663985 - Implement custom token filter for that
  It's also possible to user NGramTokenizer
*/
public class SplitTokenByChar {

    public static void main(String[] args) throws IOException {

        String theSentence = "GD52KHC GFD3 sdfsdf";
        Tokenizer whitespaceTokenizer = new WhitespaceTokenizer();
        whitespaceTokenizer.setReader(new StringReader(theSentence));
        TokenStream tokenStream = new CharDelimeterTokenFilter(whitespaceTokenizer);
        final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        tokenStream.end();
        tokenStream.close();
    }

    static class CharDelimeterTokenFilter extends TokenFilter {
        private char[] curTermBuffer;
        private int curPos;
        private int curLen;
        private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

        /**
         * Construct a token stream filtering the given input.
         *
         * @param input
         */
        protected CharDelimeterTokenFilter(TokenStream input) {
            super(input);
        }

        @Override
        public boolean incrementToken() throws IOException {
            while (true) {
                if (curTermBuffer == null) {
                    if (!input.incrementToken()) {
                        return false;
                    } else {
                        curTermBuffer = termAtt.buffer().clone();
                        curLen = ((PackedTokenAttributeImpl) termAtt).endOffset() - ((PackedTokenAttributeImpl) termAtt).startOffset();
                    }
                } else {
                    if (curPos < curLen) {
                        termAtt.copyBuffer(curTermBuffer, curPos, 1);
                        curPos++;
                        return true;
                    } else {
                        curTermBuffer = null;
                        curPos = 0;
                    }
                }
            }
        }


        @Override
        public void reset() throws IOException {
            super.reset();
            curTermBuffer = null;
            curPos = 0;
        }
    }


}
