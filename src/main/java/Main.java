import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private static final Properties props = new Properties();

    public static void main(String[] args) throws IOException {
        props.load(new FileInputStream("setting.txt"));
        String host = (props.getProperty("HOST"));
        int port = Integer.parseInt(props.getProperty("PORT"));
        new Client(host, port);
    }
}