public class Test_2 {

    public static void main(String[] args) {
        // Initialize the MemoryManagement system
        UnsafeMemory unsafeMemory = new UnsafeMemory();

        // Create an owner object for managing memory (in this case, a simple object)
        Object owner = new Object();

        // Step 1: Allocate a block of memory (e.g., 1KB) for this owner
        UnsafeMemory.MemoryBlock block = unsafeMemory.allocate(1024, owner);
        System.out.println("Memory block allocated at address: " + block.address + " with size: " + block.size);

        // Step 2: Borrow the memory block immutably (read-only)
        try {
            unsafeMemory.borrow(block, false);
            System.out.println("Memory block borrowed immutably.");
        } catch (IllegalStateException e) {
            System.out.println("Error borrowing immutably: " + e.getMessage());
        }

        // Step 3: Return the borrowed memory (immutably)
        try {
            unsafeMemory.returnBorrow(block, false);
            System.out.println("Memory block returned after immutable borrow.");
        } catch (IllegalStateException e) {
            System.out.println("Error returning borrow: " + e.getMessage());
        }

        // Step 4: Borrow the memory block mutably (write access)
        try {
            unsafeMemory.borrow(block, true);
            System.out.println("Memory block borrowed mutably.");
        } catch (IllegalStateException e) {
            System.out.println("Error borrowing mutably: " + e.getMessage());
        }

        // Step 5: Return the borrowed memory (mutably)
        try {
            unsafeMemory.returnBorrow(block, true);
            System.out.println("Memory block returned after mutable borrow.");
        } catch (IllegalStateException e) {
            System.out.println("Error returning borrow: " + e.getMessage());
        }

        // Step 6: Free the memory once done (only the owner can free it)
        try {
            unsafeMemory.free(block, owner);
            System.out.println("Memory block freed.");
        } catch (IllegalStateException e) {
            System.out.println("Error freeing memory: " + e.getMessage());
        }
    }
}
