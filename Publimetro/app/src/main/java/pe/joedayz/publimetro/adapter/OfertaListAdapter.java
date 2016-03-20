package pe.joedayz.publimetro.adapter;

/**
 * Created by josediaz on 3/19/16.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Oferta;

/**
 * Created by josediaz on 3/16/16.
 */
public class OfertaListAdapter extends BaseAdapter {



    private Activity activity;
    private LayoutInflater inflater;
    private List<Oferta> ofertaList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public OfertaListAdapter(Activity activity, List<Oferta> ofertaList) {
        this.activity = activity;
        this.ofertaList = ofertaList;
    }

    @Override
    public int getCount() {
        return ofertaList.size();
    }

    @Override
    public Object getItem(int position) {
        return ofertaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_oferta, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);

        Oferta oferta = ofertaList.get(position);

        thumbNail.setImageUrl(oferta.getFileName(), imageLoader);


        return convertView;
    }
}