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
	private ArrayList<Integer> addressAndSize = new ArrayList<>();

	/**
	 * Initializes an instance of a first fit-based memory.
	 *
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		// TODO Implement this!
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
		int startIndex = -1;
		int tempSize = 0;


		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == 0) {
				tempSize++;
				if (size == tempSize) {
					startIndex = i - size + 1;
					break;
				}
			} else {
				tempSize = 0;
			}
		}

		if (startIndex != -1) {
			for (int i = startIndex; i < startIndex + size; i++) {
				cells[i] = 1; // Mark the cells as allocated
			}
			Pointer p = new Pointer(startIndex, this);
			p.pointAt(startIndex);
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
		// TODO Implement this!
		int startIndex = p.pointsAt();
		int size = p.getSize();

		for (int i = startIndex; i < startIndex + size; i++) {
			cells[i] = 0;	// Mark the cells as free
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
	}

	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		// TODO Implement this!
	}
}
