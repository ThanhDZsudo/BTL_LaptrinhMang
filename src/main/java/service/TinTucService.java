package service;

import model.TinTuc;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Giao diện từ xa (Remote Interface) cho nghiệp vụ Tin tức
 * 
 * NGUYÊN TẮC:
 * 1. Interface kế thừa java.rmi.Remote
 * 2. Tất cả phương thức ném RemoteException
 */
public interface TinTucService extends Remote {

    /**
     * Lấy toàn bộ danh sách tin tức
     * @return Danh sách tin tức
     */
    List<TinTuc> getAllTinTuc() throws RemoteException;

    /**
     * Lấy thông tin chi tiết một tin tức theo mã
     * @param ma Mã bài viết tin tức
     * @return Đối tượng TinTuc hoặc null
     */
    TinTuc getTinTucById(int ma) throws RemoteException;

    /**
     * [NGHIỆP VỤ BTL CÁ NHÂN]: Tìm kiếm tin tức đa điều kiện
     * Tìm theo: Mã tin tức, Loại tin (Thể loại), và Ngày đăng.
     * 
     * Cách thức hoạt động:
     * - Nếu ma != null: Tìm đúng theo mã
     * - Nếu maTheLoai != null && maTheLoai > 0: Lọc theo loại tin
     * - Nếu ngayDang != null && !ngayDang.isEmpty(): Lọc theo ngày đăng (dạng yyyy-MM-dd)
     * - Có thể kết hợp 1, 2 hoặc cả 3 tiêu chí cùng lúc. Nếu để trống tất cả -> Trả về toàn bộ tin tức.
     * 
     * @param ma Mã tin tức (có thể null nếu không tìm theo mã)
     * @param maTheLoai Mã thể loại (có thể null hoặc <= 0 nếu không tìm theo loại)
     * @param ngayDang Ngày đăng dạng chuỗi "yyyy-MM-dd" (có thể null hoặc rỗng nếu không tìm theo ngày)
     * @return Danh sách tin tức thỏa mãn các tiêu chí tìm kiếm
     */
    List<TinTuc> searchTinTuc(Integer ma, Integer maTheLoai, String ngayDang) throws RemoteException;

    /**
     * Thêm mới bài viết tin tức
     * @param tt Đối tượng TinTuc cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    boolean addTinTuc(TinTuc tt) throws RemoteException;

    /**
     * Cập nhật thông tin bài viết tin tức
     * @param tt Đối tượng TinTuc chứa thông tin đã sửa
     * @return true nếu thành công, false nếu thất bại
     */
    boolean updateTinTuc(TinTuc tt) throws RemoteException;

    /**
     * Xóa bài viết tin tức theo mã
     * @param ma Mã bài viết cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deleteTinTuc(int ma) throws RemoteException;
}

