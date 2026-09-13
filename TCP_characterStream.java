import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2208;
    private static final int TIMEOUT_MS = 5000; // server giới hạn 5s / yêu cầu

    // TODO: thay bằng mã sinh viên thật của bạn (giữ nguyên định dạng, ví dụ B15DCCN999)
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

            // b. Nhận chuỗi danh sách tên miền từ server
            String domainLine = in.readLine();
            System.out.println("Nhận được: " + domainLine);

            if (domainLine == null || domainLine.isEmpty()) {
                System.out.println("Không nhận được dữ liệu từ server.");
                return;
            }

            // c. Lọc các tên miền .edu
            String[] domains = domainLine.split(",");
            List<String> eduDomains = new ArrayList<>();
            for (String d : domains) {
                String domain = d.trim();
                if (domain.endsWith(".edu")) {
                    eduDomains.add(domain);
                }
            }

            String result = String.join(", ", eduDomains);
            System.out.println("Tên miền .edu tìm được: " + result);

            // Gửi kết quả lọc được lên server
            out.write(result);
            out.newLine();
            out.flush();

            // Đọc phản hồi xác nhận (nếu server có gửi)
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



//Mã câu hỏi

0nh2mXoC
Exam Server

36.50.135.242
Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng byte (BufferedWriter/BufferedReader) theo kịch bản sau:

a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng studentCode;qCode.

Ví dụ: B15DCCN999;EC4F899B

b. Nhận một chuỗi ngẫu nhiên là danh sách các một số tên miền từ server

Ví dụ: giHgWHwkLf0Rd0.io, I7jpjuRw13D.io, wXf6GP3KP.vn, MdpIzhxDVtTFTF.edu, TUHuMfn25chmw.vn, HHjE9.com, 4hJld2m2yiweto.vn, y2L4SQwH.vn, s2aUrZGdzS.com, 4hXfJe9giAA.edu

c. Tìm kiếm các tên miền .edu và gửi lên server

Ví dụ: MdpIzhxDVtTFTF.edu, 4hXfJe9giAA.edu

d. Đóng kết nối và kết thúc chương trình.