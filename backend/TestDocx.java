import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.util.List;

public class TestDocx {
    public static void main(String[] args) throws Exception {
        XWPFDocument doc = new XWPFDocument(new FileInputStream("/home/cc/server/java-exam-paper-system/backend/src/main/resources/学号_姓名_答题纸A卷.docx"));
        List<IBodyElement> elements = doc.getBodyElements();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement elem = elements.get(i);
            if (elem instanceof XWPFParagraph) {
                System.out.println("Paragraph " + i + ": " + ((XWPFParagraph)elem).getText());
            } else if (elem instanceof XWPFTable) {
                System.out.println("Table " + i + " with " + ((XWPFTable)elem).getRows().size() + " rows");
            }
        }
    }
}
