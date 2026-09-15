import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client2211b {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2211;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B16DCCNxxx";
    private static final String Q_CODE = "E1PRS9KX"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.socket().connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), TIMEOUT_MS);
            channel.socket().setSoTimeout(TIMEOUT_MS);
            channel.configureBlocking(true);

            // a. Gửi studentCode;qCode dưới dạng frame (4 byte độ dài + payload UTF-8)
            String request = STUDENT_CODE + ";" + Q_CODE;
            writeFrame(channel, request);
            System.out.println("Đã gửi: " + request);

            // b. Nhận đúng 2 frame liên tiếp và nối payload theo đúng thứ tự
            StringBuilder jsonBuilder = new StringBuilder();
            for (int i = 0; i < 2; i++) {
                String payload = readFrame(channel);
                System.out.println("Nhận frame " + (i + 1) + ": " + payload);
                jsonBuilder.append(payload);
            }
            String json = jsonBuilder.toString();
            System.out.println("Chuỗi JSON hoàn chỉnh: " + json);

            // c. Trích xuất event, user, ok từ chuỗi JSON đơn giản bằng regex
            String event = extractStringField(json, "event");
            String user = extractStringField(json, "user");
            boolean ok = extractBooleanField(json, "ok");

            String result = "event=" + event + ";user=" + user + ";ok=" + (ok ? "1" : "0");
            System.out.println("Gửi lên server: " + result);

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
     * Trích xuất giá trị chuỗi của một trường dạng "key":"value" trong JSON đơn giản.
     */
    private static String extractStringField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Trích xuất giá trị boolean (true/false, không có dấu ngoặc kép) của một trường trong JSON.
     */
    private static boolean extractBooleanField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
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

// E1PRS9KX
// Exam Server

// 36.50.135.242
// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2211 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng SocketChannel và ByteBuffer để trao đổi thông tin theo giao thức frame: 4 byte độ dài (int32) + payload (UTF-8).

// Lưu ý: server & client đều phải đọc đủ dữ liệu bằng vòng lặp (readFully) do server luôn chia nhỏ dữ liệu khi gửi. Trình tự trao đổi như sau:

// a. Gửi mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B16DCCN999;ucpQ9zAh

// b. Nhận dữ liệu từ server gồm đúng 2 frame liên tiếp. Payload của mỗi frame là một phần của cùng một chuỗi JSON đơn giản trên một dòng (không xuống dòng). Client phải nối 2 payload theo đúng thứ tự để thu được chuỗi JSON hoàn chỉnh.

// c. Trích xuất các trường event, user, ok và gửi lại lên server theo định dạng event=<event>;user=<user>;ok=<0|1> (true=1, false=0).

// d. Đóng kết nối và kết thúc chương trình.