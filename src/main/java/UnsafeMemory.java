import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UnsafeMemory {

    private static final Unsafe unsafe;
    private final Map<MemoryBlock, BorrowTracker> borrowTrackers = new HashMap<>();

    static {
        try {
            // Accessing Unsafe instance using reflection
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access Unsafe instance", e);
        }
    }

    // Method to allocate memory
    public MemoryBlock allocate(long size, Object owner) {
        long address = unsafe.allocateMemory(size);
        return new MemoryBlock(address, size, owner);
    }

    // Method to free allocated memory
    public void free(MemoryBlock block, Object owner) {
        if (block.owner != owner) {
            throw new IllegalStateException("Only the owner can free this memory.");
        }
        unsafe.freeMemory(block.address);
    }

    // Method to borrow memory
    public void borrow(MemoryBlock block, boolean isMutable) {
        BorrowTracker tracker = borrowTrackers.get(block);
        if (tracker == null) {
            tracker = new BorrowTracker();
            borrowTrackers.put(block, tracker);
        }
        if (isMutable) {
            tracker.borrowMutable();
        } else {
            tracker.borrowImmutable();
        }
    }

    // Method to return borrowed memory
    public void returnBorrow(MemoryBlock block, boolean isMutable) {
        BorrowTracker tracker = borrowTrackers.get(block);
        if (tracker != null) {
            tracker.returnBorrow(isMutable);
        }
    }

    // MemoryBlock class representing an allocated memory block
    static class MemoryBlock {
        long address;   // Pointer to allocated memory
        long size;      // Size of the memory block
        Object owner;   // Owner of the memory block

        MemoryBlock(long address, long size, Object owner) {
            this.address = address;
            this.size = size;
            this.owner = owner;
        }
    }

    // BorrowTracker class to track borrowing of memory blocks
    static class BorrowTracker {
        private int immutableBorrows = 0;
        private boolean mutableBorrow = false;

        // Synchronized method to borrow memory immutably
        synchronized void borrowImmutable() {
            if (mutableBorrow) {
                throw new IllegalStateException("Cannot borrow immutably while a mutable borrow exists.");
            }
            immutableBorrows++;
        }

        // Synchronized method to borrow memory mutably
        synchronized void borrowMutable() {
            if (mutableBorrow || immutableBorrows > 0) {
                throw new IllegalStateException("Cannot borrow mutably while other borrows exist.");
            }
            mutableBorrow = true;
        }

        // Synchronized method to return borrowed memory
        synchronized void returnBorrow(boolean isMutable) {
            if (isMutable) {
                mutableBorrow = false;
            } else {
                immutableBorrows--;
            }
        }
    }
}
