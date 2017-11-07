package fr.polytech.ricm5.mm.rouecool.demo;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Capture extends View
{
	private final int[] states = {7, 30, 15, 41, 3};
	private final long[] times = new long[states.length];
	private final TextPaint text;
	private long start = 0L;
	private int state = -1;

	public Capture(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		text = new TextPaint(TextPaint.LINEAR_TEXT_FLAG);
		text.setTextSize(128.0F);
		text.setColor(Color.BLUE);
	}

	private void start()
	{
		start = System.currentTimeMillis();

		state = 0;
	}

	boolean isStarted()
	{
		return state > -1;
	}

	private boolean isEnded()
	{
		return state >= states.length;
	}

	void next(int k)
	{
		if(state >= 0 && state < states.length)
		{
			if(k == states[state])
			{
				long stop = System.currentTimeMillis();
				times[state++] = stop - start;

				Log.i("Demo state", String.valueOf(state));

				start = stop;
			}
		}
	}

	String[] getElements(List<Element<String>> elements)
	{
		int length = states.length;
		String[] strings = new String[length];

		for(int i = 0; i < length; i++)
		{
			strings[i] = elements.get(states[i]).formatData();
		}

		return strings;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(isEnded())
		{
			float f = text.getTextSize() + 16.0F, y = f;

			for(long time : times)
			{
				canvas.drawText(String.valueOf(time), 32.0F, y, text);

				y += f;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(!isStarted())
		{
			start();
		}

		if(isEnded())
		{
			invalidate();
		}

		return false;
	}
}
