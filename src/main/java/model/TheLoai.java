package model;

import java.io.Serializable;

/**
 * Lớp đại diện cho thực thể "Thể loại tin" (TheLoai)
 * 
 * LƯU Ý QUAN TRỌNG VỚI RMI:
 * - Lớp này BẮT BUỘC phải "implements Serializable"
 * - Lý do: Khi Client gọi hàm của Server qua RMI và nhận về danh sách Thể loại (hoặc gửi Thể loại lên Server),
 *   Java RMI cần "tuần tự hóa" (serialize) đối tượng thành luồng byte (byte stream) để truyền qua mạng Internet/LAN,
 *   và sau đó "giải tuần tự hóa" (deserialize) lại thành đối tượng ở phía bên kia.
 */
public class TheLoai implements Serializable {
    
    // Mã phiên bản tuần tự hóa, giúp đảm bảo tính tương thích giữa Client và Server khi truyền dữ liệu
    private static final long serialVersionUID = 1L;

    // Các thuộc tính tương ứng với các cột trong bảng the_loai ở CSDL
    private int ma;          // Mã thể loại (Khóa chính)
    private String ten;      // Tên thể loại (Ví dụ: Công nghệ, Thể thao...)
    private String moTa;     // Mô tả thêm về thể loại

    // Constructor không tham số (Mặc định)
    public TheLoai() {
    }

    // Constructor đầy đủ tham số
    public TheLoai(int ma, String ten, String moTa) {
        this.ma = ma;
        this.ten = ten;
        this.moTa = moTa;
    }

    // Constructor dùng khi thêm mới (chưa có mã vì mã tự tăng trong CSDL)
    public TheLoai(String ten, String moTa) {
        this.ten = ten;
        this.moTa = moTa;
    }

    // Các hàm Getter và Setter để truy xuất và cập nhật dữ liệu của thuộc tính
    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Ghi đè hàm toString() để khi hiển thị lên JComboBox (danh sách chọn) sẽ hiện tên thể loại dễ nhìn
    @Override
    public String toString() {
        return ten != null ? ten : ("Thể loại #" + ma);
    }
}

