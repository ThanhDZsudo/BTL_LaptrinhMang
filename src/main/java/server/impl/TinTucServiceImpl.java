package server.impl;

import model.TinTuc;
import server.dao.TinTucDAO;
import service.TinTucService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Lớp cài đặt (Implementation) cho Remote Interface TinTucService
 */
public class TinTucServiceImpl extends UnicastRemoteObject implements TinTucService {

    // Đối tượng DAO thao tác CSDL MySQL
    private final TinTucDAO tinTucDAO;

    /**
     * Constructor khởi tạo dịch vụ RMI Tin tức
     */
    public TinTucServiceImpl() throws RemoteException {
        super(); // Khởi tạo và export đối tượng từ xa ra mạng
        this.tinTucDAO = new TinTucDAO();
    }

    @Override
    public List<TinTuc> getAllTinTuc() throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Lấy toàn bộ danh sách Tin tức");
        return tinTucDAO.getAll();
    }

    @Override
    public TinTuc getTinTucById(int ma) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Lấy Tin tức theo mã = " + ma);
        return tinTucDAO.getById(ma);
    }

    /**
     * [NGHIỆP VỤ BTL CÁ NHÂN]: Tìm kiếm tin tức theo mã, loại tin, ngày đăng
     */
    @Override
    public List<TinTuc> searchTinTuc(Integer ma, Integer maTheLoai, String ngayDang) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Tìm kiếm tin tức (Mã=" + ma + ", Loại=" + maTheLoai + ", Ngày=" + ngayDang + ")");
        return tinTucDAO.search(ma, maTheLoai, ngayDang);
    }

    @Override
    public boolean addTinTuc(TinTuc tt) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Thêm Tin tức mới = '" + tt.getTieuDe() + "'");
        return tinTucDAO.add(tt);
    }

    @Override
    public boolean updateTinTuc(TinTuc tt) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Cập nhật Tin tức mã = " + tt.getMa());
        return tinTucDAO.update(tt);
    }

    @Override
    public boolean deleteTinTuc(int ma) throws RemoteException {
        System.out.println("[Server-RMI] Client yêu cầu: Xóa Tin tức mã = " + ma);
        return tinTucDAO.delete(ma);
    }
}

