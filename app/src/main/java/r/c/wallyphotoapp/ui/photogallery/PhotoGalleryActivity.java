package r.c.wallyphotoapp.ui.photogallery;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseActivity;
import r.c.wallyphotoapp.service.ImageDownloadService;
import r.c.wallyphotoapp.ui.photodetails.PhotoDetailFragment;
import r.c.wallyphotoapp.ui.profile.ProfileFragment;

import static r.c.wallyphotoapp.service.ImageDownloadService.KEY_FILE_NAME;
import static r.c.wallyphotoapp.service.ImageDownloadService.KEY_URL;

public class PhotoGalleryActivity extends BaseActivity implements IPhotoActivityCallback {
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .add(getContainerViewId(), new PhotoGalleryFragment(), PhotoGalleryFragment.class.getSimpleName())
                .commit();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    @Override
    public int getContainerViewId() {
        return R.id.container;
    }

    @Override
    public void moveToProfile() {
        getSupportFragmentManager().beginTransaction()
                .replace(getContainerViewId(), new ProfileFragment(), ProfileFragment.class.getSimpleName())
                .addToBackStack(ProfileFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void downloadImage(String url, String name) {
        Intent intent = new Intent(this, ImageDownloadService.class)
                .putExtra(KEY_URL, url)
                .putExtra(KEY_FILE_NAME, name);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoDetailFragment fragment = (PhotoDetailFragment) getSupportFragmentManager().findFragmentByTag(PhotoDetailFragment.class
                .getSimpleName());
        if(fragment!=null){
            fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
