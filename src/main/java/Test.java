public class Test {

    public static void main(String[] args) {
        UnsafeMemory gc = new UnsafeMemory();
        Object owner = new Object();

        // Allocate memory
        UnsafeMemory.MemoryBlock block = gc.allocate(1024, owner);
        System.out.println("Memory allocated at address: " + block.address);

        // Borrow memory immutably
        gc.borrow(block, false);
        System.out.println("Memory borrowed immutably.");

        // Try mutable borrow (should fail)
        try {
            gc.borrow(block, true);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Return borrow and free memory
        gc.returnBorrow(block, false);
        gc.free(block, owner);
        System.out.println("Memory freed.");
    }
}
