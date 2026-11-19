import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting CHIP-8 Virtual Machine ===");
        
        Chip8 cpu = new Chip8();
        cpu.init();

        // 1. Create a dummy test ROM file (test.ch8) with 3 sample opcodes
        // 0x1200 (Jump), 0x6005 (Set V0 = 5), 0x7002 (Add 2 to V0)
        String testRomName = "test.ch8";
        byte[] sampleOpcodes = new byte[] {
            (byte) 0x12, (byte) 0x00,
            (byte) 0x60, (byte) 0x05,
            (byte) 0x70, (byte) 0x02
        };

        try (FileOutputStream fos = new FileOutputStream(testRomName)) {
            fos.write(sampleOpcodes);
            System.out.println("📦 Generated test ROM: " + testRomName);
        } catch (IOException e) {
            System.err.println("Failed to write test ROM: " + e.getMessage());
        }

        // 2. Load the ROM into memory
        boolean success = cpu.loadRom(testRomName);

        // 3. Inspect the loaded opcodes in memory starting at 0x200
        if (success) {
            cpu.dumpMemory(0x200, sampleOpcodes.length);
        }

        System.out.println("======================================");
    }
}