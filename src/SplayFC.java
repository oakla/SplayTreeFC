import java.util.Iterator;
import java.util.NoSuchElementException;



/**
 * An implementation of a splay tree of strings with fast cloning and subset extraction.
 * @version 1.1
 */
public class SplayFC implements ISplayFC {
	public Cell top;  // Stores the current root cell

	public int modCount;
	/**
	 * Strings that are below (STRING_MIN) and above (STRING_MAX) all the words 
	 * that will be in a SplayFC tree.  Splaying on these is sometimes useful.
	 */
	final static String STRING_MIN = "";
	final static String STRING_MAX = Character.toString(Character.MAX_VALUE);
	
	public SplayFC() { 
		top = null;
		modCount = 0;
		}
	public SplayFC(Cell c) { 
		top = c; 
		modCount = 0;
		}
	
	public Cell getTop() { return top; }
	public void setTop(Cell c) { top = c; }

	/*
	 * See ISplayFC.  You must not modify this method.  
	 */
	public String toString() {
		if(top==null) return "[]\n";
		return top.toString("","   ", "   ",  " -");
	}

	/**
	 * A small abbreviation for the Cell constructor, which is handy when you need to
	 * construct cells in many different places in your code.
	 * @param k The key for the new cell.
	 * @param l The left child of the new cell.
	 * @param r the right child of the new cell.
	 * @return The newly constructed cell.
	 */
	private static Cell cell(String k, Cell l, Cell r) {return new Cell(k, l, r);}

	public Cell splay(Cell c, String k) {
		if(c==null)  throw new NullPointerException("splay requires a non-null Cell");
		
		// To help get you started, the code below shows how to implement
		// the cases for a left "zig-zig" step and a left "zig" step.  You need to
		// add the case for a left "zig-zag" step, and the cases for all three
		// kinds of right step (which mirror the left cases).

		int compareToCKey = k.compareTo(c.key());
		
		if(compareToCKey<0 && c.lt!=null) {                         // Search left
			int compareToLtKey = k.compareTo(c.lt.key());
			
			if(compareToLtKey<0 && c.lt.lt!=null) {             // left zig-zig step  				
				Cell ll = splay(c.lt.lt, k);                     // Search recursively
				Cell newRR = cell(c.key(), c.lt.rt, c.rt);                // Rearrange
				return cell(ll.key(), ll.lt, cell(c.lt.key(), ll.rt, newRR)); 
			
			} else if(compareToLtKey>0 && c.lt.rt != null) {    // left zig-zag step
				
				// Replace this with code similar to the zig-zig step above, but 
				// following the zig-zag case in the specification.
				Cell lr = splay(c.lt.rt, k);
				Cell newR = cell(c.key(), lr.rt, c.rt);
				return cell(lr.key(), cell(c.lt.key(), c.lt.lt, lr.lt), newR);
				//throw new RuntimeException("splay: implementation incomplete");
				
			} else                                                  // left zig step
				return cell(c.lt.key(), c.lt.lt, cell(c.key(),c.lt.rt,c.rt));
		} else if(compareToCKey>0 && c.rt!=null) {                   // Search right
			int compareToRtKey = k.compareTo(c.rt.key());
			
			if(compareToRtKey>0 && c.rt.rt!=null){
			// Replace this with code for the right cases, similar to the left cases
			// above, but with left and right swapped. 
				Cell rr = splay(c.rt.rt, k);
				Cell newll = cell(c.key(), c.lt, c.rt.lt);
				return cell(rr.key(), cell(c.rt.key(), newll, rr.lt), rr.rt);
			}else if(compareToRtKey<0 && c.rt.lt!=null){
				Cell rl = splay(c.rt.lt, k);
				Cell newL = cell(c.key(), c.lt, rl.lt);
				return cell(rl.key(), newL, cell(c.rt.key(), rl.rt, c.rt.rt));
			}else{
				return cell(c.rt.key(), cell(c.key(), c.lt, c.rt.lt), c.rt.rt);
			}
			//throw new RuntimeException("splay: implementation incomplete");
			
		} else 
			return c;  // Special cases 1 and 2 in the specification
	}

	public boolean add(String k) { 
		if(top == null) { 
			top = cell(k, null, null); 
			return true; 
		}
		Cell s = splay(top, k);
		top = s;

		int compareToSKey = k.compareTo(s.key());
		
		if(compareToSKey > 0) {
			top = cell(k, cell(s.key(), s.lt, null), s.rt);
			return true;
		}else if(compareToSKey < 0) {
			top = cell(k, s.lt, cell(s.key(), null, s.rt));
			return true;
		}else return false;

	}  

	
	// Implement the rest of the methods of SplayFC below, along with any private methods you use.
		
	public boolean remove(String k) { 
		
		modCount++;
		
		if(top == null){
			return false;
		}
		Cell s = splay(top, k);
		top = s;
		if(k.compareTo(s.key()) == 0){
			if(s.lt != null){
				Cell lMax = splay(s.lt, STRING_MAX);
			    //check if(lMax.rt == null);
				top = cell(lMax.key(), lMax.lt, s.rt); 
				return true;
			}else if(s.lt == null){
				top = s.rt;
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(String k) { 
		Cell s = splay(top, k);
		top = s;
		if(k.compareTo(s.key()) == 0){
			return true;
		}else return false;
		//throw new RuntimeException("contains: implementation incomplete");  
	}

	public SplayFC headSet(String k) { 
		if(top != null){
			Cell s = splay(top, k);
			top = s;
			
			int compareToS = k.compareTo(s.key());
			
			if(compareToS < 0){
				return new SplayFC(cell(s.key(), s.lt, null));
			} else if(compareToS == 0) {
				return new SplayFC(cell(s.lt.key(), s.lt.lt, s.lt.rt));
			}
		} 
		return null;
	}
	
	public SplayFC tailSet(String k) { throw new RuntimeException("tailSet: implementation incomplete"); }
	public SplayFC subSet(String k1, String k2) { throw new RuntimeException("subSet: implementation incomplete"); }
	public SplayFC clone() { 
		if(top == null){
			return null;
		}
		return new SplayFC(top);
	}
	public Iterator<String> snapShotIterator() { 
		return new SnapShotIterator();
	}
	public Iterator<String> updatingIterator() {  
		return new UpdatingIterator();
	}

	// Implement the two iterator methods above by implementing the two iterator classes below. 
	
	public class SnapShotIterator implements Iterator<String> {  	
		
		private Cell c;
		
		public SnapShotIterator(){
			if(top != null){
				c = splay(top, STRING_MIN);
				//c = top;
			} else {
				 c = null;
			}
		}
	  	// You need to implement the following two methods here.
	  	public boolean hasNext() { 
	  		return(c != null);
	  		//throw new RuntimeException("hasNext: implementation incomplete"); 
	  	}
		public String next() throws NoSuchElementException{
			if(hasNext()){
				String nextS = c.key();
//alternative for this 'if' statement?
				if(c.rt != null){
					c = splay(c.rt, STRING_MIN);
				} else c = null;
				return nextS;
			}else throw new NoSuchElementException("SnapShotIterator next() method called when no next element exists.");
			//throw new RuntimeException("next: implementation incomplete"); 
		}
		public String toString(){
			SplayFC itTree = new SplayFC(c);
			return itTree.toString();
		}
			
		// You should leave the remove method as unsupported.
		public void remove(){ throw new UnsupportedOperationException("remove"); }
	}

	public class UpdatingIterator implements Iterator<String> {  	

		private Cell c;
		private String maxPrev; 
		private int expectedModCount;
		
		public UpdatingIterator(){
			maxPrev = null;
			expectedModCount = modCount;
		}
	  	// You need to implement the following two methods here.
	  	public boolean hasNext() {
	  		expectedModCount = modCount;
	  		if(top != null){
	  			if(maxPrev == null){
	  				return true;
	  			}

	  			c = splay(top, maxPrev);
	  			
	  			if(c.key().compareTo(maxPrev) > 0){
	  				return true;
	  			}
	  			if(c.rt != null && c.rt.key().compareTo(maxPrev) > 0){
	  				return true;
	  			}
	  			System.out.println("hasNext returning false. maxPrev ==" +maxPrev);
	  		}
	  		return false;
	  	}
		public String next() {
			if(expectedModCount != modCount){
				throw new java.util.ConcurrentModificationException();
			}
			if(hasNext()){
				if(maxPrev == null){
	  				c = splay(top, STRING_MIN);
	  				maxPrev = c.key();
					return maxPrev;
	  			}
	  			
	  			c = splay(top, maxPrev);
	  			
	  			if(c.key().compareTo(maxPrev) > 0){
	  				maxPrev = c.key();
	  				return maxPrev;
	  			}
	  			if(c.rt != null && c.rt.key().compareTo(maxPrev) > 0){
	  				c = splay(c.rt, STRING_MIN);
	  				maxPrev = c.key();
	  				return maxPrev;
	  			}
	  			if(c.rt == null) throw new RuntimeException("Sanity Check required");
	  			return null;
			}
			throw new NoSuchElementException("UpdatingIterator next() method called when no next element exists.");
		}		
		public void remove(){ 
			c = splay(top, maxPrev);	
			if(c.key().matches(maxPrev)){
				SplayFC.this.remove(maxPrev);	
			}
		}
		public String toString(){
			SplayFC itTree = new SplayFC(c);
			return itTree.toString();
		}
	}
		
	
		
}