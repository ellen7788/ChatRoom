import java.io.*;
import java.net.Socket;

//	��M���X���b�h����
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
                String type = in.readLine(); // �f�[�^�^�C�v��M

                if(type.equals("END")) { //  �I������
                    break;
                }
                else if(type.equals("FILE")) {    //  �t�@�C��
                    byte[] buf = new byte[512];

                    // �t�@�C���T�C�Y�A�t�@�C��������M
                    int fileSize = Integer.parseInt(in.readLine());
                    String fileName = in.readLine();

                    File newFile = new File("./file/" + userName + "/" + fileName);

                    newFile.createNewFile();
                    BufferedInputStream inStream  = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(newFile));

                    //  �t�@�C����M
                    int bufSize;
                    for (int i = 0; i < (fileSize-1)/512+1; i++) {
                        bufSize = inStream.read(buf);
                        outStream.write(buf, 0, bufSize);
                    }

                    outStream.flush();
                    outStream.close();
                }
                else if(type.equals("MESSAGE")){ //���b�Z�[�W
                    String message = in.readLine();
                    System.out.println(message);
                    ui.Receive(message + '\n');
                }
                else{
                    System.out.println("�s���ȃ^�C�v�ł�");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
