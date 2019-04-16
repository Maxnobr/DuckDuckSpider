import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Crawling...");
        Index ind = new Index("https://www.petfinder.com/cat-breeds/collections/cutest-cat-breeds/",100);
        String search = "cat";
        System.out.println("Searching...");
        String[] query = search.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+");
        ArrayList<String> parsed = new ArrayList<>();
        for (String s: query) {
            s = s.replaceAll(" ","");
            if(s.length() > 1 && (!Index.crapWords.contains(s) || s.equals("or")))
                parsed.add(s);
        }
        query = new String[parsed.size()];
        parsed.toArray(query);
        List<Index.Page> pages = ind.getPages(!parsed.contains("or"),query);
        if(pages.size() == 0)
            System.out.println("No results");

        pages.sort(Comparator.comparingInt(p -> p.searchValue));
        final int[] i = {1};
        pages.iterator().forEachRemaining(s -> {
            System.out.println(i[0] +". "+s.title);
            System.out.println(s.url);
            i[0]++;
        });
    }
}
