package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.example.notes.model.Task;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;


public class SearchActivity extends Activity implements Session.SearchListener, CameraListener {

    private MapView mapView;
    private EditText searchEdit;
    private SearchManager searchManager;
    private Session searchSession;
    private static boolean initialized = false;

    private void submitQuery(String query) {
        searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!initialized) {
            MapKitFactory.setApiKey(CONSTANTS.API_KEYS.YANDEX_MAP_KIT);
            initialized = true;
        }
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);

        setContentView(R.layout.activity_location_view);
        super.onCreate(savedInstanceState);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().move(
                new CameraPosition(new Point(56.0153, 92.8932), 14.0f, 0.0f, 0.0f));

        searchEdit = findViewById(R.id.search_edit);

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitQuery(searchEdit.getText().toString());
                }

                return false;
            }
        });
        submitQuery(searchEdit.getText().toString());
    }

    @Override
    protected void onResume() {
        load_task();
        super.onResume();
    }

    @Override
    protected void onStop() {
        update_task();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onBackPressed() {
        update_task();
        super.onBackPressed();
    }

    public void load_task() {
        StandardDatabaseHelper dbh = new StandardDatabaseHelper(this);
        Task task = dbh.getTask(CONSTANTS.CACHE.getTaskId());
        searchEdit.setText(task.getLocation());
    }

    public void update_task(){
        StandardDatabaseHelper dbh = new StandardDatabaseHelper(this);
        Task task = dbh.getTask(CONSTANTS.CACHE.getTaskId());
        String new_location = searchEdit.getText().toString().trim();
        dbh.updateTask(
                String.valueOf(task.getId()), task.getName(), task.getDate(), task.getDeadline(),
                task.getCategory_id(), task.getSubject_id(), task.getStatus_id(),
                new_location, task.getComment());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finishActivity(CONSTANTS.ACTIVITY_RESULT.LOCATION_UPDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(this, R.drawable.search_result));
            }
        }
    }

    @Override
    public void onSearchError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(Map map, CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason, boolean finished) {
        if (finished) {
            submitQuery(searchEdit.getText().toString());
        }
    }
}


