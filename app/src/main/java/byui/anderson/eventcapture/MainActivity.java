package byui.anderson.eventcapture;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private PermissionChecker permissionChecker;

////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

////////////////////////////////////////////////////////////////////////////////////////////////////

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setNavigationBarColor(Color.WHITE);

        permissionChecker = new PermissionChecker(new WeakReference<Activity>(MainActivity.this));

        permissionChecker.checkPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Navigation.findNavController(findViewById(R.id.nav_host_fragment))
                        .navigate(R.id.open_settings_fragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}