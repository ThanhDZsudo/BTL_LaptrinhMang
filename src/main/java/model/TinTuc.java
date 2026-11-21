package model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Lớp đại diện cho thực thể "Tin tức" (TinTuc)
 * 
 * LƯU Ý QUAN TRỌNG VỚI RMI:
 * - Bắt buộc phải "implements Serializable" để có thể đóng gói gửi qua giao thức mạng RMI.
 */
public class TinTuc implements Serializable {

    // Khóa định danh phiên bản tuần tự hóa
    private static final long serialVersionUID = 1L;

    // Các thuộc tính ánh xạ trực tiếp từ bảng tin_tuc trong CSDL
    private int ma;              // Mã tin tức (Khóa chính)
    private String tieuDe;       // Tiêu đề bài viết
    private String chiTiet;      // Nội dung chi tiết của tin tức
    private String linkAnh;      // Đường dẫn hình ảnh minh họa
    private int maTheLoai;       // Mã thể loại tin (Khóa ngoại)
    private Date ngayDang;       // Ngày đăng bài (kiểu java.sql.Date tương thích CSDL)

    // Thuộc tính mở rộng để tiện hiển thị tên thể loại lên bảng (JTable) mà không cần truy vấn lại
    private String tenTheLoai;

    // Constructor mặc định không tham số
    public TinTuc() {
    }

    // Constructor đầy đủ tham số khi đọc từ CSDL lên
    public TinTuc(int ma, String tieuDe, String chiTiet, String linkAnh, int maTheLoai, Date ngayDang, String tenTheLoai) {
        this.ma = ma;
        this.tieuDe = tieuDe;
        this.chiTiet = chiTiet;
        this.linkAnh = linkAnh;
        this.maTheLoai = maTheLoai;
        this.ngayDang = ngayDang;
        this.tenTheLoai = tenTheLoai;
    }

    // Constructor khi thêm mới (chưa có mã tin)
    public TinTuc(String tieuDe, String chiTiet, String linkAnh, int maTheLoai, Date ngayDang) {
        this.tieuDe = tieuDe;
        this.chiTiet = chiTiet;
        this.linkAnh = linkAnh;
        this.maTheLoai = maTheLoai;
        this.ngayDang = ngayDang;
    }

    // Constructor khi cập nhật tin (có mã tin)
    public TinTuc(int ma, String tieuDe, String chiTiet, String linkAnh, int maTheLoai, Date ngayDang) {
        this.ma = ma;
        this.tieuDe = tieuDe;
        this.chiTiet = chiTiet;
        this.linkAnh = linkAnh;
        this.maTheLoai = maTheLoai;
        this.ngayDang = ngayDang;
    }

    // --- Các hàm Getter & Setter ---

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public int getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(int maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public Date getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(Date ngayDang) {
        this.ngayDang = ngayDang;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }
}

