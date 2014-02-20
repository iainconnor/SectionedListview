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

	protected View floatingListHeader;
	protected float floatingListHeaderOffset;
	protected int floatingListHeaderIndex;

	protected View floatingSectionHeader;
	protected int floatingSectionHeaderViewType;
	protected float floatingSectionHeaderOffset;
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
		floatingSectionHeader = null;
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
		if (pinHeaders && (floatingSectionHeader != null || floatingListHeader != null)) {
			float touchX = ev.getX();
			float touchY = ev.getY();
			int touchAction = ev.getAction();

			if (touchAction == MotionEvent.ACTION_DOWN && touchTarget == null && isTouchInFloatingSectionHeader(touchX, touchY)) {
				touchTarget = floatingSectionHeader;
				touchDownX = touchX;
				touchDownY = touchY;
				touchDownEvent = MotionEvent.obtain(ev);
			} else if (touchAction == MotionEvent.ACTION_DOWN && touchTarget == null && isTouchInFloatingListHeader(touchX, touchY)) {
				touchTarget = floatingListHeader;
				touchDownX = touchX;
				touchDownY = touchY;
				touchDownEvent = MotionEvent.obtain(ev);
			} else if (touchTarget != null) {
				if (isTouchInFloatingSectionHeader(touchX, touchY) || isTouchInFloatingListHeader(touchX, touchY)) {
					touchTarget.dispatchTouchEvent(ev);
				}

				if (touchAction == MotionEvent.ACTION_UP) {
					if (touchTarget.equals(floatingSectionHeader)) {
						clearTouch();
						clickFloatingSectionHeader(ev);
					} else if (touchTarget.equals(floatingListHeader)) {
						clearTouch();
						clickFloatingListHeader(ev);
					} else {
						clearTouch();
					}
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
		if (pinHeaders && sectionedAdapter != null) {
			if (floatingSectionHeader != null) {
				int count = canvas.save();
				canvas.translate(0, floatingSectionHeaderOffset);
				canvas.clipRect(0, 0, getWidth(), floatingSectionHeader.getMeasuredHeight());
				floatingSectionHeader.draw(canvas);
				canvas.restoreToCount(count);
			}

			if (floatingListHeader != null) {
				int count = canvas.save();
				canvas.translate(0, floatingListHeaderOffset);
				canvas.clipRect(0, 0, getWidth(), floatingListHeader.getMeasuredHeight());
				floatingListHeader.draw(canvas);
				canvas.restoreToCount(count);
			}
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

	protected void clickFloatingListHeader ( final MotionEvent ev ) {
		int globalPostion = floatingListHeaderIndex;
		// Note, you need to set the + 1 to get it to trigger that its a real cell.
		setSelectionFromTop(globalPostion, 1);
		post(new Runnable() {
			@Override
			public void run () {
				ViewGroup firstView = (ViewGroup) getChildAt(0);
				searchForClickableChildren(firstView, ev.getRawX(), ev.getRawY());
			}
		});
	}

	protected void clickFloatingSectionHeader ( final MotionEvent ev ) {
		int globalPostion = sectionedAdapter.getGlobalPositionForHeader(floatingHeaderSection);
		// Note, you need to set the + 1 to get it to trigger that its a real cell.
		setSelectionFromTop(getHeaderViewsCount() + globalPostion, (floatingListHeader != null ? floatingListHeader.getMeasuredHeight() : 0) + 1);
		post(new Runnable() {
			@Override
			public void run () {
				ViewGroup firstView = (ViewGroup) getChildAt(getFirstVisiblePositionAfterFloatingHeader());
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

	protected boolean isTouchInFloatingSectionHeader ( float touchX, float touchY ) {
		if (floatingSectionHeader != null) {
			Rect hitRect = new Rect();
			floatingSectionHeader.getHitRect(hitRect);

			hitRect.top += floatingSectionHeaderOffset;
			hitRect.bottom += floatingSectionHeaderOffset + getPaddingBottom();
			hitRect.left += getPaddingLeft();
			hitRect.right += getPaddingRight();

			return hitRect.contains((int) touchX, (int) touchY);
		}

		return false;
	}

	protected boolean isTouchInFloatingListHeader ( float touchX, float touchY ) {
		if (floatingListHeader != null) {
			Rect hitRect = new Rect();
			floatingListHeader.getHitRect(hitRect);

			hitRect.top = (int) floatingListHeaderOffset;
			hitRect.bottom = (int) (floatingListHeader.getMeasuredHeight() + floatingListHeaderOffset + getPaddingBottom());
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
		if (pinHeaders && sectionedAdapter != null) {
			updateFloatingHeader(firstVisibleGlobalPosition, visibleItemCount);
		} else {
			resetFloatingHeader(firstVisibleGlobalPosition, visibleItemCount);
		}
	}

	protected int getFirstVisiblePositionAfterFloatingHeader () {
		float heightSum = 0.0f;
		if (floatingListHeader != null) {
			for (int position = 0; position < getChildCount(); position++) {
				View rowView = getChildAt(position);
				if (rowView != null) {
					if (heightSum >= floatingListHeader.getMeasuredHeight()) {
						return position;
					}
					heightSum += rowView.getMeasuredHeight();
				}
			}
		}

		return 0;
	}

	protected int getFirstVisibleGlobalPositionAfterFloatingHeader ( int firstVisibleGlobalPosition, int visibleItemCount ) {
		int firstVisibleGlobalPositionAfterFloatingHeader = firstVisibleGlobalPosition;
		float heightSum = 0.0f;
		if (floatingListHeader != null) {
			for (int visiblePosition = 0; visiblePosition < visibleItemCount; visiblePosition++) {
				View rowView = getChildAt(visiblePosition);
				if (rowView != null) {
					int rowViewVisibleHeight = (rowView.getMeasuredHeight()) + (rowView.getTop() > 0 ? 0 : rowView.getTop());

					if ((heightSum + rowViewVisibleHeight) >= floatingListHeader.getMeasuredHeight()) {
						return firstVisibleGlobalPositionAfterFloatingHeader;
					}
					heightSum += rowViewVisibleHeight;
					firstVisibleGlobalPositionAfterFloatingHeader++;
				}
			}
		}

		return firstVisibleGlobalPositionAfterFloatingHeader;
	}

	protected void updateFloatingHeader ( int firstVisibleGlobalPosition, int visibleItemCount ) {
		int currentListHeaderPosition = firstVisibleGlobalPosition < getHeaderViewsCount() ? firstVisibleGlobalPosition : -1;

		floatingListHeader = getFloatingListHeader(currentListHeaderPosition, currentListHeaderPosition == -1 || currentListHeaderPosition >= floatingListHeaderIndex ? floatingListHeader : null);

		int firstVisibleGlobalPositionAfterFloatingHeader = getFirstVisibleGlobalPositionAfterFloatingHeader(firstVisibleGlobalPosition, visibleItemCount);

		int currentSection = firstVisibleGlobalPositionAfterFloatingHeader >= getHeaderViewsCount() ? sectionedAdapter.getSection(firstVisibleGlobalPositionAfterFloatingHeader - getHeaderViewsCount()) : -1;
		int sectionHeaderViewType = sectionedAdapter.getHeaderItemViewType(currentSection);

		floatingSectionHeader = getFloatingSectionHeader(currentSection, floatingSectionHeaderViewType != sectionHeaderViewType ? null : floatingSectionHeader);
		floatingSectionHeaderViewType = sectionHeaderViewType;

		floatingListHeaderOffset = 0.0f;
		floatingSectionHeaderOffset = floatingListHeader != null ? floatingListHeader.getMeasuredHeight() : 0.0f;

		for (int globalPosition = firstVisibleGlobalPosition; globalPosition < firstVisibleGlobalPosition + visibleItemCount; globalPosition++) {
			if (globalPosition < getHeaderViewsCount() && sectionedAdapter.shouldListHeaderFloat(globalPosition)) {
				View headerView = getChildAt(globalPosition - firstVisibleGlobalPosition);
				if (headerView != null && floatingListHeader != null) {
					float headerViewTop = headerView.getTop();
					float floatingHeaderHeight = floatingListHeader.getMeasuredHeight();

					if (floatingHeaderHeight >= headerViewTop && headerViewTop > 0) {
						floatingListHeaderOffset = headerViewTop - headerView.getHeight();
					}
				}
			}

			if (sectionedAdapter.isHeader(globalPosition - getHeaderViewsCount())) {
				View headerView = getChildAt(globalPosition - firstVisibleGlobalPosition);
				if (headerView != null && floatingListHeader != null) {
					float headerViewTop = headerView.getTop();
					float floatingHeaderHeight = (floatingListHeader != null ? floatingListHeader.getMeasuredHeight() : 0.0f) + (floatingSectionHeader != null ? floatingSectionHeader.getMeasuredHeight() : 0.0f);

					if (floatingHeaderHeight >= headerViewTop && headerViewTop > (floatingListHeader != null ? floatingListHeader.getMeasuredHeight() : 0.0f)) {
						floatingSectionHeaderOffset = headerViewTop - headerView.getHeight();
					}
				}
			}
		}

		invalidate();
	}

	protected View getFloatingListHeader ( int index, View currentFloatingListHeader ) {
		if (index >= 0 && sectionedAdapter.shouldListHeaderFloat(index)) {
			View headerView = getChildAt(0);
			if (index != floatingListHeaderIndex || currentFloatingListHeader == null) {
				updateDimensionsForHeader(headerView);
				floatingListHeaderIndex = index;
			}

			return headerView;
		}

		return currentFloatingListHeader;
	}

	protected View getFloatingSectionHeader ( int section, View currentFloatingHeader ) {
		if (section >= 0 && sectionedAdapter.doesSectionHaveHeader(section)) {
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
		floatingSectionHeader = null;
		floatingListHeader = null;
		floatingSectionHeaderOffset = 0.0f;
		floatingListHeaderOffset = 0.0f;
	}

	protected void setup () {
		super.setOnScrollListener(this);
		maxMoveDistanceForTouch = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
}
