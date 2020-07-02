package byui.anderson.eventcapture;

import android.app.Activity;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.lang.ref.WeakReference;
import java.util.List;

public class PermissionChecker {

    private WeakReference<Activity> mainActivity;

////////////////////////////////////////////////////////////////////////////////////////////////////

    public PermissionChecker() {
    }

    public PermissionChecker(WeakReference<Activity> mainActivity) {
        this.mainActivity = mainActivity;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    public void checkPermission(String permission) {
        Dexter.withContext(mainActivity.get())
                .withPermission(permission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(mainActivity.get(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(mainActivity.get(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(mainActivity.get(), "Permission Needed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();
    }

    public void checkPermissions(String permission1, String permission2, String permission3) {
        Dexter.withContext(mainActivity.get())
                .withPermissions(permission1, permission2, permission3)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(mainActivity.get(), "Permissions Needed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();
    }
}
