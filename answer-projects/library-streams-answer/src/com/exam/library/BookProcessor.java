import java.util.List;
import java.util.stream.Collectors;

class Book {
    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class BookProcessor {
    public List<String> processList(List<Book> list) {
        return list.stream()
                   .filter(book -> book.isValid())
                   .map(book -> book.getName())
                   .collect(Collectors.toList());
    }
}