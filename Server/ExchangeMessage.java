import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExchangeMessage extends Thread {
    Socket socket;
    User user;

    ExchangeMessage(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            //  新しいユーザーの初期設定、入室
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // データ受信用バッファの設定
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); // 送信バッファ設定

            String name = in.readLine();
            user = new User(name, socket);
            SocketController.register(socket);
            SocketController.SendLog(socket);
            SocketController.send("["+ user.name + "さんが入室しました]");
            System.out.println("enter:" + user.getLogData());

            //  データ受信、全ユーザへの送信
            while (true) {
                String type = in.readLine(); // データタイプの受信

                if (type.equals("END")) {   //  終了
                    out.println("END"); break;
                }   //  終了処理
                else if (type.equals("FILE")){ //  ファイル
                    byte[] buffer = new byte[512];

                    // ファイルサイズ、ファイル名を受信
                    int fileSize = Integer.parseInt(in.readLine());
                    String fileName = in.readLine();

                    //ファイル名変更し、作成
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                    String filePath = "./file/" + sdf.format(time.getTime()) + fileName;

                    File newFile = new File(filePath);
                    if(newFile.exists()){
                        int dot = fileName.length();
                        for(int i = 0; i < fileName.length(); i++){
                            if(fileName.charAt(i) == '.') dot = i;
                        }
                        int count = 1;
                        do {
                            StringBuilder sb = new StringBuilder();
                            sb.append(fileName);
                            sb.insert(dot, "(" + count + ")");
                            String newFilePath = "./file/" + sdf.format(time.getTime()) + new String(sb);
                            newFile = new File(newFilePath);
                            count++;
                        }while(newFile.exists());
                        newFile.createNewFile();
                    }
                    else { newFile.createNewFile(); }

                    BufferedInputStream  inputStream  = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFile));

                    //  ファイル受信
                    int bufSize;
                    for (int i = 0; i < (fileSize-1)/512+1; i++) {
                        bufSize = inputStream.read(buffer);
                        outputStream.write(buffer, 0, bufSize);
                    }

                    outputStream.flush();
                    outputStream.close();


                    System.out.println("file:" + user.getLogData() + " : {" + fileName + "}");
                    SocketController.sendFile(newFile, "[" + user.name + "さんがファイル{" + fileName + "}を送信しました]");

                }
                else if (type.equals("MESSAGE")) {  //  メッセージ
                    String message = in.readLine();
                    System.out.println("send:" + user.getLogData(message));
                    SocketController.send("[" + user.name + "さん] : " + message); // データの送信
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //  ユーザの退出、終了
            try {
                System.out.println("exit:" + user.getLogData());
                SocketController.send("["+ user.name + "さんが退出しました]");
                SocketController.remove(socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
