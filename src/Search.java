import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {
    static private int nbrOfRequests = 0;

    public static int puk(TargetArticle t, int maxIndex) throws IOException {

        System.out.println("\nsearch nbr 0:");//SOUT
        Article a = bSearch(t.getDateStart().minusDays(3), maxIndex);
        int n = 4;
        int offset = (maxIndex - a.getIndex()) / n;

        int i = 1;
        int maxId = 0;
        while (i < n) {
            System.out.println("\nsearch nbr " + i + ":");
            Article temp = bSearch(t.getDateStart().minusDays(3), maxIndex - offset * i++);
            System.out.println("\n" + temp.toString());//SOUT
            if (!temp.getDate().isAfter(t.getDateStart())) {
                maxId = temp.getIndex() > maxId ? temp.getIndex() : maxId;
            }
         }
        //System.out.println(maxId);//SOUT
        int legalPostCount = 0, illegalPostCount = 0;
        FileWriter f = new FileWriter(t.generateFileName());
        while (illegalPostCount < 7) {
            Article temp = new Article(Search.getValidSource(maxId));
            maxId = updateIndexValue(temp) + 1;
            if (temp.isLegal(t)) {
                System.out.println(temp.toString());
                f.append(temp.toString() + " url: " + generateLink(temp.getIndex()));
                f.append(System.lineSeparator());
                illegalPostCount = 0;
            }else {
                System.out.println("\t\t" + temp.toString());
                if (temp.getDate().isAfter(t.getDateEnd())) illegalPostCount++;
                else illegalPostCount = 0;
            }
        }
        f.close();
        return nbrOfRequests;

    }

    static Article bSearch(LocalDate dateToFind, int maxId) {
        int l = 1, r = maxId + 1;
        int mid = (l + r) / 2, midChecker = 0;
        Article artL = new Article();
        while (mid != midChecker /*|| r - l != 1*/) {
            //System.out.printf("\nl = %d, r = %d, mid = %d, midChecker = %d\n", l, r, mid, midChecker);//SOUT
            Article artMid = new Article(getValidSource(mid));
            midChecker = mid;
            mid = updateIndexValue(artMid);
            artL = new Article(getValidSource(l));
            l = updateIndexValue(artL);
            if (dateToFind.isEqual(artL.getDate())) {
                addToDB("DB-articles-list", artL.toString());
                return artL;
            }
            if (dateToFind.isEqual(artMid.getDate()) ||
                    dateToFind.isAfter(artMid.getDate()))
                l = mid;
            else r = mid;
            mid = (l + r) / 2;

            if (mid == midChecker ) {
                return artL;
            }
        }
        return artL;
    }

    static String getValidSource(int index) {
        String s;
        while (true) {
            if (isInList("empty", index) != null) {
                index++;
            }else if ((s = isInList("articles", index)) != null) {
                return s;
            }else {
                if ((s = getSource(generateLink(index))) != null) {
                    Article art = new Article(s, true);
                    addToDB("DB-articles-list", art.toString());
                    return art.toString();
                }else {
                    addToDB("DB-empty-pages", "'" + index + "'");
                    index++;
                }
            }
        }
    }

    static int updateIndexValue(Article a){
        return a.getIndex();
    }

    static String generateLink(int index) {
        return "https://habr.com/ru/post/" + index + "/";
    }

    static String isInList (String fileName, int index) {
        String regex = new String(), toPrint = null;
        if (fileName.equals("empty")) {
            fileName = "DB-empty-pages";
            regex = "'" + index + "'";
            toPrint = "e";
        }
        else if (fileName.equals("articles")) {
            fileName = "DB-articles-list";
            regex = "index='" + index + "'";
            toPrint = "a";
        }
        try {
            FileReader f = new FileReader(fileName);
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains(regex)) {
                    System.out.print(toPrint);
                    return s;
                }
            }
            f.close();
            sc.close();
            return null;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(fileName + " open failed");
            return null;
        }
    }

    static void addToDB(String fileName, String data) {//сделать чтобы добавлял в алфавитном порядке
        try {
            FileWriter f = new FileWriter(fileName, true);
            f.append(data);
            f.append(System.lineSeparator());
            f.close();

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(fileName + " open failed");
        }
    }

    static String getSource(String pageURL) {
        nbrOfRequests++;
        //System.out.println(pageURL);
        System.out.print(".");
        try {
            URL url = new URL(pageURL);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            //Thread.sleep(8000);
            return sb.toString();
        } catch (Exception e) {
            //   e.printStackTrace();
            return null;
        }
    }

    public static int getMaxId() {
        String regex = "shortcuts_item\" id=\"post_(\\d+?)\">";
        Pattern p = Pattern.compile(regex);
        String source = getSource("https://habr.com/ru/all/");
        Matcher m = p.matcher(source);
        int maxId = 0;
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            maxId = id > maxId ? id : maxId;
        }
        return maxId;
    }

    static String isInArticlesList (int index) {
        String strIndex = "index='" + index + "'";
        try {
            FileReader f = new FileReader("DB-articles-list");
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String s;
                if ((s = sc.nextLine()).contains(strIndex)) { //add substring(0, 10) ?
                    System.out.print("a");
                    return s;
                }
            }
            f.close();
            sc.close();
            return null;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("DB-articles-list open failed");
            return null;
        }
    } //deprecated

    static boolean isInEmptyPagesList(int index) {
        try {
            FileReader f = new FileReader("DB-empty-pages");
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                if (Integer.parseInt(sc.nextLine()) == index) {
                    System.out.print("e");
                    return true;
                }
            }
            f.close();
            sc.close();
            return false;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("DB-empty-pages open failed");
            return false;
        }
    } //deprecated

}
