package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp quản lý kết nối Cơ sở dữ liệu MySQL (JDBC)
 * 
 * GIẢI THÍCH:
 * - Sử dụng JDBC Driver của MySQL: com.mysql.cj.jdbc.Driver
 * - Cung cấp hàm getConnection() trả về đối tượng Connection để thực thi các câu lệnh SQL
 */
public class DBContext {

    // Tên database được định nghĩa trong file schema.sql
    private static final String DB_NAME = "ql_tintuc";
    
    // Đường dẫn kết nối JDBC: localhost cổng 3306, sử dụng mã hóa ký tự UTF-8
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME 
            + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    
    // Tên đăng nhập CSDL (mặc định của MySQL là root)
    private static final String DB_USER = "root";
    
    // MẬT KHẨU CSDL CỦA BẠN:
    private static final String DB_PASS = "thanh123";

    /**
     * Hàm mở và trả về kết nối tới CSDL MySQL
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu kết nối thất bại (sai pass, chưa bật MySQL, chưa tạo DB...)
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Nạp Driver JDBC của MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Lỗi: Chưa tìm thấy thư viện MySQL Connector JAR!");
            e.printStackTrace();
        }
        // Tạo và trả về kết nối
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Hàm kiểm tra nhanh kết nối CSDL (dùng để test khi khởi động Server)
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Kết nối MySQL thất bại: " + e.getMessage());
            System.err.println("👉 GỢI Ý: Hãy kiểm tra xem MySQL Service đã bật chưa và mật khẩu trong DBContext.java đã đúng chưa.");
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối MySQL...");
        if (testConnection()) {
            System.out.println("✅ Kết nối MySQL thành công!");
        } else {
            System.out.println("❌ Kết nối MySQL thất bại!");
        }
    }
}

