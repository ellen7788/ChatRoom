import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	public static int PORT;
	public static InetAddress addr;
	public static int sleepTime = 50;

	public static void main(String[] args) throws IOException {
		PORT = 8080;
		addr = InetAddress.getByName("localhost"); // IP �A�h���X�ւ̕ϊ�

		if(args.length != 0){
			for(int i = 0; i < args.length; i+=2){
				if(args[i].equals("-p") || args[i].equals("--port")){
					PORT = Integer.parseInt(args[i + 1]);
				}
				else if(args[i].equals("-a") || args[i].equals("--address")){
					addr = InetAddress.getByName(args[i+1]);
				}
				else if(args[i].equals("-s") || args[i].equals("--sleep")){
					sleepTime = Integer.parseInt(args[i+1]);
				}
			}
		}

		Scanner scanner = new Scanner(System.in);
		System.out.print("���[�U������͂��Ă��������F");
		String name = scanner.nextLine();

		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, PORT); // �\�P�b�g�̐���
		try {
			//	�t�@�C���ۑ��ꏊ�̍쐬
			File dir = new File("./file");
			if(!dir.exists()) dir.mkdir();

			File userDir = new File("./file/" + name);
			if(userDir.exists()){
				File[] files = userDir.listFiles();
				for(int i = 0; i < files.length; i++){
					files[i].delete();
				}
				userDir.delete();
			}
			userDir.mkdir();

			System.out.println("socket = " + socket);

			//	���͏�����ChatUI��
			ChatUI ui = new ChatUI(name, socket);

		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
