package client;

import client.ui.MainFrame;

import javax.swing.*;

/**
 * Lớp khởi động ứng dụng Client (Giao diện đồ họa Swing)
 * 
 * QUY TRÌNH KHỞI ĐỘNG CỦA CLIENT:
 * 1. Thiết lập giao diện hệ thống (System Look and Feel) cho đẹp mắt.
 * 2. Gọi ClientContext.initConnection() để lookup các Remote Service từ RMI Server.
 * 3. Nếu kết nối RMI thành công -> Mở cửa sổ chính MainFrame.
 * 4. Nếu Server chưa chạy -> Hiển thị hộp thoại cảnh báo người dùng.
 */
public class ClientMain {

    public static void main(String[] args) {
        // Thiết lập giao diện theo phong cách hệ thống (Windows/Mac)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Chạy trong luồng sự kiện đồ họa của Swing (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            System.out.println("=================================================");
            System.out.println("       ĐANG KHỞI ĐỘNG RMI CLIENT - QUẢN LÝ TIN TỨC       ");
            System.out.println("=================================================");

            // 1. Kết nối tới RMI Server
            boolean connected = ClientContext.initConnection();

            if (!connected) {
                // Thông báo lỗi nếu chưa bật Server
                JOptionPane.showMessageDialog(
                        null,
                        "Không thể kết nối tới RMI Server tại localhost:1099!\n\n"
                        + " HÃY CHẮC CHẮN RẰNG BẠN ĐÃ CHẠY 'ServerMain' TRƯỚC KHI MỞ CLIENT.",
                        "Lỗi kết nối Server",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(0);
                return;
            }

            // 2. Mở giao diện chính
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}

