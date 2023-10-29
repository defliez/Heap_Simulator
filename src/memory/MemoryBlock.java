package memory;

public class MemoryBlock {
    private int startAddress;
    private int size;
    private boolean allocated;

    public MemoryBlock(int startAddress, int size) {
        this.startAddress = startAddress;
        this.size = size;
        this.allocated = false;
    }
    public boolean isAllocated() { return allocated; }
    public void allocate() { this.allocated = true; }
    public void deallocate() { this.allocated = false; }
    public int getStartAddress() { return startAddress; }
    public int getSize() { return size; }
    public void setStartAddress(int newAddress) { this.startAddress = newAddress; }
    public void setSize(int i) { this.size = i; }
}