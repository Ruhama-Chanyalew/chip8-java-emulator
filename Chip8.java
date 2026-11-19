import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip8 {
    // 1. 4,096 bytes (4KB) of RAM
    public byte[] memory = new byte[4096];

    // 2. 16 8-bit general-purpose registers (V0 to VF)
    public int[] V = new int[16];

    // 3. 16-bit Index Register (I)
    public int I = 0;

    // 4. Program Counter (PC) - CHIP-8 programs start at 0x200 (address 512)
    public int pc = 0x200;

    // 5. Call Stack (16 levels) and Stack Pointer (SP)
    public int[] stack = new int[16];
    public int sp = 0;

    // 6. Timers running at 60Hz
    public int delayTimer = 0;
    public int soundTimer = 0;

    // 7. Graphics: 64x32 monochrome display buffer
    public boolean[][] display = new boolean[64][32];

    // 8. Built-in 80-byte Font Set (0-F hexadecimal characters)
    private static final int[] FONT_SET = {
        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
        0x20, 0x60, 0x20, 0x20, 0x70, // 1
        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
        0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    // Initialize/Reset the Hardware
    public void init() {
        pc = 0x200;      // Start at 0x200
        I = 0;
        sp = 0;
        delayTimer = 0;
        soundTimer = 0;

        // Clear memory and registers
        for (int i = 0; i < 4096; i++) memory[i] = 0;
        for (int i = 0; i < 16; i++) V[i] = 0;

        // Load built-in font set into memory (from 0x050 to 0x09F)
        for (int i = 0; i < FONT_SET.length; i++) {
            memory[0x050 + i] = (byte) FONT_SET[i];
        }

        System.out.println("✅ CHIP-8 CPU Initialized!");
        System.out.println("   RAM: 4,096 bytes allocated.");
        System.out.printf("   PC: 0x%03X (entry point).%n", pc);
        System.out.println("   Font set loaded into memory (0x050 - 0x09F).");
    }

    // Loads a binary ROM file into RAM starting at address 0x200 (512)
    public boolean loadRom(String romPath) {
        File file = new File(romPath);
        if (!file.exists()) {
            System.err.println("❌ Error: ROM file not found at " + romPath);
            return false;
        }

        // Available memory from 0x200 to 0xFFF is 3,584 bytes
        if (file.length() > (4096 - 0x200)) {
            System.err.println("❌ Error: ROM is too large for CHIP-8 memory (" + file.length() + " bytes).");
            return false;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] romBuffer = new byte[(int) file.length()];
            int bytesRead = fis.read(romBuffer);

            // Copy ROM bytes into memory starting at 0x200
            for (int i = 0; i < bytesRead; i++) {
                memory[0x200 + i] = romBuffer[i];
            }

            System.out.printf("✅ Loaded ROM: %s (%d bytes) into memory at [0x200 - 0x%03X]%n", 
                file.getName(), bytesRead, 0x200 + bytesRead);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error reading ROM: " + e.getMessage());
            return false;
        }
    }

    // Helper to inspect the first few opcodes loaded in memory
    public void dumpMemory(int startAddress, int numBytes) {
        System.out.println("--- Memory Dump ---");
        for (int i = 0; i < numBytes; i += 2) {
            int addr = startAddress + i;
            int opcode = ((memory[addr] & 0xFF) << 8) | (memory[addr + 1] & 0xFF);
            System.out.printf("[0x%03X]: 0x%04X%n", addr, opcode);
        }
        System.out.println("-------------------");
    }
}