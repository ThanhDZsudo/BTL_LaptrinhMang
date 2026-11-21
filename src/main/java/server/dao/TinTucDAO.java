package server.dao;

import model.TinTuc;
import server.db.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) xử lý các thao tác dữ liệu cho bảng tin_tuc
 */
public class TinTucDAO {

    /**
     * Lấy tất cả tin tức (kết hợp JOIN với bảng the_loai để lấy tên thể loại hiển thị)
     */
    public List<TinTuc> getAll() {
        List<TinTuc> list = new ArrayList<>();
        String sql = "SELECT t.ma, t.tieu_de, t.chi_tiet, t.link_anh, t.ma_the_loai, t.ngay_dang, tl.ten AS ten_the_loai "
                   + "FROM tin_tuc t LEFT JOIN the_loai tl ON t.ma_the_loai = tl.ma "
                   + "ORDER BY t.ngay_dang DESC, t.ma DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TinTuc tt = new TinTuc(
                        rs.getInt("ma"),
                        rs.getString("tieu_de"),
                        rs.getString("chi_tiet"),
                        rs.getString("link_anh"),
                        rs.getInt("ma_the_loai"),
                        rs.getDate("ngay_dang"),
                        rs.getString("ten_the_loai")
                );
                list.add(tt);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tin tức: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy 1 bài viết tin tức theo mã
     */
    public TinTuc getById(int ma) {
        String sql = "SELECT t.ma, t.tieu_de, t.chi_tiet, t.link_anh, t.ma_the_loai, t.ngay_dang, tl.ten AS ten_the_loai "
                   + "FROM tin_tuc t LEFT JOIN the_loai tl ON t.ma_the_loai = tl.ma "
                   + "WHERE t.ma = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TinTuc(
                            rs.getInt("ma"),
                            rs.getString("tieu_de"),
                            rs.getString("chi_tiet"),
                            rs.getString("link_anh"),
                            rs.getInt("ma_the_loai"),
                            rs.getDate("ngay_dang"),
                            rs.getString("ten_the_loai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tin tức theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * [TRỌNG TÂM BTL CÁ NHÂN]: Tìm kiếm Tin tức theo:
     * 1. Mã tin tức (ma)
     * 2. Loại tin tức (maTheLoai)
     * 3. Ngày đăng (ngayDang)
     * 
     * Kỹ thuật: Ghép câu SQL động (Dynamic Query) kết hợp PreparedStatement an toàn.
     * Người dùng có thể tìm theo 1 tiêu chí, 2 tiêu chí hoặc kết hợp cả 3 tiêu chí cùng lúc.
     */
    public List<TinTuc> search(Integer ma, Integer maTheLoai, String ngayDang) {
        List<TinTuc> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.ma, t.tieu_de, t.chi_tiet, t.link_anh, t.ma_the_loai, t.ngay_dang, tl.ten AS ten_the_loai "
              + "FROM tin_tuc t LEFT JOIN the_loai tl ON t.ma_the_loai = tl.ma "
              + "WHERE 1=1 "
        );

        // Danh sách lưu các giá trị tham số để gán vào PreparedStatement
        List<Object> params = new ArrayList<>();

        // Điều kiện 1: Tìm theo Mã tin tức
        if (ma != null && ma > 0) {
            sql.append(" AND t.ma = ? ");
            params.add(ma);
        }

        // Điều kiện 2: Tìm theo Loại tin (Thể loại)
        if (maTheLoai != null && maTheLoai > 0) {
            sql.append(" AND t.ma_the_loai = ? ");
            params.add(maTheLoai);
        }

        // Điều kiện 3: Tìm theo Ngày đăng (định dạng yyyy-MM-dd)
        if (ngayDang != null && !ngayDang.trim().isEmpty()) {
            sql.append(" AND t.ngay_dang = ? ");
            params.add(Date.valueOf(ngayDang.trim()));
        }

        // Sắp xếp tin mới nhất lên trước
        sql.append(" ORDER BY t.ngay_dang DESC, t.ma DESC");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Gán các tham số động vào câu lệnh SQL
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TinTuc tt = new TinTuc(
                            rs.getInt("ma"),
                            rs.getString("tieu_de"),
                            rs.getString("chi_tiet"),
                            rs.getString("link_anh"),
                            rs.getInt("ma_the_loai"),
                            rs.getDate("ngay_dang"),
                            rs.getString("ten_the_loai")
                    );
                    list.add(tt);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm tin tức: " + e.getMessage());
        }

        return list;
    }

    /**
     * Thêm bài viết tin tức mới
     */
    public boolean add(TinTuc tt) {
        String sql = "INSERT INTO tin_tuc (tieu_de, chi_tiet, link_anh, ma_the_loai, ngay_dang) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tt.getTieuDe());
            ps.setString(2, tt.getChiTiet());
            ps.setString(3, tt.getLinkAnh());
            ps.setInt(4, tt.getMaTheLoai());
            ps.setDate(5, tt.getNgayDang());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm tin tức: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin bài viết tin tức
     */
    public boolean update(TinTuc tt) {
        String sql = "UPDATE tin_tuc SET tieu_de = ?, chi_tiet = ?, link_anh = ?, ma_the_loai = ?, ngay_dang = ? WHERE ma = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tt.getTieuDe());
            ps.setString(2, tt.getChiTiet());
            ps.setString(3, tt.getLinkAnh());
            ps.setInt(4, tt.getMaTheLoai());
            ps.setDate(5, tt.getNgayDang());
            ps.setInt(6, tt.getMa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tin tức: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa bài viết tin tức theo mã
     */
    public boolean delete(int ma) {
        String sql = "DELETE FROM tin_tuc WHERE ma = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tin tức: " + e.getMessage());
            return false;
        }
    }
}

