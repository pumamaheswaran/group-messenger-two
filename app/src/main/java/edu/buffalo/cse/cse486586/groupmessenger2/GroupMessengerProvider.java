package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    private static HashMap<String,String> map = null;
    private static ArrayList<Message> messagePriorityQueue = null;
    private static final String TAG = GroupMessengerProvider.class.getSimpleName();
    private boolean flag = true;
        
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }   

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        
         /* TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */

        String key = values.get("key").toString();
        String value = values.get("value").toString();
        Message m = new Message();
        m.setMessage(value);
        m.setMessageSeq(Float.valueOf(key));
        
        messagePriorityQueue.add(m);
        //Log.d(TAG, "Message with seq:"+ m.getMessageSeq() +" and message:" + m.getMessage() + " added");
        //Log.d(TAG, "Size of priority Queue:" + messagePriorityQueue.size());
        //Code referenced from PA1 and http://www.tutorialspoint.com/java/java_serialization.htm
        FileOutputStream outputStream;
        String fileName = "GroupMessenger.ser";
        ObjectOutputStream objectOutputStream;
        
        try {
			outputStream = getContext().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(messagePriorityQueue);
			objectOutputStream.close();
			outputStream.close();
			//Log.d(TAG, "Wrote Message:" + value);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Error in writing to disk");
		} catch (IOException e) {
			Log.e(TAG, "Error in writing to disk");
		}        		
        		
        //Log.v("insert", values.toString());
        return uri;
    }   
    
    @Override
    public boolean onCreate() {
        // If you need to perform any one-

        File file = new File("GroupMessenger.ser");
        if(!file.exists()) {
            messagePriorityQueue = new ArrayList<Message>();
        }
        else {
            //De-Serialization Code
            try {
                //Code referenced from http://www.tutorialspoint.com/java/java_serialization.htm
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileInputStream);
                messagePriorityQueue = (ArrayList<Message>) in.readObject();
                in.close();
                fileInputStream.close();
            }
            catch(FileNotFoundException e) {
                Log.e(TAG, "FileNotFound exception in onCreate()");
            }
            catch(ClassNotFoundException e) {
                Log.e(TAG, "ClassNotFoundException in onCreate()");
            }
            catch(IOException e) {
                Log.e(TAG, "IOException in onCreate()");
            }
        }
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */

    	if(flag) {
    		flag = false;
    		Collections.sort(messagePriorityQueue, new MessageComparator());
    	}
        String[] colNames = {"key", "value"};
        MatrixCursor cursor = new MatrixCursor(colNames);
        MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
        rowBuilder.add("key", selection);
        
        
        //Log.d(TAG, "Size of messageList:" + messagePriorityQueue.size());
        //Log.d(TAG, "Queried for:" + selection + " and found " + messagePriorityQueue.get(Integer.parseInt(selection)).getMessage());
        rowBuilder.add("value", messagePriorityQueue.get(Integer.parseInt(selection)).getMessage());
        //Log.v(TAG,"checking for:" + selection);
        //Log.v("query", selection);        
        
        return cursor;
    }
    
    /*private void printValues() {
    	Iterator<Message> it = messagePriorityQueue.iterator();
    	while(it.hasNext()) {
    		Message m = it.next();
    		Log.d(TAG,"Found Key:" + m.getMessageSeq() + " with value:" + m.getMessage());
    	}
    }*/
}
