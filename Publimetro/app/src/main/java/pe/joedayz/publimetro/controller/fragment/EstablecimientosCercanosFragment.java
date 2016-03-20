package pe.joedayz.publimetro.controller.fragment;


import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Ciudad;
import pe.joedayz.publimetro.model.Establecimiento;
import pe.joedayz.publimetro.model.MarkerObject;
import pe.joedayz.publimetro.model.Rubro;
import pe.joedayz.publimetro.model.Ubicacion;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
/**
 * Created by Celeritech Peru on 01/11/2015.
 */
public class EstablecimientosCercanosFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    //LogCat tag
    private static final String TAG = EstablecimientosCercanosFragment.class.getSimpleName();

    private static final String url = "http://www.publiguiaperu.com/servicioweb/servicioWeb2.0.php?token=000&method=getEstablecimientos&idUbicacion=";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;

    //Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;


    //Map
    MapView mapView;
    private GoogleMap mapa;

    private Ciudad ciudad;
    private Rubro rubro;
    private ProgressDialog pDialog;

    HashMap<String, MarkerObject> markersAndEstablecimientos = new HashMap<String, MarkerObject>();

    private List<Establecimiento> establecimientoList = new ArrayList<Establecimiento>();


    public EstablecimientosCercanosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.establecimientos_cercanos_fragment, container, false);

        ciudad = (Ciudad) getActivity().getIntent().getSerializableExtra("ciudadSeleccionada");

        rubro = (Rubro) getActivity().getIntent().getSerializableExtra("rubro");


        MapsInitializer.initialize(getActivity());

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }


        mapView = (MapView) rootView.findViewById(R.id.mapa_establecimientos_cercanos);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        if (mapView != null) {
            mapa = mapView.getMap();
            mapa.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            mapa.getUiSettings().setMyLocationButtonEnabled(false);
            mapa.setMyLocationEnabled(true);

            mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i("Ir a Establecimiento: ", markersAndEstablecimientos.get(marker.getId()).getEstablecimiento().getTitulo());

                    Fragment fragment = new DetalleEstablecimientoFragment();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    getActivity().getIntent().putExtra("establecimiento", markersAndEstablecimientos.get(marker.getId()).getEstablecimiento());

                    fragment.setArguments(getActivity().getIntent().getExtras());

                }


            });

        }


        return rootView;
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
                    String titulo = "", resumen = "", detalle = "", filename = "",
                            filenameLogo = "", horario = "", facebook = "", web = "";
                    List<String> telefonosDeEstablecimientos = new ArrayList<>();
                    List<Ubicacion> ubicaciones = new ArrayList<>();
                    String telefono = "", latitude = "", longitude = "", direccion = "";

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

                            if (telefonosCantidad == 0) {
                                telefono = "";
                                telefonosDeEstablecimientos.add(telefono);
                            } else if (telefonosCantidad == 1) {
                                telefono = telefonos.getString("telefono");
                                telefonosDeEstablecimientos.add(telefono);
                            } else {
                                for (int i = 0; i < telefonos.getJSONArray("telefono").length(); i++) {
                                    String telefonoJSON = (String) telefonos.getJSONArray("telefono").get(i);
                                    telefonosDeEstablecimientos.add(telefonoJSON);
                                }
                            }

                            //JSONObject map = establecimientoJSON.getJSONObject("map");
                            JSONObject maps = establecimientoJSON.getJSONObject("maps");
                            JSONObject mapsAtributos = maps.getJSONObject("@attributes");
                            int mapsCantidad = mapsAtributos.getInt("cantidad");
                            int zoom = mapsAtributos.getInt("zoom");

                            if (mapsCantidad == 0) {
                                latitude = "";
                                longitude = "";
                                direccion = "";
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            } else if (mapsCantidad == 1) {
                                JSONObject map = maps.getJSONObject("map");
                                latitude = map.getString("latitude");
                                longitude = map.getString("longitude");
                                direccion = map.getString("direccion");
                                Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                ubicaciones.add(ubicacion);
                            } else {
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
                                    horario, facebook, web, telefonosDeEstablecimientos, ubicaciones, zoom);


                            for (Ubicacion ubicacion : establecimiento.getUbicaciones()) {
                                latitude = ubicacion.getLatitude();
                                longitude = ubicacion.getLongitude();
                                LatLng locationEstablecimiento = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                Marker marker = mapa.addMarker(new MarkerOptions()
                                        .position(locationEstablecimiento)
                                        .title(establecimiento.getTitulo())
                                        .alpha(0.7f));

                                MarkerObject markerObject = new MarkerObject(establecimiento, ubicacion);

                                markersAndEstablecimientos.put(marker.getId(), markerObject);
                            }


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



                                if (telefonosCantidad == 0) {
                                    telefono = "";
                                    telefonosDeEstablecimientos.add(telefono);
                                } else if (telefonosCantidad == 1) {
                                    telefono = telefonos.getString("telefono");
                                    telefonosDeEstablecimientos.add(telefono);
                                } else {
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

                                if (mapsCantidad == 0) {
                                    latitude = "";
                                    longitude = "";
                                    direccion = "";
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);

                                } else if (mapsCantidad == 1) {
                                    JSONObject map = maps.getJSONObject("map");
                                    latitude = map.getString("latitude");
                                    longitude = map.getString("longitude");
                                    direccion = map.getString("direccion");
                                    Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                    ubicaciones.add(ubicacion);
                                } else {
                                    for (int k = 0; k < maps.getJSONArray("map").length(); k++) {
                                        JSONObject mapJSON = (JSONObject) maps.getJSONArray("map").get(k);
                                        latitude = mapJSON.getString("latitude");
                                        longitude = mapJSON.getString("longitude");
                                        direccion = mapJSON.getString("direccion");
                                        Ubicacion ubicacion = new Ubicacion(latitude, longitude, direccion);
                                        ubicaciones.add(ubicacion);

                                    }
                                }

                                Establecimiento establecimiento = new Establecimiento(codigo, codigoRubro, titulo, resumen, detalle, filename, filenameLogo,
                                        horario, facebook, web, telefonosDeEstablecimientos, ubicaciones, zoom);


                                for (Ubicacion ubicacion : establecimiento.getUbicaciones()) {
                                    latitude = ubicacion.getLatitude();
                                    longitude = ubicacion.getLongitude();
                                    LatLng locationEstablecimiento = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                    Marker marker = mapa.addMarker(new MarkerOptions()
                                            .position(locationEstablecimiento)
                                            .title(establecimiento.getTitulo())
                                            .alpha(0.7f));

                                    MarkerObject markerObject = new MarkerObject(establecimiento, ubicacion);

                                    markersAndEstablecimientos.put(marker.getId(), markerObject);
                                }

                            }

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }


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

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(), "Este equipo no esta soportado.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        checkPlayServices();


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        hidePDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLastLocation == null) {

            mapa = mapView.getMap();
            mapa.getUiSettings().setMyLocationButtonEnabled(false);
            mapa.setMyLocationEnabled(true);

            LatLng location = new LatLng(Double.parseDouble(ciudad.getLatitude()),
                    Double.parseDouble(ciudad.getLongitud()));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17);
            mapa.animateCamera(cameraUpdate);


            mostrarMarcasDeEstablecimientos();

            Toast.makeText(getActivity(),
                    "Para esta funcionalidad es necesario activar el GPS.",
                    Toast.LENGTH_LONG).show();

        } else {
            handleNewLocation(mLastLocation);
        }


    }


    private void handleNewLocation(Location location) {

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        LatLng locationUser = new LatLng(latitude, longitude);

        mostrarMarcasDeEstablecimientos();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(locationUser, 17);
        mapa.animateCamera(cameraUpdate);

    }

    private void mostrarMarcasDeEstablecimientos() {

        pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Cargando Establecimientos...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = getEstablecimientoPorRubro();

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode()= " + connectionResult.getErrorCode());
    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        boolean not_first_time_showing_info_window;

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null);

            MarkerObject markerObject = markersAndEstablecimientos.get(marker.getId());

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);


            if (markerObject.getEstablecimiento().getFileNameLogo() != null) {
                Picasso.with(getActivity())
                        .load(markerObject.getEstablecimiento().getFileNameLogo())
                        .placeholder(R.drawable.ic_launcher)
                        .into(markerIcon, new InfoWindowRefresher(marker));
            }

            return v;
        }
    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }

        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }
    }

}