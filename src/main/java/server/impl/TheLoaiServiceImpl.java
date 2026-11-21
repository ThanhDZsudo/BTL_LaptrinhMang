package server.impl;

import model.TheLoai;
import server.dao.TheLoaiDAO;
import service.TheLoaiService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Lớp cài đặt (Implementation) cho Remote Interface TheLoaiService
 * 
 * LƯU Ý LÝ THUYẾT RMI CỰC KỲ QUAN TRỌNG KHI VẤN ĐÁP:
 * 1. Kế thừa UnicastRemoteObject:
 *    - Giúp đối tượng này có khả năng "lắng nghe" các lời gọi hàm từ xa từ Client truyền tới qua mạng.
 * 2. Constructor phải khai báo "throws RemoteException":
 *    - Vì hàm khởi tạo của lớp cha UnicastRemoteObject() thiết lập socket mạng và có thể ném RemoteException.
 */
public class TheLoaiServiceImpl extends UnicastRemoteObject implements TheLoaiService {

    // Đối tượng DAO để thao tác trực tiếp với cơ sở dữ liệu MySQL
    private final TheLoaiDAO theLoaiDAO;

    /**
     * Constructor khởi tạo dịch vụ RMI Thể loại tin
     */
    public TheLoaiServiceImpl() throws RemoteException {
        super(); // Gọi constructor của UnicastRemoteObject để export đối tượng từ xa
        this.theLoaiDAO = new TheLoaiDAO();
    }

    @Override
    public List<TheLoai> getAllTheLoai() throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Lấy toàn bộ danh sách Thể loại tin");
        return theLoaiDAO.getAll();
    }

    @Override
    public TheLoai getTheLoaiById(int ma) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Lấy Thể loại theo mã = " + ma);
        return theLoaiDAO.getById(ma);
    }

    /**
     * [NGHIỆP VỤ BTL CÁ NHÂN]: Tìm kiếm thể loại theo tên gần đúng
     */
    @Override
    public List<TheLoai> searchTheLoaiByTen(String ten) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Tìm Thể loại theo tên gần đúng = '" + ten + "'");
        return theLoaiDAO.searchByTen(ten);
    }

    @Override
    public boolean addTheLoai(TheLoai tl) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Thêm Thể loại mới = " + tl.getTen());
        return theLoaiDAO.add(tl);
    }

    @Override
    public boolean updateTheLoai(TheLoai tl) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Cập nhật Thể loại mã = " + tl.getMa());
        return theLoaiDAO.update(tl);
    }

    @Override
    public boolean deleteTheLoai(int ma) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Xóa Thể loại mã = " + ma);
        return theLoaiDAO.delete(ma);
    }
}

