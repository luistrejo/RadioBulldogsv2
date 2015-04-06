package luistrejo.com.materialdesign;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Luis Trejo on 31/03/2015.
 */
public class SlidingListFragmentCanciones extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slidingfragmentlayout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new MyListAdapter());
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView result = (TextView) convertView;
            if (result == null) {
                result = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemlistcanciones, parent, false);
            }
            result.setText("My custom element #" + position);

            return result;
        }
    }
}
