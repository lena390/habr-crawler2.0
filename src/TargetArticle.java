import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TargetArticle {
    private static int ratingMin = Integer.MIN_VALUE, ratingMax = Integer.MAX_VALUE,
            commMin = Integer.MIN_VALUE, commMax = Integer.MAX_VALUE;
    private static LocalDate dateStart = LocalDate.now().minusDays(1),
            dateEnd = LocalDate.now();

    public TargetArticle(String args[]) { //dateStart, dateEnd, rating, comments
        if (args.length == 0) return;

        try {
            setPeriodOfTime(args);
            if (args.length == 3)
                setRating(args[2]);
            else if (args.length == 4) {
                setRating(args[2]);
                setComments(args[3]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Arguments parse error. Please enter a valid date 'dd.MM.yy'");
            System.exit(-1);
        }
    }

    private void setPeriodOfTime(String[] args) {
        dateStart = setDate(args[0]);
        dateEnd = setDate(args[1]);
        if (dateStart.isAfter(dateEnd)) {
            LocalDate temp;
            temp = dateStart;
            dateStart = dateEnd;
            dateEnd = temp;
        }

    }
    private LocalDate setDate(String strDate) {
        DateTimeFormatter f;
        if (strDate.length() == 8) {
            f = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.ENGLISH);
        }else if (strDate.length() == 7) {
            f = DateTimeFormatter.ofPattern("d.MM.yy", Locale.ENGLISH);
        }else return null;

        LocalDate res = LocalDate.parse(strDate, f);
        LocalDate firstArticleOnSite = LocalDate.of(2006, 07, 13);
        return res.isBefore(firstArticleOnSite) ? firstArticleOnSite : res;
    }

    private void setRating(String s) {
        if (s.contains("-")) {
            int index = s.indexOf("-");
            ratingMin = Integer.parseInt(s.substring(0, index));
            ratingMax = Integer.parseInt(s.substring(index + 1));
            if (ratingMin > ratingMax) {
                int temp = ratingMin;
                ratingMin = ratingMax;
                ratingMax = temp;
            }
        }else
            ratingMin = Integer.parseInt(s);
    }

    private void setComments(String s) {
        if (s.contains("-")) {
            int index = s.indexOf("-");
            commMin = Integer.parseInt(s.substring(0, index));
            commMax = Integer.parseInt(s.substring(index + 1));
            if (commMin > commMax) {
                int temp = commMin;
                commMin = commMax;
                commMax = temp;
            }
        }else
            commMin = Integer.parseInt(s);
    }

    @Override
    public String toString() {
        return "Template{ Date: " + dateStart + " - " + dateEnd +
                ", rating " + ratingMin + " - " + ratingMax +
                ", comm " + commMin + " - " + commMax +
                " }";
    }
    public String generateFileName() {
        String sRes, sRating, sComments, sDate;
        sRating = generateSubstring(ratingMin, ratingMax, "rating");
        sComments = generateSubstring(commMin, commMax, "comments");
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yy");
        sDate = dateStart.format(f) + "-" + dateEnd.format(f);
        return "new_search_" + sDate + sRating + sComments + "_" + LocalDateTime.now();
    }

    private String generateSubstring(int min, int max, String name) {
        String res;
        if (min != Integer.MIN_VALUE) {
            if (max != Integer.MAX_VALUE) res = "*" + name + min + "-" + max;
            else res = "*" + name + min;
        }else res = "";
        return res;
    }

    public static int getRatingMin() {
        return ratingMin;
    }
    public static int getRatingMax() {
        return ratingMax;
    }
    public static int getCommMin() {
        return commMin;
    }
    public static LocalDate getDateEnd() {
        return dateEnd;
    }
    public static int getCommMax() {
        return commMax;
    }
    public static LocalDate getDateStart() {
        return dateStart;
    }
}
