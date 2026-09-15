import java.io.*;
import java.net.Socket;

public class Client2208b {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2208;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "0nh2mXoC"; // Mã câu hỏi đề bài đã cho

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

            // c. Tìm từ dài nhất (phân tách bởi khoảng trắng) và vị trí bắt đầu của nó
            // trong chuỗi gốc (duyệt thủ công để lấy đúng chỉ số bắt đầu, kể cả khi
            // có nhiều khoảng trắng liên tiếp giữa các từ)
            String longestWord = "";
            int longestStart = -1;

            int n = randomString.length();
            int i = 0;
            while (i < n) {
                // Bỏ qua khoảng trắng
                while (i < n && Character.isWhitespace(randomString.charAt(i))) {
                    i++;
                }
                if (i >= n) break;

                int start = i;
                while (i < n && !Character.isWhitespace(randomString.charAt(i))) {
                    i++;
                }
                String word = randomString.substring(start, i);

                if (word.length() > longestWord.length()) {
                    longestWord = word;
                    longestStart = start;
                }
            }

            System.out.println("Từ dài nhất: " + longestWord + " (vị trí bắt đầu: " + longestStart + ")");

            // Gửi lần lượt 2 giá trị lên server: từ dài nhất, rồi vị trí bắt đầu
            out.write(longestWord);
            out.newLine();
            out.flush();
            System.out.println("Đã gửi: " + longestWord);

            out.write(String.valueOf(longestStart));
            out.newLine();
            out.flush();
            System.out.println("Đã gửi: " + longestStart);

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

// Mã câu hỏi0nh2mXoC
// Exam Server36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng byte (BufferedWriter/BufferedReader) theo kịch bản sau:
// a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng studentCode;qCode.
// Ví dụ: B15DCCN999;EC4F899B
// b. Nhận một chuỗi ngẫu nhiên từ server
// c. Tìm từ dài nhất trong chuỗi đó từ là chuỗi con phân tách bời khoảng trắng, xác định vị trí bắt đầu cuad từ dài nhất trong chuỗi ban đầu. gửi lần lượt 2 giá trị lên server
// d. Đóng kết nối và kết thúc chương trình.