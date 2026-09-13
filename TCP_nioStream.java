import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client2211 {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2211;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B16DCCNxxx";
    private static final String Q_CODE = "GlQYybOz"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.socket().connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT_MS);
            channel.socket().setSoTimeout(TIMEOUT_MS);
            channel.configureBlocking(true);

            // a. Gửi studentCode;qCode dưới dạng frame (4 byte độ dài + payload UTF-8)
            String request = STUDENT_CODE + ";" + Q_CODE;
            writeFrame(channel, request);
            System.out.println("Đã gửi: " + request);

            // b. Nhận đúng 3 frame liên tiếp và nối payload theo đúng thứ tự
            StringBuilder httpRequestBuilder = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                String payload = readFrame(channel);
                System.out.println("Nhận frame " + (i + 1) + ": " + payload);
                httpRequestBuilder.append(payload);
            }
            String httpRequest = httpRequestBuilder.toString();
            System.out.println("HTTP request hoàn chỉnh:\n" + httpRequest);

            // c. Trích xuất METHOD, PATH (kèm query-string), HOST
            String[] lines = httpRequest.split("\r\n");

            // Dòng đầu tiên dạng: METHOD PATH HTTP/1.1
            String[] requestLineParts = lines[0].split(" ");
            String method = requestLineParts[0];
            String path = requestLineParts[1]; // đã bao gồm query-string nếu có

            // Tìm header Host trong các dòng còn lại
            String host = "";
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.toLowerCase().startsWith("host:")) {
                    host = line.substring(line.indexOf(':') + 1).trim();
                    break;
                }
            }

            String result = method + ";" + path + ";" + host;
            System.out.println("Gửi lên server: " + result);

            // Gửi kết quả dưới dạng frame
            writeFrame(channel, result);

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                String reply = readFrame(channel);
                System.out.println("Phản hồi từ server: " + reply);
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
     * Đọc đủ dữ liệu để lấp đầy buffer, lặp lại vì dữ liệu có thể đến thành nhiều lần.
     */
    private static void readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Kết nối bị đóng trước khi đọc đủ dữ liệu.");
            }
        }
    }

    /**
     * Đọc một frame: 4 byte độ dài (int32) + payload UTF-8 với độ dài tương ứng.
     */
    private static String readFrame(SocketChannel channel) throws IOException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readFully(channel, lengthBuffer);
        lengthBuffer.flip();
        int length = lengthBuffer.getInt();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(length);
        readFully(channel, payloadBuffer);
        payloadBuffer.flip();

        byte[] payloadBytes = new byte[length];
        payloadBuffer.get(payloadBytes);
        return new String(payloadBytes, StandardCharsets.UTF_8);
    }

    /**
     * Ghi một frame: 4 byte độ dài (int32) + payload UTF-8, đảm bảo ghi đủ toàn bộ buffer.
     */
    private static void writeFrame(SocketChannel channel, String payload) throws IOException {
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + payloadBytes.length);
        buffer.putInt(payloadBytes.length);
        buffer.put(payloadBytes);
        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}

// Mã câu hỏi

// GlQYybOz
// Exam Server

// 36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2211 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng SocketChannel và ByteBuffer để trao đổi thông tin theo giao thức frame: 4 byte độ dài (int32) + payload (UTF-8).

// Lưu ý: server & client đều phải đọc đủ dữ liệu bằng vòng lặp (readFully) do server luôn chia nhỏ dữ liệu khi gửi. Trình tự trao đổi như sau:

// a. Gửi mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B16DCCN999;fkdRJYuX

// b. Nhận dữ liệu từ server gồm đúng 3 frame liên tiếp. Payload của mỗi frame là một phần của cùng một HTTP request, client phải nối 3 payload theo đúng thứ tự để thu được chuỗi HTTP request hoàn chỉnh (các dòng phân tách bởi \r\n và kết thúc bằng \r\n\r\n).

// c. Từ chuỗi HTTP request hoàn chỉnh, trích xuất và gửi lại lên server theo định dạng METHOD;PATH;HOST trong đó PATH luôn bao gồm query-string.

// d. Đóng kết nối và kết thúc chương trình.