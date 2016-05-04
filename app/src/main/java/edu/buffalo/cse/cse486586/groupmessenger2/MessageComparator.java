package edu.buffalo.cse.cse486586.groupmessenger2;
import java.util.Comparator;

/**
 * Comparator class for sorting messages according to sequence values.
 * Code referenced from http://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
 * @author pravin
 *
 */
public class MessageComparator implements Comparator<Message>{

	@Override
	public int compare(Message o1, Message o2) {
		int returnValue = 0;
		String[] a = String.valueOf(o1.getMessageSeq()).split("\\.");
		String[] b = String.valueOf(o2.getMessageSeq()).split("\\.");
		
		if(Float.parseFloat(a[0]) > Float.parseFloat(b[0])) {
			returnValue = 1;
		}
		else
			if(Float.parseFloat(a[0]) < Float.parseFloat(b[0])) {
				returnValue = -1;
			}
			else
				if(Float.parseFloat(a[0]) == Float.parseFloat(b[0])) {
					if(Float.parseFloat("0" +"."+a[1]) > Float.parseFloat("0" +"."+b[1])) {
						returnValue = 1;
					}
					else
						if(Float.parseFloat("0" +"."+a[1]) < Float.parseFloat("0" +"."+b[1])) {
							returnValue = -1;
						}
				}
		
		return returnValue;
	}

}
