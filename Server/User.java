import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class User {
    String name;
    Socket socket;

    public User(String name, Socket socket){
        this.name = name;
        this.socket = socket;
    }

    public String getLogData(){
        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return "[" + name + "(" + socket.getInetAddress() + "," + socket.getPort() + "){" + sdf.format(time.getTime()) + "}]";
    }

    public String getLogData(String message){
        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return "[" + name + "(" + socket.getInetAddress() + "," + socket.getPort() + "){" + sdf.format(time.getTime()) + "}]" + " : " + message;
    }
}
