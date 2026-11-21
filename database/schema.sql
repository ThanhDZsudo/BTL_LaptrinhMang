-- ==============================================================
-- BÀI TẬP LỚN LẬP TRÌNH MẠNG (INT1433) - PTIT
-- ĐỀ TÀI: QUẢN LÝ TIN TỨC (GIAO THỨC RMI)
-- FILE TẠO CƠ SỞ DỮ LIỆU & DỮ LIỆU MẪU
-- ==============================================================

-- 1. Tạo cơ sở dữ liệu nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS ql_tintuc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ql_tintuc;

-- 2. Xóa bảng cũ nếu đã có (để tránh lỗi khi chạy lại script)
DROP TABLE IF EXISTS tin_tuc;
DROP TABLE IF EXISTS the_loai;

-- 3. Tạo bảng Thể loại tin (TheLoai)
-- Gồm: Mã (Khóa chính, tự tăng), Tên thể loại, Mô tả
CREATE TABLE the_loai (
    ma INT AUTO_INCREMENT PRIMARY KEY,
    ten VARCHAR(150) NOT NULL,
    mo_ta TEXT
);

-- 4. Tạo bảng Tin tức (TinTuc)
-- Gồm: Mã (Khóa chính, tự tăng), Tiêu đề, Chi tiết, Link ảnh, Mã loại tin (Khóa ngoại), Ngày đăng
CREATE TABLE tin_tuc (
    ma INT AUTO_INCREMENT PRIMARY KEY,
    tieu_de VARCHAR(255) NOT NULL,
    chi_tiet TEXT,
    link_anh VARCHAR(255),
    ma_the_loai INT,
    ngay_dang DATE NOT NULL,
    CONSTRAINT fk_tintuc_theloai FOREIGN KEY (ma_the_loai) 
        REFERENCES the_loai(ma) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE
);

-- ==============================================================
-- 5. CHÈN DỮ LIỆU MẪU ĐỂ TEST
-- ==============================================================

-- Chèn dữ liệu mẫu cho Thể loại tin
INSERT INTO the_loai (ma, ten, mo_ta) VALUES
(1, 'Công nghệ', 'Tin tức về công nghệ thông tin, trí tuệ nhân tạo, thiết bị di động'),
(2, 'Thể thao', 'Tin tức bóng đá, quần vợt, thể thao trong nước và quốc tế'),
(3, 'Giáo dục', 'Thông tin tuyển sinh, giáo dục đại học, học bổng và đào tạo'),
(4, 'Kinh tế', 'Thị trường tài chính, chứng khoán, bất động sản, doanh nghiệp'),
(5, 'Giải trí', 'Điện ảnh, âm nhạc, đời sống nghệ thuật và sự kiện văn hóa');

-- Chèn dữ liệu mẫu cho Tin tức
INSERT INTO tin_tuc (ma, tieu_de, chi_tiet, link_anh, ma_the_loai, ngay_dang) VALUES
(101, 'Ra mắt chip xử lý AI thế hệ mới với hiệu năng đột phá', 
 'Tập đoàn công nghệ vừa công bố vi xử lý mới giúp tăng tốc độ xử lý các mô hình học sâu lên gấp 3 lần, tiết kiệm 40% điện năng tiêu thụ.', 
 'https://images.unsplash.com/photo-1518770660439-4636190af475', 1, '2026-11-01'),

(102, 'Đội tuyển bóng đá nam giành chiến thắng kịch tính ở phút 90', 
 'Trận đấu đầy cảm xúc đã khép lại với bàn thắng quyết định từ chấm phạt đền ở những phút bù giờ cuối cùng, mang lại 3 điểm quý giá.', 
 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2', 2, '2026-11-02'),

(103, 'PTIT công bố phương án tuyển sinh và mở thêm ngành học mới', 
 'Học viện Công nghệ Bưu chính Viễn thông thông báo mở thêm chuyên ngành Trí tuệ nhân tạo và An toàn thông tin chất lượng cao.', 
 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1', 3, '2026-11-03'),

(104, 'Thị trường chứng khoán phục hồi mạnh mẽ trong phiên giao dịch', 
 'Chỉ số VN-Index tăng hơn 15 điểm với thanh khoản cải thiện đáng kể, nhóm cổ phiếu công nghệ và ngân hàng dẫn dắt đà tăng.', 
 'https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3', 4, '2026-11-04'),

(105, 'Bộ phim hoạt hình 3D Việt Nam gây sốt phòng vé quốc tế', 
 'Tác phẩm hoạt hình được sản xuất hoàn toàn bởi đội ngũ kỹ sư và nghệ sĩ Việt đã đạt doanh thu ấn tượng ngay tuần đầu công chiếu.', 
 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba', 5, '2026-11-05'),

(106, 'Xu hướng lập trình đa nền tảng đang thịnh hành năm 2026', 
 'Các lập trình viên ngày càng ưa chuộng các công nghệ tích hợp giải pháp mạng và điện toán đám mây cho ứng dụng doanh nghiệp.', 
 'https://images.unsplash.com/photo-1555066931-4365d14bab8c', 1, '2026-11-06'),

(107, 'Giải chạy marathon thu hút hơn 10.000 vận động viên tham gia', 
 'Cung đường chạy ven biển tuyệt đẹp đã đem lại nhiều trải nghiệm đáng nhớ cho các vận động viên chuyên nghiệp lẫn phong trào.', 
 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3', 2, '2026-11-06'),

(108, 'Hội thảo khoa học về bảo mật thông tin trong kỷ nguyên số', 
 'Nhiều chuyên gia an ninh mạng quốc tế đã chia sẻ các giải pháp bảo vệ dữ liệu người dùng và phòng chống tấn công mạng.', 
 'https://images.unsplash.com/photo-1563986768609-322da13575f3', 1, '2026-11-07');

