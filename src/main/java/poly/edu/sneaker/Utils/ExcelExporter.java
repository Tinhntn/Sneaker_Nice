package poly.edu.sneaker.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import poly.edu.sneaker.DAO.ThongKeDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<ThongKeDTO> listThongKe;

    public ExcelExporter(List<ThongKeDTO> listThongKe) {
        this.listThongKe = listThongKe;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("ThongKe");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        createCell(row, 0, "Doanh Thu", style);
        createCell(row, 1, "Số Sản Phẩm", style);
        createCell(row, 2, "Hóa Đơn Thành Công", style);
        createCell(row, 3, "Hóa Đơn Hủy", style);
        // Thêm các cột cho chi tiết trạng thái đơn hàng
        createCell(row, 4, "Chưa thanh toán", style);
        createCell(row, 5, "Đã thanh toán", style);
        createCell(row, 6, "Chờ xác nhận", style);
        createCell(row, 7, "Chờ lấy hàng", style);
        createCell(row, 8, "Đang giao", style);
        createCell(row, 9, "Đã giao", style);
        createCell(row, 10, "Đã hủy", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);

        for (ThongKeDTO tk : listThongKe) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, tk.getDoanhThu() != null ? tk.getDoanhThu() : 0.0, style);
            createCell(row, columnCount++, tk.getSoSanPham() != null ? tk.getSoSanPham() : 0, style);
            createCell(row, columnCount++, tk.getSoHoaDonThanhCong() != null ? tk.getSoHoaDonThanhCong() : 0, style);
            createCell(row, columnCount++, tk.getSoHoaDonHuy() != null ? tk.getSoHoaDonHuy() : 0, style);

            // Ghi dữ liệu chi tiết trạng thái đơn hàng, nếu có
            Map<String, Integer> hd = tk.getHd();
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_0", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_1", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_2", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_3", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_4", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_5", 0) : 0), style);
            createCell(row, columnCount++, (hd != null ? hd.getOrDefault("trangThai_6", 0) : 0), style);
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
