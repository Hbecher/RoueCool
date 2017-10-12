package fr.polytech.ricm5.mm.rouecool;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends AppCompatActivity
{
	private final List<TextView> elements = new ArrayList<>();
	private int selected;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		list = (ListView) findViewById(R.id.demo_list);

		populateElements();

		list.setAdapter(new Adapter());
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				select(i);
			}
		});

		Wheel wheel = (Wheel) findViewById(R.id.demo_wheel);
		wheel.addWheelTickListener(new Wheel.WheelTickListener()
		{
			@Override
			public void onWheelTick(Wheel.WheelTickEvent e)
			{
				select(mod(selected + e.getDirection(), elements.size()));
			}
		});
	}

	private void populateElements()
	{
		elements.clear();

		for(int i = 0; i < 1000; i++)
		{
			TextView textView = new TextView(this);
			textView.setText(String.valueOf(i));

			if(i == selected)
			{
				select(textView);
			}
			else
			{
				deselect(textView);
			}

			elements.add(textView);
		}
	}

	private int mod(int f, int m)
	{
		int mod = f % m;

		return mod < 0 ? mod + m : mod;
	}

	private void select(int i)
	{
		deselect(elements.get(selected));
		selected = i;
		select(elements.get(selected));
	}

	private void select(TextView textView)
	{
		textView.setSelected(true);
		textView.setTextColor(Color.RED);
		textView.setTextSize(32.0F);

		list.setSelection(selected);
	}

	private void deselect(TextView textView)
	{
		textView.setSelected(false);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(16.0F);
	}

	private class Adapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return elements.size();
		}

		@Override
		public Object getItem(int i)
		{
			return elements.get(i).getText().toString();
		}

		@Override
		public long getItemId(int i)
		{
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup)
		{
			return elements.get(i);
		}
	}
}
