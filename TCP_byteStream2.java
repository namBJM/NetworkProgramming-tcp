import java.io.*;
import java.net.Socket;

public class Client2206b {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2206;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B16DCCNxxx";
    private static final String Q_CODE = "UqMvWy1f"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi studentCode;qCode
            String request = STUDENT_CODE + ";" + Q_CODE;
            out.write(request.getBytes());
            out.flush();
            System.out.println("Đã gửi: " + request);

            // b. Nhận chuỗi các số nguyên từ server
            byte[] buffer = new byte[4096];
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Server đã đóng kết nối, không nhận được dữ liệu.");
                return;
            }
            String numberLine = new String(buffer, 0, bytesRead).trim();
            System.out.println("Nhận được: " + numberLine);

            // Tách chuỗi thành mảng số nguyên
            String[] parts = numberLine.split(",");
            int[] numbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                numbers[i] = Integer.parseInt(parts[i].trim());
            }

            // c. Tìm giá trị lớn thứ hai (phân biệt, khác giá trị lớn nhất)
            int max = Integer.MIN_VALUE;
            int secondMax = Integer.MIN_VALUE;
            for (int num : numbers) {
                if (num > max) {
                    secondMax = max;
                    max = num;
                } else if (num > secondMax && num != max) {
                    secondMax = num;
                }
            }

            // Tìm vị trí xuất hiện đầu tiên của secondMax trong mảng gốc (chỉ số bắt đầu từ 0)
            int position = -1;
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == secondMax) {
                    position = i;
                    break;
                }
            }

            String result = secondMax + "," + position;
            System.out.println("Gửi lên server: " + result);

            out.write(result.getBytes());
            out.flush();

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    String serverReply = new String(buffer, 0, bytesRead).trim();
                    System.out.println("Phản hồi từ server: " + serverReply);
                }
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

// UqMvWy1f
// Exam Server

// 36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng các luồng byte (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:

// a. Gửi mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B16DCCN999;2B3A6510

// b. Nhận dữ liệu từ server là một chuỗi các giá trị số nguyên được phân tách nhau bởi ký tự ,.

// Ví dụ: 1,3,9,19,33,20

// c. Tìm và gửi lên server giá trị lớn thứ hai cùng vị trí xuất hiện của nó trong chuỗi.Ví dụ: 20,5

// d. Đóng kết nối và kết thúc chương trình