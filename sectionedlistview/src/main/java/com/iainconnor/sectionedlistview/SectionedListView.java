package com.iainconnor.sectionedlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SectionedListView extends ListView implements AbsListView.OnScrollListener {
	protected OnScrollListener subclassOnScrollListener;
	protected SectionedAdapter sectionedAdapter;
	protected View floatingHeader;
	protected int floatingHeaderViewType;
	protected float floatingHeaderOffset;
	protected int floatingHeaderSection;
	protected boolean pinHeaders = true;
	protected int widthMode;
	protected int heightMode;
	protected View touchTarget;
	protected MotionEvent touchDownEvent;
	protected float touchDownX = -1;
	protected float touchDownY = -1;
	protected int maxMoveDistanceForTouch;

	public SectionedListView ( Context context ) {
		super(context);
		setup();
	}

	public SectionedListView ( Context context, AttributeSet attrs ) {
		super(context, attrs);
		setup();
	}

	public SectionedListView ( Context context, AttributeSet attrs, int defStyle ) {
		super(context, attrs, defStyle);
		setup();
	}

	public boolean isPinHeaders () {
		return pinHeaders;
	}

	public void setPinHeaders ( boolean pinHeaders ) {
		this.pinHeaders = pinHeaders;
	}

	@Override
	public void setAdapter ( ListAdapter adapter ) {
		floatingHeader = null;
		sectionedAdapter = (SectionedAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	public void setOnScrollListener ( OnScrollListener onScrollListener ) {
		this.subclassOnScrollListener = onScrollListener;
	}

	@Override
	public void onScrollStateChanged ( AbsListView view, int scrollState ) {
		// Bubble action up to parent if necessary
		if (subclassOnScrollListener != null) {
			subclassOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll ( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
		// Bubble action up to parent if necessary
		if (subclassOnScrollListener != null) {
			subclassOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

		checkForFloatingHeader(firstVisibleItem, visibleItemCount);
	}

	@Override
	public boolean dispatchTouchEvent ( MotionEvent ev ) {
		if (pinHeaders && floatingHeader != null) {
			float touchX = ev.getX();
			float touchY = ev.getY();
			int touchAction = ev.getAction();

			if (touchAction == MotionEvent.ACTION_DOWN && touchTarget == null && isTouchInFloatingHeader(touchX, touchY)) {
				touchTarget = floatingHeader;
				touchDownX = touchX;
				touchDownY = touchY;
				touchDownEvent = MotionEvent.obtain(ev);
			} else if (touchTarget != null) {
				if (isTouchInFloatingHeader(touchX, touchY)) {
					touchTarget.dispatchTouchEvent(ev);
				}

				if (touchAction == MotionEvent.ACTION_UP) {
					clearTouch();
					clickFloatingHeader(ev);
				} else if (touchAction == MotionEvent.ACTION_CANCEL) {
					clearTouch();
				} else if (touchAction == MotionEvent.ACTION_MOVE && (Math.abs(touchDownX - touchX) > maxMoveDistanceForTouch || Math.abs(touchDownY - touchY) > maxMoveDistanceForTouch)) {
					MotionEvent event = MotionEvent.obtain(ev);
					if (event != null) {
						event.setAction(MotionEvent.ACTION_CANCEL);
						touchTarget.dispatchTouchEvent(event);
						event.recycle();
					}

					super.dispatchTouchEvent(touchDownEvent);
					super.dispatchTouchEvent(ev);
					clearTouch();
				}

				return true;
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	@SuppressWarnings ("NullableProblems")
	@Override
	protected void dispatchDraw ( Canvas canvas ) {
		super.dispatchDraw(canvas);
		if (pinHeaders && sectionedAdapter != null && floatingHeader != null) {
			int count = canvas.save();
			canvas.translate(0, floatingHeaderOffset);
			canvas.clipRect(0, 0, getWidth(), floatingHeader.getMeasuredHeight());
			floatingHeader.draw(canvas);
			canvas.restoreToCount(count);
		}
	}

	@Override
	protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		widthMode = MeasureSpec.getMode(widthMeasureSpec);
		heightMode = MeasureSpec.getMode(heightMeasureSpec);
	}

	public void setOnItemClickListener ( SectionedListViewOnItemClickListener listener ) {
		super.setOnItemClickListener(listener);
	}

	protected void searchForClickableChildren ( ViewGroup parent, float touchX, float touchY ) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ViewGroup) {
				if (((ViewGroup) child).getChildCount() > 0) {
					searchForClickableChildren((ViewGroup) child, touchX, touchY);
				}
			}

			if (child.isClickable()) {
				if (isTouchInView(child, touchX, touchY)) {
					child.performClick();
				}
			}
		}
	}

	protected void clickFloatingHeader ( final MotionEvent ev ) {
		int globalPostion = sectionedAdapter.getGlobalPositionForHeader(floatingHeaderSection);
		setSelectionFromTop(getHeaderViewsCount() + globalPostion, 1);
		post(new Runnable() {
			@Override
			public void run () {
				ViewGroup firstView = (ViewGroup) getChildAt(0);
				searchForClickableChildren(firstView, ev.getRawX(), ev.getRawY());
			}
		});
	}

	protected boolean isTouchInView ( View view, float touchX, float touchY ) {
		int[] screenLocation = new int[2];
		view.getLocationOnScreen(screenLocation);

		Rect hitRect = new Rect();
		view.getHitRect(hitRect);
		hitRect.offset(screenLocation[0] - view.getLeft(), screenLocation[1] - view.getTop());

		return hitRect.contains((int) touchX, (int) touchY);
	}

	protected boolean isTouchInFloatingHeader ( float touchX, float touchY ) {
		if (floatingHeader != null) {
			Rect hitRect = new Rect();
			floatingHeader.getHitRect(hitRect);

			hitRect.top += floatingHeaderOffset;
			hitRect.bottom += floatingHeaderOffset + getPaddingBottom();
			hitRect.left += getPaddingLeft();
			hitRect.right += getPaddingRight();

			return hitRect.contains((int) touchX, (int) touchY);
		}

		return false;
	}

	protected void clearTouch () {
		touchTarget = null;
		touchDownX = -1;
		touchDownY = -1;

		if (touchDownEvent != null) {
			touchDownEvent.recycle();
			touchDownEvent = null;
		}
	}

	protected void checkForFloatingHeader ( int firstVisibleGlobalPosition, int visibleItemCount ) {
		if (pinHeaders && sectionedAdapter != null && sectionedAdapter.getGlobalCount() > 1 && (firstVisibleGlobalPosition >= getHeaderViewsCount())) {
			updateFloatingHeader(firstVisibleGlobalPosition - getHeaderViewsCount(), visibleItemCount);
		} else {
			resetFloatingHeader(firstVisibleGlobalPosition, visibleItemCount);
		}
	}

	protected void updateFloatingHeader ( int firstVisibleGlobalPosition, int visibleItemCount ) {
		int currentSection = sectionedAdapter.getSection(firstVisibleGlobalPosition);
		int headerViewType = sectionedAdapter.getHeaderItemViewType(currentSection);

		floatingHeader = getFloatingHeader(currentSection, floatingHeaderViewType != headerViewType ? null : floatingHeader);
		floatingHeaderViewType = headerViewType;

		updateDimensionsForHeader(floatingHeader);

		floatingHeaderOffset = 0.0f;

		for (int globalPosition = firstVisibleGlobalPosition; globalPosition < firstVisibleGlobalPosition + visibleItemCount; globalPosition++) {
			if (sectionedAdapter.isHeader(globalPosition)) {
				View headerView = getChildAt(globalPosition - firstVisibleGlobalPosition);
				if (headerView != null && floatingHeader != null) {
					float headerViewTop = headerView.getTop();
					float floatingHeaderHeight = floatingHeader.getMeasuredHeight();
					headerView.setVisibility(VISIBLE);

					if (floatingHeaderHeight >= headerViewTop && headerViewTop > 0) {
						floatingHeaderOffset = headerViewTop - headerView.getHeight();
					} else if (headerViewTop <= 0) {
						headerView.setVisibility(INVISIBLE);
					}
				}
			}
		}

		invalidate();
	}

	protected View getFloatingHeader ( int section, View currentFloatingHeader ) {
		if (sectionedAdapter.doesSectionHaveHeader(section)) {
			View headerView = sectionedAdapter.getHeaderView(section, currentFloatingHeader, this);
			if (section != floatingHeaderSection || currentFloatingHeader == null) {
				updateDimensionsForHeader(headerView);
				floatingHeaderSection = section;
			}

			return headerView;
		}

		return null;
	}

	protected void updateDimensionsForHeader ( View headerView ) {
		if (headerView != null && headerView.isLayoutRequested()) {
			int widthMeasure = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), widthMode);
			int heightMeasure;

			ViewGroup.LayoutParams layoutParams = headerView.getLayoutParams();
			if (layoutParams != null && layoutParams.height > 0) {
				heightMeasure = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
			} else {
				heightMeasure = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			}

			headerView.measure(widthMeasure, heightMeasure);
			headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());
		}
	}

	protected void resetFloatingHeader ( int firstVisibleGlobalPosition, int visibleItemCount ) {
		floatingHeader = null;
		floatingHeaderOffset = 0.0f;

		for (int globalPosition = firstVisibleGlobalPosition; globalPosition < firstVisibleGlobalPosition + visibleItemCount; globalPosition++) {
			View header = getChildAt(globalPosition);
			if (header != null) {
				header.setVisibility(VISIBLE);
			}
		}
	}

	protected void setup () {
		super.setOnScrollListener(this);
		maxMoveDistanceForTouch = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
}
