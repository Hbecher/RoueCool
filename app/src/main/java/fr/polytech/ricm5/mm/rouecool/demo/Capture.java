package fr.polytech.ricm5.mm.rouecool.demo;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import fr.polytech.ricm5.mm.rouecool.util.Rectangle;

public class Capture extends View
{
	private final int[] transitions = {7, 30, 15, 41, 3, 90};
	private final long[] times = new long[transitions.length + 1];
	private final TextPaint text;
	private final Paint fill;
	private final Rectangle rect = new Rectangle();
	private long start = 0L;
	private int state = -1;

	public Capture(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		text = new TextPaint(TextPaint.LINEAR_TEXT_FLAG);
		text.setTextSize(64.0F);
		text.setColor(Color.BLUE);

		fill = new Paint();
		fill.setStyle(Paint.Style.FILL);
		fill.setColor(Color.GREEN);
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
		return state >= transitions.length;
	}

	void next(int k)
	{
		if(state >= 0 && state < transitions.length)
		{
			if(k == transitions[state])
			{
				long stop = System.currentTimeMillis();
				long elapsed = stop - start;
				times[state++] = elapsed;

				times[transitions.length] += elapsed;

				Log.d("Demo", String.valueOf(state));

				start = stop;
			}
		}
	}

	String[] getElements(List<Element<String>> elements)
	{
		int length = transitions.length;
		String[] strings = new String[length];

		for(int i = 0; i < length; i++)
		{
			strings[i] = elements.get(transitions[i]).formatData();
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
				String s = String.valueOf(time);

				rect.setX(24.0F);
				rect.setY(y - text.getTextSize());
				rect.setWidth(text.measureText(s) + 16.0F);
				rect.setHeight(text.getTextSize() + 16.0F);

				canvas.drawRect(rect.rect(), fill);
				canvas.drawText(s, 32.0F, y, text);

				y += f;
			}

			Log.d("Demo", "END");
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
