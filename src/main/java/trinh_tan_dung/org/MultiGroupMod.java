package trinh_tan_dung.org;

import net.fabricmc.api.ModInitializer;

public class MultiGroupMod implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("[MultiGroupMod] 🚀 Khởi động affinity...");

        // Gắn affinity cho thread hiện tại (thường là Server thread)
        ProcessorAffinity.bindCurrentThreadToGroup((short) 1);
        ProcessorAffinity.detectCurrentThreadGroup();

        // Spawn dummy thread trong từng group để duy trì
        ProcessorAffinity.spawnThreadsAcrossAllGroups();

        // Bật AffinityEnforcer
        ProcessorAffinity.startAffinityEnforcerAcrossAllGroups();

        // Bắt đầu status reporter
        ProcessorAffinity.startStatusReporter();

        // Delay gắn affinity
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {}
            ProcessorAffinity.applyAffinity();
            System.out.println("[MultiGroupMod] ✅ Đã gán affinity sau delay");
        }, "Affinity-DelayedInit").start();
    }
}
