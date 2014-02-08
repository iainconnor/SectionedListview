package com.iainconnor.sectionedlistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import uk.co.chrisjenx.paralloid.ParallaxViewController;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.Transformer;

public class ParallaxSectionedListView extends SectionedListView implements Parallaxor {
	ParallaxViewController parallaxViewController;

	public ParallaxSectionedListView ( Context context ) {
		super(context);
		setupParallax();
	}

	public ParallaxSectionedListView ( Context context, AttributeSet attrs ) {
		super(context, attrs);
		setupParallax();
	}

	public ParallaxSectionedListView ( Context context, AttributeSet attrs, int defStyle ) {
		super(context, attrs, defStyle);
		setupParallax();
	}

	protected void setupParallax () {
		parallaxViewController = ParallaxViewController.wrap(this);
	}

	@Override
	public void parallaxViewBy ( View view, float v ) {
		parallaxViewController.parallaxViewBy(view, v);
	}

	@Override
	public void parallaxViewBy ( View view, Transformer transformer, float v ) {
		parallaxViewController.parallaxViewBy(view, transformer, v);
	}

	@Override
	public void parallaxViewBackgroundBy ( View view, Drawable drawable, float v ) {
		parallaxViewController.parallaxViewBackgroundBy(view, drawable, v);
	}
}