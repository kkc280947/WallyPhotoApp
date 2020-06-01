package r.c.wallyphotoapp.ui.photodetails;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseCallbackFragment;
import r.c.wallyphotoapp.databinding.FragmentPhotoDetailBinding;
import r.c.wallyphotoapp.ui.photogallery.IPhotoActivityCallback;
import r.c.wallyphotoapp.ui.photogallery.PhotoGalleryViewModel;
import r.c.wallyphotoapp.utils.WallyConstants;

import static r.c.wallyphotoapp.utils.CommonAppUtils.isNetworkConnected;
import static r.c.wallyphotoapp.utils.CommonAppUtils.showInternetError;
import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_DOWNLOAD;
import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_FAIL;
import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_UPDATE;
import static r.c.wallyphotoapp.utils.WallyConstants.KEY_PROGRESS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoDetailFragment extends BaseCallbackFragment<PhotoGalleryViewModel, IPhotoActivityCallback> {

    private static final String KEY_TRANSITION_IDENTIFIER = "transition_key";
    private static final String KEY_DOWNLOAD_URL = "download_url";
    private static final String KEY_URL = "url";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private int transitionKey;
    private String url;
    private FragmentPhotoDetailBinding binding;
    private FileDownloadReceiver mFileDownloadReceiver;
    private boolean isImageDownloaded = false;

    public PhotoDetailFragment() {
        // Required empty public constructor
    }

    public static PhotoDetailFragment newInstance(int key, String url, String downloadURL) {
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TRANSITION_IDENTIFIER, key);
        args.putString(KEY_URL, url);
        args.putString(KEY_DOWNLOAD_URL, downloadURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transitionKey = getArguments().getInt(KEY_TRANSITION_IDENTIFIER);
            url = getArguments().getString(KEY_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_detail, container, false);
        initActivityCallback(IPhotoActivityCallback.class);
        registerDatabaseUpdateReceiver();
        init(savedInstanceState);
        setUpListeners();
        return binding.getRoot();
    }

    private void init(Bundle savedInstanceState) {
        loadImageSharedAnimation();
        prepareSharedElementTransition();
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        startTextAnimation();
    }

    private void setUpListeners() {
        binding.textViewDownload.setOnClickListener(v -> {
            if (checkPermission()) {
                checkForFileDownload();
            } else {
                requestPermission();
            }
        });
        binding.imageBackArrow.setOnClickListener(v -> goBack());
    }

    private void checkForFileDownload() {
        Context context = getContext();
        if (context != null) {
            if (isNetworkConnected(context)) {
                downloadImage();
            } else {
                showInternetError(context, (dialog, which) -> {
                    dialog.dismiss();
                });
            }
        }
    }

    private void loadImageSharedAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.imageViewDetail.setTransitionName(WallyConstants.IMAGE_TRANSITION_TAG + transitionKey);
            binding.textViewName.setTransitionName(WallyConstants.TEXT_TRANSITION_TAG + transitionKey);
        }
        binding.textViewName.setText(String.format(Locale.getDefault(), "Image %d", (transitionKey + 1)));
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                            target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.imageViewDetail);
    }

    private void startTextAnimation() {
        ValueAnimator animation = ValueAnimator.ofFloat(Objects.requireNonNull(getActivity()).getWindowManager()
                .getDefaultDisplay().getHeight(), binding.textOne.getY());
        animation.setDuration(1000);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        animation.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            binding.textOne.setTranslationY(animatedValue);
            binding.textTwo.setTranslationX(animatedValue);
        });
        animation.start();

        ValueAnimator arrowAnimator = ValueAnimator.ofFloat(0, 1);
        arrowAnimator.setDuration(1000);
        arrowAnimator.setStartDelay(600);
        arrowAnimator.setInterpolator(new FastOutSlowInInterpolator());
        arrowAnimator.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            binding.imageBackArrow.setAlpha(animatedValue);
        });
        arrowAnimator.start();
    }

    /**
     * Prepares the shared element transition from and back to the fragment with grids.
     */
    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        if (getView() == null) {
                            return;
                        }
                        sharedElements.put(names.get(0), binding.imageViewDetail);
                        sharedElements.put(names.get(1), binding.textViewName);
                    }
                });
    }

    private void downloadImage() {
        if (!isImageDownloaded) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                isImageDownloaded = true;
                getActivityCallback().downloadImage(bundle.getString(KEY_DOWNLOAD_URL),
                        binding.textViewName.getText().toString().trim());
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.text_file_already_downloaded),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void registerDatabaseUpdateReceiver() {
        mFileDownloadReceiver = new FileDownloadReceiver();
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_DOWNLOAD);
            intentFilter.addAction(ACTION_UPDATE);
            Activity activity = getActivity();
            if (activity != null) {
                getActivity().registerReceiver(mFileDownloadReceiver, intentFilter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class FileDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (ACTION_DOWNLOAD.equals(intent.getAction())) {
                    isImageDownloaded = true;
                    Toast.makeText(context, getString(R.string.notification_text_download_complete), Toast.LENGTH_SHORT).show();
                } else if (ACTION_UPDATE.equals(intent.getAction())) {
                    isImageDownloaded = true;
                    updateProgress(intent.getIntExtra(KEY_PROGRESS, 0));
                } else if (ACTION_FAIL.equals(intent.getAction())) {
                    isImageDownloaded = false;
                    resetDownloadState();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void resetDownloadState() {
        binding.textViewDownload.setText(getString(R.string.download));
    }

    private void updateProgress(int progress) {
        binding.textViewDownload.setText(String.format(Locale.getDefault(), "%d%%", progress));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Activity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(mFileDownloadReceiver);
        }
    }

    private boolean checkPermission() {
        Context context = getContext();
        if(context!=null){
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermission() {
        Activity activity = getActivity();
        if(activity!=null){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForFileDownload();
            } else {
                Toast.makeText(getContext(), getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
