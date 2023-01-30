import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Thread {
    private static final String COMMAND_CLOSE_CHAT = "\\exit";
    private static final String FILE_NAME = "logger.txt";
    private static final File FILE_LOGGER = new File(FILE_NAME);

    private BufferedReader in;
    private BufferedReader inputUser;
    private BufferedWriter out;
    private Socket clientSocket;

    private String clientName;


    public Client(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Не удалось подключиться");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            this.writeNickname();
            createFile();

            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException e) {
            Client.this.downService();
        }
    }

    private void writeNickname() {
        System.out.print("Пожалуйста, напишите своё имя: ");
        try {
            this.clientName = inputUser.readLine();
            out.write(this.clientName + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    class ReadMessage extends Thread {
        @Override
        public void run() {
            String text;
            try {
                while (true) {
                    text = in.readLine();
                    if (text.equals(COMMAND_CLOSE_CHAT)) {
                        Client.this.downService();
                        break;
                    }
                    writeFileLog(text);
                    System.out.println(text);
                }
            } catch (IOException e) {
                Client.this.downService();
            }
        }
    }


    private class WriteMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String textChat = inputUser.readLine();

                    Date time = new Date();
                    SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String dtime = dt1.format(time);

                    if (COMMAND_CLOSE_CHAT.equals(textChat)) {
                        out.write(COMMAND_CLOSE_CHAT + "\n");
                        out.flush();
                        Client.this.downService();
                        break;
                    }
                    out.write("(" + dtime + ") " + clientName + ": " + textChat + "\n");
                    out.flush();

                } catch (IOException e) {
                    Client.this.downService();
                }
            }
        }
    }

    private void downService() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }
    }

    public static void createFile() throws IOException {
        File fileSrc = FILE_LOGGER;
        if (!fileSrc.exists())
            fileSrc.createNewFile();
    }

    public static void writeFileLog(String log) throws IOException {
        try (FileWriter writer = new FileWriter(FILE_LOGGER, true)) {
            writer.write(log + "\n");
        } catch (IOException ignored) {
        }
    }
}