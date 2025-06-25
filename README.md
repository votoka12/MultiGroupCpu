# 🧠 Đa nhóm CPU (MultiGroupCpu)

> Một bản mod Minecraft được thiết kế để gán CPU Affinity cho tất cả các **CPU Group** trên hệ điều hành Windows, dành riêng cho những hệ thống đa socket hoặc đa nhóm CPU.

---

## 🚀 Giới thiệu

**MultiGroupCpu** là một bản mod Minecraft sử dụng Java và thư viện JNA, được viết cho các máy tính có nhiều `CPU Group` (thường gặp ở hệ thống sử dụng nhiều CPU vật lý hoặc hệ điều hành Windows với giới hạn CPU group). Mục tiêu là gán affinity cho tiến trình Minecraft để khai thác tất cả các nhóm CPU.

> ⚠️ **Lưu ý:** Việc gán affinity có thể **không mang lại hiệu suất rõ rệt** trong một số trường hợp. Mod không can thiệp sâu vào engine Minecraft mà chỉ thiết lập affinity tại thời điểm khởi động.

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


**🔧 Cơ chế hoạt động**
Khi Minecraft khởi động, mod sẽ tự động:
Dò số lượng nhóm CPU (GroupCount) đang tồn tại trong hệ thống.
Lặp qua từng nhóm và thiết lập affinity mask cho tiến trình Minecraft.
Sử dụng API native từ Windows thông qua JNA để thực hiện việc gán affinity.

***Toàn bộ quá trình không yêu cầu quyền admin và diễn ra trong nền, không ảnh hưởng đến trải nghiệm người dùng.***


**🧩 Cài đặt**
Tải file .jar từ releases hoặc tự build từ mã nguồn.
Chép file .jar vào thư mục mods trong thư mục cài đặt Minecraft của bạn.
Khởi động Minecraft bằng loader NeoForge.
Mod sẽ tự động chạy và thực hiện gán affinity cho toàn bộ nhóm CPU thông qua JNA.

**⚠️ Lưu ý hiệu năng**
***Mặc dù mod này thực hiện việc gán affinity cho toàn bộ CPU Group, nhưng trong thực tế có thể không mang lại hiệu suất cải thiện rõ rệt, đặc biệt là với các modpack thông thường hoặc cấu hình không bị giới hạn CPU.
Mod chủ yếu phù hợp với các hệ thống đa CPU, đa nhóm affinity, hoặc dùng trong mục đích kiểm thử / tối ưu hóa.***


**📌 Ghi chú**
Mod không có giao diện người dùng.
Hoạt động tốt trên hệ điều hành Windows với hệ thống nhiều CPU group.
Không yêu cầu cấu hình thêm – chạy là áp dụng.
Dành cho người dùng kỹ thuật cao hoặc các hệ thống hiệu năng đặc biệt.
