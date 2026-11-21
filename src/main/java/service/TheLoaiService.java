package service;

import model.TheLoai;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Giao diện từ xa (Remote Interface) cho nghiệp vụ Thể loại tin
 * 
 * NGUYÊN TẮC QUAN TRỌNG TRONG JAVA RMI:
 * 1. Bắt buộc phải kế thừa interface "java.rmi.Remote".
 * 2. Mọi phương thức khai báo trong interface này đều BẮT BUỘC phải "throws RemoteException".
 *    Lý do: Khi gọi hàm qua mạng, luôn có thể xảy ra các sự cố mạng (mất mạng, server tắt, timeout...)
 *    nên Java RMI yêu cầu phải bắt hoặc khai báo ném ngoại lệ RemoteException.
 */
public interface TheLoaiService extends Remote {

    /**
     * Lấy toàn bộ danh sách thể loại tin
     * @return Danh sách thể loại
     */
    List<TheLoai> getAllTheLoai() throws RemoteException;

    /**
     * Lấy thông tin chi tiết một thể loại theo mã
     * @param ma Mã thể loại cần lấy
     * @return Đối tượng TheLoai hoặc null nếu không tìm thấy
     */
    TheLoai getTheLoaiById(int ma) throws RemoteException;

    /**
     * [NGHIỆP VỤ BTL CÁ NHÂN]: Tìm kiếm Thể loại theo tên gần đúng
     * Ví dụ: Tìm "công" -> Trả về "Công nghệ", "Công cộng"...
     * @param ten Từ khóa tên thể loại cần tìm
     * @return Danh sách thể loại có tên khớp gần đúng
     */
    List<TheLoai> searchTheLoaiByTen(String ten) throws RemoteException;

    /**
     * Thêm một thể loại mới vào CSDL
     * @param tl Đối tượng Thể loại cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    boolean addTheLoai(TheLoai tl) throws RemoteException;

    /**
     * Cập nhật thông tin thể loại
     * @param tl Đối tượng Thể loại chứa thông tin mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    boolean updateTheLoai(TheLoai tl) throws RemoteException;

    /**
     * Xóa thể loại theo mã
     * @param ma Mã thể loại cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    boolean deleteTheLoai(int ma) throws RemoteException;
}

