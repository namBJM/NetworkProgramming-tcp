import java.io.*;
import java.net.Socket;

public class Client2207 {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2207;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "BaUi7AuX"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi studentCode;qCode
            String request = STUDENT_CODE + ";" + Q_CODE;
            out.writeUTF(request);
            out.flush();
            System.out.println("Đã gửi: " + request);

            // b. Nhận lần lượt hai số nguyên a và b từ server
            int a = in.readInt();
            int b = in.readInt();
            System.out.println("Nhận được: a = " + a + ", b = " + b);

            // c. Tính tổng và tích, gửi lần lượt lên server
            int sum = a + b;
            int product = a * b;

            out.writeInt(sum);
            out.flush();
            System.out.println("Đã gửi tổng: " + sum);

            out.writeInt(product);
            out.flush();
            System.out.println("Đã gửi tích: " + product);

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                String serverReply = in.readUTF();
                System.out.println("Phản hồi từ server: " + serverReply);
            } catch (IOException e) {
                // Không bắt buộc có phản hồi cuối, bỏ qua nếu quá thời gian chờ
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // d. Kết nối tự động đóng nhờ try-with-resources
        System.out.println("Đã đóng kết nối.");
    }
}

// Mã câu hỏi

// BaUi7AuX
// Exam Server

// 36.50.135.242
// Một chương trình máy chủ cho phép kết nối qua TCP tại cổng 2207 (hỗ trợ thời gian liên lạc tối đa cho mỗi yêu cầu là 5s), yêu cầu xây dựng chương trình (tạm gọi là client) thực hiện kết nối tới server tại cổng 2207, sử dụng luồng byte dữ liệu (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:

// a. Gửi chuỗi là mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B15DCCN999;1D25ED92

// b. Nhận lần lượt hai số nguyên a và b từ server

// c. Thực hiện tính toán tổng, tích và gửi lần lượt từng giá trị theo đúng thứ tự trên lên server

// d. Đóng kết nối và kết thúc