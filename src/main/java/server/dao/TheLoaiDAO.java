package server.dao;

import model.TheLoai;
import server.db.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) xử lý các thao tác truy vấn CSDL cho Thể loại tin
 * 
 * Áp dụng:
 * - Chuẩn JDBC với PreparedStatement để chống SQL Injection
 * - Khối try-with-resources để tự động đóng Connection, PreparedStatement, ResultSet tránh rò rỉ tài nguyên
 */
public class TheLoaiDAO {

    /**
     * Lấy toàn bộ danh sách thể loại từ CSDL
     */
    public List<TheLoai> getAll() {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT ma, ten, mo_ta FROM the_loai ORDER BY ma ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TheLoai tl = new TheLoai(
                        rs.getInt("ma"),
                        rs.getString("ten"),
                        rs.getString("mo_ta")
                );
                list.add(tl);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thể loại: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy 1 thể loại theo mã
     */
    public TheLoai getById(int ma) {
        String sql = "SELECT ma, ten, mo_ta FROM the_loai WHERE ma = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TheLoai(
                            rs.getInt("ma"),
                            rs.getString("ten"),
                            rs.getString("mo_ta")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thể loại theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * [TRỌNG TÂM BTL CÁ NHÂN]: Tìm kiếm Thể loại theo tên gần đúng
     * Sử dụng toán tử LIKE %keyword% trong SQL
     */
    public List<TheLoai> searchByTen(String ten) {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT ma, ten, mo_ta FROM the_loai WHERE LOWER(ten) LIKE ? ORDER BY ma ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Đặt tham số dạng %từ_khóa% để tìm kiếm gần đúng (chứa từ khóa)
            String keyword = (ten == null) ? "" : ten.trim().toLowerCase();
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TheLoai tl = new TheLoai(
                            rs.getInt("ma"),
                            rs.getString("ten"),
                            rs.getString("mo_ta")
                    );
                    list.add(tl);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm thể loại theo tên: " + e.getMessage());
        }
        return list;
    }

    /**
     * Thêm một thể loại mới vào bảng the_loai
     */
    public boolean add(TheLoai tl) {
        String sql = "INSERT INTO the_loai (ten, mo_ta) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tl.getTen());
            ps.setString(2, tl.getMoTa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thể loại: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin thể loại
     */
    public boolean update(TheLoai tl) {
        String sql = "UPDATE the_loai SET ten = ?, mo_ta = ? WHERE ma = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tl.getTen());
            ps.setString(2, tl.getMoTa());
            ps.setInt(3, tl.getMa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thể loại: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa thể loại theo mã
     */
    public boolean delete(int ma) {
        String sql = "DELETE FROM the_loai WHERE ma = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa thể loại: " + e.getMessage());
            return false;
        }
    }
}

