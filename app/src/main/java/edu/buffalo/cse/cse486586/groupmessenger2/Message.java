package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float messageSeq;
	private String message;
	private String fromAvd;
	
	public float getMessageSeq() {
		return messageSeq;
	}
	public void setMessageSeq(float messageSeq) {
		this.messageSeq = messageSeq;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFromAvd() {
		return fromAvd;
	}
	public void setFromAvd(String fromAvd) {
		this.fromAvd = fromAvd;
	}
	/*@Override
	public int compareTo(Message another) {
		int returnValue = 0;
		String[] a = String.valueOf(this.messageSeq).split("\\.");
		String[] b = String.valueOf(another.messageSeq).split("\\.");
		
		if(Float.valueOf(a[0]) > Float.valueOf(b[0])) {
			returnValue = 1;
		}
		else
			if(Float.valueOf(a[0]) < Float.valueOf(b[0])) {
				returnValue = -1;
			}
			else
				if(Float.valueOf(a[0]) == Float.valueOf(b[0])) {
					if(Float.valueOf(a[0]) > Float.valueOf(a[1])) {
						returnValue = 1;
					}
					else
						if(Float.valueOf(a[0]) < Float.valueOf(a[1])) {
							returnValue = -1;
						}
				}
		
		return returnValue;
		
	}*/
	
}
