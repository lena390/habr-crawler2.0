import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

class Main {

    public static void main(String[] args) throws IOException {

        Instant start = Instant.now();
        String[] arr = {"14.07.19", "15.07.19"};

        TargetArticle t = new TargetArticle(args);
        int constID = Search.getMaxId();
        int i = 0;
        i = Search.puk(t, constID);

        System.out.println("nbr of requests = " + i);

        System.out.println(Duration.between(start, Instant.now()));
    }
}