package pe.joedayz.publimetro.controller.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Establecimiento;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleEstablecimientoFragment extends Fragment {


    private Establecimiento establecimiento;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;

    public DetalleEstablecimientoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detalle_establecimiento_fragment, container, false);

        establecimiento = (Establecimiento) getActivity().getIntent().getSerializableExtra("establecimiento");

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        NetworkImageView imageView = (NetworkImageView) rootView.findViewById(R.id.imgDetalleEstablecimiento);
        imageView.setImageUrl(establecimiento.getFileName(), imageLoader);


        TextView txtDescripcion = (TextView) rootView.findViewById(R.id.descripcionEstablecimiento);
        txtDescripcion.setText(establecimiento.getResumen());

        //Tabs
        mTabWidget = (TabWidget) rootView.findViewById(android.R.id.tabs);

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);


        TabHost.TabSpec specMapa = mTabHost.newTabSpec("mapa");
        View tabIndicatorOne = LayoutInflater.from(getActivity()).inflate(R.layout.tab_indicator, mTabWidget, false);
        ((ImageView) tabIndicatorOne.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_mapa);
        specMapa.setIndicator(tabIndicatorOne);

        mTabHost.addTab(specMapa, DetalleMapaFragment.class, null);


        TabHost.TabSpec specDetalle = mTabHost.newTabSpec("detalle");
        View tabIndicatorTwo = LayoutInflater.from(getActivity()).inflate(R.layout.tab_indicator, mTabWidget, false);
        ((ImageView) tabIndicatorTwo.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_detalle);
        specDetalle.setIndicator(tabIndicatorTwo);

        mTabHost.addTab(specDetalle, DetalleInfoFragment.class, null);


        TabHost.TabSpec specOfertas = mTabHost.newTabSpec("ofertas");
        View tabIndicatorThree = LayoutInflater.from(getActivity()).inflate(R.layout.tab_indicator, mTabWidget, false);
        ((ImageView) tabIndicatorThree.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_ofertas);
        specOfertas.setIndicator(tabIndicatorThree);

        mTabHost.addTab(specOfertas, DetalleOfertasFragment.class, null);

        return rootView;
    }


}
