import TCP.Laptop;

import java.io.*;
import java.net.Socket;

public class Client2209 {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2209;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "SW1fImsa"; // Mã câu hỏi đề bài đã cho

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            // Quan trọng: tạo ObjectOutputStream trước và flush() ngay
            // để tránh treo chương trình khi tạo ObjectInputStream ở cả hai phía
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // 1) Gửi chuỗi studentCode;qCode dưới dạng đối tượng String
            String request = STUDENT_CODE + ";" + Q_CODE;
            out.writeObject(request);
            out.flush();
            System.out.println("Đã gửi: " + request);

            // 2) Nhận đối tượng Laptop từ server
            Object obj = in.readObject();
            if (!(obj instanceof Laptop)) {
                System.out.println("Dữ liệu nhận được không phải đối tượng Laptop.");
                return;
            }
            Laptop laptop = (Laptop) obj;
            System.out.println("Nhận được: " + laptop);

            // 3) Sửa tên: đổi ngược từ đầu tiên và từ cuối cùng về đúng thứ tự ban đầu
            String[] words = laptop.getName().trim().split("\\s+");
            if (words.length >= 2) {
                String temp = words[0];
                words[0] = words[words.length - 1];
                words[words.length - 1] = temp;
            }
            laptop.setName(String.join(" ", words));

            // Sửa số lượng: đảo ngược lại các chữ số về giá trị đúng
            String reversedDigits = new StringBuilder(String.valueOf(laptop.getQuantity()))
                    .reverse()
                    .toString();
            laptop.setQuantity(Integer.parseInt(reversedDigits));

            System.out.println("Đã sửa thành: " + laptop);

            // Gửi đối tượng Laptop đã sửa lên server
            out.writeObject(laptop);
            out.flush();
            System.out.println("Đã gửi đối tượng đã sửa lên server.");

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                Object reply = in.readObject();
                System.out.println("Phản hồi từ server: " + reply);
            } catch (IOException | ClassNotFoundException e) {
                // Không bắt buộc có phản hồi cuối, bỏ qua nếu quá thời gian chờ
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 4) Đóng kết nối tự động nhờ try-with-resources
        System.out.println("Đã đóng kết nối.");
    }
}

// Mã câu hỏi

// SW1fImsa
// Exam Server

// 36.50.135.242
// Thông tin sản phẩm vì một lý do nào đó đã bị sửa đổi thành không đúng, cụ thể:

// a) Tên sản phẩm bị đổi ngược từ đầu tiên và từ cuối cùng, ví dụ: “lenovo thinkpad T520” bị chuyển thành “T520 thinkpad lenovo”

// b) Số lượng sản phẩm cũng bị đảo ngược giá trị, ví dụ từ 9981 thành 1899

// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectInputStream / ObjectOutputStream) để gửi/nhận và sửa các thông tin bị sai của sản phẩm. Chi tiết dưới đây:

// a) Đối tượng trao đổi là thể hiện của lớp Laptop được mô tả như sau

// Tên đầy đủ của lớp: TCP.Laptop
// Các thuộc tính: id int, code String, name String, quantity int
// Hàm khởi tạo đầy đủ các thuộc tính được liệt kê ở trên
// Trường dữ liệu: private static final long serialVersionUID = 20150711L;
// b) Tương tác với server theo kịch bản

// 1) Gửi đối tượng là chuỗi chứa mã sinh viên và mã câu hỏi với định dạng studentCode;qCode.

// Ví dụ: B15DCCN999;5AD2B818

// 2) Nhận một đối tượng là thể hiện của lớp Laptop từ server

// 3) Sửa các thông tin sai của sản phẩm về tên và số lượng.

// Gửi đối tượng vừa được sửa sai lên server

// 4) Đóng socket và kết thúc chương trình.