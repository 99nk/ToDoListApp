package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.todolistapp.R;
import com.example.todolistapp.data.AlarmReminderContract;
import com.example.todolistapp.reminder.AlarmScheduler;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import static java.lang.System.out;

public class AddReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_VEHICLE_LOADER=0;

    private EditText mTitleText;
    private TextView mDateText,mTimeText,mRepeatText,mRepeatNoText,mRepeatTypeText;
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;
    private Calendar mCalender;
    private int mYear,mMonth,mHour,mMinute,mDay;
    private long mRepeatTime;
    private Switch mRepeatSwitch;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private Uri mCurrentReminderUri;
    private boolean mVehicleHasChanged=false;
    private static final String KEY_TITLE="title_key";
    private static final String KEY_TIME="time_key";
    private static final String KEY_DATE="date_key";
    private static final String KEY_REPEAT="repeat_key";
    private static final String KEY_REPEAT_TYPE="repeat_type_key";
    private static final String KEY_REPEAT_NO="repeat_no_key";
    private static final String KEY_ACTIVE="active_key";
    private static final long milMinute=60000L;
    private static final long milHour=3600000L;
    private static final long milDay=86400000L;
    private static final long milWeek=604800000L;
    private static final long milMonth=2592000000L;
    private View.OnTouchListener mTouchListen=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mVehicleHasChanged=true;
            return false;
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Intent intent=getIntent();
        mCurrentReminderUri=intent.getData();
        if(mCurrentReminderUri==null)
        {
            setTitle("Add reminder details");
            invalidateOptionsMenu();
        }
        else
        {
            setTitle("Edit Reminder");
            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER,null,this);
        }

        mTitleText=(EditText)findViewById(R.id.reminder_title);
        mDateText=(TextView)findViewById(R.id.set_date);
        mTimeText=(TextView)findViewById(R.id.set_time);
        mRepeatText=(TextView)findViewById(R.id.set_repeat);
        mRepeatNoText=(TextView)findViewById(R.id.set_repeatNo);
        mRepeatTypeText=(TextView)findViewById(R.id.set_repeatType);
        mRepeatSwitch=(Switch)findViewById(R.id.repeat_switch);
        mFAB1=(FloatingActionButton)findViewById(R.id.starred1);
        mFAB2=(FloatingActionButton)findViewById(R.id.starred2);
        mActive="true";
        mRepeat="true";
        mRepeatNo=Integer.toString(1);
        mRepeatType="Hour";
        mCalender=Calendar.getInstance();
        mHour=mCalender.get(Calendar.HOUR_OF_DAY);
        mMinute=mCalender.get(Calendar.MINUTE);
        mYear=mCalender.get(Calendar.YEAR);
        mMonth=mCalender.get(Calendar.MONTH);
        mDay=mCalender.get(Calendar.DATE);
        mDate=mDay+"/"+mMonth+"/"+mYear;
        mTime=mHour+":"+mMinute;
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle=s.toString().trim();
                mTitleText.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every "+mRepeatNo+" "+mRepeatType+"(s)");
        if(savedInstanceState!=null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String savedRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(savedRepeat);
            mRepeat = savedRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }
        if(mActive.equals("false"))
        {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);
        }
        else if(mActive.equals("true"))
        {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        }

        getSupportActionBar().setTitle("Add Reminder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }



    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TITLE,mTitleText.getText());
        outState.putCharSequence(KEY_TIME,mTimeText.getText());
        outState.putCharSequence(KEY_DATE,mDateText.getText());
        outState.putCharSequence(KEY_REPEAT,mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO,mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE,mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE,mActive);

    }

    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        int hour=now.get(Calendar.HOUR_OF_DAY);
        int min=now.get(Calendar.MINUTE);
        TimePickerDialog tpd;
        tpd=new TimePickerDialog(AddReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute<10){
                    mTimeText.setText(hourOfDay+":0"+minute);}
                else {mTimeText.setText(hourOfDay+":"+minute);}


            }},hour,min,false);
        tpd.setTitle("Select time");
        tpd.show();
    }


    public void setDate(View v){
        Calendar now=Calendar.getInstance();
        DatePickerDialog dpd=new DatePickerDialog(AddReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mDateText.setText(dayOfMonth+"/"+(++month)+"/"+year);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }


    public void selectFab1(View v)
    {
        mFAB1=(FloatingActionButton)findViewById(R.id.starred1);
        mFAB1.setVisibility(View.GONE);
        mFAB2=(FloatingActionButton)findViewById(R.id.starred2);
        mFAB2.setVisibility(View.VISIBLE);
        mActive="true";
    }
    public void selectFab2(View v)
    {
        mFAB2=(FloatingActionButton)findViewById(R.id.starred2);
        mFAB2.setVisibility(View.GONE);
        mFAB1=(FloatingActionButton)findViewById(R.id.starred1);
        mFAB1.setVisibility(View.VISIBLE);
        mActive="false";
    }
    public void onSwitchRepeat(View view)
    {
        boolean on=((Switch)view).isChecked();
        if(on)
        {
            mRepeat="true";
            mRepeatText.setText("Every "+mRepeatNo+" "+mRepeatType+"(s)");
        }
        else {
            mRepeat="false";
            mRepeatText.setText("Off");
        }
    }
    public void selectRepeatType(View v)
    {
        final String[] items=new String[5];
        items[0]="Minute";
        items[1]="Hour";
        items[2]="Day";
        items[3]="Week";
        items[4]="Month";
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRepeatType=items[which];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText("Every "+mRepeatNo+" "+mRepeatType+"(s)");
            }
        });
        AlertDialog alert =builder.create();
        alert.show();
    }
    public void setRepeatNo(View v)
    {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");
        final EditText input=new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length() == 0) {
                    mRepeatNo = Integer.toString(1);
                    mRepeatNoText.setText(mRepeatNo);
                    mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                } else {
                    mRepeatNo = input.getText().toString().trim();
                    mRepeatNoText.setText(mRepeatNo);
                    mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                }
            }

        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add_reminder,menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {super.onPrepareOptionsMenu(menu);

        if(mCurrentReminderUri==null)
        {
            MenuItem menuItem=menu.findItem(R.id.discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_reminder:
                if(mTitleText.getText().toString().length()==0)
                {
                    mTitleText.setError("Title cannot be blank");
                }
                else
                {
                    saveReminder();
                    finish();
                }
                return true;
            case R.id.discard_reminder:
                showDeleteReminderDialog();
                return true;

            case  android.R.id.home:
                if(!mVehicleHasChanged){
                    NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener= new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing");
        builder.setPositiveButton("discard",discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void showDeleteReminderDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete reminder");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteReminder();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null)
                {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void deleteReminder()
    {
        if(mCurrentReminderUri!=null)
        {
            int rowsDeleted=getContentResolver().delete(mCurrentReminderUri,null,null);
            if(rowsDeleted==0)
            {
                Toast.makeText(this,"Error with deleting reminder",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Reminder deleted",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    public void saveReminder()
    {
        ContentValues values=new ContentValues();
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,mTitle);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TIME,mTime);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_DATE,mDate);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT,mRepeat);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,mRepeatNo);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,mRepeatType);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE,mActive);

        mCalender.set(Calendar.MONTH,--mMonth);
        mCalender.set(Calendar.YEAR,mYear);
        mCalender.set(Calendar.DAY_OF_MONTH,mDay);
        mCalender.set(Calendar.HOUR_OF_DAY,mHour);
        mCalender.set(Calendar.MINUTE,mMinute);
        mCalender.set(Calendar.SECOND,0);

        long selectTimeStamp=mCalender.getTimeInMillis();

        if(mRepeatType.equals("Minute"))
        {
            mRepeatTime=Integer.parseInt(mRepeatNo)*milMinute;
        }

        else if(mRepeatType.equals("Hours"))
        {
            mRepeatTime=Integer.parseInt(mRepeatNo)*milHour;
        }
        else if(mRepeatType.equals("Day")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milDay;

        }
        else if(mRepeatType.equals("Week")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milWeek;

        }
        else if(mRepeatType.equals("Month")){
            mRepeatTime=Integer.parseInt(mRepeatNo)*milMonth;

        }
        if(mCurrentReminderUri==null)
        {
            Uri newUri = getContentResolver().insert(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, values);
            if (newUri == null)
            {
                Toast.makeText(this, "Error with saving reminder", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowsAffected = getContentResolver().update(mCurrentReminderUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating reminder", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show();
            }
        }

        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                new AlarmScheduler().setRepeatAlarm(getApplicationContext(), selectTimeStamp, mCurrentReminderUri, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new AlarmScheduler().setAlarm(getApplicationContext(), selectTimeStamp, mCurrentReminderUri);
            }
            Toast.makeText(this, "Alarm time is " + selectTimeStamp, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                AlarmReminderContract.AlarmReminderEntry._ID,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
                AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
                AlarmReminderContract.AlarmReminderEntry.KEY_TIME,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE,
        };
        return new CursorLoader(this,mCurrentReminderUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data==null|| data.getCount()<1){
            return;
        }
        if(data.moveToFirst()){
            int titleColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
            int timeColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
            int dateColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
            int repeatColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT);
            int repeatNoColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO);
            int repeatTypeColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE);
            int activeColumnIndex=data.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);

            String title=data.getString(titleColumnIndex);
            String time=data.getString(timeColumnIndex);
            String date=data.getString(dateColumnIndex);
            String repeat=data.getString(repeatColumnIndex);
            String repeatNo=data.getString(repeatNoColumnIndex);
            String repeatType=data.getString(repeatTypeColumnIndex);
            String active=data.getString(activeColumnIndex);
            mTitleText.setText(title);
            mTimeText.setText(time);
            mDateText.setText(date);
            mRepeatNoText.setText(repeatNo);
            mRepeatTypeText.setText(repeatType);
            mRepeatText.setText("Every "+repeatNo+" "+repeatType+"(s)");


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
