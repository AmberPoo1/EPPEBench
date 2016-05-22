package com.jingpu.android.apersistance;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;

/**
 * Created by Jing Pu on 2015/9/17.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRun, btnClose;
    EditText editTextTTrans, editTextScale, editTextTerminals, editTextPhaseInterval, editTextAORMTrans; // editTextBm,
    Spinner spinnerBm;
    CheckBox checkBoxPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRun = (Button)findViewById(R.id.btnRun);
        btnClose = (Button)findViewById(R.id.btnClose);

        spinnerBm = (Spinner)findViewById(R.id.spinnerBm);
        spinnerBm.setVisibility(View.VISIBLE);

        editTextTTrans = (EditText)findViewById(R.id.editTextTTrans);
        editTextScale = (EditText)findViewById(R.id.editTextScale);
        editTextTerminals = (EditText)findViewById(R.id.editTextTerminals);
        editTextPhaseInterval = (EditText)findViewById(R.id.editTextPhaseInterval);
        editTextAORMTrans = (EditText)findViewById(R.id.editTextAORMTrans);

        checkBoxPause = (CheckBox)findViewById(R.id.checkBoxPause);

        btnRun.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (view == findViewById(R.id.btnRun)) {
            // set run time
            AppContext.getInstance().setStartRunDate(new Date());

            /*
            // get sqlite version
            Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
            String sqliteVersion = "";
            while(cursor.moveToNext()){
                sqliteVersion += cursor.getString(0);
            }

            Toast.makeText(this, sqliteVersion, Toast.LENGTH_LONG).show();
            */

            int totalTrans = -1;
            short scale = -1;
            int terminals = -1;

            String benchmark = null;
            String bmText = spinnerBm.getSelectedItem().toString();
            switch (bmText) {
                case "D ActiveAndroid":
                    benchmark = BaseBenchmark.BM_DACAPO_ACTIVEANDROID;
                    break;
                case "D greenDAO":
                    benchmark = BaseBenchmark.BM_DACAPO_GREENDAO;
                    break;
                case "D OrmLite":
                    benchmark = BaseBenchmark.BM_DACAPO_ORMLITE;
                    break;
                case "D Sugar ORM":
                    benchmark = BaseBenchmark.BM_DACAPO_SUGARORM;
                    break;
                case "D SQLite":
                    benchmark = BaseBenchmark.BM_DACAPO_SQLITE;
                    break;
                case "D Realm":
                    benchmark = BaseBenchmark.BM_DACAPO_REALM;
                    break;
                case "A ActiveAndroid Initialization":
                    benchmark = BaseBenchmark.BM_AORM_ACTIVEANDROID_INI;
                    break;
                case "A ActiveAndroid Insert":
                    benchmark = BaseBenchmark.BM_AORM_ACTIVEANDROID_I;
                    break;
                case "A ActiveAndroid Update":
                    benchmark = BaseBenchmark.BM_AORM_ACTIVEANDROID_U;
                    break;
                case "A ActiveAndroid Select":
                    benchmark = BaseBenchmark.BM_AORM_ACTIVEANDROID_S;
                    break;
                case "A ActiveAndroid Delete":
                    benchmark = BaseBenchmark.BM_AORM_ACTIVEANDROID_D;
                    break;
                case "A greenDAO Initialization":
                    benchmark = BaseBenchmark.BM_AORM_GREENDAO_INI;
                    break;
                case "A greenDAO Insert":
                    benchmark = BaseBenchmark.BM_AORM_GREENDAO_I;
                    break;
                case "A greenDAO Update":
                    benchmark = BaseBenchmark.BM_AORM_GREENDAO_U;
                    break;
                case "A greenDAO Select":
                    benchmark = BaseBenchmark.BM_AORM_GREENDAO_S;
                    break;
                case "A greenDAO Delete":
                    benchmark = BaseBenchmark.BM_AORM_GREENDAO_D;
                    break;
                case "A OrmLite Initialization":
                    benchmark = BaseBenchmark.BM_AORM_ORMLITE_INI;
                    break;
                case "A OrmLite Insert":
                    benchmark = BaseBenchmark.BM_AORM_ORMLITE_I;
                    break;
                case "A OrmLite Update":
                    benchmark = BaseBenchmark.BM_AORM_ORMLITE_U;
                    break;
                case "A OrmLite Select":
                    benchmark = BaseBenchmark.BM_AORM_ORMLITE_S;
                    break;
                case "A OrmLite Delete":
                    benchmark = BaseBenchmark.BM_AORM_ORMLITE_D;
                    break;
                case "A Sugar ORM Initialization":
                    benchmark = BaseBenchmark.BM_AORM_SUGARORM_INI;
                    break;
                case "A Sugar ORM Insert":
                    benchmark = BaseBenchmark.BM_AORM_SUGARORM_I;
                    break;
                case "A Sugar ORM Update":
                    benchmark = BaseBenchmark.BM_AORM_SUGARORM_U;
                    break;
                case "A Sugar ORM Select":
                    benchmark = BaseBenchmark.BM_AORM_SUGARORM_S;
                    break;
                case "A Sugar ORM Delete":
                    benchmark = BaseBenchmark.BM_AORM_SUGARORM_D;
                    break;
                case "A SQLite Initialization":
                    benchmark = BaseBenchmark.BM_AORM_SQLITE_INI;
                    break;
                case "A SQLite Insert":
                    benchmark = BaseBenchmark.BM_AORM_SQLITE_I;
                    break;
                case "A SQLite Update":
                    benchmark = BaseBenchmark.BM_AORM_SQLITE_U;
                    break;
                case "A SQLite Select":
                    benchmark = BaseBenchmark.BM_AORM_SQLITE_S;
                    break;
                case "A SQLite Delete":
                    benchmark = BaseBenchmark.BM_AORM_SQLITE_D;
                    break;
                case "A Realm Initialization":
                    benchmark = BaseBenchmark.BM_AORM_REALM_INI;
                    break;
                case "A Realm Insert":
                    benchmark = BaseBenchmark.BM_AORM_REALM_I;
                    break;
                case "A Realm Update":
                    benchmark = BaseBenchmark.BM_AORM_REALM_U;
                    break;
                case "A Realm Select":
                    benchmark = BaseBenchmark.BM_AORM_REALM_S;
                    break;
                case "A Realm Delete":
                    benchmark = BaseBenchmark.BM_AORM_REALM_D;
                    break;
                default:
                    benchmark = "unknow";
            }

            if (!benchmark.endsWith("Ini")) {
                if (null == editTextScale.getText().toString() || "".equals(editTextScale.getText().toString().trim())) {
                    new AlertDialog.Builder(this).setTitle("Information")
                            .setMessage("Please enter scale.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                scale = Short.parseShort(editTextScale.getText().toString());
            }

            if (benchmark.startsWith("DaCapo")) {
                if (null == editTextTTrans.getText().toString() || "".equals(editTextTTrans.getText().toString().trim())) {
                    new  AlertDialog.Builder(this).setTitle("Information" )
                            .setMessage("Please enter total transactions." )
                            .setPositiveButton("OK",  null )
                            .show();
                    return;
                }

                totalTrans = Integer.parseInt(editTextTTrans.getText().toString());

                if (null == editTextTerminals.getText().toString() || "".equals(editTextTerminals.getText().toString().trim())) {
                    new  AlertDialog.Builder(this).setTitle("Information" )
                            .setMessage("Please enter number of terminals.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                terminals = Integer.parseInt(editTextTerminals.getText().toString());
            }

            int aORMTrans = -1;
            if (benchmark.startsWith("AORM") && !benchmark.endsWith("Ini")) {
                if (null == editTextAORMTrans.getText().toString()
                        || "".equals(editTextAORMTrans.getText().toString().trim())) {
                    new  AlertDialog.Builder(this).setTitle("Information" )
                            .setMessage("Please enter transactions of AORM benchmark.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                aORMTrans = Integer.parseInt(editTextAORMTrans.getText().toString());
            }

            int phaseInterval = -1;
            if (checkBoxPause.isChecked()) {
                if (null == editTextPhaseInterval.getText().toString() ||
                        "".equals(editTextPhaseInterval.getText().toString().trim())) {
                    new  AlertDialog.Builder(this).setTitle("Information" )
                            .setMessage("Please enter interval value." )
                            .setPositiveButton("OK",  null )
                            .show();
                    return;
                }

                phaseInterval = Integer.parseInt(editTextPhaseInterval.getText().toString());
            }

            AppContext.getInstance().setBenchmark(benchmark);
            AppContext.getInstance().setTotalTrans(totalTrans);
            AppContext.getInstance().setScale(scale);
            AppContext.getInstance().setTerminals(terminals);
            AppContext.getInstance().setPhaseInterval(phaseInterval);
            AppContext.getInstance().setAORMTrans(aORMTrans);

            // save parameters in the file
            Intent intent = new Intent(this, RunActivity.class);
            startActivity(intent);

        } else if (view == findViewById(R.id.btnClose)) {
            finish();
        }
    }
}
