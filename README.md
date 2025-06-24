# 🧠 Đa nhóm CPU (MultiGroupCpu)

> Một bản mod Minecraft được thiết kế để gán CPU Affinity cho tất cả các **CPU Group** trên hệ điều hành Windows, dành riêng cho những hệ thống đa socket hoặc đa nhóm CPU.

---

## 🚀 Giới thiệu

**MultiGroupCpu** là một bản mod Minecraft sử dụng Java và thư viện JNA, được viết cho các máy tính có nhiều `CPU Group` (thường gặp ở hệ thống sử dụng nhiều CPU vật lý hoặc hệ điều hành Windows với giới hạn CPU group). Mục tiêu là gán affinity cho tiến trình Minecraft để khai thác tất cả các nhóm CPU.

> ⚠️ Lưu ý: Việc gán affinity có thể **không mang lại hiệu suất rõ rệt** trong một số trường hợp. Mod không can thiệp sâu vào engine Minecraft mà chỉ thiết lập affinity tại thời điểm khởi động.

---

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ:** Java
- **Minecraft API:** [NeoForge](https://neoforged.net/)
- **Thư viện Native:** JNA (`com.sun.jna.*`)
- **Sử dụng các package:**
  ```java
  import net.neoforged.bus.api.IEventBus;
  import net.neoforged.fml.common.Mod;
  import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
  import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

  import com.sun.jna.*;
  import com.sun.jna.platform.win32.BaseTSD;
  import com.sun.jna.platform.win32.WinNT;


🧩 Cài đặt
Tải file .jar từ releases hoặc tự build từ mã nguồn.

Chép file .jar vào thư mục mods trong thư mục cài đặt Minecraft của bạn.

Khởi động Minecraft bằng loader NeoForge.

Mod sẽ tự động chạy và thực hiện gán affinity cho toàn bộ nhóm CPU.



📌 Ghi chú
Mod không có giao diện người dùng.

Hoạt động tốt trên hệ điều hành Windows với hệ thống nhiều CPU group.

Không yêu cầu cấu hình thêm – chạy là áp dụng.

Dành cho người dùng kỹ thuật cao hoặc các hệ thống hiệu năng đặc biệt.


> 🧪 **Lưu ý hiệu năng**: Trong hầu hết trường hợp, việc gán affinity không tạo ra sự khác biệt rõ rệt về FPS hoặc TPS trong Minecraft. Tuy nhiên, nó hữu ích với các hệ thống đặc biệt hoặc khi cần kiểm soát CPU group.
