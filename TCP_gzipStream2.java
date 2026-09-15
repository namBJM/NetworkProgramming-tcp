import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Client2210b {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2210;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B16DCCNxxx";
    private static final String Q_CODE = "qrD2dZpv"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            // a. Gửi studentCode;qCode (nén GZIP)
            String request = STUDENT_CODE + ";" + Q_CODE;
            sendGzipLine(socket, request);
            System.out.println("Đã gửi: " + request);

            // b. Nhận chuỗi văn bản từ server (giải nén GZIP)
            String received = receiveGzipLine(socket);
            System.out.println("Nhận được: " + received);

            if (received == null) {
                System.out.println("Không nhận được dữ liệu từ server.");
                return;
            }

            // c. Sắp xếp các ký tự theo thứ tự ASCII tăng dần
            char[] chars = received.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);

            System.out.println("Gửi lên server: " + sorted);
            sendGzipLine(socket, sorted);

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                String reply = receiveGzipLine(socket);
                if (reply != null) {
                    System.out.println("Phản hồi từ server: " + reply);
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

    /**
     * Nén một dòng text bằng GZIP rồi gửi qua socket.
     * Dùng finish() (KHÔNG dùng close()) để không làm đóng luôn socket.
     */
    private static void sendGzipLine(Socket socket, String line) throws IOException {
        OutputStream rawOut = socket.getOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(rawOut);
        byte[] data = (line + "\n").getBytes(StandardCharsets.UTF_8);
        gzipOut.write(data);
        gzipOut.finish();
        rawOut.flush();
    }

    /**
     * Đọc và giải nén một dòng text được server gửi dưới dạng GZIP.
     * Không đóng luồng để giữ socket mở cho các lượt trao đổi tiếp theo.
     */
    private static String receiveGzipLine(Socket socket) throws IOException {
        GZIPInputStream gzipIn = new GZIPInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(gzipIn, StandardCharsets.UTF_8));
        return reader.readLine();
    }
}

// qrD2dZpv
// Exam Server

// 36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2210 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng GZIPInputStream/GZIPOutputStream để trao đổi thông tin (mỗi thông điệp là một dòng text UTF-8 kết thúc bằng ‘\n’ và toàn bộ dữ liệu truyền/nhận đều được nén GZIP), theo thứ tự sau:

// a. Gửi mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B16DCCN999;GZCRC_LEN03

// b. Nhận dữ liệu từ server, sau khi giải nén là một chuỗi văn bản.

// c. Sắp xếp các ký tự trong chuỗi nhận được theo thứ tự từ điển (tăng dần theo mã ASCII). Sau đó gửi chuỗi kết quả đã sắp xếp lên server.

// Ví dụ: Nhận về dbca1 thì gửi lên server 1abcd

// d. Đóng kết nối và kết thúc chương trình.