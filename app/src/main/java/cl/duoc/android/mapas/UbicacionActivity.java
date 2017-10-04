package cl.duoc.android.mapas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class UbicacionActivity extends AppCompatActivity {

    public static final int COD_PETICION_PERMISOS = 407;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        String[] permisos = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        // pedir permisos peligrosos explícitamente desde Android 6.0+
        // WRITE_EXTERNAL_STORAGE and ACCESS_COARSE_LOCATION/ACCESS_FINE_LOCATION
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, permisos, COD_PETICION_PERMISOS);
        } else {
            cargarMapa();
        }
    }

    /**
     * reviso los permisos solicitados
     * en caso de ser concedidos cargo el mapa
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case COD_PETICION_PERMISOS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    cargarMapa();
                } else {
                    Toast.makeText(this, "No se puede mostrar el mapa sin los permisos", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void cargarMapa() {
        // carga el mapa
        // instrucciones para no ser baneado de OSM
        Context applicationContext = getApplicationContext();
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext));
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // agrega controles de zoom
        mapView.setBuiltInZoomControls(true);
        // permite zoom con 2 dedos
        mapView.setMultiTouchControls(true);

        // setea zoom inicial de 20
        IMapController mapController = mapView.getController();
        mapController.setZoom(20);

        // configura donde se mostrará el mapa
        // metro las mercedes -33.6027522,-70.5778938
        double latitud = -33.6027522;
        double longitud = -70.5778938;
        GeoPoint geoPoint = new GeoPoint(latitud, longitud);
        mapController.setCenter(geoPoint);

        // marcador sobre la direccion
        List<OverlayItem> overlayItems = new ArrayList<>();
        String titulo       = "Metro Las Mercedes";
        String descripcion  = "Línea 4 - Metro Las Mercedes";
        overlayItems.add(new OverlayItem(titulo, descripcion, geoPoint));
        ItemizedOverlayWithFocus<OverlayItem> itemizedOverlayWithFocus = new ItemizedOverlayWithFocus<>(overlayItems, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        }, mapView.getContext());
        itemizedOverlayWithFocus.setFocusItemsOnTap(true);
        mapView.getOverlays().add(itemizedOverlayWithFocus);
    }
}
