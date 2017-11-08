package fr.polytech.ricm5.mm.rouecool.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.polytech.ricm5.mm.rouecool.R;
import fr.polytech.ricm5.mm.rouecool.res.sounds.SoundManager;
import fr.polytech.ricm5.mm.rouecool.wheel.Wheel;
import fr.polytech.ricm5.mm.rouecool.wheel.WheelAdapter;
import fr.polytech.ricm5.mm.rouecool.wheel.WheelListener;
import fr.polytech.ricm5.mm.rouecool.wheel.WheelTickEvent;

public class Demo extends AppCompatActivity
{
	private final List<Element<String>> elements = new ArrayList<>();
	private Capture capture;
	private int selected, highlighted;
	private SoundManager soundManager;
	private TextView selection;
	private ListView list;
	private Wheel wheel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		capture = (Capture) findViewById(R.id.demo_capture);
		selection = (TextView) findViewById(R.id.demo_select);
		list = (ListView) findViewById(R.id.demo_list);
		wheel = (Wheel) findViewById(R.id.demo_wheel);

		populateElements();

		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setAdapter(new Adapter());
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				setSelected(position);

				playSelectSound();
			}
		});

		WheelListener l = new WheelAdapter()
		{
			@Override
			public void onWheelClick()
			{
				setSelected(highlighted);

				playSelectSound();
			}

			@Override
			public void onWheelTick(WheelTickEvent e)
			{
				setHighlighted(mod(highlighted + e.getDirection() * e.getAmount(), elements.size()));

				playHighlightSound();
			}
		};
		wheel.addWheelTickListener(l);
		wheel.addWheelClickListener(l);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if(soundManager != null)
		{
			soundManager.stop();
			soundManager = null;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if(soundManager == null)
		{
			soundManager = new SoundManager(getBaseContext());
			soundManager.start();
		}
	}

	private void populateElements()
	{
		elements.clear();

		BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.names)));

		try
		{
			String line;

			while((line = reader.readLine()) != null)
			{
				elements.add(new Element<>(this, line));
			}
		}
		catch(IOException ignored)
		{
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(IOException ignored)
			{
			}
		}

		setSelected(0);

		Log.i("Demo", Arrays.toString(capture.getElements(elements)));
	}

	private int mod(int k, int n)
	{
		int m = k % n;

		return m < 0 ? m + n : m;
	}

	private void setSelected(int index)
	{
		selected = index;

		if(capture.isStarted())
		{
			capture.next(index);
		}

		selection.setText(elements.get(index).formatData());

		setHighlighted(index);
	}

	private void setHighlighted(final int index)
	{
		elements.get(highlighted).setSelected(false);

		highlighted = index;

		elements.get(index).setSelected(true);

		if(wheel.isWheelEnabled())
		{
			list.setSelection(index);
		}
	}

	private void playHighlightSound()
	{
		soundManager.playSound(R.raw.pap);
	}

	private void playSelectSound()
	{
		soundManager.playSound(R.raw.cla);
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
			return elements.get(i).getData();
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
