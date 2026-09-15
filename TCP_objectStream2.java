import TCP.Customer;

import java.io.*;
import java.net.Socket;

public class Client2209b {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2209;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng)
    private static final String STUDENT_CODE = "B15DCCNxxx";
    private static final String Q_CODE = "enAJrpDa"; // Mã câu hỏi đề bài đã cho

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

            // 2) Nhận đối tượng Customer từ server
            Object obj = in.readObject();
            if (!(obj instanceof Customer)) {
                System.out.println("Dữ liệu nhận được không phải đối tượng Customer.");
                return;
            }
            Customer customer = (Customer) obj;
            System.out.println("Nhận được: " + customer);

            // 3) Chuẩn hóa tên, ngày sinh, sinh username rồi gán lại vào đối tượng
            String newName = formatName(customer.getName());
            String newDayOfBirth = formatDate(customer.getDayOfBirth());
            String newUserName = generateUserName(customer.getName());

            customer.setName(newName);
            customer.setDayOfBirth(newDayOfBirth);
            customer.setUserName(newUserName);

            System.out.println("Đã chuẩn hóa thành: " + customer);

            // Gửi đối tượng Customer đã chuẩn hóa lên server
            out.writeObject(customer);
            out.flush();
            System.out.println("Đã gửi đối tượng đã chuẩn hóa lên server.");

            // Đọc phản hồi xác nhận từ server (nếu có)
            try {
                Object reply = in.readObject();
                System.out.println("Phản hồi từ server: " + reply);
            } catch (IOException | ClassNotFoundException e) {
                // Không bắt buộc có phản hồi cuối
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 4) Đóng kết nối tự động nhờ try-with-resources
        System.out.println("Đã đóng kết nối.");
    }

    /**
     * Chuẩn hóa tên: từ cuối viết HOA toàn bộ, các từ còn lại viết hoa chữ đầu,
     * định dạng: "LAST, First Second ..."
     * Ví dụ: "nguyen van hai duong" -> "DUONG, Nguyen Van Hai"
     */
    private static String formatName(String rawName) {
        String[] words = rawName.trim().toLowerCase().split("\\s+");
        String lastWord = words[words.length - 1].toUpperCase();

        if (words.length == 1) {
            return lastWord;
        }

        StringBuilder firstPart = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            if (i > 0) {
                firstPart.append(" ");
            }
            firstPart.append(capitalize(words[i]));
        }

        return lastWord + ", " + firstPart;
    }

    /**
     * Viết hoa chữ cái đầu của một từ, phần còn lại viết thường.
     */
    private static String capitalize(String word) {
        if (word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /**
     * Đổi định dạng ngày sinh từ mm-dd-yyyy sang dd/mm/yyyy.
     * Ví dụ: "10-11-2012" -> "11/10/2012"
     */
    private static String formatDate(String rawDate) {
        String[] parts = rawDate.trim().split("-");
        String month = parts[0];
        String day = parts[1];
        String year = parts[2];
        return day + "/" + month + "/" + year;
    }

    /**
     * Sinh username: nối chữ cái đầu của tất cả các từ (trừ từ cuối)
     * với toàn bộ từ cuối, tất cả viết thường.
     * Ví dụ: "nguyen van hai duong" -> "nvhduong"
     */
    private static String generateUserName(String rawName) {
        String[] words = rawName.trim().toLowerCase().split("\\s+");

        StringBuilder userName = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            if (!words[i].isEmpty()) {
                userName.append(words[i].charAt(0));
            }
        }
        userName.append(words[words.length - 1]);

        return userName.toString();
    }
}

// enAJrpDa
// Exam Server

// 36.50.135.242
// Thông tin khách hàng cần thay đổi định dạng lại cho phù hợp với khu vực, cụ thể:

// a. Tên khách hàng cần được chuẩn hóa theo định dạng mới.

// Ví dụ: nguyen van hai duong -> DUONG, Nguyen Van Hai

// b. Ngày sinh của khách hàng hiện đang ở dạng mm-dd-yyyy, cần được chuyển thành định dạng dd/mm/yyyy.

// Ví dụ: 10-11-2012 -> 11/10/2012

// c. Tài khoản khách hàng là các chữ cái in thường được sinh tự động từ họ tên khách hàng.

// Ví dụ: nguyen van hai duong -> nvhduong

// Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectInputStream / ObjectOutputStream) thực hiện gửi/nhận đối tượng khách hàng và chuẩn hóa. Cụ thể:

// a. Đối tượng trao đổi là thể hiện của lớp Customer được mô tả như sau

// Tên đầy đủ của lớp: TCP.Customer
// Các thuộc tính: id int, code String, name String, dayOfBirth String, userName String
// Hàm khởi tạo đầy đủ các thuộc tính được liệt kê ở trên
// Trường dữ liệu: private static final long serialVersionUID = 20170711L;
// b. Tương tác với server theo kịch bản dưới đây:

// 1) Gửi đối tượng là một chuỗi gồm mã sinh viên và mã câu hỏi ở định dạng studentCode;qCode.

// Ví dụ: B15DCCN999;F2DA54F3

// 2) Nhận một đối tượng là thể hiện của lớp Customer từ server với các thông tin đã được thiết lập

// 3) Thay đổi định dạng theo các yêu cầu ở trên và gán vào các thuộc tính tương ứng.

// Gửi đối tượng đã được sửa đổi lên server

// 4) Đóng socket và kết thúc chương trình.