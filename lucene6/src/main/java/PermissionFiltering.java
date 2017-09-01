import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.query.FilterQuery;

/**
 * @see https://stackoverflow.com/q/45997270/2663985
 */
public class PermissionFiltering {

    public static void main(String[] args) throws IOException {

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        addDocument(writer, "open post", "user");
        addDocument(writer, "secured post", "super-user");
        addDocument(writer, "very secured post", "admin");

        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new BooleanQuery.Builder()
            .add(new BooleanClause(new TermQuery(new Term("*:*")), Occur.MUST))
            .add(new BooleanClause(new TermQuery(new Term("id", "admin")), Occur.FILTER))
            .build();

        final ScoreDoc[] scoreDocs = searcher.search(query, 10).scoreDocs;
        for (ScoreDoc doc: scoreDocs) {
            System.out.println(doc.doc + " " + doc.score);
        }


    }

    private static void addDocument(IndexWriter writer, String title, String id) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Store.YES));
        doc.add(new StringField("id", id, Store.YES));
        writer.addDocument(doc);
    }
}
