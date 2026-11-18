public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting CHIP-8 Virtual Machine ===");
        
        Chip8 cpu = new Chip8();
        cpu.init();
        
        System.out.println("======================================");
    }
}