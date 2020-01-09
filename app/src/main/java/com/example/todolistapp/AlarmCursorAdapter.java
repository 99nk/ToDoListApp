package com.example.todolistapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.todolistapp.data.AlarmReminderContract;
import com.example.todolistapp.R;
import com.example.todolistapp.data.AlarmReminderContract;

public class AlarmCursorAdapter extends CursorAdapter {
    private TextView mTitleText,mDateAndTimeText,mRepeatInfoText;
    private ImageView mActiveImage,mThumbnailImage;
    private ColorGenerator mColorGenerator=ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c,0/*flags*/);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTitleText = (TextView) view.findViewById(R.id.recycle_title);
        mDateAndTimeText = (TextView) view.findViewById(R.id.recycle_date_time);
        mRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
        mActiveImage = (ImageView) view.findViewById(R.id.active_image);
        mThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);


        int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO);
        int repeatTypeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE);
        int activeColumns = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);


        String title = cursor.getString((titleColumnIndex)+1);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String active = cursor.getString(activeColumns);

        setReminderTitle(title);
        if (date != null) {
            String dateTime = date + " " + time;
            setReminderDateTime(dateTime);
        } else {
            mDateAndTimeText.setText("Date not set");
        }
        if (repeat != null) {
            setReminderRepeatInfo(repeat, repeatNo, repeatType);
        } else {
            mRepeatInfoText.setText("Repeat not set");
        }
        if (active != null) {
            setActiveImage(active);
        } else {
            mActiveImage.setImageResource(R.drawable.grey_bell);
        }
    }





    public void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";
        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }
        int color = mColorGenerator.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);


    }
    public void setReminderDateTime(String dateTime)
    {
        mDateAndTimeText.setText(dateTime);
    }
    public void setReminderRepeatInfo(String repeat,String repeatNo,String repeatType)
    {
        if(repeat.equals("true"))
        {
            mRepeatInfoText.setText("Every "+repeatNo+" "+repeatType+"(s)");
        }
        else if(repeat.equals("false"))
        {
            mRepeatInfoText.setText("Repeat Off");
        }
    }
    public void setActiveImage(String active){
        if(active.equals("true"))
        {
            mActiveImage.setImageResource(R.drawable.bell);
        }
        else if(active.equals("false")){
            mActiveImage.setImageResource(R.drawable.grey_bell);
        }
    }
}
