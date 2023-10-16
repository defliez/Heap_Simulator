package memory;

import java.util.*;

/**
 * This memory model allocates memory cells based on the first-fit method. 
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {

	// LinkedList to store and manage memory blocks
	private LinkedList<MemoryBlock> memoryBlocks;

	/**
	 * Initializes an instance of a first fit-based memory.
	 *
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		memoryBlocks = new LinkedList<>();
		memoryBlocks.add(new MemoryBlock(0, size));
	}

	/**
	 * Allocates a number of memory cells. 
	 *
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {

		for (MemoryBlock block : memoryBlocks) {

			if (!block.isAllocated() && block.getSize() >= size) {
				// Allocate the block and split if necessary
				block.allocate();
				// If splitting is needed
				if (block.getSize() > size) {
					MemoryBlock newFreeBlock = new MemoryBlock(block.getStartAddress() + size, block.getSize() - size);
					memoryBlocks.add(newFreeBlock);
					block.setSize(size);
				}

				Pointer p = new Pointer(block.getStartAddress(), this);
				p.pointAt(block.getStartAddress());
				memoryBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));
				return p;
			}
		}
		System.out.println("Allocation failed");	// debug
		return null;	// Allocation failed
	}

	/**
	 * Releases a number of data cells
	 *
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		int addressToRelease = p.pointsAt();

		for (MemoryBlock block : memoryBlocks) {
			if (block.getStartAddress() == addressToRelease && block.isAllocated()) {
				block.deallocate();

				// Merge adjacent free blocks if present
				mergeAdjacentFreeBlocks();
				return;
			}
		}
	}

	// Merge adjacent free blocks in the memoryBlocks list
	private void mergeAdjacentFreeBlocks() {
		// Sort the list by start address
		memoryBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));

		// Merge adjacent free blocks
		boolean sortingComplete = false;

		while (!sortingComplete) {
			sortingComplete = true;
			for (int i = 0; i < memoryBlocks.size() - 1; i++) {
				MemoryBlock currentBlock = memoryBlocks.get(i);
				MemoryBlock nextBlock = memoryBlocks.get(i + 1);

				if (!(currentBlock.isAllocated() || nextBlock.isAllocated())) {
					currentBlock.setSize(currentBlock.getSize() + nextBlock.getSize());
					memoryBlocks.remove(nextBlock);
					sortingComplete = false;
				}
			}
		}
	}

	/**
	 * Prints a simple model of the memory. Example:
	 *
	 * |    0 -  110 | Allocated
	 * |  111 -  150 | Free
	 * |  151 -  999 | Allocated
	 * | 1000 - 1024 | Free
	 */
	@Override
	public void printLayout() {
		// TODO Implement this!
		System.out.println("Memory layout:");
		System.out.println("--------------");
		System.out.println("Allocated\tFree");

		for (MemoryBlock block : memoryBlocks) {
			if (block.isAllocated()) {
				System.out.println("A " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));
			} else {
				System.out.println("F " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));
			}
		}
		System.out.println("--------------");
	}

	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		// TODO Implement this!
	}

	private static class MemoryBlock {
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
		public void setSize(int i) { this.size = i; }
	}
}
