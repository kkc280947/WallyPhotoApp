package r.c.wallyphotoapp.base;

import android.app.Activity;
import android.app.Application;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import javax.inject.Inject;

import r.c.wallyphotoapp.WallyApplication;
import r.c.wallyphotoapp.di.components.AppComponent;

/***
 * Example;
 *
 * public class TestFragment extends BaseFragment<TestViewModel>
 *
 * @param <T> A ViewModel which inherits from ViewModel with an injected constructor.
 */
public abstract class BaseFragment<T extends ViewModel> extends Fragment {

	protected @Inject
	ViewModelFactory mViewModelFactory;

	private T mViewModel;

	protected void initViewModel(Class<T> cls) {
		FragmentActivity activity = getActivity();
		if (activity == null) {
			throw new RuntimeException(
					"Error: initViewModel() - Cannot create view model with null Activity.");
		}

		if (mViewModelFactory != null) {
			mViewModel = ViewModelProviders.of(activity, mViewModelFactory).get(cls);
		} else {
			throw new RuntimeException(
					"Error: initViewModel() - Fragment not injected using dagger. Be sure to call "
							+ "getMAppComponentKt().inject(Fragment.this) before initialising the view model.");
		}
	}

	protected AppComponent getAppComponent() {
		Activity activity = getActivity();
		if (activity != null) {
			Application app = activity.getApplication();
			if (app instanceof WallyApplication) {
				return ((WallyApplication) app).getAppComponent();
			}
		}

		throw new RuntimeException(
				"Error: getAppComponent() - Could not locate AppComponent.");
	}

	protected T getViewModel() {
		if (mViewModel != null) {
			return mViewModel;
		} else {
			throw new RuntimeException(
					"Error: getViewModel() - ViewModel not initialised please called initViewModel<T>.");
		}
	}

	protected void finishActivity() {
		Activity activity = getActivity();
		if (activity != null) {
			activity.finish();
		}
	}

}
