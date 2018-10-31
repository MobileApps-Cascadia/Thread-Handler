package edu.cascadia.brianb.threadhandler;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/* Code example based on http://www.techotopia.com/index.php/A_Basic_Overview_of_Android_Threads_and_Thread_handlers
 */
public class ThreadExample extends Activity {
    private static final int START_THREAD = 100;
    private static final int END_THREAD = 200;
    int numThreads;
    TextView threadCounterView, myTextView;
    //TODO define mHandler as an anonymous inner class

   Handler mHandler = new Handler(){
       @Override
       public void handleMessage(Message msg){
           //TODO test if message.what equals START_THREAD
           // increment numThreads counter

           //TODO else if the message.what equals END_THREAD,
           //decrement numThreads counter

           //use setText to update the myTextView to the string passed in the message
//TODO use setText to update the threadCounterView display

           TextView myTextView = (TextView) findViewById(R.id.myTextView);
           myTextView.setText("Starting Thread");
           Bundle b = msg.getData();
           String s = b.getString("myKey");
           myTextView.setText(s);
           if(msg.what == START_THREAD){
               numThreads++;
           }
           else if(msg.what == END_THREAD ){
               numThreads--;
           }
       }
    };

        //TODO override handleMessage to update the UI
        //TODO use the Message pased from the thread to update the UI
        //TODO increment and decrement numThreads counter display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_example);
        threadCounterView = (TextView) findViewById(R.id.threadCount);
        myTextView = (TextView) findViewById(R.id.myTextView);

    }

    public void buttonClick(View view)
    {
        //Create a new thread to do the time consuming operation
        Thread timeLapse = new Thread( new Runnable() {
            @Override
            public void run() {
                //    it breaks the rule for thread handling, "Only update UIViews from UI thread"
              //  myTextView.setText("Starting Thread"); //violates android's second rule for thread handling

                //This is where the time goes while the thread is running
                takeSomeTime(5);

                //Send a message to the UI Thread through a Handler
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    SimpleDateFormat dateformat =
                            new SimpleDateFormat("HH:mm:ss", Locale.US);
                    String dateString = dateformat.format(new Date());
                    bundle.putString("myKey", "It's now: " + dateString);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        });
        mHandler.sendEmptyMessage(START_THREAD);
        timeLapse.start();

        myTextView.setText("This might take a moment...");
        threadCounterView.setText("Thread Count: " + String.valueOf(numThreads));
    }

    // Mimic time delay in a network activity
    public void takeSomeTime(int seconds){
        long endTime = System.currentTimeMillis() + seconds*1000;

        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) { e.printStackTrace();}
            }
        }
    }
}
