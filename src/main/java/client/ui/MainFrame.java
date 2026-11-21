package client.ui;

import client.ClientContext;
import model.TheLoai;
import model.TinTuc;
import service.TheLoaiService;
import service.TinTucService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện chính của ứng dụng Client (Java Swing)
 * 
 * CẤU TRÚC GỒM 3 TAB:
 * 1. Tab "Quản lý Tin Tức": Thêm, sửa, xóa, hiển thị danh sách tin tức.
 * 2. Tab "Quản lý Thể Loại": Thêm, sửa, xóa, hiển thị danh mục thể loại.
 * 3. Tab "Tìm Kiếm (BTL Cá Nhân)":
 *    - Tìm Thể loại theo tên gần đúng
 *    - Tìm Tin tức theo Mã, Loại tin, Ngày đăng và Hiển thị kết quả chi tiết.
 */
public class MainFrame extends JFrame {

    // Tham chiếu tới 2 Remote Service
    private final TheLoaiService theLoaiService;
    private final TinTucService tinTucService;

    // --- CÁC COMPONENT TAB QUẢN LÝ TIN TỨC ---
    private JTable tblTinTuc;
    private DefaultTableModel modelTableTinTuc;
    private JTextField txtTinTucMa;
    private JTextField txtTinTucTieuDe;
    private JComboBox<TheLoai> cbTinTucTheLoai;
    private JTextField txtTinTucNgayDang;
    private JTextField txtTinTucLinkAnh;
    private JTextArea txtTinTucChiTiet;

    // --- CÁC COMPONENT TAB QUẢN LÝ THỂ LOẠI ---
    private JTable tblTheLoai;
    private DefaultTableModel modelTableTheLoai;
    private JTextField txtTheLoaiMa;
    private JTextField txtTheLoaiTen;
    private JTextArea txtTheLoaiMoTa;

    // --- CÁC COMPONENT TAB TÌM KIẾM (BTL CÁ NHÂN) ---
    // 1. Tìm thể loại
    private JTextField txtTimTheLoaiTen;
    private JTable tblKetQuaTheLoai;
    private DefaultTableModel modelKetQuaTheLoai;
    private JLabel lblSoLuongTheLoai;

    // 2. Tìm tin tức
    private JTextField txtTimTinMa;
    private JComboBox<Object> cbTimTinTheLoai;
    private JTextField txtTimTinNgay;
    private JTable tblKetQuaTinTuc;
    private DefaultTableModel modelKetQuaTinTuc;
    private JLabel lblSoLuongTinTuc;
    private JLabel lblChiTietTieuDe;
    private JLabel lblChiTietThongTin;
    private JTextArea txtChiTietNoiDung;

    public MainFrame() {
        // Lấy dịch vụ RMI từ ClientContext
        this.theLoaiService = ClientContext.getTheLoaiService();
        this.tinTucService = ClientContext.getTinTucService();

        // Cài đặt thuộc tính cơ bản cho cửa sổ chính
        setTitle("HỆ THỐNG QUẢN LÝ TIN TỨC (GIAO THỨC RMI) - BTL LTM INT1433");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị ra chính giữa màn hình

        // Sử dụng JTabbedPane để phân chia 3 chức năng chính rõ ràng
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Khởi tạo giao diện từng tab
        tabbedPane.addTab("📰 Quản lý Tin Tức", createPanelQuanLyTinTuc());
        tabbedPane.addTab("🏷️ Quản lý Thể Loại", createPanelQuanLyTheLoai());
        tabbedPane.addTab("🔍 TÌM KIẾM (BTL Cá Nhân)", createPanelTimKiem());

        // Thêm vào JFrame
        add(tabbedPane);

        // Nạp dữ liệu ban đầu từ Server
        loadTheLoaiToComboBoxes();
        loadAllTinTucToTable();
        loadAllTheLoaiToTable();
    }

    // =========================================================================
    // 1. GIAO DIỆN & XỬ LÝ: QUẢN LÝ TIN TỨC
    // =========================================================================
    private JPanel createPanelQuanLyTinTuc() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Bảng danh sách tin tức (bên trên) ---
        String[] columns = {"Mã tin", "Tiêu đề", "Thể loại", "Ngày đăng", "Link ảnh"};
        modelTableTinTuc = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        tblTinTuc = new JTable(modelTableTinTuc);
        tblTinTuc.setRowHeight(24);
        tblTinTuc.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblTinTuc.getColumnModel().getColumn(1).setPreferredWidth(350);
        tblTinTuc.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblTinTuc.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Sự kiện khi click vào một hàng trong bảng -> Đổ dữ liệu lên form bên dưới
        tblTinTuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiTinTucLenForm();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblTinTuc);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách Tin tức (Click để chọn tin cần sửa/xóa)"));

        // --- Form nhập liệu tin tức (bên dưới) ---
        JPanel panelForm = new JPanel(new BorderLayout(10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin bài viết"));

        JPanel panelFields = new JPanel(new GridLayout(5, 2, 8, 8));

        panelFields.add(new JLabel("Mã tin (Tự tăng):"));
        txtTinTucMa = new JTextField();
        txtTinTucMa.setEditable(false);
        txtTinTucMa.setBackground(new Color(240, 240, 240));
        panelFields.add(txtTinTucMa);

        panelFields.add(new JLabel("Tiêu đề bài viết (*):"));
        txtTinTucTieuDe = new JTextField();
        panelFields.add(txtTinTucTieuDe);

        panelFields.add(new JLabel("Thể loại tin (*):"));
        cbTinTucTheLoai = new JComboBox<>();
        panelFields.add(cbTinTucTheLoai);

        panelFields.add(new JLabel("Ngày đăng (yyyy-MM-dd) (*):"));
        txtTinTucNgayDang = new JTextField(LocalDate.now().toString());
        panelFields.add(txtTinTucNgayDang);

        panelFields.add(new JLabel("Link hình ảnh:"));
        txtTinTucLinkAnh = new JTextField();
        panelFields.add(txtTinTucLinkAnh);

        // Phần nhập chi tiết nội dung tin tức
        JPanel panelChiTiet = new JPanel(new BorderLayout(5, 5));
        panelChiTiet.add(new JLabel("Nội dung chi tiết bài viết:"), BorderLayout.NORTH);
        txtTinTucChiTiet = new JTextArea(4, 20);
        txtTinTucChiTiet.setLineWrap(true);
        txtTinTucChiTiet.setWrapStyleWord(true);
        panelChiTiet.add(new JScrollPane(txtTinTucChiTiet), BorderLayout.CENTER);

        // Các nút bấm thao tác
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        JButton btnThem = new JButton("➕ Thêm mới");
        JButton btnCapNhat = new JButton("✏️ Cập nhật");
        JButton btnXoa = new JButton("🗑️ Xóa tin");
        JButton btnLamMoi = new JButton("🔄 Làm mới form");

        panelButtons.add(btnThem);
        panelButtons.add(btnCapNhat);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);

        // Gắn sự kiện cho các nút
        btnThem.addActionListener(e -> xuLyThemTinTuc());
        btnCapNhat.addActionListener(e -> xuLyCapNhatTinTuc());
        btnXoa.addActionListener(e -> xuLyXoaTinTuc());
        btnLamMoi.addActionListener(e -> lamMoiFormTinTuc());

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(panelFields, BorderLayout.NORTH);
        panelBottom.add(panelChiTiet, BorderLayout.CENTER);
        panelBottom.add(panelButtons, BorderLayout.SOUTH);

        panelForm.add(panelBottom, BorderLayout.CENTER);

        panel.add(scrollTable, BorderLayout.CENTER);
        panel.add(panelForm, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Đổ dữ liệu của dòng đang chọn trong bảng Tin tức lên form
     */
    private void hienThiTinTucLenForm() {
        int row = tblTinTuc.getSelectedRow();
        if (row >= 0) {
            int maTin = (int) modelTableTinTuc.getValueAt(row, 0);
            try {
                // Gọi RMI Server để lấy thông tin chi tiết bài viết
                TinTuc tt = tinTucService.getTinTucById(maTin);
                if (tt != null) {
                    txtTinTucMa.setText(String.valueOf(tt.getMa()));
                    txtTinTucTieuDe.setText(tt.getTieuDe());
                    txtTinTucNgayDang.setText(tt.getNgayDang().toString());
                    txtTinTucLinkAnh.setText(tt.getLinkAnh());
                    txtTinTucChiTiet.setText(tt.getChiTiet());

                    // Chọn đúng thể loại trong JComboBox
                    for (int i = 0; i < cbTinTucTheLoai.getItemCount(); i++) {
                        TheLoai tl = cbTinTucTheLoai.getItemAt(i);
                        if (tl.getMa() == tt.getMaTheLoai()) {
                            cbTinTucTheLoai.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (RemoteException ex) {
                showLoiRMI(ex);
            }
        }
    }

    /**
     * Xử lý thêm mới tin tức
     */
    private void xuLyThemTinTuc() {
        String tieuDe = txtTinTucTieuDe.getText().trim();
        TheLoai theLoai = (TheLoai) cbTinTucTheLoai.getSelectedItem();
        String ngayDangStr = txtTinTucNgayDang.getText().trim();
        String linkAnh = txtTinTucLinkAnh.getText().trim();
        String chiTiet = txtTinTucChiTiet.getText().trim();

        if (tieuDe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề bài viết!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (theLoai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại tin!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date ngayDang = Date.valueOf(ngayDangStr);
            TinTuc tt = new TinTuc(tieuDe, chiTiet, linkAnh, theLoai.getMa(), ngayDang);

            // GỌI RMI SERVER: Thêm tin tức
            boolean ok = tinTucService.addTinTuc(tt);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm bài viết mới thành công!");
                loadAllTinTucToTable();
                lamMoiFormTinTuc();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm bài viết thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày đăng không đúng! Hãy dùng định dạng: yyyy-MM-dd", "Lỗi ngày", JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Xử lý cập nhật thông tin bài viết
     */
    private void xuLyCapNhatTinTuc() {
        String maStr = txtTinTucMa.getText().trim();
        if (maStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài viết từ bảng để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ma = Integer.parseInt(maStr);
        String tieuDe = txtTinTucTieuDe.getText().trim();
        TheLoai theLoai = (TheLoai) cbTinTucTheLoai.getSelectedItem();
        String ngayDangStr = txtTinTucNgayDang.getText().trim();
        String linkAnh = txtTinTucLinkAnh.getText().trim();
        String chiTiet = txtTinTucChiTiet.getText().trim();

        try {
            Date ngayDang = Date.valueOf(ngayDangStr);
            TinTuc tt = new TinTuc(ma, tieuDe, chiTiet, linkAnh, theLoai.getMa(), ngayDang);

            // GỌI RMI SERVER: Cập nhật bài viết
            boolean ok = tinTucService.updateTinTuc(tt);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật bài viết thành công!");
                loadAllTinTucToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật bài viết thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày đăng không đúng (yyyy-MM-dd)!", "Lỗi ngày", JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Xử lý xóa bài viết
     */
    private void xuLyXoaTinTuc() {
        String maStr = txtTinTucMa.getText().trim();
        if (maStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài viết cần xóa từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tin tức mã #" + maStr + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // GỌI RMI SERVER: Xóa bài viết
                boolean ok = tinTucService.deleteTinTuc(Integer.parseInt(maStr));
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã xóa bài viết thành công!");
                    loadAllTinTucToTable();
                    lamMoiFormTinTuc();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa bài viết thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                showLoiRMI(ex);
            }
        }
    }

    /**
     * Xóa trắng form nhập tin tức
     */
    private void lamMoiFormTinTuc() {
        txtTinTucMa.setText("");
        txtTinTucTieuDe.setText("");
        txtTinTucLinkAnh.setText("");
        txtTinTucChiTiet.setText("");
        txtTinTucNgayDang.setText(LocalDate.now().toString());
        tblTinTuc.clearSelection();
    }

    // =========================================================================
    // 2. GIAO DIỆN & XỬ LÝ: QUẢN LÝ THỂ LOẠI TIN
    // =========================================================================
    private JPanel createPanelQuanLyTheLoai() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Bảng danh sách thể loại ---
        String[] columns = {"Mã thể loại", "Tên thể loại", "Mô tả"};
        modelTableTheLoai = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTheLoai = new JTable(modelTableTheLoai);
        tblTheLoai.setRowHeight(24);
        tblTheLoai.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTheLoai.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblTheLoai.getColumnModel().getColumn(2).setPreferredWidth(450);

        // Sự kiện click bảng thể loại
        tblTheLoai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblTheLoai.getSelectedRow();
                if (row >= 0) {
                    txtTheLoaiMa.setText(modelTableTheLoai.getValueAt(row, 0).toString());
                    txtTheLoaiTen.setText(modelTableTheLoai.getValueAt(row, 1).toString());
                    Object moTa = modelTableTheLoai.getValueAt(row, 2);
                    txtTheLoaiMoTa.setText(moTa != null ? moTa.toString() : "");
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblTheLoai);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách Thể loại tin"));

        // --- Form nhập thể loại ---
        JPanel panelForm = new JPanel(new BorderLayout(10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin Thể loại"));

        JPanel panelInputs = new JPanel(new GridLayout(2, 2, 8, 8));
        panelInputs.add(new JLabel("Mã thể loại (Tự tăng):"));
        txtTheLoaiMa = new JTextField();
        txtTheLoaiMa.setEditable(false);
        txtTheLoaiMa.setBackground(new Color(240, 240, 240));
        panelInputs.add(txtTheLoaiMa);

        panelInputs.add(new JLabel("Tên thể loại (*):"));
        txtTheLoaiTen = new JTextField();
        panelInputs.add(txtTheLoaiTen);

        JPanel panelMoTa = new JPanel(new BorderLayout(5, 5));
        panelMoTa.add(new JLabel("Mô tả thể loại:"), BorderLayout.NORTH);
        txtTheLoaiMoTa = new JTextArea(3, 20);
        txtTheLoaiMoTa.setLineWrap(true);
        txtTheLoaiMoTa.setWrapStyleWord(true);
        panelMoTa.add(new JScrollPane(txtTheLoaiMoTa), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        JButton btnThemTL = new JButton("➕ Thêm mới");
        JButton btnSuaTL = new JButton("✏️ Cập nhật");
        JButton btnXoaTL = new JButton("🗑️ Xóa");
        JButton btnLamMoiTL = new JButton("🔄 Làm mới form");

        panelButtons.add(btnThemTL);
        panelButtons.add(btnSuaTL);
        panelButtons.add(btnXoaTL);
        panelButtons.add(btnLamMoiTL);

        btnThemTL.addActionListener(e -> xuLyThemTheLoai());
        btnSuaTL.addActionListener(e -> xuLyCapNhatTheLoai());
        btnXoaTL.addActionListener(e -> xuLyXoaTheLoai());
        btnLamMoiTL.addActionListener(e -> lamMoiFormTheLoai());

        JPanel panelCenterForm = new JPanel(new BorderLayout());
        panelCenterForm.add(panelInputs, BorderLayout.NORTH);
        panelCenterForm.add(panelMoTa, BorderLayout.CENTER);
        panelCenterForm.add(panelButtons, BorderLayout.SOUTH);

        panelForm.add(panelCenterForm, BorderLayout.CENTER);

        panel.add(scrollTable, BorderLayout.CENTER);
        panel.add(panelForm, BorderLayout.SOUTH);

        return panel;
    }

    private void xuLyThemTheLoai() {
        String ten = txtTheLoaiTen.getText().trim();
        String moTa = txtTheLoaiMoTa.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thể loại!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TheLoai tl = new TheLoai(ten, moTa);
            // GỌI RMI SERVER: Thêm thể loại
            boolean ok = theLoaiService.addTheLoai(tl);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm thể loại thành công!");
                loadAllTheLoaiToTable();
                loadTheLoaiToComboBoxes();
                lamMoiFormTheLoai();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thể loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    private void xuLyCapNhatTheLoai() {
        String maStr = txtTheLoaiMa.getText().trim();
        if (maStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần cập nhật từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ten = txtTheLoaiTen.getText().trim();
        String moTa = txtTheLoaiMoTa.getText().trim();

        try {
            TheLoai tl = new TheLoai(Integer.parseInt(maStr), ten, moTa);
            // GỌI RMI SERVER: Cập nhật thể loại
            boolean ok = theLoaiService.updateTheLoai(tl);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thể loại thành công!");
                loadAllTheLoaiToTable();
                loadTheLoaiToComboBoxes();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thể loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    private void xuLyXoaTheLoai() {
        String maStr = txtTheLoaiMa.getText().trim();
        if (maStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa thể loại #" + maStr + "?\nCác bài viết thuộc thể loại này sẽ được đặt về thể loại trống.", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // GỌI RMI SERVER: Xóa thể loại
                boolean ok = theLoaiService.deleteTheLoai(Integer.parseInt(maStr));
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thể loại thành công!");
                    loadAllTheLoaiToTable();
                    loadTheLoaiToComboBoxes();
                    loadAllTinTucToTable();
                    lamMoiFormTheLoai();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thể loại thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                showLoiRMI(ex);
            }
        }
    }

    private void lamMoiFormTheLoai() {
        txtTheLoaiMa.setText("");
        txtTheLoaiTen.setText("");
        txtTheLoaiMoTa.setText("");
        tblTheLoai.clearSelection();
    }

    // =========================================================================
    // 3. GIAO DIỆN & XỬ LÝ: TÌM KIẾM (TRỌNG TÂM BTL CÁ NHÂN)
    // =========================================================================
    private JPanel createPanelTimKiem() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 12, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // -------------------------------------------------------------
        // NỬA BÊN TRÁI: TÌM THỂ LOẠI THEO TÊN GẦN ĐÚNG
        // -------------------------------------------------------------
        JPanel panelTimTheLoai = new JPanel(new BorderLayout(8, 8));
        panelTimTheLoai.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                "1. Tìm Thể loại theo tên gần đúng",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(41, 128, 185)
        ));

        // Thanh tìm kiếm
        JPanel pnlInputTL = new JPanel(new BorderLayout(5, 5));
        pnlInputTL.setBorder(new EmptyBorder(5, 5, 5, 5));
        pnlInputTL.add(new JLabel("Nhập tên thể loại cần tìm: "), BorderLayout.WEST);
        txtTimTheLoaiTen = new JTextField();
        pnlInputTL.add(txtTimTheLoaiTen, BorderLayout.CENTER);

        JPanel pnlBtnTL = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnTimTL = new JButton("🔍 Tìm thể loại");
        JButton btnTatCaTL = new JButton("Xem tất cả");
        pnlBtnTL.add(btnTimTL);
        pnlBtnTL.add(btnTatCaTL);
        pnlInputTL.add(pnlBtnTL, BorderLayout.EAST);

        // Bảng hiển thị kết quả tìm Thể loại
        String[] colsTL = {"Mã TL", "Tên thể loại", "Mô tả"};
        modelKetQuaTheLoai = new DefaultTableModel(colsTL, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKetQuaTheLoai = new JTable(modelKetQuaTheLoai);
        tblKetQuaTheLoai.setRowHeight(22);
        tblKetQuaTheLoai.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblKetQuaTheLoai.getColumnModel().getColumn(1).setPreferredWidth(120);

        lblSoLuongTheLoai = new JLabel("Chưa thực hiện tìm kiếm.");
        lblSoLuongTheLoai.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSoLuongTheLoai.setBorder(new EmptyBorder(5, 5, 5, 5));

        panelTimTheLoai.add(pnlInputTL, BorderLayout.NORTH);
        panelTimTheLoai.add(new JScrollPane(tblKetQuaTheLoai), BorderLayout.CENTER);
        panelTimTheLoai.add(lblSoLuongTheLoai, BorderLayout.SOUTH);

        // Sự kiện tìm thể loại
        btnTimTL.addActionListener(e -> xuLyTimKiemTheLoai());
        txtTimTheLoaiTen.addActionListener(e -> xuLyTimKiemTheLoai()); // Nhấn Enter cũng tìm
        btnTatCaTL.addActionListener(e -> {
            txtTimTheLoaiTen.setText("");
            xuLyTimKiemTheLoai();
        });

        // -------------------------------------------------------------
        // NỬA BÊN PHẢI: TÌM TIN TỨC THEO MÃ, LOẠI TIN, NGÀY ĐĂNG
        // -------------------------------------------------------------
        JPanel panelTimTinTuc = new JPanel(new BorderLayout(8, 8));
        panelTimTinTuc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
                "2. Tìm Tin tức theo Mã, Loại tin, Ngày đăng",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(39, 174, 96)
        ));

        // Bộ lọc 3 tiêu chí
        JPanel pnlFilters = new JPanel(new GridLayout(3, 2, 6, 6));
        pnlFilters.setBorder(new EmptyBorder(5, 5, 5, 5));

        pnlFilters.add(new JLabel("Mã bài viết (để trống nếu ko lọc):"));
        txtTimTinMa = new JTextField();
        pnlFilters.add(txtTimTinMa);

        pnlFilters.add(new JLabel("Thể loại tin:"));
        cbTimTinTheLoai = new JComboBox<>();
        pnlFilters.add(cbTimTinTheLoai);

        pnlFilters.add(new JLabel("Ngày đăng (yyyy-MM-dd):"));
        txtTimTinNgay = new JTextField();
        pnlFilters.add(txtTimTinNgay);

        JPanel pnlBtnTT = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton btnTimTT = new JButton("🔍 Tìm tin tức");
        JButton btnResetTT = new JButton("Tất cả tin");
        pnlBtnTT.add(btnTimTT);
        pnlBtnTT.add(btnResetTT);

        JPanel pnlNorthTT = new JPanel(new BorderLayout());
        pnlNorthTT.add(pnlFilters, BorderLayout.CENTER);
        pnlNorthTT.add(pnlBtnTT, BorderLayout.SOUTH);

        // Bảng kết quả tìm tin tức
        String[] colsTT = {"Mã", "Tiêu đề", "Thể loại", "Ngày đăng"};
        modelKetQuaTinTuc = new DefaultTableModel(colsTT, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKetQuaTinTuc = new JTable(modelKetQuaTinTuc);
        tblKetQuaTinTuc.setRowHeight(22);
        tblKetQuaTinTuc.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblKetQuaTinTuc.getColumnModel().getColumn(1).setPreferredWidth(230);
        tblKetQuaTinTuc.getColumnModel().getColumn(2).setPreferredWidth(90);

        lblSoLuongTinTuc = new JLabel("Chưa thực hiện tìm kiếm.");
        lblSoLuongTinTuc.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSoLuongTinTuc.setBorder(new EmptyBorder(4, 5, 4, 5));

        // Khung xem chi tiết kết quả khi click vào dòng trong bảng tìm kiếm
        JPanel pnlXemChiTiet = new JPanel(new BorderLayout(5, 5));
        pnlXemChiTiet.setBorder(BorderFactory.createTitledBorder("Xem chi tiết kết quả được chọn"));
        pnlXemChiTiet.setPreferredSize(new Dimension(100, 180));

        JPanel pnlHeaderChiTiet = new JPanel(new GridLayout(2, 1));
        lblChiTietTieuDe = new JLabel("Chọn một bài viết để xem chi tiết...");
        lblChiTietTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblChiTietTieuDe.setForeground(new Color(24, 76, 120));

        lblChiTietThongTin = new JLabel("");
        lblChiTietThongTin.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnlHeaderChiTiet.add(lblChiTietTieuDe);
        pnlHeaderChiTiet.add(lblChiTietThongTin);

        txtChiTietNoiDung = new JTextArea();
        txtChiTietNoiDung.setEditable(false);
        txtChiTietNoiDung.setLineWrap(true);
        txtChiTietNoiDung.setWrapStyleWord(true);
        txtChiTietNoiDung.setBackground(new Color(248, 249, 250));

        pnlXemChiTiet.add(pnlHeaderChiTiet, BorderLayout.NORTH);
        pnlXemChiTiet.add(new JScrollPane(txtChiTietNoiDung), BorderLayout.CENTER);

        JPanel pnlCenterTT = new JPanel(new BorderLayout());
        pnlCenterTT.add(new JScrollPane(tblKetQuaTinTuc), BorderLayout.CENTER);
        pnlCenterTT.add(lblSoLuongTinTuc, BorderLayout.SOUTH);

        panelTimTinTuc.add(pnlNorthTT, BorderLayout.NORTH);
        panelTimTinTuc.add(pnlCenterTT, BorderLayout.CENTER);
        panelTimTinTuc.add(pnlXemChiTiet, BorderLayout.SOUTH);

        // Sự kiện tìm tin tức
        btnTimTT.addActionListener(e -> xuLyTimKiemTinTuc());
        btnResetTT.addActionListener(e -> {
            txtTimTinMa.setText("");
            txtTimTinNgay.setText("");
            if (cbTimTinTheLoai.getItemCount() > 0) cbTimTinTheLoai.setSelectedIndex(0);
            xuLyTimKiemTinTuc();
        });

        // Sự kiện click bảng kết quả tìm tin tức -> Hiện chi tiết
        tblKetQuaTinTuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiChiTietTinTimKiem();
            }
        });

        mainPanel.add(panelTimTheLoai);
        mainPanel.add(panelTimTinTuc);

        return mainPanel;
    }

    /**
     * [BTL CÁ NHÂN]: Xử lý tìm kiếm Thể loại theo tên gần đúng
     */
    private void xuLyTimKiemTheLoai() {
        String tuKhoa = txtTimTheLoaiTen.getText().trim();
        try {
            // GỌI RMI SERVER: Tìm thể loại theo tên gần đúng
            List<TheLoai> list = theLoaiService.searchTheLoaiByTen(tuKhoa);

            // Cập nhật lên JTable
            modelKetQuaTheLoai.setRowCount(0);
            for (TheLoai tl : list) {
                modelKetQuaTheLoai.addRow(new Object[]{
                        tl.getMa(),
                        tl.getTen(),
                        tl.getMoTa()
                });
            }

            lblSoLuongTheLoai.setText("Tìm thấy: " + list.size() + " thể loại phù hợp với từ khóa '" + tuKhoa + "'");
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * [BTL CÁ NHÂN]: Xử lý tìm kiếm Tin tức theo: Mã, Loại tin, Ngày đăng
     */
    private void xuLyTimKiemTinTuc() {
        // 1. Lấy mã tin tức (nếu có nhập)
        Integer ma = null;
        String maStr = txtTimTinMa.getText().trim();
        if (!maStr.isEmpty()) {
            try {
                ma = Integer.parseInt(maStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã bài viết phải là số nguyên!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // 2. Lấy mã thể loại (nếu có chọn)
        Integer maTheLoai = null;
        Object selected = cbTimTinTheLoai.getSelectedItem();
        if (selected instanceof TheLoai) {
            maTheLoai = ((TheLoai) selected).getMa();
        }

        // 3. Lấy ngày đăng (nếu có nhập)
        String ngayDang = txtTimTinNgay.getText().trim();
        if (!ngayDang.isEmpty()) {
            try {
                Date.valueOf(ngayDang); // Kiểm tra xem đúng định dạng yyyy-MM-dd chưa
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Ngày đăng không đúng định dạng (yyyy-MM-dd)!", "Lỗi ngày", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            // GỌI RMI SERVER: Tìm kiếm tin tức đa điều kiện
            List<TinTuc> list = tinTucService.searchTinTuc(ma, maTheLoai, ngayDang);

            // Cập nhật kết quả lên bảng
            modelKetQuaTinTuc.setRowCount(0);
            for (TinTuc tt : list) {
                modelKetQuaTinTuc.addRow(new Object[]{
                        tt.getMa(),
                        tt.getTieuDe(),
                        tt.getTenTheLoai() != null ? tt.getTenTheLoai() : "(Trống)",
                        tt.getNgayDang()
                });
            }

            lblSoLuongTinTuc.setText("Tìm thấy: " + list.size() + " bài viết thỏa mãn.");

            // Reset khung xem chi tiết
            lblChiTietTieuDe.setText("Chọn một bài viết để xem chi tiết...");
            lblChiTietThongTin.setText("");
            txtChiTietNoiDung.setText("");

        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Hiển thị chi tiết bài viết khi người dùng click vào dòng trong bảng kết quả tìm kiếm
     */
    private void hienThiChiTietTinTimKiem() {
        int row = tblKetQuaTinTuc.getSelectedRow();
        if (row >= 0) {
            int maTin = (int) modelKetQuaTinTuc.getValueAt(row, 0);
            try {
                TinTuc tt = tinTucService.getTinTucById(maTin);
                if (tt != null) {
                    lblChiTietTieuDe.setText("📌 " + tt.getTieuDe());
                    lblChiTietThongTin.setText("Thể loại: " + (tt.getTenTheLoai() != null ? tt.getTenTheLoai() : "Chưa rõ") 
                            + " | Ngày đăng: " + tt.getNgayDang() 
                            + " | Link ảnh: " + (tt.getLinkAnh() != null ? tt.getLinkAnh() : "Không có"));
                    txtChiTietNoiDung.setText(tt.getChiTiet());
                }
            } catch (RemoteException ex) {
                showLoiRMI(ex);
            }
        }
    }

    // =========================================================================
    // CÁC HÀM TIỆN ÍCH DÙNG CHUNG ĐỂ NẠP DỮ LIỆU TỪ RMI SERVER
    // =========================================================================

    /**
     * Nạp toàn bộ tin tức vào JTable tab Quản lý Tin tức
     */
    private void loadAllTinTucToTable() {
        try {
            List<TinTuc> list = tinTucService.getAllTinTuc();
            modelTableTinTuc.setRowCount(0);
            for (TinTuc tt : list) {
                modelTableTinTuc.addRow(new Object[]{
                        tt.getMa(),
                        tt.getTieuDe(),
                        tt.getTenTheLoai() != null ? tt.getTenTheLoai() : "(Trống)",
                        tt.getNgayDang(),
                        tt.getLinkAnh()
                });
            }
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Nạp toàn bộ thể loại vào JTable tab Quản lý Thể loại
     */
    private void loadAllTheLoaiToTable() {
        try {
            List<TheLoai> list = theLoaiService.getAllTheLoai();
            modelTableTheLoai.setRowCount(0);
            for (TheLoai tl : list) {
                modelTableTheLoai.addRow(new Object[]{
                        tl.getMa(),
                        tl.getTen(),
                        tl.getMoTa()
                });
            }
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Nạp danh sách Thể loại vào các JComboBox (chọn thể loại ở Form và ở Bộ lọc tìm kiếm)
     */
    private void loadTheLoaiToComboBoxes() {
        try {
            List<TheLoai> list = theLoaiService.getAllTheLoai();

            // Cập nhật cho ComboBox chọn thể loại ở Tab 1
            cbTinTucTheLoai.removeAllItems();
            for (TheLoai tl : list) {
                cbTinTucTheLoai.addItem(tl);
            }

            // Cập nhật cho ComboBox lọc thể loại ở Tab 3 (Tìm kiếm)
            cbTimTinTheLoai.removeAllItems();
            cbTimTinTheLoai.addItem("-- Tất cả thể loại --");
            for (TheLoai tl : list) {
                cbTimTinTheLoai.addItem(tl);
            }
        } catch (RemoteException ex) {
            showLoiRMI(ex);
        }
    }

    /**
     * Hiển thị thông báo khi xảy ra lỗi gọi hàm RMI qua mạng
     */
    private void showLoiRMI(RemoteException ex) {
        JOptionPane.showMessageDialog(this, 
                "Mất kết nối tới RMI Server!\nVui lòng kiểm tra ServerMain có đang chạy không.\nChi tiết: " + ex.getMessage(), 
                "Lỗi giao tiếp RMI", JOptionPane.ERROR_MESSAGE);
    }
}

