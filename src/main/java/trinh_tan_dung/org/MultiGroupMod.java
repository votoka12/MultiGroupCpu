package trinh_tan_dung.org;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;









@Mod("multigroup")
public class MultiGroupMod {

    public MultiGroupMod(IEventBus modBus) {
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onClientSetup);


    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            System.out.println("[MultiGroupMod] 🚀 Khởi động affinity...");

            // Gắn affinity cho thread hiện tại (thường là Server thread)
            ProcessorAffinity.bindCurrentThreadToGroup((short) 1);
            ProcessorAffinity.detectCurrentThreadGroup();

            // Spawn dummy thread trong từng group để duy trì
            ProcessorAffinity.spawnThreadsAcrossAllGroups();

            // Bật AffinityEnforcer
            ProcessorAffinity.startAffinityEnforcerAcrossAllGroups();

            // Gán delay gắn affinity sau 5s (trong trường hợp JVM chưa sẵn sàng ngay)

            ProcessorAffinity.startStatusReporter();

            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
                ProcessorAffinity.applyAffinity();
                System.out.println("[MultiGroupMod] ✅ Đã gán affinity sau delay");
            }, "Affinity-DelayedInit").start();
        });
    }

    private void onClientSetup(FMLClientSetupEvent event) {
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
