package luistrejo.com.materialdesign.Chatlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import luistrejo.com.materialdesign.Chat;
import luistrejo.com.materialdesign.R;


/**
 * Created by Luis Trejo on 01/01/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView usuario;
        TextView comentario;
        TextView fecha;


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View itemView = inflater.inflate(R.layout.chat_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        usuario = (TextView) itemView.findViewById(R.id.usuario);
        comentario = (TextView) itemView.findViewById(R.id.comentario);
        fecha = (TextView) itemView.findViewById(R.id.fecha);


        // Capture position and set results to the TextViews
        usuario.setText(resultp.get(Chat.usuario));
        comentario.setText(resultp.get(Chat.comentario));
        fecha.setText(resultp.get(Chat.fecha));

        // Capture ListView item click
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


            }
        });
        return itemView;
    }
}

