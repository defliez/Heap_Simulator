package memory;

import java.util.*;

/**
 * This memory model allocates memory cells based on the first-fit method. 
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {

	// maybe use a linked list to store the address and size of the allocated memory
	private LinkedList<MemoryBlock> memoryBlocks;

	/**
	 * Initializes an instance of a first fit-based memory.
	 *
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		// TODO Implement this!
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
		// TODO Implement this!
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
				//displayBlocks();
				System.out.println("Allocated " + size + " cells at " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));	// debug

				Pointer p = new Pointer(block.getStartAddress(), this);
				p.pointAt(block.getStartAddress());
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

				System.out.println("Released " + block.getSize() + " cells at " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));

				// Merge adjacent free blocks if present
				//mergeAdjacentFreeBlocks();
				return;
			}
		}
		//displayBlocks();

	}

	// Merge adjacent free blocks in the memoryBlocks list
	private void mergeAdjacentFreeBlocks() {
		memoryBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));
		List<MemoryBlock> mergedBlocks = new ArrayList<>();

		MemoryBlock currentBlock = null;
		for (MemoryBlock block : memoryBlocks) {
			if (block.isAllocated()) {
				// If the current block is allocated, add it to the merged list
				mergedBlocks.add(block);
			} else {
				// If the current block is free, merge it with the previous block if it is also free
				if (currentBlock != null && !currentBlock.isAllocated()) {
					currentBlock.setSize(currentBlock.getSize() + block.getSize());
				} else {
					// Add the free block to the merged list
					mergedBlocks.add(block);
					currentBlock = block;
				}
			}
		}
		// Update the memoryBlocks list
		memoryBlocks = new LinkedList<>(mergedBlocks);
	}

	public void displayBlocks() {
		System.out.println("Allocated blocks:");
		for (MemoryBlock block : memoryBlocks) {
			if (block.isAllocated()) {
				System.out.println(block.startAddress + "-" + (block.startAddress + block.size - 1));
			}
		}

		System.out.println("Free blocks:");
		for (MemoryBlock block : memoryBlocks) {
			if (!block.isAllocated()) {
				System.out.println(block.startAddress + "-" + (block.startAddress + block.size - 1));
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
			//System.out.print(block.isAllocated() ? "\t\t" : "\t\t\t\t");
			if (block.isAllocated()) {
				System.out.println("A " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));
			} else if (!block.isAllocated()) {
				System.out.println("F " + block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1));
			}
		}
		System.out.println("--------------");

		/*
		int startIndex = -1;
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == 0) {
				if (startIndex == -1) {
					startIndex = i;
				}
			} else {
				if (startIndex != -1) {
					System.out.println("| " + startIndex + " - " + (i - 1) + " | Free");
					startIndex = -1;
				}
				System.out.println("| " + i + " | Allocated");
			}
		}

		if (startIndex != -1) {
			System.out.println("| " + startIndex + " - " + (cells.length - 1) + " | Free");
		}

		 */


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

		public void allocate() {
			this.allocated = true;
		}
		public boolean isAllocated() { return allocated; }

		public int getSize() { return size; }

		public int getStartAddress() { return startAddress; }

		public void deallocate() { allocated = false; }

		public void setSize(int i) {
			this.size = i;
		}
	}
}
