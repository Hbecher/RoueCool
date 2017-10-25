package fr.polytech.ricm5.mm.rouecool.demo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.polytech.ricm5.mm.rouecool.R;
import fr.polytech.ricm5.mm.rouecool.res.sounds.SoundManager;
import fr.polytech.ricm5.mm.rouecool.wheel.Wheel;
import fr.polytech.ricm5.mm.rouecool.wheel.WheelListener;
import fr.polytech.ricm5.mm.rouecool.wheel.WheelTickEvent;

public class Demo extends AppCompatActivity
{
	private final List<Element<Integer>> elements = new ArrayList<>();
	private final int size = 600;
	private int selected, highlighted;
	private SoundManager soundManager;
	private TextView selection;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		soundManager = new SoundManager(getBaseContext());
		soundManager.start();

		selection = (TextView) findViewById(R.id.demo_select);
		list = (ListView) findViewById(R.id.demo_list);

		populateElements();

		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setAdapter(new Adapter());
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(position != selected)
				{
					setSelected(position);

					playSelectSound();
				}
			}
		});

		Wheel wheel = (Wheel) findViewById(R.id.demo_wheel);
		WheelListener l = new WheelListener()
		{
			@Override
			public void onWheelClick()
			{
				if(highlighted != selected)
				{
					setSelected(highlighted);

					playSelectSound();
				}
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

	private void populateElements()
	{
		elements.clear();

		for(int i = 0; i < size; i++)
		{
			Element<Integer> element = new Element<>(this, i);

			element.setSelected(i == selected);

			elements.add(element);
		}
	}

	private int mod(int k, int n)
	{
		int m = k % n;

		return m < 0 ? m + n : m;
	}

	private void setSelected(int index)
	{
		selected = index;

		selection.setText(elements.get(index).formatData());

		setHighlighted(index);
	}

	private void setHighlighted(final int index)
	{
		elements.get(highlighted).setSelected(false);

		highlighted = index;

		elements.get(index).setSelected(true);

		list.setSelection(index);
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
			return size;
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
