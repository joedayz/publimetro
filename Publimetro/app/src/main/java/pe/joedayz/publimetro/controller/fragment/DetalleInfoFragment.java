package pe.joedayz.publimetro.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Establecimiento;

public class DetalleInfoFragment extends Fragment {


    private Establecimiento establecimiento;
    private ViewGroup mLinearLayout;

    public DetalleInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.detalle_info_fragment, container, false);

        establecimiento = (Establecimiento) getActivity().getIntent().getSerializableExtra("establecimiento");

        TextView txtTitulo = (TextView) rootView.findViewById(R.id.tituloEstablecimientoFragment);
        txtTitulo.setText(establecimiento.getTitulo());

        TextView txtDescripcion = (TextView) rootView.findViewById(R.id.detalleEstablecimientoFragment);
        txtDescripcion.setText(establecimiento.getDetalle());


        mLinearLayout = (ViewGroup) rootView.findViewById(R.id.linearLayoutItems);

        initItems();

        return rootView;
    }

    private void initItems() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        if (establecimiento.getHorario() != null && !establecimiento.getHorario().equals("{}")) {

            LinearLayout rowHorario = new LinearLayout(getActivity());
            rowHorario.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageViewHorario = new ImageView(getActivity());
            imageViewHorario.setImageResource(R.drawable.ic_detalle_horario);
            imageViewHorario.setLayoutParams(params);

            rowHorario.addView(imageViewHorario);


            TextView textHorario = new TextView(getActivity());
            textHorario.setText(establecimiento.getHorario());
            textHorario.setLayoutParams(params);
            textHorario.setTextColor(getResources().getColor(R.color.blackDesc));


            rowHorario.addView(textHorario);

            mLinearLayout.addView(rowHorario);
        }

        for (final String telefono : establecimiento.getTelefonos()) {

            if (!telefono.equals("")) {

                LinearLayout rowTelefono = new LinearLayout(getActivity());
                rowTelefono.setOrientation(LinearLayout.HORIZONTAL);
                ImageView imageViewTelefono = new ImageView(getActivity());
                imageViewTelefono.setImageResource(R.drawable.ic_detalle_telefono);
                imageViewTelefono.setLayoutParams(params);
                rowTelefono.addView(imageViewTelefono);

                TextView textTelefono = new TextView(getActivity());
                textTelefono.setText(telefono);
                textTelefono.setLayoutParams(params);
                textTelefono.setTextColor(getResources().getColor(R.color.blackDesc));

                textTelefono.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + Uri.encode(telefono.trim())));
                        startActivity(callIntent );
                    }
                });


                rowTelefono.addView(textTelefono);

                mLinearLayout.addView(rowTelefono);
            }
        }
        if (establecimiento.getFacebook() != null && !establecimiento.getFacebook().equals("{}")) {

            LinearLayout rowFacebook = new LinearLayout(getActivity());
            rowFacebook.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageViewFacebook = new ImageView(getActivity());
            imageViewFacebook.setImageResource(R.drawable.ic_detalle_facebook);
            imageViewFacebook.setLayoutParams(params);
            rowFacebook.addView(imageViewFacebook);

            TextView textFacebook = new TextView(getActivity());
            textFacebook.setText(establecimiento.getFacebook());
            textFacebook.setLayoutParams(params);
            textFacebook.setTextColor(getResources().getColor(R.color.blackDesc));
            rowFacebook.addView(textFacebook);

            mLinearLayout.addView(rowFacebook);
        }

        if (establecimiento.getWeb() != null && !establecimiento.getWeb().equals("{}")) {

            LinearLayout rowWeb = new LinearLayout(getActivity());
            rowWeb.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageViewWeb = new ImageView(getActivity());
            imageViewWeb.setImageResource(R.drawable.ic_detalle_web);
            imageViewWeb.setLayoutParams(params);
            rowWeb.addView(imageViewWeb);

            TextView textWeb = new TextView(getActivity());
            textWeb.setText(establecimiento.getWeb());
            textWeb.setLayoutParams(params);
            textWeb.setTextColor(getResources().getColor(R.color.blackDesc));
            rowWeb.addView(textWeb);

            mLinearLayout.addView(rowWeb);
        }
    }

}
