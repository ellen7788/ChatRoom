import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class SocketController {
    private static final ArrayList<Socket> sockets = new ArrayList<Socket>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static File logfile;
    private static File dir;

    //  必要ファイルを作成
    public static void init(){
        logfile = new File("log.txt");
        dir = new File("./file");
        try {
            if(logfile.exists()) logfile.delete();
            logfile.createNewFile();

            if(dir.exists()){
                File[] files = dir.listFiles();
                for(int i = 0; i < files.length; i++){
                    files[i].delete();
                }
                dir.delete();
            }
            dir.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  登録
    public static void register(Socket socket){
        lock.lock();
        sockets.add(socket);
        lock.unlock();
    }

    //  送信
    public static void send(String message){
        lock.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logfile, true));
            bw.write(message);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Socket socket: sockets) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println("MESSAGE");
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    //  ファイルを送信
    public static void sendFile(File file, String message){
        lock.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logfile, true));
            bw.write(message);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Socket socket: sockets) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Thread.sleep(Server.sleepTime);
                out.println("MESSAGE");
                out.println(message);

                out.println("FILE");

                //  ファイルサイズ、ファイル名を送信
                int fileSize = (int)file.length();
                out.println(fileSize);
                out.println(file.getName());

                byte[] buffer = new byte[512];
                BufferedInputStream  inputStream  = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());

                Thread.sleep(Server.sleepTime);
                //  ファイル送信
                int bufSize;
                for (int i = 0; i < (fileSize-1)/512+1; i++) {
                    bufSize = inputStream.read(buffer);
                    outputStream.write(buffer, 0, bufSize);
                }

                outputStream.flush();
                inputStream.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    //  途中参加者にログを送信
    public static void SendLog(Socket socket){
        lock.lock();
        try {
            BufferedReader br = new BufferedReader(new FileReader(logfile));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            String str;
            while((str = br.readLine()) != null){
                out.println("MESSAGE");
                out.println(str);
                System.out.println("send log:" + str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    //  削除
    public static void remove(Socket socket){
        lock.lock();
        sockets.remove(socket);
        lock.unlock();
    }
}
