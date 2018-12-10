
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class main {
    public static void getImgMsg(InputStream input) throws IOException {
        //同样是先读长度


        FileOutputStream fos = new FileOutputStream("server.bmp");
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = input.read(buf)) != -1)
        {
            fos.write(buf,0,len);
        }

        fos.close();
    }

    public static void main(String[] args) {
        for (;;) {
            try {
                ServerSocket server = new ServerSocket(40000);
                System.out.println("waiting");
                Socket socket = server.accept();
                System.out.println("處理中");
                getImgMsg(socket.getInputStream());
                System.out.println("ok");

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("Hello Client,I get the message.".getBytes("UTF-8"));
                outputStream.close();
                socket.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

