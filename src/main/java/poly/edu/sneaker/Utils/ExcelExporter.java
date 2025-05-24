package poly.edu.sneaker.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import poly.edu.sneaker.DAO.SanPhamBanChayResponse;
import poly.edu.sneaker.DAO.ThongKeDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ExcelExporter {

    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<ThongKeDTO> listThongKe;
    private List<SanPhamBanChayResponse> top5SanPham;

    public ExcelExporter(List<ThongKeDTO> listThongKe, List<SanPhamBanChayResponse> top5SanPham) {
        this.listThongKe = listThongKe;
        this.top5SanPham = top5SanPham;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("ThongKe");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        // Tạo tiêu đề cột
        createCell(row, 0, "ID Sản Phẩm", style, false);
        createCell(row, 1, "Tên Sản Phẩm", style, false);
        createCell(row, 2, "Số Lượng Sản Phẩm", style, false);
        createCell(row, 3, "Giá Bán", style, false);
        createCell(row, 4, "Tổng Tiền", style, false);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style, boolean isCurrency) {
        Cell cell = row.createCell(columnCount);

        if (isCurrency && (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer)) {
            // Chỉ thêm "VND" cho giá trị tiền tệ
            DecimalFormat df = new DecimalFormat("#,###"); // Định dạng số với dấu phân cách hàng nghìn
            cell.setCellValue(df.format(value) + " VND");
        } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float) {
            // Định dạng số không phải tiền tệ
            cell.setCellValue(Double.parseDouble(value.toString()));
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else {
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);

        // Ghi dữ liệu từ top 5 sản phẩm bán chạy
        for (SanPhamBanChayResponse sp : top5SanPham) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // Điền dữ liệu sản phẩm
            createCell(row, columnCount++, rowCount - 1, style, false); // ID Sản Phẩm
            createCell(row, columnCount++, sp.getTenSanPham(), style, false); // Tên Sản Phẩm
            createCell(row, columnCount++, sp.getSoLuongBan(), style, false); // Số Lượng Sản Phẩm
            createCell(row, columnCount++, sp.getGiaBan(), style, true); // Giá Bán
            createCell(row, columnCount++, sp.getGiaBan() * sp.getSoLuongBan(), style, true); // Tổng Tiền
        }

        // Ghi dữ liệu thống kê tổng quan
        for (ThongKeDTO tk : listThongKe) {
            Row detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Hóa Đơn Thành Công", style, false);
            createCell(detailRow, 7, tk.getSoHoaDonThanhCong() != null ? tk.getSoHoaDonThanhCong() : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Hóa Đơn Hủy", style, false);
            createCell(detailRow, 7, tk.getSoHoaDonHuy() != null ? tk.getSoHoaDonHuy() : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Chưa thanh toán", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_0", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Đã thanh toán", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_1", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Chờ xác nhận", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_2", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Chờ lấy hàng", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_3", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Đang giao", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_4", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Đã giao", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_5", 0) : 0, style, false);

            detailRow = sheet.createRow(rowCount++);
            createCell(detailRow, 6, "Đã hủy", style, false);
            createCell(detailRow, 7, tk.getHd() != null ? tk.getHd().getOrDefault("trangThai_6", 0) : 0, style, false);

            // Tổng số sản phẩm và doanh thu
            Row totalRow = sheet.createRow(rowCount++);
            createCell(totalRow, 6, "Tổng Số Sản Phẩm", style, false);
            createCell(totalRow, 7, tk.getSoSanPham() != null ? tk.getSoSanPham() : 0, style, false);

            totalRow = sheet.createRow(rowCount++);
            createCell(totalRow, 6, "Doanh Thu", style, true);
            createCell(totalRow, 7, tk.getDoanhThu() != null ? tk.getDoanhThu() : 0.0, style, true);
        }

        // Tự động điều chỉnh kích thước cột

        int totalColumns = 8; // Số lượng cột tối đa (bao gồm cả cột G và H)
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}