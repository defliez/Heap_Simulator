package memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This memory model allocates memory cells based on the first-fit method. 
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {

	// maybe use a linked list to store the address and size of the allocated memory
	private LinkedList<MemoryBlock> allocatedBlocks;
	private LinkedList<MemoryBlock> freeBlocks;

	/**
	 * Initializes an instance of a first fit-based memory.
	 *
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		// TODO Implement this!
		allocatedBlocks = new LinkedList<>();
		freeBlocks = new LinkedList<>();
		freeBlocks.add(new MemoryBlock(0, size));

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
		for (MemoryBlock block : freeBlocks) {

			if (block.size >= size) {
				allocatedBlocks.add(block);			// block size = 1000
				freeBlocks.remove(block);

				// If there are any free cells left, create a new free block
				if (block.size > size) {
					MemoryBlock newFreeBlock = new MemoryBlock(block.startAddress + size, block.size - size);
					freeBlocks.add(newFreeBlock);
				}

													// FIRST ITERATION
				allocatedBlocks.remove(block);		// block size = 1000
				block.size = size;					// block size = 100
				allocatedBlocks.add(block);			// adds block with size 100 to allocatedBlocks

				///////////// CHECK HERE IF THE ALLOCATED BLOCK IS CORRECT SIZE ///////////////

				/*
				System.out.println("Allocated " + size + " cells at index " + block.startAddress
				+ " - " + (block.startAddress + block.size - 1));	// debug
				 */

				displayBlocks();

				Pointer p = new Pointer(block.startAddress, this);
				p.pointAt(block.startAddress);

				displayBlocks();

				return p;
			}
		}
		System.out.println("Allocation failed");	// debug
		return null;	// Allocation failed
	}

	public void displayBlocks() {
		System.out.println("Allocated blocks:");
		for (MemoryBlock block : allocatedBlocks) {
			System.out.println(block.startAddress + "-" + (block.startAddress + block.size - 1));
		}

		System.out.println("Free blocks:");
		for (MemoryBlock block : freeBlocks) {
			System.out.println(block.startAddress + "-" + (block.startAddress + block.size - 1));
		}
	}

	/**
	 * Releases a number of data cells
	 *
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		int addressToRelease = p.pointsAt();
		MemoryBlock blockToRelease = null;

		for (MemoryBlock block : allocatedBlocks) {
			if (block.startAddress == addressToRelease) {
				blockToRelease = block;
				// System.out.println("Released " + block.size + " cells at index "+ block.startAddress);	// debug
				break;
			}
		}

		if (blockToRelease != null) {
			for (MemoryBlock freeBlock : freeBlocks) {
				if (freeBlock.startAddress + freeBlock.size == blockToRelease.startAddress) {
					freeBlock.size += blockToRelease.size;
					blockToRelease = freeBlock;
					break;
				}
			}

			for (MemoryBlock freeBlock : freeBlocks) {
				if (freeBlock.startAddress == blockToRelease.startAddress + blockToRelease.size) {
					blockToRelease.size += freeBlock.size;
					freeBlocks.remove(freeBlock);
					break;
				}
			}

			//////// FREE BLOCKS LAYERED! ////////

			freeBlocks.add(blockToRelease);
			allocatedBlocks.remove(blockToRelease);

			displayBlocks();
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

		// Print allocated blocks
		for (MemoryBlock block : allocatedBlocks) {
			System.out.println(block.startAddress + "-" + (block.startAddress + block.size - 1) + "\t\t");
		}

		// Print free blocks
		for (MemoryBlock block : freeBlocks) {
			System.out.println("\t\t\t\t" + block.startAddress + "-" + (block.startAddress + block.size - 1));
		}

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

		public MemoryBlock(int startAddress, int size) {
			this.startAddress = startAddress;
			this.size = size;
		}
	}
}
