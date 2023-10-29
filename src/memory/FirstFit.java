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
	 * Compacts the memory space.
	 *
	 * @return
	 */
	/*
	@Override
	public Pointer[] compact(Pointer[] pointers) {

		// Sort the list by start address
		memoryBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));

		// Create a new list to store the compacted memory blocks
		LinkedList<MemoryBlock> compactedMemoryBlocks = new LinkedList<>();

		// Create a new list to store the compacted pointers
		LinkedList<Pointer> compactedPointers = new LinkedList<>();

		int tempAddress = 0;
		int nextAddress = 0;

		for (MemoryBlock block : memoryBlocks) {
			if (block.isAllocated()) {
				for (Pointer pointer : pointers) {
					if (pointer.pointsAt() == block.getStartAddress()) {
						block.setStartAddress(tempAddress);
						pointer.pointAt(tempAddress);

						compactedMemoryBlocks.add(block);
						tempAddress += block.getSize();
					}
				}
			}
		}

		for (MemoryBlock block : memoryBlocks) {
			if (!block.isAllocated()) {
				block.setStartAddress(tempAddress);
				compactedMemoryBlocks.add(block);
				tempAddress += block.getSize();
			}
		}


		return compactedPointers.toArray(new Pointer[0]);
	}
	 */


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
		System.out.println("Memory layout:");
		System.out.println("--------------");
		for (MemoryBlock block : memoryBlocks) {
			if (block.isAllocated()) {
				System.out.println(block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1) + "| Allocated");
			} else {
				System.out.println(block.getStartAddress() + "-" + (block.getStartAddress() + block.getSize() - 1) + "| Free");
			}
		}
		System.out.println("--------------");
	}
}
