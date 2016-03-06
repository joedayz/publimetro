package pe.joedayz.publimetro.controller.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.joedayz.publimetro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleOfertasFragment extends Fragment {


    public DetalleOfertasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.detalle_ofertas_fragment, container, false);
    }

}
