package com.semisky.multimedia.common.base_view;

import android.app.Activity;
import android.os.Bundle;

import com.semisky.multimedia.common.base_presenter.BasePresenter;


public abstract class BaseActivity<V, P extends BasePresenter<V>> extends Activity {

	protected P mPresenter;

	protected abstract P createPresenter();

	protected boolean isBindPresenter() {
		return (null != this.mPresenter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPresenter = createPresenter();
		mPresenter.onAttachView((V) this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPresenter.onDetachView();
	}

}
