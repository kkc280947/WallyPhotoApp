package r.c.wallyphotoapp.di.modules;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import r.c.wallyphotoapp.WallyApplication;

import javax.inject.Singleton;

@Module
public class AppModule {

	private final WallyApplication mWallyApp;

	public AppModule(WallyApplication wallyApp) {
		mWallyApp = wallyApp;
	}

	@Singleton
	@Provides
	public Application provideApplicationContext() {
		return mWallyApp;
	}
}
