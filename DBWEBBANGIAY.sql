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
    mat_khau NVARCHAR(255) NOT NULL, -- Mật khẩu
    trang_thai BIT DEFAULT 1 -- Trạng thái (1: hoạt động, 0: không hoạt động)
);

CREATE TABLE KhuyenMai (
	id INT PRIMARY KEY IDENTITY (1,1),
	ma_khuyen_mai NVARCHAR(50) NOT NULL,
	ten_khuyen_mai NVARCHAR(200) NOT NULL,
	mo_ta NVARCHAR(1000) NOT NULL,
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
