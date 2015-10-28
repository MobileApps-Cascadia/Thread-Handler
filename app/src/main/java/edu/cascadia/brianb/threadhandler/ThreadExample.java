package edu.cascadia.brianb.threadhandler;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/* Code example based on http://www.techotopia.com/index.php/A_Basic_Overview_of_Android_Threads_and_Thread_handlers
 */
public class ThreadExample extends Activity {

    int numThreads;
    TextView threadCounterView, myTextView;
    Handler mHandler;

    //TODO: define mHandler as an anonymous class and override handleMessage to use msg data to update the UI
    //TODO: increment and decrement numThreads counter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_example);
        threadCounterView = (TextView) findViewById(R.id.threadCount);
        myTextView = (TextView) findViewById(R.id.myTextView);
        numThreads = 1; // 1 for the main activity thread
        mHandler = new UIHandler(this);
        // anonymous class
        /* mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    Bundle bundle = msg.getData();
                    String date = bundle.getString("myKey");
                    myTextView.setText(date);
                    threadCounterView.setText("Thread Count: " + String.valueOf(numThreads));
                }
            }
        }; */
    }

    public void buttonClick(View view)
    {
        ++numThreads;

        //Create a new thread to do the time consuming operation
        Thread timeLapse = new Thread( new Runnable() {
            @Override
            public void run() {

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
                    --numThreads;
                }
            }
        });
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
                } catch (Exception e) {}
            }
        }
    }

    // static inner handler class to avoid memory leaks
    private static class UIHandler extends Handler {

        private final WeakReference<ThreadExample> mAct;

        private UIHandler(ThreadExample activity) {
            mAct = new WeakReference<ThreadExample>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                ThreadExample activity = mAct.get();
                Bundle bundle = msg.getData();
                String date = bundle.getString("myKey");
                activity.myTextView.setText(date);
                activity.threadCounterView.setText("Thread Count: " + String.valueOf(activity.numThreads));
            }
        }
    }
}
