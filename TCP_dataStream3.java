import java.io.*;
import java.net.Socket;

public class Client2207b {

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

            // b. Nhận kích thước mảng n và các phần tử a1..an từ server
            int n = in.readInt();
            int[] numbers = new int[n];
            for (int i = 0; i < n; i++) {
                numbers[i] = in.readInt();
            }
            System.out.print("Nhận được mảng: ");
            for (int num : numbers) {
                System.out.print(num + " ");
            }
            System.out.println();

            // c. Tính tổng, trung bình cộng, phương sai
            int sum = 0;
            for (int num : numbers) {
                sum += num;
            }

            float average = (float) sum / n;

            float varianceSum = 0f;
            for (int num : numbers) {
                float diff = num - average;
                varianceSum += diff * diff;
            }
            float variance = varianceSum / n;

            System.out.println("Tổng: " + sum);
            System.out.println("Trung bình cộng: " + average);
            System.out.println("Phương sai: " + variance);

            // Gửi lần lượt tổng (int), trung bình cộng (float), phương sai (float)
            out.writeInt(sum);
            out.flush();
            System.out.println("Đã gửi tổng: " + sum);

            out.writeFloat(average);
            out.flush();
            System.out.println("Đã gửi trung bình cộng: " + average);

            out.writeFloat(variance);
            out.flush();
            System.out.println("Đã gửi phương sai: " + variance);

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

// Mã câu hỏiBaUi7AuX
// Exam Server36.50.135.242
// Một chương trình máy chủ cho phép kết nối qua TCP tại cổng 2207 (hỗ trợ thời gian liên lạc tối đa cho mỗi yêu cầu là 5s), yêu cầu xây dựng chương trình (tạm gọi là client) thực hiện kết nối tới server tại cổng 2207, sử dụng luồng byte dữ liệu (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:
// a. Gửi chuỗi là mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.
// Ví dụ: B15DCCN999;1D25ED92
// b. Nhận lần lượt hai số nguyên n là kích thước của mảng và các số nguyên a1 a2 ... an là các giá trị của mảng từ server
// c. Thực hiện tính toán tổng, trung bình cộng, phương sai của mảng (phương sai =  ((x1-trung bình)^2 +(x2-trung bình)^2 +....)/n)và gửi lần lượt từng giá trị dưới dạng số nguyên hoac float theo đúng thứ tự trên lên server
// d. Đóng kết nối và kết thúc