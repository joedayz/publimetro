package pe.joedayz.publimetro.controller.fragment;


import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Ciudad;
import pe.joedayz.publimetro.model.Establecimiento;
import pe.joedayz.publimetro.model.MarkerObject;
import pe.joedayz.publimetro.model.Ubicacion;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleMapaFragment extends Fragment
            implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = DetalleMapaFragment.class.getSimpleName();

    private Establecimiento establecimiento;
    private Ciudad ciudad;

    //Mapa y markers

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog pDialog;

    MapView mapView;
    private GoogleMap mapa;

    //Markers
    HashMap<String, MarkerObject> markersAndEstablecimientos = new HashMap<String, MarkerObject>();
    List<Marker> markersList = new ArrayList<>();

    public DetalleMapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detalle_mapa_fragment, container, false);

        establecimiento = (Establecimiento) getActivity().getIntent().getSerializableExtra("establecimiento");
        ciudad = (Ciudad) getActivity().getIntent().getSerializableExtra("ciudadSeleccionada");


        MapsInitializer.initialize(getActivity());

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }


        mapView = (MapView) rootView.findViewById(R.id.mapa_detalle_establecimiento);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        if (mapView != null) {
            mapa = mapView.getMap();
            mapa.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            mapa.getUiSettings().setMyLocationButtonEnabled(false);
            mapa.setMyLocationEnabled(true);
        }


        return rootView;
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


    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {

            mapa = mapView.getMap();
            mapa.getUiSettings().setMyLocationButtonEnabled(false);
            mapa.setMyLocationEnabled(true);

            mostrarMarcasDeUbicacionesDeEstablecimiento();

            centerBetweenMarkers();


        } else {
            handleNewLocation(mLastLocation);
        }
    }

    private void handleNewLocation(Location location) {

        mostrarMarcasDeUbicacionesDeEstablecimiento();

        centerBetweenMarkers();
    }

    private void centerBetweenMarkers() {

        if(markersList.size()==1){

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(markersList.get(0).getPosition(), 17);
            mapa.animateCamera(cu);

        }else{

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(Marker m : markersList) {
                builder.include(m.getPosition());
            }

            LatLngBounds bounds = builder.build();

            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(establecimiento.getZoom());


            mapa.moveCamera(cu);
            mapa.animateCamera(zoom);
        }

    }


    private void mostrarMarcasDeUbicacionesDeEstablecimiento() {

        pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Cargando Ubicaciones...");
        pDialog.show();

        List<Ubicacion> ubicaciones = establecimiento.getUbicaciones();

        Double latitude, longitude;

        for (Ubicacion ubicacion : ubicaciones) {
            latitude = Double.parseDouble(ubicacion.getLatitude());
            longitude = Double.parseDouble(ubicacion.getLongitude());
            LatLng locationEstablecimiento = new LatLng(latitude, longitude);

            Marker marker = mapa.addMarker(new MarkerOptions()
                    .position(locationEstablecimiento)
                    .title(establecimiento.getTitulo())
                    .alpha(0.7f));

            markersList.add(marker);

            MarkerObject markerObject = new MarkerObject(establecimiento, ubicacion);

            markersAndEstablecimientos.put(marker.getId(), markerObject);
        }


        hidePDialog();

    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode()= " + connectionResult.getErrorCode());
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
            View v = getActivity().getLayoutInflater().inflate(R.layout.infowindow_only_address_layout, null);

            MarkerObject markerObject = markersAndEstablecimientos.get(marker.getId());

            TextView address = (TextView) v.findViewById(R.id.establecimiento_address);
            address.setText(markerObject.getUbicacion().getDireccion());
            return v;
        }
    }

}
