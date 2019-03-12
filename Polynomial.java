package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		//node to be returned
		Node ret = new Node(0, 0, null); 
		//pointer for ret node
		Node retPointer = ret;
		Node c1 = poly1;
		Node c2 = poly2;
		
		while(c1 != null && c2 != null) {
			//case 1: degrees match
			if(c1.term.degree == c2.term.degree) {
				//create new node with relevant info
				Node temp = new Node(c1.term.coeff + c2.term.coeff, c1.term.degree, null);
				//prevent nodes with 0 coef to be added
				if(temp.term.coeff == 0) {
					c1 = c1.next;
					c2 = c2.next;
					continue;
				}
				//add temp's values to ret, move pointers
				retPointer.next = temp;
				c1 = c1.next;
				c2 = c2.next;
				retPointer = retPointer.next;
			}
			//case 2: c1Deg < c2Deg
			else if(c1.term.degree < c2.term.degree) {
				//create new temp node with relevant info
				Node temp = new Node(c1.term.coeff, c1.term.degree, null);
				retPointer.next = temp;
				c1 = c1.next;
				retPointer = retPointer.next;
			}
			//case 3: c2Deg < c1Deg
			else if(c2.term.degree < c1.term.degree) {
				Node temp = new Node(c2.term.coeff, c2.term.degree, null);
				retPointer.next = temp;
				c2 = c2.next;
				retPointer = retPointer.next;
			}
		}
		//if poly1/2 ends before poly1/2
		if(c1 != null) {
			//iterate through each term and add to ret
			while(c1 != null) {
				Node temp = new Node(c1.term.coeff, c1.term.degree, null);
				retPointer.next = temp;
				retPointer = retPointer.next;
				c1 = c1.next;
			}
		}
		if(c2 != null) {
			//iterate through each term and add to ret
			while(c2 != null) {
				Node temp = new Node(c2.term.coeff, c2.term.degree, null);
				retPointer.next = temp;
				retPointer = retPointer.next;
				c2 = c2.next;
			}
		}
		//prevent "correct answer + 0.0" issue
		//issue: ret created with (0,0,null), so + 0 is added to correct answer
		//move ret to ret.next which is beginning of correct LL
		if(ret.term.coeff == 0) {
			ret = ret.next;
		}
		return ret;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node ret = new Node(0, 0, null);	//poly1 * poly2 but not simplified
		Node fin = new Node(0, 0, null);	//final linked list, polynomial simplified
		//pointers
		Node finPointer = fin;
		Node retPointer = ret;
		Node c1 = poly1;
		Node c2 = poly2;
		
		//if one LL is a zero polynomial, multiplication will be 0
		if(c1 == null || c2 == null) {
			Node zeroResult = new Node(0, 0, null);
			return zeroResult;
		}
		//CALCULATE POLY1 * POLY2
		//access first/second/etc term of c1
		while(c1 != null) {
			//access each term of c2
			while(c2 != null) {
				//create new node with multiplied/added values
				Node temp = new Node(c1.term.coeff * c2.term.coeff, c1.term.degree + c2.term.degree, null);
				retPointer.next = temp;	//retPointer = temp;
				retPointer = retPointer.next;
				c2 = c2.next;
			}
			c2 = poly2;
			c1 = c1.next;
		}
		
		//make sure ret does not contain initial 0 before simplifying to new list
		if(ret.term.coeff == 0) {
			ret = ret.next;
		}
		//return ret;		//end method here to get unsimplified multiplication
		
		//SIMPLIFY POLYNOMIAL LINKED LIST
		//organize result: combine like terms, etc
		Node pointer1 = ret;
		Node pointer2 = pointer1.next;
		
		while(pointer1 != null) {
			//private method to check if pointer1.degree is anywhere in fin
				//if match (private method returns true), pointer1 = pointer1.next, p2 = p1.next, continue
			boolean degreeMatches = degreeMatches(fin, pointer1);
			if(degreeMatches == true) {
				pointer1 = pointer1.next;
				//prevent nullPointerException when pointer1 is on last term and pointer2 is set to null
				//will only execute when pointer1 is on the last node, in which case copy node to fin
				//no need to do degree check bc last node has highest degree in polynomial multiplication 
				if(pointer1.next == null) {
					finPointer.next = pointer1;
					finPointer = finPointer.next;
					break;
				}
				pointer2 = pointer1.next;
				continue;
			}
			//create temp node with relevant info to add to fin
			Node temp = new Node(pointer1.term.coeff, pointer1.term.degree, null);
			//iterate pointer2 through each node and if degree matches, adjust temp coef
			while(pointer2 != null) {
				if(pointer2.term.degree == pointer1.term.degree) {
					temp.term.coeff += pointer2.term.coeff;
					//temp.term.coeff = temp.term.coeff + pointer2.term.coeff; 	also correct
				}
				pointer2 = pointer2.next;
			}
			if(temp.term.coeff != 0) {
				finPointer.next = temp;
				finPointer = finPointer.next;
			}
			pointer1 = pointer1.next;
			//prevent nullPointerException when pointer1 is on last term and pointer2 is set to null
			//will only execute when pointer1 is on the last node, in which case copy node to fin
			//no need to do degree check bc polynomial will never have duplicate degree at beginning
			if(pointer1.next == null) {
				finPointer.next = pointer1;
				finPointer = finPointer.next;
				break;
			}
			pointer2 = pointer1.next;
		}
		
		//to revert to last correct state set fin to ret:
		//make sure fin does not contain extra 0.0 term
		if(fin.term.coeff == 0) {
			fin = fin.next;
		}
		
		//call private method to organize fin
		//timeout occurrence
		fin = mergeSort(fin);
		
		return fin;
		
	}
	/**
	 * use merge sort to organize final linked list
	 * 
	 * @param fin
	 * @return
	 */
	private static Node mergeSort(Node fin) {
		Node middle = getMiddle(fin);
		if(middle.next == null) return middle;
		Node newLow = middle.next;		//this will be used in next iteration when right side need to be sorted
		
		middle.next = null;
		
		//left side
		Node l = mergeSort(fin);
		//right side
		Node r = mergeSort(newLow);
		//sort the left and right nodes
		Node ret = sort(l, r);
		
		return ret;
	}
	
	/**
	 * to organize each node from broken up linked list
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	private static Node sort(Node left, Node right) {
		//will be used when method is called recursively after establishing one node
		if(left == null) return right;
		if(right == null) return left;
		
		Node ret = null;
		
		if(left.term.degree < right.term.degree) {
			ret = left;
			ret.next = sort(left.next, right);
		}
		else if(right.term.degree < left.term.degree){
			ret = right;
			ret.next = sort(left, right.next);
		}
	
		return ret;
	}
	
	/**
	 * to be used by merge sort method 
	 * method returns node at the middle of the linked list
	 * 
	 * @param x
	 * @return
	 */
	private static Node getMiddle(Node x) {
		//use two pointers to traverse linked list at the same time, one twice of other
		//pointer1 will reach end while pointer2 will be halfway
		Node pointer1 = x.next;
		Node pointer2 = x;
		
		while(pointer1 != null) {
			pointer1 = pointer1.next;
			if(pointer1 != null) {
				pointer1 = pointer1.next;
				pointer2 = pointer2.next;
			}
		}
		
		return pointer2;
	}
	
	/**
	 * method compares degree of pointer1 (node) to every degree in fin (list)
	 * if degree match, then return true
	 * 
	 * @param list	fin
	 * @param node	pointer1
	 * @return boolean to determine whether degree has already been handled
	 */
	private static boolean degreeMatches(Node list, Node node) {
		int listCount = 0;
		while(list != null) {
			if(node.term.degree == list.term.degree && listCount != 0)	//prevent initial 0 from stopping any x^0 term
				return true;
			list = list.next;
			listCount++;
		}
		return false;
	}
	
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		//create float to return
		float ret = 0;
		//iterate through linked list
		Node current = poly;
		while(current != null) {
			//evaluate Term and add result to ret
			ret += (Math.pow(x, current.term.degree) * current.term.coeff);
			current = current.next;
		}
		return ret;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
