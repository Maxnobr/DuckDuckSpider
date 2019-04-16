import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Index {

    private Page firstPage = null;
    private int pageCount = 0;
    public static List<String> crapWords = Arrays.asList("a","about","after","all","also","an","any","and","are","as","at","back",
            "be","been","being","between","but","by","can","com","could","do","did","first","for","from","have","havent",
            "had","has","have","her","him","his","how","i","if","in","into","is","isnt","it","its","just","like",
            "made","many","may","me","more","most","my","new","no","not","now","of","on","one","only","of","or",
            "out","other","our","over","said","see","she","should","shouldnt","so","some","than","that",
            "the","then","this","that","them","these","they","their","there","theyre","to","two","too","up","very",
            "time","up","was","wasnt","way","we","were","werent","what","when","where","which","who","will","with",
            "would","you","your","youre","yours");

    Index(String url, int maxPages){
        try {
            addPage(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page p = firstPage;
        while(pageCount < maxPages) {
            int pagesLeftToAdd = maxPages - pageCount;
            for(int i = 0; i < pagesLeftToAdd && i < p.links.size(); i++){
                try {
                    addPage(p.links.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(p.nextPage != null)
                p = p.nextPage;
            else{
                System.out.println("found "+pageCount+" / "+maxPages+" pages");
            }
        }
    }

    List<Page> getPages(boolean and,String... words){
        ArrayList<Page> matches = new ArrayList<>();
        Page temp = firstPage;
        int i = 1;
        while(temp != null) {
            System.out.print("Searching page #"+i++);
            temp.searchValue = -temp.words.findWord(and,words);
            if(temp.searchValue < 0){
                matches.add(temp);
                System.out.println(" hit : "+(-temp.searchValue));
            }
            else System.out.println();

            temp = temp.nextPage;
        }
        return matches;
    }

    private void addPage(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        Words words = new Words();

        Elements links = doc.select("a[href]");
        List<String> nLinks = new ArrayList<>();
        for (Element l : links) {
            String href = l.attr("abs:href");
            if(!nLinks.contains(href))
                nLinks.add(href);
        }

        //get text from title
        if(doc.title() != null)
            stringParser(doc.title()).iterator().forEachRemaining(s -> words.addWord(s,10));

        //get text from headers
        for(int i = 1;i <= 6 ; i++) {
            Elements headlines = doc.select("h"+i);
            for (Element l: headlines) {
                int finalI = i;
                stringParser(l.text()).iterator().forEachRemaining(s -> words.addWord(s,8 - finalI));
            }
        }

        //get the rest of the text
        stringParser(doc.wholeText()).iterator().forEachRemaining(s -> words.addWord(s,1));

        Page newOne = new Page(doc.title(),url,nLinks,words);
        pageCount++;
        System.out.println("Crawling Page #"+pageCount);
        if(firstPage == null) firstPage = newOne;
        else{
            Page temp = firstPage;
            //iterate temp till last page
            while(temp.nextPage != null) temp = temp.nextPage;
            temp.nextPage = newOne;
        }
    }

    private List<String> stringParser(String str){
        ArrayList<String> output = new ArrayList<>();
        String[] parsed = str.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+");
        for (String s: parsed) {
            s = s.replaceAll(" ","");
            if(s.length() > 1 && !crapWords.contains(s))
                output.add(s);
        }
        return output;
    }

    class Page{
        String title;
        String url;
        List<String> links;
        Words words;
        Page nextPage = null;
        int searchValue = 0;

        Page(String title,String url,List<String> links,Words words){
            this.title = title;
            this.url = url;
            this.links = links;
            this.words = words;
        }
    }
}
