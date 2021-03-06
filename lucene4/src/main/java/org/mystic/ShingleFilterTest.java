package org.mystic;

import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ShingleFilterTest {

  public static void main(String[] args) throws IOException {

    String theSentence = "THiS Is a bIg doG";
    StringReader reader = new StringReader(theSentence);
    Tokenizer whitespaceTokenizer = new WhitespaceTokenizer(reader);
    whitespaceTokenizer.setReader(reader);
    TokenStream tokenStream = new StandardFilter(whitespaceTokenizer);
    tokenStream = new ShingleFilter(tokenStream, 2, 100);
    ((ShingleFilter) tokenStream).setOutputUnigrams(true);
    tokenStream = new LowerCaseFilter(tokenStream);

    final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();

    while (tokenStream.incrementToken()) {
      System.out.println(charTermAttribute.toString());
    }

    tokenStream.end();
    tokenStream.close();
  }
}
