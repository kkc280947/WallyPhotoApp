package r.c.wallyphotoapp.ui.photogallery;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import r.c.wallyphotoapp.repository.ImageFileRepository;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.ListResult;

public class PhotoGalleryViewModel extends ViewModel {

    @Inject
    public PhotoGalleryViewModel(){

    }

    @Inject
    ImageFileRepository imageFileRepository;

    private MutableLiveData<ListResult> photoLiveData = new MutableLiveData<>();

    MutableLiveData<ListResult> getPhotosList(){
        if(photoLiveData.getValue() == null || photoLiveData.getValue().getPhotoModelList().isEmpty() ){
            new Thread(() -> {
                   ListResult result = imageFileRepository.getImages();
                   photoLiveData.postValue(result);
            }).start();
        }
        return photoLiveData;
    }
}
