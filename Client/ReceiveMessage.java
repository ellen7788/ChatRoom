import java.io.*;
import java.net.Socket;

//	受信をスレッド処理
public class ReceiveMessage extends Thread {
    Socket socket;
    BufferedReader in;
    ChatUI ui;
    String userName;

    ReceiveMessage(Socket socket, ChatUI ui, String userName){
        this.socket = socket;
        this.ui = ui;
        this.userName = userName;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            while (true) {
                String type = in.readLine(); // データタイプ受信

                if(type.equals("END")) { //  終了処理
                    break;
                }
                else if(type.equals("FILE")) {    //  ファイル
                    byte[] buf = new byte[512];

                    // ファイルサイズ、ファイル名を受信
                    int fileSize = Integer.parseInt(in.readLine());
                    String fileName = in.readLine();

                    File newFile = new File("./file/" + userName + "/" + fileName);

                    newFile.createNewFile();
                    BufferedInputStream inStream  = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(newFile));

                    //  ファイル受信
                    int bufSize;
                    for (int i = 0; i < (fileSize-1)/512+1; i++) {
                        bufSize = inStream.read(buf);
                        outStream.write(buf, 0, bufSize);
                    }

                    outStream.flush();
                    outStream.close();
                }
                else if(type.equals("MESSAGE")){ //メッセージ
                    String message = in.readLine();
                    System.out.println(message);
                    ui.Receive(message + '\n');
                }
                else{
                    System.out.println("不正なタイプです");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
