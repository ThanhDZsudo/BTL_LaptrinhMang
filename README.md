# BÀI TẬP LỚN LẬP TRÌNH MẠNG (INT1433) - PTIT

* **Đề tài**: Quản lý tin tức
* **Giao thức**: Java RMI (Remote Method Invocation)
* **Công nghệ**: Java 17+, MySQL 8.0+, JDBC thuần, Java Swing
* **Nghiệp vụ cá nhân thực hiện**:
  1. Tìm kiếm Thể loại theo tên gần đúng (`LIKE %...%`)
  2. Tìm kiếm Tin tức theo mã, loại tin, ngày đăng (Tìm kiếm đa điều kiện linh hoạt)
  3. Hiển thị kết quả tìm kiếm trực quan trên JTable và khung xem chi tiết bài viết.

---

## 📁 Cấu trúc thư mục dự án

```text
BTL/
│── database/
│   └── schema.sql                # Script tạo Database và chèn dữ liệu mẫu
│── lib/
│   └── mysql-connector-j-8.3.0.jar # Thư viện kết nối CSDL MySQL (JDBC Driver)
│── src/main/java/
│   ├── model/                    # Các thực thể dữ liệu (implements Serializable)
│   │   ├── TheLoai.java
│   │   └── TinTuc.java
│   ├── service/                  # Remote Interfaces (extends Remote)
│   │   ├── TheLoaiService.java
│   │   └── TinTucService.java
│   ├── server/                   # Phía Server (Database, DAO, Implementations)
│   │   ├── db/
│   │   │   └── DBContext.java    # Quản lý kết nối MySQL (Đổi mật khẩu tại đây)
│   │   ├── dao/
│   │   │   ├── TheLoaiDAO.java   # Thao tác SQL bảng the_loai
│   │   │   └── TinTucDAO.java    # Thao tác SQL bảng tin_tuc & Tìm kiếm đa điều kiện
│   │   ├── impl/
│   │   │   ├── TheLoaiServiceImpl.java # Kế thừa UnicastRemoteObject
│   │   │   └── TinTucServiceImpl.java
│   │   └── ServerMain.java       # Khởi tạo RMI Registry (port 1099) & rebind
│   └── client/                   # Phía Client (Giao diện Swing)
│       ├── ClientContext.java    # Lookup dịch vụ từ RMI Server
│       ├── ui/
│       │   └── MainFrame.java    # Giao diện 3 tab (Tin tức, Thể loại, Tìm kiếm)
│       └── ClientMain.java       # Khởi động ứng dụng Client
│── build.gradle                  # File cấu hình Gradle (Quản lý thư viện MySQL & Tasks)
│── settings.gradle               # Cấu hình tên project Gradle
│── run-server.bat                # File chạy nhanh Server (chạy không cần cài Maven)
│── run-client.bat                # File chạy nhanh Client
└── README.md                     # Hướng dẫn chi tiết & Câu hỏi vấn đáp thường gặp
```

---

## ⚙️ Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Tạo Cơ sở dữ liệu MySQL
1. Mở **MySQL Workbench** (hoặc phpMyAdmin trong XAMPP).
2. Mở file [database/schema.sql](file:///e:/PTITBaiTap/INT1433-LapTrinhMang/BTL/database/schema.sql).
3. Nhấn **Execute (hình tia sét)** để tạo database `ql_tintuc`, 2 bảng `the_loai`, `tin_tuc` và nạp sẵn dữ liệu mẫu.

### Bước 2: Cấu hình mật khẩu MySQL
1. Mở file [DBContext.java](file:///e:/PTITBaiTap/INT1433-LapTrinhMang/BTL/src/main/java/server/db/DBContext.java).
2. Sửa biến `DB_PASS` cho khớp với mật khẩu MySQL trên máy của bạn (ví dụ: `""`, `"root"`, `"123456"`...).

### Bước 3: Mở trong IntelliJ IDEA
1. Mở **IntelliJ IDEA** $\rightarrow$ Chọn **Open** $\rightarrow$ Trỏ tới thư mục `BTL`.
2. IntelliJ sẽ tự động nhận diện file `build.gradle` (Gradle Project) và tự tải thư viện MySQL Connector.
3. Để chạy chương trình:
   * **Cách 1 (Chuột phải chạy trực tiếp)**:
     * Chạy file [ServerMain.java](file:///e:/PTITBaiTap/INT1433-LapTrinhMang/BTL/src/main/java/server/ServerMain.java) trước $\rightarrow$ Thấy báo *"RMI SERVER ĐÃ SẴN SÀNG"*.
     * Chạy file [ClientMain.java](file:///e:/PTITBaiTap/INT1433-LapTrinhMang/BTL/src/main/java/client/ClientMain.java) $\rightarrow$ Cửa sổ giao diện Swing sẽ hiện lên.
   * **Cách 2 (Dùng Gradle Tasks bên thanh công cụ Gradle của IntelliJ)**:
     * Nhấp vào tab **Gradle** bên mép phải IntelliJ $\rightarrow$ `Tasks` $\rightarrow$ `application` $\rightarrow$ Double click vào `runServer`, sau đó double click vào `runClient`.

---

## 💡 BÍ KÍP VẤN ĐÁP KHI BẢO VỆ BTL (CỰC KỲ QUAN TRỌNG)

### 1. Java RMI là gì? Khác gì với Socket thuần?
* **Trả lời**: RMI (Remote Method Invocation) là cơ chế cho phép một đối tượng chạy trên máy này (Client) có thể **gọi trực tiếp phương thức** của một đối tượng chạy trên máy khác (Server) giống hệt như đang gọi hàm nội bộ. Khác với Socket phải tự phân tích chuỗi byte/text thô (như `SEND_MSG|hello`), RMI trừu tượng hóa toàn bộ tầng mạng, tự động đóng gói đối tượng (Serialization) và gửi qua mạng.

### 2. Tại sao các Class Model (`TheLoai`, `TinTuc`) phải `implements Serializable`?
* **Trả lời**: Vì khi Client gọi hàm từ xa và nhận về danh sách đối tượng từ Server (hoặc gửi đối tượng lên Server), đối tượng đó phải được chuyển thành luồng byte (Byte Stream) để truyền qua mạng mạng máy tính. Quá trình này gọi là **Tuần tự hóa (Serialization)**. Nếu không có `implements Serializable`, Java RMI sẽ ném ngoại lệ `NotSerializableException`.

### 3. Tại sao Interface phải `extends Remote` và các hàm phải `throws RemoteException`?
* **Trả lời**:
  * `extends Remote`: Để đánh dấu cho JVM biết đây là một giao diện dịch vụ từ xa.
  * `throws RemoteException`: Vì việc truyền thông qua mạng luôn tiềm ẩn rủi ro (đứt mạng, Server tắt, quá thời gian chờ...). Java bắt buộc phải khai báo ngoại lệ này để phía Client xử lý lỗi mạng.

### 4. Lớp `...ServiceImpl` tại sao phải `extends UnicastRemoteObject`?
* **Trả lời**: Lớp `UnicastRemoteObject` cung cấp các cơ chế hỗ trợ đối tượng lắng nghe cuộc gọi từ xa (Remote Call) trên một cổng TCP và export dịch vụ RMI ra môi trường mạng.

### 5. Nghiệp vụ cá nhân của bạn được hiện thực ở đâu và như thế nào?
* **Tìm kiếm thể loại theo tên gần đúng**:
  * Nằm trong hàm `searchTheLoaiByTen(String ten)` ở `TheLoaiDAO.java`.
  * Dùng câu lệnh SQL: `SELECT * FROM the_loai WHERE LOWER(ten) LIKE ?` với tham số là `"%keyword%"`.
* **Tìm kiếm tin tức theo Mã, Loại tin, Ngày đăng**:
  * Nằm trong hàm `search(Integer ma, Integer maTheLoai, String ngayDang)` ở `TinTucDAO.java`.
  * Sử dụng kỹ thuật **ghép câu SQL động (Dynamic SQL Query)** với `StringBuilder` và `PreparedStatement`: Ban đầu có `WHERE 1=1`. Người dùng nhập điều kiện nào (Mã, Loại tin hoặc Ngày đăng) thì câu SQL mới nối thêm `AND ... = ?`, nhờ đó hỗ trợ tìm kiếm linh hoạt bất kỳ tổ hợp tiêu chí nào.

