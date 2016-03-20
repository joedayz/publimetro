package pe.joedayz.publimetro.controller.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import pe.joedayz.publimetro.adapter.EstablecimientoListAdapter;
import pe.joedayz.publimetro.model.Ciudad;
import pe.joedayz.publimetro.model.Establecimiento;
import pe.joedayz.publimetro.model.Rubro;
import pe.joedayz.publimetro.model.Ubicacion;


public class EstablecimientosFragment extends Fragment {

    private static final String TAG = EstablecimientosFragment.class.getSimpleName();


    private static final String url = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getEstablecimientos&idUbicacion=";


    private static final String urlQueryText = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getConsultaEstablecimientos&idUbicacion=";

    private Ciudad ciudad;
    private Rubro rubro;
    private ListView listView;
    private EstablecimientoListAdapter adapter;



    private ProgressDialog pDialog;
    private List<Establecimiento> establecimientoList = new ArrayList<Establecimiento>();
    private String queryText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_establecimientos, container, false);

        ciudad = (Ciudad) getActivity().getIntent().getSerializableExtra("ciudadSeleccionada");

        rubro = (Rubro) getActivity().getIntent().getSerializableExtra("rubro");

        queryText = (String) getActivity().getIntent().getSerializableExtra("queryText");

        listView = (ListView) rootView.findViewById(R.id.listview_comercios);
        adapter = new EstablecimientoListAdapter(getActivity(), establecimientoList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Cargando Establecimientos...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = null;
        if (queryText != null && !queryText.equals("")) {
            jsonObjReq = getEstablecimientoPorQueryText(queryText.trim());
            getActivity().getIntent().putExtra("queryText", "");
        } else {
            jsonObjReq = getEstablecimientoPorRubro();
        }

        
        


        AppController.getInstance().addToRequestQueue(jsonObjReq);




        

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Establecimiento establecimiento = (Establecimiento) adapterView.getItemAtPosition(position);

                Fragment fragment = new DetalleEstablecimientoFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                getActivity().getIntent().putExtra("establecimiento", establecimiento);

                fragment.setArguments(getActivity().getIntent().getExtras());
            }
        });


        return rootView;
    }

    private JsonObjectRequest getEstablecimientoPorQueryText(String queryText) {
        return new JsonObjectRequest(Request.Method.GET,
                urlQueryText + ciudad.getCodigo() + "&criterioBusqueda=" + queryText, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject lista = response.getJSONObject("lista");
                    JSONObject atributos = lista.getJSONObject("@attributes");
                    int cantidad = atributos.getInt("cantidad");
                    String codigo = null;
                    String codigoRubro = null;
                    String titulo ="", resumen="", detalle="", filename="",
                            filenameLogo="", horario = "", facebook="", web="";
                    List<String> telefonosDeEstablecimientos = new ArrayList<>();
                    List<Ubicacion> ubicaciones = new ArrayList<>();
                    String telefono = "", latitude="", longitude="", direccion="";

                    if (cantidad == 1) {


                        JSONObject establecimientoJSON = (JSONObject) lista.getJSONObject("establecimiento");
                        codigo = establecimientoJSON.getString("codigo");
                        codigoRubro = establecimientoJSON.getString("categoriacodigo");


                        if (rubro == null || rubro.getCodigo().equals("0") ||
                                rubro.getCodigo().equals(codigoRubro)) {


                            titulo = establecimientoJSON.getString("titulo");
                            resumen = establecimientoJSON.getString("resumen");
                            detalle = establecimientoJSON.getString("detalle");
                            filename = establecimientoJSON.getString("filename");
                            filenameLogo = establecimientoJSON.getString("filenamelogo");
                            horario = establecimientoJSON.getString("horario");
                            facebook = establecimientoJSON.getString("facebook");
                            web = establecimientoJSON.getString("web");

                            JSONObject telefonos = establecimientoJSON.getJSONObject("telefonos");
                            JSONObject telefonosAtributos = telefonos.getJSONObject("@attributes");
                            int telefonosCantidad = telefonosAtributos.getInt("cantidad");


                            if(telefonosCantidad==0) {
                                telefono = "";
                                telefonosDeEstablecimientos.add(telefono);
                            }
                            else if(telefonosCantidad==1){
                                telefono = telefonos.getString("telefono");
                                telefonosDeEstablecimientos.add(telefono);
                            }else{
                                for (int i = 0; i < telefonos.getJSONArray("telefono").length(); i++) {
                                    String telefonoJSON = (String) telefonos.getJSONArray("telefono").get(i);
                                    telefonosDeEstablecimientos.add(telefonoJSON);
                                }
                            }

                            //JSONObject map = establecimientoJSON.getJSONObject("map");
                            JSONObject maps = establecimientoJSON.getJSONObject("maps");
                            JSONObject mapsAtributos = maps.getJSONObject("@attributes");
                            int mapsCantidad = mapsAtributos.getInt("cantidad");
                            int zoom =  mapsAtributos.getInt("zoom");

                            if(mapsCantidad==0){
                                latitude = ""; longitude=""; direccion="";
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            }
                            else if(mapsCantidad==1){
                                JSONObject map = maps.getJSONObject("map");
                                latitude = map.getString("latitude");
                                longitude = map.getString("longitude");
                                direccion = map.getString("direccion");
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            }else{
                                for (int i = 0; i < maps.getJSONArray("map").length(); i++) {
                                    JSONObject mapJSON = (JSONObject) maps.getJSONArray("map").get(i);
                                    latitude = mapJSON.getString("latitude");
                                    longitude = mapJSON.getString("longitude");
                                    direccion = mapJSON.getString("direccion");
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);

                                }
                            }

                            Establecimiento establecimiento = new Establecimiento(codigo, codigoRubro, titulo, resumen, detalle, filename, filenameLogo,
                                    horario, facebook, web,telefonosDeEstablecimientos, ubicaciones, zoom);


                            establecimientoList.add(establecimiento);
                        }


                    } else if (cantidad > 1) {
                        JSONArray establecimientos = lista.getJSONArray("establecimiento");

                        for (int i = 0; i < establecimientos.length(); i++) {
                            telefonosDeEstablecimientos = new ArrayList<>();
                            ubicaciones = new ArrayList<>();

                            JSONObject establecimientoJSON = (JSONObject) establecimientos
                                    .get(i);
                            codigo = establecimientoJSON.getString("codigo");
                            codigoRubro = establecimientoJSON.getString("categoriacodigo");

                            if (rubro == null || rubro.getCodigo().equals("0") ||
                                    rubro.getCodigo().equals(codigoRubro)) {



                                titulo = establecimientoJSON.getString("titulo");
                                resumen = establecimientoJSON.getString("resumen");
                                detalle = establecimientoJSON.getString("detalle");
                                filename = establecimientoJSON.getString("filename");
                                filenameLogo = establecimientoJSON.getString("filenamelogo");
                                horario = establecimientoJSON.getString("horario");
                                facebook = establecimientoJSON.getString("facebook");
                                web = establecimientoJSON.getString("web");

                                JSONObject telefonos = establecimientoJSON.getJSONObject("telefonos");
                                JSONObject telefonosAtributos = telefonos.getJSONObject("@attributes");
                                int telefonosCantidad = telefonosAtributos.getInt("cantidad");


                                if(telefonosCantidad==0){
                                    telefono = "";
                                    telefonosDeEstablecimientos.add(telefono);
                                }
                                else if(telefonosCantidad==1){
                                    telefono = telefonos.getString("telefono");
                                    telefonosDeEstablecimientos.add(telefono);
                                }else{
                                    for (int j = 0; j < telefonos.getJSONArray("telefono").length(); j++) {
                                        String telefonoJSON = (String) telefonos.getJSONArray("telefono").get(j);
                                        telefonosDeEstablecimientos.add(telefonoJSON);
                                    }
                                }

                                //JSONObject map = establecimientoJSON.getJSONObject("map");
                                JSONObject maps = establecimientoJSON.getJSONObject("maps");
                                JSONObject mapsAtributos = maps.getJSONObject("@attributes");
                                int mapsCantidad = mapsAtributos.getInt("cantidad");
                                int zoom =  mapsAtributos.getInt("zoom");

                                if(mapsCantidad==0){
                                    latitude =""; longitude=""; direccion="";
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);

                                }
                                else if(mapsCantidad==1){
                                    JSONObject map = maps.getJSONObject("map");
                                    latitude = map.getString("latitude");
                                    longitude = map.getString("longitude");
                                    direccion = map.getString("direccion");
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);
                                }else{
                                    for (int k = 0; k< maps.getJSONArray("map").length(); k++) {
                                        JSONObject mapJSON = (JSONObject) maps.getJSONArray("map").get(k);
                                        latitude = mapJSON.getString("latitude");
                                        longitude = mapJSON.getString("longitude");
                                        direccion = mapJSON.getString("direccion");
                                        Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                        ubicaciones.add(ubicacion);

                                    }
                                }

                                Establecimiento establecimiento = new Establecimiento(codigo, codigoRubro, titulo, resumen, detalle, filename, filenameLogo,
                                        horario, facebook, web,telefonosDeEstablecimientos, ubicaciones, zoom);



                                establecimientoList.add(establecimiento);

                            }

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

    private JsonObjectRequest getEstablecimientoPorRubro() {
        return new JsonObjectRequest(Request.Method.GET,
                url + ciudad.getCodigo(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject lista = response.getJSONObject("lista");
                    JSONObject atributos = lista.getJSONObject("@attributes");
                    int cantidad = atributos.getInt("cantidad");
                    String codigo = null;
                    String codigoRubro = null;
                    String titulo ="", resumen="", detalle="", filename="",
                            filenameLogo="", horario = "", facebook="", web="";
                    List<String> telefonosDeEstablecimientos = new ArrayList<>();
                    List<Ubicacion> ubicaciones = new ArrayList<>();
                    String telefono = "", latitude="", longitude="", direccion="";

                    if (cantidad == 1) {


                        JSONObject establecimientoJSON = (JSONObject) lista.getJSONObject("establecimiento");
                        codigo = establecimientoJSON.getString("codigo");
                        codigoRubro = establecimientoJSON.getString("categoriacodigo");


                        if (rubro == null || rubro.getCodigo().equals("0") ||
                                rubro.getCodigo().equals(codigoRubro)) {


                            titulo = establecimientoJSON.getString("titulo");
                            resumen = establecimientoJSON.getString("resumen");
                            detalle = establecimientoJSON.getString("detalle");
                            filename = establecimientoJSON.getString("filename");
                            filenameLogo = establecimientoJSON.getString("filenamelogo");
                            horario = establecimientoJSON.getString("horario");
                            facebook = establecimientoJSON.getString("facebook");
                            web = establecimientoJSON.getString("web");

                            JSONObject telefonos = establecimientoJSON.getJSONObject("telefonos");
                            JSONObject telefonosAtributos = telefonos.getJSONObject("@attributes");
                            int telefonosCantidad = telefonosAtributos.getInt("cantidad");

                            if(telefonosCantidad==0) {
                                telefono = "";
                                telefonosDeEstablecimientos.add(telefono);
                            }
                            else if(telefonosCantidad==1){
                                telefono = telefonos.getString("telefono");
                                telefonosDeEstablecimientos.add(telefono);
                            }else{
                                for (int i = 0; i < telefonos.getJSONArray("telefono").length(); i++) {
                                    String telefonoJSON = (String) telefonos.getJSONArray("telefono").get(i);
                                    telefonosDeEstablecimientos.add(telefonoJSON);
                                }
                            }

                            //JSONObject map = establecimientoJSON.getJSONObject("map");
                            JSONObject maps = establecimientoJSON.getJSONObject("maps");
                            JSONObject mapsAtributos = maps.getJSONObject("@attributes");
                            int mapsCantidad = mapsAtributos.getInt("cantidad");
                            int zoom =  mapsAtributos.getInt("zoom");

                            if(mapsCantidad==0){
                                latitude = ""; longitude=""; direccion="";
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            }
                            else if(mapsCantidad==1){
                                JSONObject map = maps.getJSONObject("map");
                                latitude = map.getString("latitude");
                                longitude = map.getString("longitude");
                                direccion = map.getString("direccion");
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            }else{
                                for (int i = 0; i < maps.getJSONArray("map").length(); i++) {
                                    JSONObject mapJSON = (JSONObject) maps.getJSONArray("map").get(i);
                                    latitude = mapJSON.getString("latitude");
                                    longitude = mapJSON.getString("longitude");
                                    direccion = mapJSON.getString("direccion");
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);

                                }
                            }

                            Establecimiento establecimiento = new Establecimiento(codigo, codigoRubro, titulo, resumen, detalle, filename, filenameLogo,
                                    horario, facebook, web,telefonosDeEstablecimientos, ubicaciones, zoom);


                            establecimientoList.add(establecimiento);
                        }


                    } else if (cantidad > 1) {
                        JSONArray establecimientos = lista.getJSONArray("establecimiento");

                        for (int i = 0; i < establecimientos.length(); i++) {
                            telefonosDeEstablecimientos = new ArrayList<>();
                            ubicaciones = new ArrayList<>();

                            JSONObject establecimientoJSON = (JSONObject) establecimientos
                                    .get(i);
                            codigo = establecimientoJSON.getString("codigo");
                            codigoRubro = establecimientoJSON.getString("categoriacodigo");

                            if (rubro == null || rubro.getCodigo().equals("0") ||
                                    rubro.getCodigo().equals(codigoRubro)) {



                                titulo = establecimientoJSON.getString("titulo");
                                resumen = establecimientoJSON.getString("resumen");
                                detalle = establecimientoJSON.getString("detalle");
                                filename = establecimientoJSON.getString("filename");
                                filenameLogo = establecimientoJSON.getString("filenamelogo");
                                horario = establecimientoJSON.getString("horario");
                                facebook = establecimientoJSON.getString("facebook");
                                web = establecimientoJSON.getString("web");

                                JSONObject telefonos = establecimientoJSON.getJSONObject("telefonos");
                                JSONObject telefonosAtributos = telefonos.getJSONObject("@attributes");
                                int telefonosCantidad = telefonosAtributos.getInt("cantidad");

                                if(telefonosCantidad==0){
                                    telefono = "";
                                    telefonosDeEstablecimientos.add(telefono);
                                }
                                else if(telefonosCantidad==1){
                                    telefono = telefonos.getString("telefono");
                                    telefonosDeEstablecimientos.add(telefono);
                                }else{
                                    for (int j = 0; j < telefonos.getJSONArray("telefono").length(); j++) {
                                        String telefonoJSON = (String) telefonos.getJSONArray("telefono").get(j);
                                        telefonosDeEstablecimientos.add(telefonoJSON);
                                    }
                                }

                                //JSONObject map = establecimientoJSON.getJSONObject("map");
                                JSONObject maps = establecimientoJSON.getJSONObject("maps");
                                JSONObject mapsAtributos = maps.getJSONObject("@attributes");
                                int mapsCantidad = mapsAtributos.getInt("cantidad");
                                int zoom =  mapsAtributos.getInt("zoom");

                                if(mapsCantidad==0){
                                    latitude =""; longitude=""; direccion="";
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);

                                }
                                else if(mapsCantidad==1){
                                    JSONObject map = maps.getJSONObject("map");
                                    latitude = map.getString("latitude");
                                    longitude = map.getString("longitude");
                                    direccion = map.getString("direccion");
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);
                                }else{
                                    for (int k = 0; k< maps.getJSONArray("map").length(); k++) {
                                        JSONObject mapJSON = (JSONObject) maps.getJSONArray("map").get(k);
                                        latitude = mapJSON.getString("latitude");
                                        longitude = mapJSON.getString("longitude");
                                        direccion = mapJSON.getString("direccion");
                                        Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                        ubicaciones.add(ubicacion);

                                    }
                                }

                                Establecimiento establecimiento = new Establecimiento(codigo, codigoRubro, titulo, resumen, detalle, filename, filenameLogo,
                                        horario, facebook, web,telefonosDeEstablecimientos, ubicaciones, zoom);



                                establecimientoList.add(establecimiento);

                            }

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
