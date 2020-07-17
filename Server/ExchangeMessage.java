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
            //  �V�������[�U�[�̏����ݒ�A����
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // �f�[�^��M�p�o�b�t�@�̐ݒ�
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); // ���M�o�b�t�@�ݒ�

            String name = in.readLine();
            user = new User(name, socket);
            SocketController.register(socket);
            SocketController.SendLog(socket);
            SocketController.send("["+ user.name + "���񂪓������܂���]");
            System.out.println("enter:" + user.getLogData());

            //  �f�[�^��M�A�S���[�U�ւ̑��M
            while (true) {
                String type = in.readLine(); // �f�[�^�^�C�v�̎�M

                if (type.equals("END")) {   //  �I��
                    out.println("END"); break;
                }   //  �I������
                else if (type.equals("FILE")){ //  �t�@�C��
                    byte[] buffer = new byte[512];

                    // �t�@�C���T�C�Y�A�t�@�C��������M
                    int fileSize = Integer.parseInt(in.readLine());
                    String fileName = in.readLine();

                    //�t�@�C�����ύX���A�쐬
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

                    //  �t�@�C����M
                    int bufSize;
                    for (int i = 0; i < (fileSize-1)/512+1; i++) {
                        bufSize = inputStream.read(buffer);
                        outputStream.write(buffer, 0, bufSize);
                    }

                    outputStream.flush();
                    outputStream.close();


                    System.out.println("file:" + user.getLogData() + " : {" + fileName + "}");
                    SocketController.sendFile(newFile, "[" + user.name + "���񂪃t�@�C��{" + fileName + "}�𑗐M���܂���]");

                }
                else if (type.equals("MESSAGE")) {  //  ���b�Z�[�W
                    String message = in.readLine();
                    System.out.println("send:" + user.getLogData(message));
                    SocketController.send("[" + user.name + "����] : " + message); // �f�[�^�̑��M
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //  ���[�U�̑ޏo�A�I��
            try {
                System.out.println("exit:" + user.getLogData());
                SocketController.send("["+ user.name + "���񂪑ޏo���܂���]");
                SocketController.remove(socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
