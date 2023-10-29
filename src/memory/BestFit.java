package memory;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * This memory model allocates memory cells based on the best-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {

	private LinkedList<MemoryBlock> memoryBlocks;

	/**
	 * Initializes an instance of a best fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
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

		MemoryBlock bestBlock = null;
		int bestSize = Integer.MAX_VALUE;
		boolean sameSize = false;

		for (MemoryBlock block : memoryBlocks) {
			if (!block.isAllocated() && block.getSize() >= size && block.getSize() < bestSize) {
				bestBlock = block;
				bestSize = block.getSize();

				// If the block is the same size as the requested size, break the loop
				if (block.getSize() == size) {
					sameSize = true;
					break;
				}
			}
		}

		if (bestBlock != null) {
			// Allocate the block and split if necessary
			bestBlock.allocate();
			// If splitting is needed
			if (!sameSize) {
				MemoryBlock newFreeBlock = new MemoryBlock(bestBlock.getStartAddress() + size, bestBlock.getSize() - size);
				memoryBlocks.add(newFreeBlock);
				bestBlock.setSize(size);
			}

			Pointer p = new Pointer(bestBlock.getStartAddress(), this);
			p.pointAt(bestBlock.getStartAddress());
			memoryBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));
			return p;
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
