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
import java.util.TimeZone;

/* Code example based on http://www.techotopia.com/index.php/A_Basic_Overview_of_Android_Threads_and_Thread_handlers
 */
public class ThreadExample extends Activity {
    private static final int START_THREAD = 100;
    private static final int END_THREAD = 200;
    int numThreads;
    TextView threadCounterView, myTextView;

    //Define mHandler as an anonymous, custom inner class that extends Handler
    Handler mHandler = new Handler(){
        //TODO @Override the handleMessage function to update the UI

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView mtv = (TextView) findViewById(R.id.myTextView);
            TextView tc = (TextView) findViewById(R.id.threadCount);

            //TODO test if message.what equals START_THREAD
                // increment numThreads counter
            if(msg.what == START_THREAD){
                numThreads++;
            }

            //TODO else if the message.what equals END_THREAD,
            //decrement numThreads counter
            //use setText to update the myTextView to the string passed in the message
            else if(msg.what == END_THREAD){
                numThreads--;
                mtv.setText(msg.getData().getString("myKey"));
            }

            //TODO use setText to update the threadCounterView display
            tc.setText("Thread count:" + numThreads);
        }
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    };

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
            //Note that this block is executing on a separate thread
                //TODO: remove this setText method
                //    it breaks the rule for thread handling, "Only update UIViews from UI thread"
                //myTextView.setText("Starting Thread");

                //This is where the time goes while the thread is running
                takeSomeTime(5);

                //Sends a message with a Data Bundle to the UI Thread via Handler
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    SimpleDateFormat dateformat =
                            new SimpleDateFormat("HH:mm:ss", Locale.US);
                    String dateString = dateformat.format(new Date());
                    bundle.putString("myKey", "It's now: " + dateString);
                    msg.setData(bundle);
                    msg.what = END_THREAD;
                    mHandler.sendMessage(msg);
                }
            }
        });
        mHandler.sendEmptyMessage(START_THREAD);
        timeLapse.start();

        //Notice that this is executed only on the UI Thread
        //myTextView.setText("This might take a moment...");
        //threadCounterView.setText("Thread Count: " + String.valueOf(numThreads));
    }

    // Helper method to mimic a time delay such as network activity
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
