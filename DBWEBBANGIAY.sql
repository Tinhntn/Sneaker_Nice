Create database web_ban_giay

CREATE TABLE KhachHang (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự tăng
    ma_khach_hang NVARCHAR(50) NOT NULL, -- Mã khách hàng
    ten_khach_hang NVARCHAR(100) NOT NULL, -- Tên khách hàng
    tinh_thanh_pho NVARCHAR(100), -- Tỉnh/Thành phố
    quan_huyen NVARCHAR(100), -- Quận/Huyện
    phuong_xa NVARCHAR(100), -- Phường/Xã
    gioi_tinh NVARCHAR(10), -- Giới tính
    email NVARCHAR(100), -- Email
    sdt NVARCHAR(15), -- Số điện thoại
    ngay_sinh DATE, -- Ngày sinh
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo (mặc định là thời gian hiện tại)
    ngay_sua DATETIME, -- Ngày sửa
    mat_khau NVARCHAR(50) NOT NULL, -- Mật khẩu
    trang_thai BIT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);
CREATE TABLE KhuyenMai (
	id INT PRIMARY KEY IDENTITY (1,1),
	ma_khuyen_mai NVARCHAR(50) NOT NULL,
	ten_khuyen_mai NVARCHAR(200) NOT NULL,
	mo_ta NVARCHAR(2000) NOT NULL,
	dieu_kien_ap_dung DECIMAL(18,2) NOT NULL,
	loai_khuyen_mai BIT NOT NULL,
	gia_tri_giam DECIMAL(18,2) NOT NULL,
	gioi_han_su_dung TINYINT NOT NULL,
	da_su_dung TINYINT NOT NULL,
	so_luong SMALLINT NOT NULL,
	ngay_bat_dau DATETIME NOT NULL, 
	ngay_ket_thuc DATETIME NOT NULL,
	ngay_tao DATETIME DEFAULT GETDATE(),
	ngay_sua DATETIME,
	trang_thai BIT DEFAULT 1 NOT NULL
);
CREATE TABLE ChucVu (
	id INT PRIMARY KEY IDENTITY(1,1),
	ma_chuc_vu VARCHAR(50) NOT NULL,
	ten_chuc_vu NVARCHAR(200) NOT NULL
)
CREATE TABLE NhanVien (
	id INT PRIMARY KEY IDENTITY (1,1),
	id_chuc_vu INT NOT NULL,
	ma_nhan_vien VARCHAR(50) NOT NULL,
	ho_va_ten NVARCHAR(100) NOT NULL,
	ngay_sinh DATE ,
	dia_chi NVARCHAR(200),
	sdt VARCHAR(15) NOT NULL,
	email VARCHAR(100) NOT NULL,
	mat_khau VARCHAR(50) NOT NULL,
	hinh_anh VARCHAR(250) ,
	ngay_tao DATETIME DEFAULT GETDATE(),
	ngay_sua DATETIME,
	trang_thai BIT DEFAULT 1,
		CONSTRAINT id_chuc_vu FOREIGN KEY (id_chuc_vu) REFERENCES ChucVu(id),
);

CREATE TABLE SanPham (
    id INT PRIMARY KEY IDENTITY(1,1),          -- Khóa chính, tự tăng
    ma_san_pham VARCHAR(50) NOT NULL,         -- Mã sản phẩm, kiểu chuỗi
    ten_san_pham NVARCHAR(255) NOT NULL,       -- Tên sản phẩm, kiểu chuỗi
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi chỉnh sửa
    trang_thai TINYINT DEFAULT 1              -- Trạng thái (1: hoạt động, 0: không hoạt động)
);
CREATE TABLE Hang (
    id INT PRIMARY KEY IDENTITY(1,1),          -- Khóa chính, tự tăng
    ma_hang VARCHAR(50) NOT NULL,             -- Mã hãng, không được để trống
    ten_hang NVARCHAR(255) NOT NULL,           -- Tên hãng, không được để trống
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME, -- Ngày sửa, tự động cập nhật khi chỉnh sửa
    trang_thai TINYINT DEFAULT 1               -- Trạng thái (1: hoạt động, 0: không hoạt động)
);
CREATE TABLE ChatLieu (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự tăng
    ma_chat_lieu VARCHAR(50) NOT NULL, -- Mã chất liệu
    ten_chat_lieu NVARCHAR(255) NOT NULL, -- Tên chất liệu
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME, -- Ngày sửa, tự động cập nhật khi có sửa đổi
    trang_thai TINYINT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);
CREATE TABLE MauSac (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    ma_mau_sac VARCHAR(50) NOT NULL, -- Mã màu sắc
    ten_mau_sac NVARCHAR(255) NOT NULL, -- Tên màu sắc
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi chỉnh sửa
    trang_thai TINYINT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);
CREATE TABLE HinhAnh (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    ma_hinh_anh VARCHAR(50) NOT NULL, -- Mã hình ảnh
    url1 VARCHAR(1000) NOT NULL, -- Đường dẫn hình ảnh 1
    url2 VARCHAR(1000) DEFAULT NULL, -- Đường dẫn hình ảnh 2 (tùy chọn)
    url3 VARCHAR(1000) DEFAULT NULL, -- Đường dẫn hình ảnh 3 (tùy chọn)
    url4 VARCHAR(1000) DEFAULT NULL, -- Đường dẫn hình ảnh 4 (tùy chọn)
    url5 VARCHAR(1000) DEFAULT NULL, -- Đường dẫn hình ảnh 5 (tùy chọn)
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi có thay đổi
    trang_thai TINYINT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);

CREATE TABLE DanhMuc (
    id INT PRIMARY KEY IDENTITY, -- Khóa chính tự động tăng
    ma_danh_muc VARCHAR(50) NOT NULL, -- Mã danh mục
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
	ngay_sua DATETIME ,
    trang_thai TINYINT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);

CREATE TABLE Size (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    ma_size VARCHAR(50) NOT NULL, -- Mã size
    ten_size NVARCHAR(100) NOT NULL, -- Tên size
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi chỉnh sửa
    trang_thai TINYINT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);

CREATE TABLE GioHang (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    id_khach_hang INT NOT NULL, -- ID khách hàng (liên kết tới bảng Khách Hàng)
    id_khuyen_mai INT DEFAULT NULL, -- ID khuyến mãi (liên kết tới bảng Khuyến Mãi, nếu có)
    ma_gio_hang VARCHAR(50) NOT NULL, -- Mã định danh cho giỏ hàng
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi có thay đổi
    trang_thai TINYINT DEFAULT 1, -- Trạng thái (1: hoạt động, 0: không hoạt động)
    ghi_chu NVARCHAR(300) DEFAULT NULL, -- Ghi chú cho giỏ hàng
    FOREIGN KEY (id_khach_hang) REFERENCES KhachHang(id) ON DELETE CASCADE, -- Ràng buộc khóa ngoại với bảng Khách Hàng
    FOREIGN KEY (id_khuyen_mai) REFERENCES KhuyenMai(id) ON DELETE SET NULL -- Ràng buộc khóa ngoại với bảng Khuyến Mãi
);
CREATE TABLE ChiTietSanPham (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    id_san_pham INT NOT NULL, -- ID sản phẩm (liên kết tới bảng Sản Phẩm)
    id_hang INT NOT NULL, -- ID hãng (liên kết tới bảng Hãng)
    id_size INT NOT NULL, -- ID size (liên kết tới bảng Size)
    id_danh_muc INT NOT NULL, -- ID danh mục (liên kết tới bảng Danh Mục)
    id_chat_lieu INT NOT NULL, -- ID chất liệu (liên kết tới bảng Chất Liệu)
    id_mau_sac INT NOT NULL, -- ID màu sắc (liên kết tới bảng Màu Sắc)
    gia_nhap DECIMAL(15, 2) NOT NULL, -- Giá nhập
    gia_ban DECIMAL(15, 2) NOT NULL, -- Giá bán
    hinh_anh VARCHAR(255) DEFAULT NULL, -- Đường dẫn hình ảnh sản phẩm
    so_luong INT NOT NULL, -- Số lượng tồn kho
    mo_ta TEXT DEFAULT NULL, -- Mô tả sản phẩm
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME , -- Ngày sửa, tự động cập nhật khi có thay đổi
    trang_thai TINYINT DEFAULT 1, -- Trạng thái (1: hoạt động, 0: không hoạt động)
    FOREIGN KEY (id_san_pham) REFERENCES SanPham(id) ON DELETE CASCADE, -- Khóa ngoại với bảng Sản Phẩm
    FOREIGN KEY (id_hang) REFERENCES Hang(id) ON DELETE CASCADE, -- Khóa ngoại với bảng Hãng
    FOREIGN KEY (id_size) REFERENCES Size(id) ON DELETE CASCADE, -- Khóa ngoại với bảng Size
    FOREIGN KEY (id_danh_muc) REFERENCES DanhMuc(id) ON DELETE CASCADE, -- Khóa ngoại với bảng Danh Mục
    FOREIGN KEY (id_chat_lieu) REFERENCES ChatLieu(id) ON DELETE CASCADE, -- Khóa ngoại với bảng Chất Liệu
    FOREIGN KEY (id_mau_sac) REFERENCES MauSac(id) ON DELETE CASCADE -- Khóa ngoại với bảng Màu Sắc
);


CREATE TABLE GioHangChiTiet (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    id_gio_hang INT NOT NULL, -- ID giỏ hàng (liên kết tới bảng Giỏ Hàng)
    id_chi_tiet_san_pham INT NOT NULL, -- ID chi tiết sản phẩm (liên kết tới bảng Chi Tiết Sản Phẩm)
    so_luong INT NOT NULL, -- Số lượng sản phẩm
    don_gia DECIMAL(18, 2) NOT NULL, -- Đơn giá của sản phẩm
    tong_tien DECIMAL(18, 2) , -- Tổng tiền (tính tự động)
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo, mặc định là thời gian hiện tại
    ngay_sua DATETIME, -- Ngày sửa, tự động cập nhật khi có thay đổi
    trang_thai TINYINT DEFAULT 1, -- Trạng thái (1: hoạt động, 0: không hoạt động)
    FOREIGN KEY (id_gio_hang) REFERENCES GioHang(id) ON DELETE CASCADE, -- Ràng buộc khóa ngoại với bảng Giỏ Hàng
    FOREIGN KEY (id_chi_tiet_san_pham) REFERENCES ChiTietSanPham(id) ON DELETE CASCADE -- Ràng buộc khóa ngoại với bảng Chi Tiết Sản Phẩm
);

CREATE TABLE HoaDon (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    id_nhan_vien INT DEFAULT NULL, -- ID nhân viên (liên kết đến bảng Nhân Viên)
    id_khach_hang INT NOT NULL, -- ID khách hàng (liên kết đến bảng Khách Hàng)
    id_khuyen_mai INT DEFAULT NULL, -- ID khuyến mãi (liên kết đến bảng Khuyến Mãi)
    ma_hoa_don VARCHAR(50) NOT NULL, -- Mã hóa đơn
    thanh_tien DECIMAL(15, 2) NOT NULL, -- Tổng tiền hàng
    ghi_chu TEXT DEFAULT NULL, -- Ghi chú về hóa đơn
    ngay_tao DATETIME DEFAULT GETDATE(), -- Ngày tạo hóa đơn
    ngay_sua DATETIME , -- Ngày cập nhật cuối
    tien_khach_dua DECIMAL(18, 2) DEFAULT NULL, -- Tiền khách hàng thanh toán
    tong_tien DECIMAL(18, 2) NOT NULL, -- Tổng tiền phải trả
    tien_thua DECIMAL(18, 2) DEFAULT NULL, -- Tiền thừa trả lại khách hàng
    tong_tien_giam DECIMAL(18, 2) DEFAULT NULL, -- Tổng tiền giảm giá
    ngay_giao_hang DATETIME DEFAULT NULL, -- Ngày giao hàng
    don_vi_giao_hang VARCHAR(255) DEFAULT NULL, -- Đơn vị giao hàng
    phi_ship DECIMAL(18, 2) DEFAULT NULL, -- Phí ship
    ten_nguoi_giao_hang VARCHAR(255) DEFAULT NULL, -- Tên người giao hàng
    email_nguoi_nhan VARCHAR(255) DEFAULT NULL, -- Email người nhận
    dia_chi_chi_tiet NVARCHAR(300) DEFAULT NULL, -- Địa chỉ chi tiết người nhận
    tinh_thanh_pho VARCHAR(100) DEFAULT NULL, -- Tỉnh/thành phố người nhận
    quan_huyen VARCHAR(100) DEFAULT NULL, -- Quận/huyện người nhận
    phuong_xa VARCHAR(100) DEFAULT NULL, -- Phường/xã người nhận
    loai_hoa_don BIT DEFAULT 1, -- Loại hóa đơn (ví dụ: online, tại cửa hàng)
    loai_thanh_toan BIT DEFAULT 1, -- Loại thanh toán (tiền mặt, chuyển khoản, thẻ)
    ten_nguoi_giao VARCHAR(255) DEFAULT NULL, -- Tên người giao hàng (nếu tự giao)
    sdt_nguoi_giao VARCHAR(20) DEFAULT NULL, -- Số điện thoại người giao hàng
    trang_thai BIT DEFAULT 1, -- Trạng thái hóa đơn (ví dụ: Đang xử lý, Hoàn tất, Hủy)
    FOREIGN KEY (id_nhan_vien) REFERENCES NhanVien(id) ON DELETE SET NULL, -- Khóa ngoại đến bảng Nhân Viên
    FOREIGN KEY (id_khach_hang) REFERENCES KhachHang(id) ON DELETE CASCADE, -- Khóa ngoại đến bảng Khách Hàng
    FOREIGN KEY (id_khuyen_mai) REFERENCES KhuyenMai(id) ON DELETE SET NULL -- Khóa ngoại đến bảng Khuyến Mãi
);

CREATE TABLE LichSuTrangThai (
    id INT PRIMARY KEY IDENTITY(1,1), -- Khóa chính tự động tăng
    id_hoa_don INT NOT NULL, -- ID hóa đơn (liên kết tới bảng Hóa Đơn)
    thoi_gian DATETIME DEFAULT GETDATE(), -- Thời gian thay đổi trạng thái
    trang_thai BIT DEFAULT 1 NOT NULL, -- Trạng thái mới (ví dụ: Đang xử lý, Đã giao, Đã hủy)
    ghi_chu NVARCHAR(300) DEFAULT NULL, -- Ghi chú thêm về thay đổi trạng thái
    FOREIGN KEY (id_hoa_don) REFERENCES HoaDon(id) ON DELETE CASCADE -- Khóa ngoại với bảng Hóa Đơn
);






