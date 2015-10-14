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

    private static class MyHandler extends Handler {
        private final WeakReference<ThreadExample> mActivity;

        public MyHandler(ThreadExample ta) {
            mActivity = new WeakReference<ThreadExample>(ta);
        }

        @Override
        public void handleMessage(Message msg) {
            ThreadExample ta = mActivity.get();
            if (ta != null) {
                ((TextView) ta.findViewById(R.id.myTextView))
                        .setText(msg.getData().getString("myKey"));
                ta.addThreadCount(-1);
            }
        }
    }

    MyHandler mHandler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_example);
        threadCounterView = (TextView) findViewById(R.id.threadCount);
        myTextView = (TextView) findViewById(R.id.myTextView);
    }

    public void buttonClick(View view) {
        //Create a new thread to do the time consuming operation
        Thread timeLapse = new Thread( new Runnable() {
            @Override
            public void run() {

                //This is where the time goes while the thread is running
                takeSomeTime(1);

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
        timeLapse.start();
        addThreadCount(1);

        myTextView.setText("This might take a moment...");
    }

    void addThreadCount(int add) {
        numThreads += add;
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
}
