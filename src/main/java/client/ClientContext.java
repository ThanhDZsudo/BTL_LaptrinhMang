package client;

import service.TheLoaiService;
import service.TinTucService;

import java.rmi.Naming;

/**
 * Lớp quản lý kết nối RMI từ phía Client
 * 
 * NGUYÊN LÝ HOẠT ĐỘNG (VẤN ĐÁP):
 * - Client sử dụng "Naming.lookup(URL)" để tìm kiếm đối tượng dịch vụ trên RMI Registry của Server.
 * - Sau khi lookup thành công, Client nhận về một đối tượng đại diện (gọi là Stub/Proxy).
 * - Khi Client gọi bất kỳ hàm nào trên đối tượng này (ví dụ: theLoaiService.getAllTheLoai()),
 *   lời gọi hàm sẽ được đóng gói truyền qua mạng tới Server để thực thi và nhận kết quả trả về.
 */
public class ClientContext {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1099;

    private static TheLoaiService theLoaiService;
    private static TinTucService tinTucService;

    /**
     * Khởi tạo kết nối tới RMI Server
     */
    public static boolean initConnection() {
        try {
            String urlTheLoai = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/TheLoaiService";
            String urlTinTuc = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/TinTucService";

            // Tìm kiếm đối tượng dịch vụ từ xa qua RMI Registry
            theLoaiService = (TheLoaiService) Naming.lookup(urlTheLoai);
            tinTucService = (TinTucService) Naming.lookup(urlTinTuc);

            System.out.println("✅ Client kết nối thành công tới RMI Server tại " + SERVER_HOST + ":" + SERVER_PORT);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Không thể kết nối tới RMI Server! Hãy chắc chắn ServerMain đang chạy.");
            e.printStackTrace();
            return false;
        }
    }

    public static TheLoaiService getTheLoaiService() {
        return theLoaiService;
    }

    public static TinTucService getTinTucService() {
        return tinTucService;
    }
}

