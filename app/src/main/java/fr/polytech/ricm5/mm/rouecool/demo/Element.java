package fr.polytech.ricm5.mm.rouecool.demo;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

class Element<T> extends LinearLayout
{
	private static final float TEXT_SIZE = 32.0F, HIGHLIGHTED_SIZE = 48.0F;
	private static final int TEXT_COLOR = Color.BLACK, HIGHLIGHTED_COLOR = Color.RED;
	private final TextView view;
	private final Formatter<T> formatter;
	private T data;

	Element(Context context)
	{
		this(context, null);
	}

	Element(Context context, T data)
	{
		this(context, data, Formatter.TO_STRING);
	}

	Element(Context context, T data, Formatter<T> formatter)
	{
		super(context);

		addView(view = new TextView(context));
		this.formatter = formatter;
		setData(data);

	}

	void setData(T data)
	{
		this.data = data;

		view.setText(formatData());
	}

	T getData()
	{
		return data;
	}

	public String formatData()
	{
		return formatter.format(data);
	}

	public void setSelected(boolean selected)
	{
		if(selected)
		{
			select();
		}
		else
		{
			deselect();
		}
	}

	private void select()
	{
		view.setSelected(true);
		view.setTextColor(HIGHLIGHTED_COLOR);
		view.setTextSize(HIGHLIGHTED_SIZE);
	}

	private void deselect()
	{
		view.setSelected(false);
		view.setTextColor(TEXT_COLOR);
		view.setTextSize(TEXT_SIZE);
	}

	public interface Formatter<T>
	{
		Formatter TO_STRING = new Formatter()
		{
			@Override
			public String format(Object data)
			{
				return data == null ? "null" : data.toString();
			}
		};

		String format(T data);
	}
}
