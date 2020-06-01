package r.c.wallyphotoapp.base;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

/**
*  ViewModelFactory class maps viewmodel classes declared in ViewModelModule to
 *  generate dagger classes.
* */
public class ViewModelFactory implements ViewModelProvider.Factory {

	private final Map<Class<? extends ViewModel>, Provider<ViewModel>> mCreators;

	@Inject
	public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
		mCreators = creators;
	}

	@NonNull
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		Provider<? extends ViewModel> creator = mCreators.get(modelClass);
		if (creator == null) {
			for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : mCreators.entrySet()) {
				if (modelClass.isAssignableFrom(entry.getKey())) {
					creator = entry.getValue();
					break;
				}
			}
		}

		if (creator != null) {
			try {
				return (T) creator.get();
			} catch (Exception e) {
				Log.e(getClass().toString(),e.toString());
				throw new RuntimeException(e);
			}
		}

		Log.e("Unknown ViewModel - %s", modelClass.getName());
		throw new IllegalArgumentException("Unknown ViewModel - " + modelClass);
	}
}
