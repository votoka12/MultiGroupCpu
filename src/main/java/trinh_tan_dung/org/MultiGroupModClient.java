package trinh_tan_dung.org;

import net.fabricmc.api.ClientModInitializer;

public class MultiGroupModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[MultiGroupMod] ✅ Client setup bắt đầu");

        // Gán affinity cho Render Thread (client)
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                System.out.println("[Binder] 🎮 Gán Render thread → Group 0");
                ProcessorAffinity.bindCurrentThreadToGroup((short) 0);
            } catch (Exception ignored) {}
        }, "Affinity-BinderInit").start();
    }
}
