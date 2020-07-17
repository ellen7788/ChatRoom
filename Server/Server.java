import java.io.*;
import java.net.*;

public class Server {
	public static int PORT = 8080; // ポート番号を設定する．
	public static int sleepTime = 50;

	public static void main(String[] args) throws IOException {
		if(args.length != 0){
			for(int i = 0; i < args.length; i++){
				if(args[i].equals("-p") || args[i].equals("--port")){
					PORT = Integer.parseInt(args[i+1]);
					i++;
				}
				else if(args[i].equals("-s") || args[i].equals("--sleep")){
					sleepTime = Integer.parseInt(args[i+1]);
					i++;
				}
				else{
					PORT = Integer.parseInt(args[i]);
				}
			}
		}

		SocketController.init();

		ServerSocket s = new ServerSocket(PORT); // ソケットを作成する
		System.out.println("Started: " + s);
		try {
			while (true){
				Socket socket = s.accept(); // コネクション設定要求を待つ
				System.out.println("Connection accepted: " + socket);
				ExchangeMessage exMess = new ExchangeMessage(socket);
				exMess.start();
			}
		} finally {
			s.close();
		}
	}
}