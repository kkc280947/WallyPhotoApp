package r.c.wallyphotoapp.ui.photogallery;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.databinding.ItemPhotoBinding;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.PhotoModel;
import r.c.wallyphotoapp.ui.photodetails.PhotoDetailFragment;

import static r.c.wallyphotoapp.utils.WallyConstants.IMAGE_TRANSITION_TAG;
import static r.c.wallyphotoapp.utils.WallyConstants.TEXT_TRANSITION_TAG;

/**
 * A fragment for displaying a grid of images.
 */
public class GridPhotoAdapter extends RecyclerView.Adapter<GridPhotoAdapter.ImageViewHolder> {

    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;
    private List<PhotoModel> photoModelList = new ArrayList<>();

    public GridPhotoAdapter(Fragment fragment) {
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPhotoBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_photo, parent, false);
        return new ImageViewHolder(binding, requestManager, viewHolderListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.onBind(photoModelList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return photoModelList.size();
    }

    public void updateList(List<PhotoModel> photoModels) {
        photoModelList = photoModels;
        notifyDataSetChanged();
    }

    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    private class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onLoadCompleted(ImageView view, int position) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (PhotoGalleryActivity.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.
            PhotoGalleryActivity.currentPosition = position;
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

            ImageView transitioningView = view.findViewById(R.id.imageItemPhoto);
            TextView transitioningTextView = view.findViewById(R.id.textViewLabel);

            if (fragment.getFragmentManager() != null) {
                fragment.getFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true) // Optimize for shared element transition
                        .addSharedElement(transitioningView, transitioningView.getTransitionName())
                        .addSharedElement(transitioningTextView, transitioningTextView.getTransitionName())
                        .replace(R.id.container, PhotoDetailFragment.newInstance(position,photoModelList.get(position).getUrls().getRegular(),
                                photoModelList.get(position).getLinks().getDownload()), PhotoDetailFragment.class
                                .getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    /**
     * ViewHolder for the grid's images.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;
        private final ItemPhotoBinding binding;

        ImageViewHolder(ItemPhotoBinding itemPhotoBinding, RequestManager requestManager,
                        ViewHolderListener viewHolderListener) {
            super(itemPhotoBinding.getRoot());
            this.binding = itemPhotoBinding;
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            binding.itemContainer.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void onBind(PhotoModel photoModel, int adapterPosition) {
            setImage(adapterPosition,photoModel.getUrls().getRegular());
            // Set the string value of the image resource as the unique transition name for the view.
            binding.imageItemPhoto.setTransitionName(IMAGE_TRANSITION_TAG + adapterPosition);
            binding.textViewLabel.setTransitionName(TEXT_TRANSITION_TAG + adapterPosition);
            binding.textViewLabel.setText(String.format(Locale.getDefault(),"Image %d", (adapterPosition+1)));
        }

        void setImage(final int adapterPosition, String url) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(binding.imageItemPhoto, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(binding.imageItemPhoto, adapterPosition);
                            return false;
                        }
                    })
                    .into(binding.imageItemPhoto);
        }

        @Override
        public void onClick(View view) {
            viewHolderListener.onItemClicked(view, getAdapterPosition());
        }
    }
}