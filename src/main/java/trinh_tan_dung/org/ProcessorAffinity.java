package trinh_tan_dung.org;

import com.sun.jna.*;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinNT;


public class ProcessorAffinity {

    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        Pointer GetCurrentProcess();
        WinNT.HANDLE GetCurrentThread();
        boolean SetProcessAffinityMask(Pointer hProcess, BaseTSD.ULONG_PTR mask);
        boolean GetProcessAffinityMask(Pointer hProcess, BaseTSD.ULONG_PTRByReference procMask, BaseTSD.ULONG_PTRByReference sysMask);

        int GetActiveProcessorGroupCount();
        short GetMaximumProcessorCount(short group);
        boolean SetThreadGroupAffinity(WinNT.HANDLE hThread, GROUP_AFFINITY groupAffinity, GROUP_AFFINITY previousGroupAffinity);
        boolean GetThreadGroupAffinity(WinNT.HANDLE hThread, GROUP_AFFINITY groupAffinity);
    }

    public static class GROUP_AFFINITY extends Structure {
        public long Mask;
        public short Group;
        public short Reserved0, Reserved1, Reserved2;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("Mask", "Group", "Reserved0", "Reserved1", "Reserved2");
        }

        public GROUP_AFFINITY() {}
        public GROUP_AFFINITY(long mask, short group) {
            this.Mask = mask;
            this.Group = group;
        }
    }

    public static int getGroupCount() {
        return Kernel32.INSTANCE.GetActiveProcessorGroupCount();
    }

    private static long makeMask(int threadCount) {
        if ( threadCount>= 64) return -1L;
        if ( threadCount<= 0) return 0L;
        return (1L << threadCount) - 1;
    }

    public static void spawnThreadsAcrossAllGroups() {
        int groupCount = getGroupCount();

        for (short group = 0; group < groupCount; group++) {
            final short grp = group;

            Thread worker = new Thread(() -> {
                short maxProcs = Kernel32.INSTANCE.GetMaximumProcessorCount(grp);
                if (maxProcs <= 0) {
                    System.out.printf("[Affinity] ❌ Group %d không có core nào.%n", grp);
                    return;
                }

                WinNT.HANDLE thread = Kernel32.INSTANCE.GetCurrentThread();
                long mask = makeMask(maxProcs);
                GROUP_AFFINITY affinity = new GROUP_AFFINITY(mask, grp);
                boolean success = Kernel32.INSTANCE.SetThreadGroupAffinity(thread, affinity, null);

                System.out.printf("[Affinity] 🧵 Worker Thread → Group %d | OK=%s | Mask=0x%016X%n", grp, success, mask);
                try {
                    while (true) Thread.sleep(60_000);
                } catch (InterruptedException ignored) {}
            }, "Affinity-WorkerGroup-" + group);

            worker.setDaemon(true);
            worker.start();
        }

        // Tự động bật báo cáo định kỳ sau khi tạo worker
        startStatusReporter();
    }

    public static void applyAffinity() {
        GROUP_AFFINITY currentAffinity = new GROUP_AFFINITY();
        boolean gotAffinity = Kernel32.INSTANCE.GetThreadGroupAffinity(Kernel32.INSTANCE.GetCurrentThread(), currentAffinity);

        if (!gotAffinity) {
            System.out.println("[Affinity] ❌ Không lấy được group hiện tại.");
            return;
        }

        short group = currentAffinity.Group;
        short maxProcs = Kernel32.INSTANCE.GetMaximumProcessorCount(group);

        if (maxProcs <= 0) {
            System.out.printf("[Affinity] ❌ Group %d không có core.%n", group);
            return;
        }

        long mask = makeMask(maxProcs);
        GROUP_AFFINITY newAffinity = new GROUP_AFFINITY(mask, group);
        boolean success = Kernel32.INSTANCE.SetThreadGroupAffinity(Kernel32.INSTANCE.GetCurrentThread(), newAffinity, null);

        System.out.printf("[Affinity] ✅ Gán lại affinity cho thread hiện tại → Group %d | Mask=0x%016X | OK=%s%n", group, mask, success);
    }

    public static void startAffinityEnforcerAcrossAllGroups() {
        int groupCount = getGroupCount();

        for (short group = 0; group < groupCount; group++) {
            final short grp = group;

            Thread enforcer = new Thread(() -> {
                Pointer process = Kernel32.INSTANCE.GetCurrentProcess();
                short maxProcs = Kernel32.INSTANCE.GetMaximumProcessorCount(grp);
                long expectedMask = makeMask(maxProcs);

                BaseTSD.ULONG_PTRByReference procMaskRef = new BaseTSD.ULONG_PTRByReference();
                BaseTSD.ULONG_PTRByReference sysMaskRef = new BaseTSD.ULONG_PTRByReference();
                boolean okGetMask = Kernel32.INSTANCE.GetProcessAffinityMask(process, procMaskRef, sysMaskRef);
                if (okGetMask) {
                    long actualMask = procMaskRef.getValue().longValue();
                    expectedMask &= actualMask;
                }

                while (true) {
                    try {
                        BaseTSD.ULONG_PTRByReference currentProcRef = new BaseTSD.ULONG_PTRByReference();
                        BaseTSD.ULONG_PTRByReference currentSysRef = new BaseTSD.ULONG_PTRByReference();
                        boolean ok = Kernel32.INSTANCE.GetProcessAffinityMask(process, currentProcRef, currentSysRef);
                        long currentMask = ok ? currentProcRef.getValue().longValue() : -1;

                        System.out.printf("[AffinityEnforcer] Group %d | Mask hiện tại: 0x%016X | Expect: 0x%016X%n",
                                grp, currentMask, expectedMask);

                        Thread.sleep(15_000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "AffinityEnforcer-Group-" + group);

            enforcer.setDaemon(true);
            enforcer.start();
        }
    }

    public static void bindIfNotInGroup(short expectedGroup) {
        GROUP_AFFINITY aff = new GROUP_AFFINITY();
        boolean ok = Kernel32.INSTANCE.GetThreadGroupAffinity(Kernel32.INSTANCE.GetCurrentThread(), aff);
        if (ok && aff.Group != expectedGroup) {
            bindCurrentThreadToGroup(expectedGroup);
        }
    }

    public static void bindCurrentThreadToGroup(short group) {
        short maxProcs = Kernel32.INSTANCE.GetMaximumProcessorCount(group);
        long mask = makeMask(maxProcs);
        WinNT.HANDLE thread = Kernel32.INSTANCE.GetCurrentThread();
        GROUP_AFFINITY affinity = new GROUP_AFFINITY(mask, group);
        boolean success = Kernel32.INSTANCE.SetThreadGroupAffinity(thread, affinity, null);

        System.out.printf("[Affinity] ✅ Thread: %s → Group %d | Mask=0x%016X | OK=%s%n",
                Thread.currentThread().getName(), group, mask, success);
    }

    public static void detectCurrentThreadGroup() {
        GROUP_AFFINITY aff = new GROUP_AFFINITY();
        boolean ok = Kernel32.INSTANCE.GetThreadGroupAffinity(Kernel32.INSTANCE.GetCurrentThread(), aff);

        if (ok) {
            System.out.printf("[Affinity] Thread hiện tại ở Group %d | Mask: 0x%016X%n", aff.Group, aff.Mask);
        } else {
            System.out.println("[Affinity] ❌ Không thể lấy Group hiện tại.");
        }
    }

    public static void startStatusReporter() {
        Thread reporter = new Thread(() -> {
            while (true) {
                try {
                    int groupCount = getGroupCount();
                    System.out.println("=== [Affinity Status Report] ===");
                    System.out.println("Tổng số group CPU: " + groupCount);
                    for (short group = 0; group < groupCount; group++) {
                        short maxProcs = Kernel32.INSTANCE.GetMaximumProcessorCount(group);
                        System.out.printf("Group %d | Cores: %d | Mask: 0x%016X%n", group, maxProcs, makeMask(maxProcs));
                    }
                    System.out.println("===============================");
                    Thread.sleep(15_000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Affinity-StatusReporter");

        reporter.setDaemon(true);
        reporter.start();
    }
}
