package com.jingpu.android.apersistance;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jingpu.android.apersistance.util.TPCCLog;

public class RunActivity extends AppCompatActivity implements View.OnClickListener{

    TextView textViewLog;
    Button btnRunClose;
    InfoHandler infoHandler = new InfoHandler();
    InfoThread infoThread = new InfoThread();
    Thread mThread = new Thread(infoThread);
    boolean enableClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        textViewLog = (TextView)findViewById(R.id.textViewLog);

        textViewLog.setText("");

        textViewLog.append("=== Benchmark[" + AppContext.getInstance().getBenchmark() + "] [run time: "
                + BaseBenchmark.dateFormat.format(AppContext.getInstance().getStartRunDate())
                + "; Current time millis:" + AppContext.getInstance().getStartRunDate().getTime() + "] ==="); // editTextInfo
        textViewLog.append(" Parameters [scale=" + AppContext.getInstance().getScale()  //  editTextInfo
                + ",terminalNumber=" + AppContext.getInstance().getTerminals()
                + ",phaseInterval=" + AppContext.getInstance().getPhaseInterval()
                + ",totalTransactions=" + AppContext.getInstance().getTotalTrans()
                + ",aOrmTrans=" + AppContext.getInstance().getAORMTrans()+ "]");

        TPCCLog.i(RunActivity.class.getName(), "Input params: [Benchmark] " + AppContext.getInstance().getBenchmark()
                + ", [Scale] " + AppContext.getInstance().getScale()
                + ", [TerminalNum] " + AppContext.getInstance().getTerminals()
                + ", [PhaseInterval]" + AppContext.getInstance().getPhaseInterval()
                + ", [TotalTrans] " + AppContext.getInstance().getTotalTrans()
                + ", [AORM Transactions]" + AppContext.getInstance().getAORMTrans()
                + ", [Click Time] " + "Date: "
                + BaseBenchmark.dateFormat.format(AppContext.getInstance().getStartRunDate())
                + "; Current time millis:" + AppContext.getInstance().getStartRunDate().getTime());

        btnRunClose = (Button)findViewById(R.id.btnRunClose);
        btnRunClose.setOnClickListener(this);

        mThread.start();

        // Do not block current UI thread
        DbAsyncTask task = new DbAsyncTask();
        task.execute();
    }

    class DbAsyncTask extends AsyncTask {

        protected Integer doInBackground(Object... params) {

            // run benchmark
            return new BaseBenchmark().setUp();
        }

        protected void onPostExecute(Integer result) {
            RunActivity.this.textViewLog.append("=== Benchmark finish ===");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
       if (view == findViewById(R.id.btnRunClose)) {
           finish();
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        infoThread.stop();
        mThread.interrupt();
        infoHandler.removeCallbacksAndMessages(null);
    }

    class InfoHandler extends Handler {

        public InfoHandler() {

        }

        public InfoHandler(Looper L) {
            super(L);
        }

        @Override

        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            Bundle b = msg.getData();

            String info = b.getString("info");

            if (null != info  && info.length() > 0) {
                RunActivity.this.textViewLog.append(info); //editTextInfo

                if (info.endsWith(BaseBenchmark.LOG_END_TAG)) {
                    Toast.makeText(RunActivity.this, "Finish!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class InfoThread implements Runnable {

        private volatile boolean mIsStopped = false;

        public void run() {

            while (!mIsStopped) {
                try {
                    Message msg = new Message();

                    Bundle b = new Bundle();

                    String info = AppContext.getInstance().getUIInfo();

                    if (null != info && info.length() > 0) {
                        TPCCLog.i(RunActivity.class.getName(), "send UI message: " + info);

                        b.putString("info", info);
                        msg.setData(b);
                        RunActivity.this.infoHandler.sendMessage(msg);
                    }

                    Thread.sleep(20);

                } catch (InterruptedException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                }
            }
        }

        public boolean isStopped() {
            return mIsStopped;
        }

        private void setStopped(boolean isStop) {
            if (mIsStopped != isStop)
                mIsStopped = isStop;
        }

        public void stop() {
            setStopped(true);
        }
    }
}
