import java.io.*;
import java.net.Socket;

public class Client2207c {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2207;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "HNdkJ3Ej"; // Mã câu hỏi đề bài đã cho

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

            // b. Nhận lần lượt chuỗi đã mã hóa Caesar và giá trị dịch chuyển s
            String encrypted = in.readUTF();
            int s = in.readInt();
            System.out.println("Nhận được chuỗi mã hóa: " + encrypted);
            System.out.println("Nhận được giá trị dịch chuyển s: " + s);

            // c. Giải mã Caesar: dịch ngược lại s ký tự, giữ nguyên hoa/thường
            // và không thay đổi các ký tự không phải chữ cái
            String decrypted = decryptCaesar(encrypted, s);
            System.out.println("Chuỗi sau khi giải mã: " + decrypted);

            // Gửi thông điệp đã giải mã lên server
            out.writeUTF(decrypted);
            out.flush();
            System.out.println("Đã gửi: " + decrypted);

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

    /**
     * Giải mã Caesar: mỗi ký tự trong bản mã được thay bằng ký tự đứng trước nó s vị trí
     * (ngược lại với quá trình mã hóa). Giữ nguyên chữ hoa/thường, không đổi ký tự
     * không phải chữ cái (số, khoảng trắng, dấu câu...).
     */
    private static String decryptCaesar(String text, int s) {
        int shift = ((s % 26) + 26) % 26; // chuẩn hóa s về khoảng 0-25, tránh số âm
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char decoded = (char) ('A' + ((c - 'A' - shift + 26) % 26));
                result.append(decoded);
            } else if (Character.isLowerCase(c)) {
                char decoded = (char) ('a' + ((c - 'a' - shift + 26) % 26));
                result.append(decoded);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}

// HNdkJ3Ej
// Exam Server

// 36.50.135.242
// Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s.

// Ví dụ: với s = 3 thì ký tự A sẽ được thay thế bằng ký tự D

// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:

// a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode.

// Ví dụ: B15DCCN999;D68C93F7

// b. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên

// c. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server

// d. Đóng kết nối và kết thúc chương trình.