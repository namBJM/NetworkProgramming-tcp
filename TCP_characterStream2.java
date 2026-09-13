import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client2208 {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2208;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "PBdTpHLt"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            // a. Gửi studentCode;qCode
            String request = STUDENT_CODE + ";" + Q_CODE;
            out.write(request);
            out.newLine();
            out.flush();
            System.out.println("Đã gửi: " + request);

            // b. Nhận chuỗi ngẫu nhiên từ server
            String randomString = in.readLine();
            System.out.println("Nhận được: " + randomString);

            if (randomString == null) {
                System.out.println("Không nhận được dữ liệu từ server.");
                return;
            }

            // c. Đếm số lần xuất hiện của từng ký tự chữ/số, giữ thứ tự xuất hiện đầu tiên
            Map<Character, Integer> countMap = new LinkedHashMap<>();
            for (char c : randomString.toCharArray()) {
                if (Character.isLetterOrDigit(c)) {
                    countMap.put(c, countMap.getOrDefault(c, 0) + 1);
                }
            }

            StringBuilder resultBuilder = new StringBuilder();
            for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() > 1) {
                    resultBuilder.append(entry.getKey())
                                 .append(":")
                                 .append(entry.getValue())
                                 .append(",");
                }
            }

            String result = resultBuilder.toString();
            System.out.println("Gửi lên server: " + result);

            out.write(result);
            out.newLine();
            out.flush();

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                String serverReply = in.readLine();
                if (serverReply != null) {
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

// PBdTpHLt
// Exam Server

// 36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng byte (BufferedWriter/BufferedReader) theo kịch bản sau:

// a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng studentCode;qCode.

// Ví dụ: B15DCCN999;BAA62945

// b. Nhận một chuỗi ngẫu nhiên từ server

// Ví dụ: dgUOo ch2k22ldsOo

// c. Liệt kê các ký tự (là chữ hoặc số) xuất hiện nhiều hơn một lần trong chuỗi và số lần xuất hiện của chúng và gửi lên server

// Ví dụ: d:2,O:2,o:2,2:3,

// d. Đóng kết nối và kết thúc chương trình.