package r.c.wallyphotoapp.base;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;

/***
 * Example;
 *
 * public class TestFragment extends BaseCallbackFragment<TestViewModel, ITestActivityCallback>
 *
 * Where the parent activity to TestFragment implements the interface ITestActivityCallback.
 *
 *
 * @param <T> A ViewModel which inherits from ViewModel with an injected constructor.
 * @param <U> An interface of an activity to callback to.
 */
public class BaseCallbackFragment<T extends ViewModel, U> extends BaseFragment<T> {

	private WeakReference<U> mActivityCallback;

	protected void initActivityCallback(Class<U> cls) {
		Context context = getContext();

		if(cls.isInstance(context)) {
			mActivityCallback = new WeakReference<>((U) context);
		} else {
			throw new RuntimeException("Error: initCallback() - Could not initialise callback.");
		}
	}

	protected U getActivityCallback() {
		U activityCallback = mActivityCallback.get();
		if(activityCallback != null) {
			return activityCallback;
		} else {
			Log.d("Error:","getActivityCallback() ActivityCallback was null.");
			return null;
		}
	}
}