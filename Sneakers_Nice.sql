CREATE DATABASE SneakersNice;
GO

USE SneakersNice;
GO

-- Bảng ChucVu
CREATE TABLE ChucVu (
    id INT IDENTITY PRIMARY KEY,
    ma_chuc_vu NVARCHAR(50) UNIQUE NOT NULL,
    ten_chuc_vu NVARCHAR(255) NOT NULL,
   
);

-- Bảng Hang
CREATE TABLE Hang (
    id INT PRIMARY KEY IDENTITY,
    ma_hang NVARCHAR(50) UNIQUE NOT NULL,
    ten_hang NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,

);

-- Bảng ChatLieu
CREATE TABLE ChatLieu (
    id INT PRIMARY KEY IDENTITY,
    ma_chat_lieu NVARCHAR(50) UNIQUE NOT NULL,
    ten_chat_lieu NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,

);

-- Bảng DanhMuc
CREATE TABLE DanhMuc (
    id INT PRIMARY KEY IDENTITY,
    ma_danh_muc NVARCHAR(50) UNIQUE NOT NULL,
    ten_danh_muc NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
);

-- Bảng SanPham
CREATE TABLE SanPham (
    id INT IDENTITY PRIMARY KEY,
    id_hang INT FOREIGN KEY REFERENCES Hang(id),
    id_chat_lieu INT FOREIGN KEY REFERENCES ChatLieu(id),
    id_danh_muc INT FOREIGN KEY REFERENCES DanhMuc(id),
    ma_san_pham NVARCHAR(50) UNIQUE NOT NULL,
    ten_san_pham NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
   
);

-- Bảng NhanVien
CREATE TABLE NhanVien (
    id INT IDENTITY PRIMARY KEY,
    id_chuc_vu INT FOREIGN KEY REFERENCES ChucVu(id),
    ma_nhan_vien NVARCHAR(50) UNIQUE NOT NULL,
    ho_va_ten NVARCHAR(255) NOT NULL,
    ngay_sinh DATE,
    dia_chi NVARCHAR(255),
    sdt NVARCHAR(15) NOT NULL,
    email NVARCHAR(255) UNIQUE,
    mat_khau NVARCHAR(255) NOT NULL,
    hinh_anh NVARCHAR(500),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,

);

-- Bảng KhachHang
CREATE TABLE KhachHang (
    id INT IDENTITY PRIMARY KEY,
    ma_khach_hang NVARCHAR(50) UNIQUE NOT NULL,
    ten_khach_hang NVARCHAR(255) NOT NULL,
    tinh_thanh_pho NVARCHAR(255),
    quan_huyen NVARCHAR(255),
    phuong_xa NVARCHAR(255),
    gioi_tinh BIT,
    email NVARCHAR(255) UNIQUE,
    sdt NVARCHAR(15),
    ngay_sinh DATE,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    mat_khau NVARCHAR(255) NOT NULL,
    hinh_anh NVARCHAR(500),
    trang_thai BIT DEFAULT 1,
);

-- Bảng DiaChi
CREATE TABLE DiaChi (
    id INT IDENTITY PRIMARY KEY,
    id_khach_hang INT FOREIGN KEY REFERENCES KhachHang(id),
    tinh_thanh_pho NVARCHAR(255) NOT NULL,
    quan_huyen NVARCHAR(255) NOT NULL,
    phuong_xa NVARCHAR(255) NOT NULL,
    dia_chi_cu_the NVARCHAR(255) NOT NULL,
    sdt NVARCHAR(15) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 0,
);

-- Bảng MauSac
CREATE TABLE MauSac (
    id INT PRIMARY KEY IDENTITY,
    ma_mau_sac NVARCHAR(50) UNIQUE NOT NULL,
    ten_mau_sac NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
    deleted_at BIT DEFAULT 0
);

-- Bảng Size
CREATE TABLE Size (
    id INT PRIMARY KEY IDENTITY,
    ma_size NVARCHAR(50) UNIQUE NOT NULL,
    ten_size NVARCHAR(255) NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
    deleted_at BIT DEFAULT 0
);

-- Bảng KhuyenMai
CREATE TABLE KhuyenMai(
    id INT PRIMARY KEY IDENTITY,
    ma_khuyen_mai NVARCHAR(50) UNIQUE NOT NULL,
    ten_khuyen_mai NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX),
    dieu_kien_ap_dung FLOAT,
    loai_khuyen_mai BIT DEFAULT 1,
    gia_tri_giam FLOAT NOT NULL,
    muc_giam_gia_toi_da FLOAT NOT NULL,
    da_su_dung INT,
    so_luong INT NOT NULL,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
);

-- Bảng ChiTietSanPham
CREATE TABLE ChiTietSanPham (
    id INT PRIMARY KEY IDENTITY,
    id_san_pham INT FOREIGN KEY REFERENCES SanPham(id),
    id_size INT FOREIGN KEY REFERENCES Size(id),
    id_mau_sac INT FOREIGN KEY REFERENCES MauSac(id),
    trong_luong FLOAT NOT NULL,
    gia_nhap FLOAT NOT NULL,
    gia_ban FLOAT NOT NULL,
    hinh_anh NVARCHAR(1000),
    so_luong INT NOT NULL,
    mo_ta NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
    deleted_at BIT DEFAULT 0
);

-- Bảng HoaDon
CREATE TABLE HoaDon (
    id INT PRIMARY KEY IDENTITY,
    id_nhan_vien INT FOREIGN KEY REFERENCES NhanVien(id),
    id_khach_hang INT FOREIGN KEY REFERENCES KhachHang(id),
    id_khuyen_mai INT FOREIGN KEY REFERENCES KhuyenMai(id),
    ma_hoa_don NVARCHAR(50) NOT NULL,
    thanh_tien FLOAT NOT NULL,
    ghi_chu NVARCHAR(MAX),
    tien_khach_dua FLOAT NOT NULL,
    tong_tien FLOAT NOT NULL,
    tien_thua FLOAT NOT NULL,
    tong_tien_giam FLOAT,
    ngay_giao_hang DATE,
    don_vi_giao_hang NVARCHAR(255),
    phi_ship FLOAT,
    email_nguoi_nhan NVARCHAR(255),
    dia_chi_chi_tiet NVARCHAR(355),
    tinh_thanh_pho NVARCHAR(255),
    quan_huyen NVARCHAR(255),
    phuong_xa NVARCHAR(255),
    loai_hoa_don BIT DEFAULT 1,
    loai_thanh_toan BIT DEFAULT 1,
    ten_nguoi_giao NVARCHAR(255),
    sdt_nguoi_giao NVARCHAR(15),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
);

-- Bảng HoaDonChiTiet
CREATE TABLE HoaDonChiTiet (
    id INT PRIMARY KEY IDENTITY,
    id_hoa_don INT FOREIGN KEY REFERENCES HoaDon(id),
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES ChiTietSanPham(id),
    so_luong INT NOT NULL,
    tong_trong_luong FLOAT NOT NULL,
    don_gia FLOAT NOT NULL,
    ghi_chu NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1
);

-- Bảng GioHang
CREATE TABLE GioHang (
    id INT PRIMARY KEY IDENTITY,
    id_khach_hang INT FOREIGN KEY REFERENCES KhachHang(id),
    id_khuyen_mai INT FOREIGN KEY REFERENCES KhuyenMai(id),
    ma_gio_hang NVARCHAR(50),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    ghi_chu NVARCHAR(255),
    trang_thai BIT DEFAULT 1,
);

-- Bảng GioHangChiTiet
CREATE TABLE GioHangChiTiet (
    id INT PRIMARY KEY IDENTITY,
    id_gio_hang INT FOREIGN KEY REFERENCES GioHang(id),
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES ChiTietSanPham(id),
    so_luong INT NOT NULL,
    tong_trong_luong FLOAT NOT NULL,
    don_gia FLOAT NOT NULL,
    tong_tien FLOAT NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_sua DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
);

-- Bảng TrangThaiDonHang
CREATE TABLE TrangThaiDonHang(
    id INT PRIMARY KEY IDENTITY,
    id_hoa_don INT FOREIGN KEY REFERENCES HoaDon(id),
    ngay_cap_nhat DATETIME,
    trang_thai TINYINT NOT NULL,
    ghi_chu NVARCHAR(MAX),
);