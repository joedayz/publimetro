package pe.joedayz.publimetro.controller.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.adapter.OfertaListAdapter;
import pe.joedayz.publimetro.model.Ciudad;
import pe.joedayz.publimetro.model.Establecimiento;
import pe.joedayz.publimetro.model.Oferta;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleOfertasFragment extends Fragment {


    private static final String TAG = DetalleOfertasFragment.class.getSimpleName();

    //&idUbicacion=1&idEstablecimiento=14
    private static final String  url  = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getOfertasEstablecimiento";

    private static Establecimiento establecimiento;
    private ListView listView;
    private List<Oferta> ofertaList;
    private OfertaListAdapter adapter;
    private ProgressDialog pDialog;
    private Ciudad ciudad;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.detalle_ofertas_fragment, container, false);

        establecimiento = (Establecimiento) getActivity().getIntent().getSerializableExtra("establecimiento");
        ciudad = (Ciudad) getActivity().getIntent().getSerializableExtra("ciudadSeleccionada");

        listView = (ListView) rootView.findViewById(R.id.listview_ofertas_establecimiento);
        ofertaList = new ArrayList<>();
        adapter = new OfertaListAdapter(getActivity(), ofertaList);
        listView.setAdapter(adapter);


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Cargando Ofertas...");
        pDialog.show();



        JsonObjectRequest jsonObjReq = null;
        jsonObjReq = getOfertasPorEstablecimiento();



        AppController.getInstance().addToRequestQueue(jsonObjReq);

        return rootView;
    }

    private JsonObjectRequest getOfertasPorEstablecimiento() {

        return new JsonObjectRequest(Request.Method.GET,
                url + "&idUbicacion=" + ciudad.getCodigo() + "&idEstablecimiento=" + establecimiento.getCodigo(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());



                try {

                    JSONObject lista = response.getJSONObject("lista");
                    JSONObject atributos = lista.getJSONObject("@attributes");
                    int cantidad = atributos.getInt("cantidad");

                    String codigo= "", ofertaCodigo = "", categoriaCodigo="", detalle="", fileName="";

                    if (cantidad == 1) {

                        JSONObject ofertaJSON = (JSONObject) lista.getJSONObject("oferta");
                        codigo = ofertaJSON.getString("codigo");
                        ofertaCodigo = ofertaJSON.getString("ofertacodigo");
                        categoriaCodigo = ofertaJSON.getString("categoriacodigo");
                        detalle = ofertaJSON.getString("detalle");
                        fileName = ofertaJSON.getString("filename");

                        Oferta oferta = new Oferta(codigo, ofertaCodigo, categoriaCodigo, detalle, fileName);

                        ofertaList.add(oferta);

                    }else if (cantidad > 1) {

                        JSONArray ofertas = lista.getJSONArray("oferta");

                        for (int i = 0; i < ofertas.length(); i++) {

                            JSONObject ofertaJSON = (JSONObject) ofertas
                                    .get(i);
                            codigo = ofertaJSON.getString("codigo");
                            ofertaCodigo = ofertaJSON.getString("ofertacodigo");
                            categoriaCodigo = ofertaJSON.getString("categoriacodigo");
                            detalle = ofertaJSON.getString("detalle");
                            fileName = ofertaJSON.getString("filename");

                            Oferta oferta = new Oferta(codigo, ofertaCodigo, categoriaCodigo, detalle, fileName);

                            ofertaList.add(oferta);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();

                hidePDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                hidePDialog();
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}