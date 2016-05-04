package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    private static final String TAG = GroupMessengerActivity.class.getSimpleName();
    private static final int SERVER_PORT = 10000;
    private static final String PROPOSAL = "P";
    private static final String FINAL_MESSAGE = "M";
    private static final String PROPOSAL_REPLY = "PR";
    private static int proposal_counter = 0;
    private static String myPort = null;
    private static ConcurrentHashMap<String, List<Float>> proposalBuffer =  new ConcurrentHashMap<String, List<Float>>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        //Code referenced from PA1
        /*
         * Calculate the port number that this AVD listens on.
         * It is just a hack that I came up with to get around the networking limitations of AVDs.
         * The explanation is provided in the PA1 spec.
         */
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            //new CheckerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            //new CheckerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message =  ((TextView) findViewById(R.id.editText1)).getText().toString();
                ((TextView) findViewById(R.id.editText1)).setText("");
                //Log.d(TAG, "Entering message:" + message);
                //Log.d(TAG, "Requesting SEQ No for:" + message);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, PROPOSAL+ ";" + 0 + ";" + message + ";"+ myPort, "11108","11112","11116","11120","11124");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    //Code referenced from PA1
    /***
     * ServerTask is an AsyncTask that should handle incoming messages. It is created by
     * ServerTask.executeOnExecutor() call in SimpleMessengerActivity.
     *
     * Please make sure you understand how AsyncTask works by reading
     * http://developer.android.com/reference/android/os/AsyncTask.html
     *
     * @author stevko
     *
     */
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            while(true) {

                try {

                    Socket socket = serverSocket.accept();
                    /*Code referenced from http://www.tutorialspoint.com/java/java_networking.htm  and http://stackoverflow.com/questions/2500107/how-should-i-read-from-a-buffered-reader*/
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while((line=br.readLine()) != null) {
                        sb.append(line);
                    }
                    
                    //Message has been received here. Parsing of message starts(Whether proposal or actual message)
                    String message = sb.toString();
                    String[] sArray = message.split(";");
                    
                    if(sArray[0].equals(PROPOSAL)) {
                    	String messageToBeSent = PROPOSAL_REPLY + ";" + ++proposal_counter + "." + myPort + ";" + sArray[2];
                    	//Log.d(TAG, "Sending Proposal for message: " + sArray[2] + " to " + sArray[3]);
                    	new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, messageToBeSent, sArray[3]);                    	
                    }
                    else
                    	if(sArray[0].equals(FINAL_MESSAGE)) {
                    		
                    		/*Log.d(TAG, "Final Message received.");*/
                    		ContentValues cv = new ContentValues();
    	        			cv.put("key",sArray[1]);
    	                    cv.put("value", sArray[2]);
    	                    
    	                    //Code referenced from http://stackoverflow.com/questions/2709087/turning-a-string-into-a-uri-android
    	                    Uri myUri = Uri.parse("content://edu.buffalo.cse.cse486586.groupmessenger2.provider");
    	                    getContentResolver().insert(myUri, cv);
    	                    publishProgress(sArray[1],sArray[2]);
                    	}
                    	else
                    		if(sArray[0].equals(PROPOSAL_REPLY)) {
                    			
                    			//TODO:How to process proposal? Delay it or something else?
                    			//Log.d(TAG, "Received Proposal Reply for message: " + sArray[2] +" with SEQ NO:" +sArray[1]);
                    			List<Float> currentValues = proposalBuffer.get(sArray[2]);
                    			
                    			if(currentValues == null) {
                    				currentValues = new ArrayList<Float>();
                    				currentValues.add(Float.valueOf(sArray[1]));
                    				proposalBuffer.put(sArray[2], currentValues);
                    			}
                    			else {
                    				currentValues.add(Float.valueOf(sArray[1]));
                    			}                 			
                    			
                    			Set<String> keySet = proposalBuffer.keySet();
            					Iterator<String> it = keySet.iterator();
            					
            					while(it.hasNext()) {
            						String key = it.next();
            						List<Float> valueList = proposalBuffer.get(key);
            						if(valueList.size() == 5) {
            							Float max = Float.valueOf(0);
            							for(Float f: valueList) {
            								if (max < f) {
            									max = f;
            								}
            							}							
            							
            		                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, FINAL_MESSAGE + ";" + String.valueOf(max)+ ";" + key + ";" + myPort, "11108","11112","11116","11120","11124"); 
            							publishProgress(max.toString(), key);
            							//Remove object from buffer
            							proposalBuffer.remove(key);
            						}
            					}
                    		}           
                }
                catch(IOException e) {
                    Log.e(TAG, "IOException occurred while receiving message");
                }

            }
        }
        //Code referenced from PA1
        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            //String strReceived = strings[0].trim();
            //Log.d(TAG, strReceived);
            ((TextView) findViewById(R.id.textView1)).append(strings[0] + " : " + strings[1] + "\n");

            return;
        }
    }

    //Code referenced from PA1
    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {
    	
        @Override
        protected Void doInBackground(String... msgs) {
        	
        	List<Integer> portList = new ArrayList<Integer>();
        	
        	String message = msgs[0];
        	
        	for(int i = 1; i< msgs.length;i++) {
        		portList.add(Integer.parseInt(msgs[i]));
        	}
        	
        	try {
        		
                for(Integer i : portList) {
                /*Code referenced from http://www.tutorialspoint.com/java/java_networking.htm*/
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            i);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(message);
                    printWriter.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }
            return null;
        }
    }	
}


