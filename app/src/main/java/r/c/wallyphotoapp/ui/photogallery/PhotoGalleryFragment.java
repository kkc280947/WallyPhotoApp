package r.c.wallyphotoapp.ui.photogallery;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseCallbackFragment;
import r.c.wallyphotoapp.databinding.PhotoGalleryFragmentBinding;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.PhotoModel;
import static r.c.wallyphotoapp.utils.CommonAppUtils.isNetworkConnected;
import static r.c.wallyphotoapp.utils.CommonAppUtils.showInternetError;

public class PhotoGalleryFragment extends BaseCallbackFragment<PhotoGalleryViewModel, IPhotoActivityCallback> {

    private RecyclerView recyclerViewPhotos;
    private GridPhotoAdapter gridAdapter;
    private PhotoGalleryFragmentBinding binding;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        initViewModel(PhotoGalleryViewModel.class);
        initActivityCallback(IPhotoActivityCallback.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater,R.layout.photo_gallery_fragment, container, false);
            init();
            loadData();
            setListeners();
            loadAnimations(savedInstanceState);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    private void init() {
        recyclerViewPhotos = binding.recyclerView;
        setUpRecycler(recyclerViewPhotos);
    }

    private void setUpRecycler(RecyclerView recyclerPhotos) {
        gridAdapter = new GridPhotoAdapter(this);
        recyclerPhotos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerPhotos.setAdapter(gridAdapter);
    }

    private void loadData() {
        if(gridAdapter.getItemCount() ==0){
            observePhotoList();
            getPhotoList();
        }
    }

    private void setListeners() {
        binding.imageProfile.setOnClickListener(v -> getActivityCallback().moveToProfile());
        binding.buttonRetry.setOnClickListener(v -> getPhotoList());
    }

    private void loadAnimations(Bundle savedInstanceState){
        prepareTransitions();
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
    }

    private void observePhotoList() {
        getViewModel().getPhotosList().observe(getViewLifecycleOwner(), listResult -> {
            binding.progressGallery.setVisibility(View.GONE);
            List<PhotoModel> photoModelList = listResult.getPhotoModelList();
            if(!photoModelList.isEmpty()){
                hideError(true);
                binding.recyclerView.setVisibility(View.VISIBLE);
                gridAdapter.updateList(photoModelList);
            }

            if(listResult.getError() != null){
                hideError(false);
                binding.textError.setText(listResult.getError());
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void hideError(boolean hide){
        binding.textError.setVisibility(hide ? View.GONE :View.VISIBLE);
        binding.buttonRetry.setVisibility(hide ? View.GONE :View.VISIBLE);
    }

    private void getPhotoList(){
        Context context = getContext();
        if(context!=null){
            if(isNetworkConnected(getContext())){
                binding.progressGallery.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                hideError(true);
                getViewModel().getPhotosList();
            }else {
                showInternetError(context, (dialog, which) -> {
                    dialog.dismiss();
                });
            }
        }
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow.
     */
    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.item_grid_exit_transition));
        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = recyclerViewPhotos
                                .findViewHolderForAdapterPosition(PhotoGalleryActivity.currentPosition);
                        if (selectedViewHolder == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.imageItemPhoto));
                        sharedElements
                                .put(names.get(1), selectedViewHolder.itemView.findViewById(R.id.textViewLabel));
                    }
                });
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private void scrollToPosition() {

        recyclerViewPhotos.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                recyclerViewPhotos.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerViewPhotos.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(PhotoGalleryActivity.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerViewPhotos.post(() -> layoutManager.scrollToPosition(PhotoGalleryActivity.currentPosition));
                }
            }
        });
    }
}
