import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Article {

    private String title, url;
    private int rating, nbrOfComments, index;
    private LocalDate dateOfCreation;
    private static String txt;

    public Article(String s, boolean b) {
        try {
            txt = s;
            setUrl();
            setIndex();
            setDateOfCreation();
            setTitle();
            setRating();
            setNbrOfComments();
        } catch (Exception e) {
            System.out.println("\nException 1 in " + index);
            e.printStackTrace();
        }
    }

    public Article(String s) {
        try {
            txt = s;
            //System.out.println("\n" + s);//SOUT
            String[] args = s.split(",\t");

            String str = findMatch("(\\d+)", args[0]);
            index = Integer.parseInt(str);

            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateOfCreation = LocalDate.parse(args[1], f);

            title = findMatch("title='(.+?)'", args[2]);

            str = findMatch("(\\d++)", args[3]);
            rating = Integer.parseInt(str);

            str = findMatch("(\\d++)", args[4]);
            nbrOfComments = Integer.parseInt(str);
        } catch (Exception e) {
            System.out.println("\nException 2 in " + index);
            e.printStackTrace();
        }
    }

    public Article() {}

    private String findMatch(String REGEX, String txt) {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(txt);
        if (m.find()) {
            return m.group(1);
        } else
            return null;
    }

    private void setTitle() {
        String REGEX = "<title>(.+?) /.+?</title>";
        String s = findMatch(REGEX, txt);
        this.title = s.replaceAll(String.valueOf((char) 9), " ");

    }

    private void setDateOfCreation() {
        String REGEX = "data-time_published=\"(.+?)T";
        String dateOfCreation = findMatch(REGEX, txt);

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.dateOfCreation = LocalDate.parse(dateOfCreation, f);
    }

    private void setUrl() {
        String REGEX = "<meta property=\"og:url\" content=\"(https://.+?)\"";
        this.url = findMatch(REGEX, txt);
    }

    private void setRating() {
        String REGEX = "Всего голосов.+?>(.*)</span>";
        String s = findMatch(REGEX, txt).replaceAll("–", "-");
        int i = Integer.parseInt(s);
        this.rating = i;
    }

    private void setNbrOfComments() {
        String REGEX = "<span class=\"comments-section__head-counter\" id=\"comments_count\">\n" +
                "          (.*)\n";
        String s = findMatch(REGEX, txt);
        this.nbrOfComments = Integer.parseInt(s);
    }

    private void setIndex() {
        String REGEX = "https://.+?/(\\d*)/";
        String s = findMatch(REGEX, url);
        this.index = Integer.parseInt(s);
    }

    public boolean isLegal(TargetArticle t) {
        if ((rating >= t.getRatingMin() && rating <= t.getRatingMax()) &&
                (nbrOfComments >= t.getCommMin() && nbrOfComments <= t.getCommMax()) &&
                ((dateOfCreation.isAfter(t.getDateStart()) && dateOfCreation.isBefore(t.getDateEnd()) ||
                dateOfCreation.isEqual(t.getDateStart()) || dateOfCreation.isEqual(t.getDateEnd())))) {
            return true;
        }
        return false;
    }

    public boolean dateIsLegal(TargetArticle t) {
        if ((dateOfCreation.isAfter(t.getDateStart()) || dateOfCreation.isEqual(t.getDateStart()) &&
                dateOfCreation.isBefore(t.getDateEnd()) || dateOfCreation.isEqual(t.getDateEnd())))
            return true;
        return false;
    }

    public String toString() {
        return "index='" + index + "',\t" + dateOfCreation +
                ",\ttitle='" + title + "'" +
                ",\trating=" + rating +
                ",\tnbrOfComments=" + nbrOfComments;
    }

    public LocalDate getDate() {
        return dateOfCreation;
    }

    public int getIndex() {
        return index;
    }

}