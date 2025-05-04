$(document).ready(function () {
    $(".quantity-right-plus").click(function () {
        var quantityInput = $("#quantity");
        var quantity = parseInt(quantityInput.val());
        quantityInput.val(quantity + 1);

    });

    $(".quantity-left-minus").click(function () {
        var quantityInput = $("#quantity");
        var quantity = parseInt(quantityInput.val());
        var min = parseInt(quantityInput.attr("min"));

        if (quantity > min) {
            quantityInput.val(quantity - 1);
        }
    });
});
var notyf = new Notyf({ ripple: true });

function selectSize(button) {
    // Xóa class active khỏi tất cả các nút size

    document.querySelectorAll('.size-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Đánh dấu nút được chọn
    button.classList.add('active');

    // Cập nhật giá trị size được chọn
    document.getElementById("selectedSize").value = button.getAttribute("data-idSize");

}

function selectMauSac(button) {
    // Xóa class active khỏi tất cả các nút màu sắc
    document.querySelectorAll('.btn-mau-sac').forEach(btn => {
        btn.classList.remove('active');


    });
    // Đánh dấu nút được chọn
    button.classList.add('active');
}
document.addEventListener("DOMContentLoaded", function () {
    updateCartCount();
    var combinations = /*[[${lstCTSP}]]*/[];
    const colorButtons = document.querySelectorAll(".btn-mau-sac");
    const sizeButtons = document.querySelectorAll(".size-btn");
    const quantityText = document.getElementById("soLuong");
    const giaBan = document.getElementById('giaBan');

    let selectedColor = null;
    let selectedSize = null;
    function updateQuantity() {
        let idMauSac = document.querySelector(".btn-mau-sac.active")?.getAttribute("data-idMauSac") || "";
        let idSize = document.querySelector(".size-btn.active")?.getAttribute("data-idSize") || "";
        const idSanPham = document.getElementById("productId").value;
        let query = new URLSearchParams({ idSanPham, idSize, idMauSac }).toString();

        fetch(`/Sneakers_Nice/lay-combination?${query}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        })
            .then(response => response.json())
            .then(data => {
                if (data && data.chiTietSanPham !== undefined) {
                    quantityText.innerText = `Còn hàng: ${data.chiTietSanPham.soLuong}`;
                    giaBan.innerText = `Giá: ${Number(data.chiTietSanPham.giaBan).toLocaleString('vi-VN')} VNĐ`;
                    document.getElementById("idMauSac").innerText = `${data.chiTietSanPham.idMauSac.tenMauSac}`;
                    document.getElementById("idMauSac").setAttribute("data-mauSac", `${data.chiTietSanPham.idMauSac.id}`);
                } else {
                    giaBan.innerText = `Giá: ${Number(data.chiTietSanPham.giaBan).toLocaleString('vi-VN')} VNĐ`;
                    document.getElementById("idMauSac").innerText = `${data.chiTietSanPham.idMauSac.tenMauSac}`;
                    document.getElementById("idMauSac").setAttribute("data-mauSac", `${data.chiTietSanPham.idMauSac.id}`)
                    quantityText.innerText = "Hết hàng";

                }
            })
            .catch(error => {
                console.error("Lỗi:", error);
            });
    }


    function updateSizes() {
        sizeButtons.forEach((btn) => {
            const size = btn.getAttribute("data-idSize");
            const isAvailable = combinations.some(c => c.idMauSac.id == selectedColor && c.idSize.id == size);
            btn.disabled = !isAvailable;
            if (!isAvailable && btn.classList.contains("active")) {
                btn.classList.remove("active");
            }
        });
    }

    function updateColors() {
        colorButtons.forEach((btn) => {
            const color = btn.getAttribute("data-idMauSac");
            const isAvailable = combinations.some(c => c.idSize.id == selectedSize && c.idMauSac.id == color);
            btn.disabled = !isAvailable;
            if (!isAvailable && btn.classList.contains("active")) {
                btn.classList.remove("active");
            }
        });
    }

    colorButtons.forEach(btn => {
        btn.addEventListener("click", function () {
            selectedColor = this.getAttribute("data-idMauSac");
            updateSizes();
            if (selectedColor && selectedSize) {
                updateQuantity();
            }
        });
    });

    sizeButtons.forEach(btn => {
        btn.addEventListener("click", function () {
            selectedSize = this.getAttribute("data-idSize");
            updateColors();
            if (selectedColor && selectedSize) {
                updateQuantity();
            }
        });
    });
    document.addEventListener("click", function (event) {
        // Kiểm tra nếu phần tử được click không phải là các nút size hoặc màu sắc
        if (!event.target.classList.contains("btn-mau-sac") && !event.target.classList.contains("size-btn")) {
            selectedColor = null;
            selectedSize = null;

            // Bật lại tất cả các nút
            sizeButtons.forEach(btn => btn.disabled = false);
            colorButtons.forEach(btn => btn.disabled = false);
        }
    });

});

document.querySelectorAll(".btn-mau-sac").forEach(button => {
    button.addEventListener("click", function () {
        // Lấy id màu sắc từ nút được click
        const idMauSac = this.getAttribute("data-idMauSac");

        // Cập nhật value cho input ẩn
        document.getElementById("selectedMauSac").value = idMauSac;

        // Lấy id sản phẩm từ Thymeleaf (tùy bạn dùng kiểu nào)
        const idSP = /*[[${chiTietSanPham != null && chiTietSanPham.idSanPham!=null ? chiTietSanPham.idSanPham.id : ''}]]*/ "";

        // Gọi API để lấy ảnh theo idSP và idMauSac
        fetch(`/Sneakers_Nice/layAnh?idSP=${idSP}&idMauSac=${idMauSac}`)
            .then(response => response.json())
            .then(data => {
                if (data && data.hinhAnh) {
                    document.getElementById("productImage").src = data.hinhAnh;
                }
            })
            .catch(error => {
                console.error("Lỗi khi lấy ảnh:", error);
                notyf.error('Có lỗi xảy ra khi cập nhật ảnh!');
            });

        // Cập nhật class active cho nút được chọn
        document.querySelectorAll(".btn-mau-sac").forEach(btn => btn.classList.remove("active"));
        this.classList.add("active");
    });
});

$("#themVaoGioHang").click(async function themSanPhamVaoGioHang() {
    var idMauSac = $(".btn-mau-sac.active").attr("data-idMauSac");
    var idSize = $(".size-btn.active").attr("data-idSize");
    var soLuong = document.getElementById("quantity").value;
    var idSanPham = /*[[${chiTietSanPham != null && chiTietSanPham.idSanPham != null ? chiTietSanPham.idSanPham.id : ''}]]*/ "";

    if (soLuong < 1) {
        notyf.error('Số lượng phải lớn hơn 0');
        return;
    } else if (idSize == null) {
        notyf.error('Vui lòng chọn size');
        return;
    }
    else if (idMauSac == null) {
        notyf.error('Vui lòng chọn màu sắc');
        return;
    }
    else {
        let formData = {
            idSanPham: idSanPham,
            idSize: idSize,
            idMauSac: idMauSac,
            soLuong: soLuong
        };
        let response = await fetch(`/gio-hang/them-vao-gio-hang`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(formData)
        });
        if (!response.ok) {
            let errorData = {};
            try {
                errorData = await response.json();
            } catch (e) {
                console.error("Không parse được lỗi JSON:", e);
            }

            const message = errorData?.message || "Có lỗi xảy ra";

            switch (response.status) {
                case 401:
                    notyf.error(message);
                    setTimeout(() => window.location.href = "/dang-nhap", 2000);
                    break;
                case 400:
                    notyf.error(message);
                    break;
                default:
                    notyf.error("Lỗi không xác định: " + message);
            }
        }

        else if (response.status === 200) {
            let data = await response.json();
            if (data && data.message) {
                updateCartCount();
                notyf.success(data.message);
            } else {
                updateCartCount();
                notyf.success('Thêm sản phẩm vào giỏ hàng thành công');
                return;
            }
        }
    }
});
$("#muaNgay").click(async function muaNgay() {
    var idMauSac = $(".btn-mau-sac.active").attr("data-idMauSac");
    var idSize = $(".size-btn.active").attr("data-idSize");
    var soLuong = document.getElementById("quantity").value;
    var idSanPham = /*[[${chiTietSanPham != null && chiTietSanPham.idSanPham != null ? chiTietSanPham.idSanPham.id : ''}]]*/ "";

    if (soLuong < 1) {
        notyf.error('Số lượng phải lớn hơn 0');
        return;
    } else if (idSize == null) {
        notyf.error('Vui lòng chọn size');
        return;
    }
    else if (idMauSac == null) {
        notyf.error('Vui lòng chọn màu sắc');
        return;
    }
    else {
        let formData = {
            idSanPham: idSanPham,
            idSize: idSize,
            idMauSac: idMauSac,
            soLuong: soLuong
        };
        let response = await fetch(`/gio-hang/them-vao-gio-hang`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(formData)
        });
        if (!response.ok) {
            let errorData;
            try {
                errorData = await response.json(); // Lấy dữ liệu lỗi từ server
            } catch (e) {
                errorData = { message: "Lỗi không xác định" }; // Nếu không lấy được dữ liệu
            }
            const message = errorData.message || "Có lỗi xảy ra";

            if (response.status === 401) {
                notyf.error(errorData.message);
                setTimeout(() => window.location.href = "/dang-nhap", 2000);
                return;
            }

        } else if (response.status === 200) {
            window.location.href = "/gio-hang/thanh-toan"
        }

    }

});