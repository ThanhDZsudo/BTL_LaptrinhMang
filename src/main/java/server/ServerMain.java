package server;

import server.db.DBContext;
import server.impl.TheLoaiServiceImpl;
import server.impl.TinTucServiceImpl;
import service.TheLoaiService;
import service.TinTucService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Lớp khởi động RMI Server
 * 
 * CÁC BƯỚC KHỞI ĐỘNG RMI SERVER (CÂU HỎI THƯỜNG GẶP KHI THI/VẤN ĐÁP):
 * Bước 1: Khởi động bộ quản lý đăng ký tên dịch vụ RMI (RMI Registry) tại một cổng mạng (mặc định 1099)
 *         bằng lệnh "LocateRegistry.createRegistry(1099)".
 * Bước 2: Tạo đối tượng thực thi dịch vụ (TheLoaiServiceImpl, TinTucServiceImpl).
 * Bước 3: Đăng ký dịch vụ vào Registry với một tên định danh (URL dạng "rmi://localhost:1099/TênDịchVụ")
 *         bằng lệnh "Naming.rebind(...)".
 * Bước 4: Server chuyển sang trạng thái chờ nhận các cuộc gọi từ Client.
 */
public class ServerMain {

    // Cổng mạng RMI Registry mặc định
    private static final int RMI_PORT = 1099;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      HỆ THỐNG RMI SERVER - QUẢN LÝ TIN TỨC      ");
        System.out.println("=================================================");

        // 1. Kiểm tra kết nối CSDL MySQL trước khi khởi động dịch vụ
        System.out.println("[1] Đang kiểm tra kết nối tới MySQL Database...");
        if (!DBContext.testConnection()) {
            System.err.println(" CẢNH BÁO: Không thể kết nối tới CSDL MySQL!");
            System.err.println(" Vui lòng đảm bảo:");
            System.err.println("   1. MySQL đã được Start (trong XAMPP hoặc MySQL Workbench).");
            System.err.println("   2. Đã chạy script tạo CSDL trong file 'database/schema.sql'.");
            System.err.println("   3. Đã điền đúng mật khẩu tài khoản root trong file 'DBContext.java'.");
            System.err.println("-------------------------------------------------");
        } else {
            System.out.println(" Kết nối MySQL Database thành công!");
        }

        try {
            // 2. Tạo RMI Registry tại cổng 1099
            System.out.println("[2] Đang khởi tạo RMI Registry tại cổng " + RMI_PORT + "...");
            try {
                LocateRegistry.createRegistry(RMI_PORT);
                System.out.println(" Đã tạo mới RMI Registry trên cổng " + RMI_PORT);
            } catch (Exception e) {
                // Nếu cổng 1099 đã có registry chạy trước đó
                System.out.println("ℹ RMI Registry đã tồn tại trên cổng " + RMI_PORT);
            }

            // 3. Khởi tạo các đối tượng dịch vụ
            System.out.println("[3] Đang khởi tạo các Remote Service Objects...");
            TheLoaiService theLoaiService = new TheLoaiServiceImpl();
            TinTucService tinTucService = new TinTucServiceImpl();

            // 4. Đăng ký các dịch vụ vào Registry
            String urlTheLoai = "rmi://localhost:" + RMI_PORT + "/TheLoaiService";
            String urlTinTuc = "rmi://localhost:" + RMI_PORT + "/TinTucService";

            Naming.rebind(urlTheLoai, theLoaiService);
            Naming.rebind(urlTinTuc, tinTucService);

            System.out.println(" Đã đăng ký thành công dịch vụ: " + urlTheLoai);
            System.out.println(" Đã đăng ký thành công dịch vụ: " + urlTinTuc);

            System.out.println("=================================================");
            System.out.println(" Bây giờ bạn có thể khởi chạy ClientMain để thao tác.");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println(" Lỗi nghiêm trọng khi khởi động RMI Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

