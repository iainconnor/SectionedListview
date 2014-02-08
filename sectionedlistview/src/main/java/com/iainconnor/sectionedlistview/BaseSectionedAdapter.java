package com.iainconnor.sectionedlistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

abstract public class BaseSectionedAdapter extends BaseAdapter implements SectionedAdapter {
	private final int COUNT_CACHE_INVALID_TOMBSTONE = -1;

	protected ArrayList<Integer> positionInSectionForGlobalPositionCache;
	protected ArrayList<Integer> countInSectionCache;
	protected ArrayList<Integer> sectionForGlobalPositionCache;
	protected ArrayList<Integer> globalPositionSectionStartCache;
	protected int globalCountCache;
	protected int sectionCountCache;

	public BaseSectionedAdapter () {
		super();
		positionInSectionForGlobalPositionCache = new ArrayList<Integer>();
		countInSectionCache = new ArrayList<Integer>();
		sectionForGlobalPositionCache = new ArrayList<Integer>();
		globalPositionSectionStartCache = new ArrayList<Integer>();
		globalCountCache = sectionCountCache = COUNT_CACHE_INVALID_TOMBSTONE;
	}

	abstract public int getItemViewType ( int section, int position );

	abstract public int getHeaderItemViewType ( int section );

	abstract public int getItemViewTypeCount ();

	abstract public int getHeaderViewTypeCount ();

	abstract public Object getItem ( int section, int position );

	abstract public long getItemId ( int section, int position );

	abstract public View getView ( int section, int position, View convertView, ViewGroup parent );

	abstract public View getHeaderView ( int section, View convertView, ViewGroup parent );

	abstract public int getSectionCount ();

	abstract public int getCountInSection ( int section );

	@Override
	public void notifyDataSetChanged () {
		resetCaches();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated () {
		resetCaches();
		super.notifyDataSetInvalidated();
	}

	@Override
	public int getCount () {
		if (globalCountCache != COUNT_CACHE_INVALID_TOMBSTONE) {
			return globalCountCache;
		} else {
			return calculateGlobalCount();
		}
	}

	@Override
	public int getGlobalCount () {
		return getCount();
	}

	@Override
	public Object getItem ( int globalPosition ) {
		return getItem(getSection(globalPosition), getPositionInSection(globalPosition));
	}

	@Override
	public long getItemId ( int globalPosition ) {
		return getItemId(getSection(globalPosition), getPositionInSection(globalPosition));
	}

	@Override
	public View getView ( int globalPosition, View convertView, ViewGroup parent ) {
		if (isHeader(globalPosition)) {
			return getHeaderView(getSection(globalPosition), convertView, parent);
		} else {
			return getView(getSection(globalPosition), getPositionInSection(globalPosition), convertView, parent);
		}
	}

	@Override
	public int getItemViewType ( int globalPosition ) {
		if (isHeader(globalPosition)) {
			// Avoids collision
			return getItemViewTypeCount() + getHeaderItemViewType(getSection(globalPosition));
		} else {
			return getItemViewType(getSection(globalPosition), getPositionInSection(globalPosition));
		}
	}

	@Override
	public int getViewTypeCount () {
		return getItemViewTypeCount() + getHeaderViewTypeCount();
	}

	@Override
	public int getSection ( int globalPosition ) {
		Integer cachedSection = sectionForGlobalPositionCache.size() > globalPosition ? sectionForGlobalPositionCache.get(globalPosition) : null;
		if (cachedSection != null) {
			return cachedSection;
		} else {
			return calculateSection(globalPosition);
		}
	}

	@Override
	public int getPositionInSection ( int globalPosition ) {
		Integer cachedPosition = positionInSectionForGlobalPositionCache.size() > globalPosition ? positionInSectionForGlobalPositionCache.get(globalPosition) : null;
		if (cachedPosition != null) {
			return cachedPosition;
		} else {
			return calculatePositionInSection(globalPosition);
		}
	}

	@Override
	public boolean isHeader ( int globalPosition ) {
		if (globalPositionSectionStartCache.contains(globalPosition)) {
			return true;
		} else {
			return calculateIsHeader(globalPosition);
		}
	}

	@Override
	public int getGlobalPositionForHeader ( int section ) {
		if (globalPositionSectionStartCache.size() >= section) {
			return globalPositionSectionStartCache.get(section);
		} else {
			return calculateGlobalPositionForHeader(section);
		}
	}

	@Override
	public int getGlobalPositionForItem ( int section, int position ) {
		int sum = 0;

		for (int i = section; i > 0; i--) {
			if (i == section) {
				sum += position;
			} else {
				sum += getCountInSection(i);
			}
		}

		return sum + section;
	}

	protected int calculateGlobalCount () {
		int countSum = 0;

		for (int i = 0; i < getSectionCount(); i++) {
			countSum += getCountInSection(i) + 1;
		}

		globalCountCache = countSum;

		return countSum;
	}

	protected int calculatePositionInSection ( int globalPosition ) {
		int globalPositionSectionStart = 0;
		for (int section = 0; section < getSectionCount(); section++) {
			if (globalPositionSectionStartCache.size() <= section) {
				globalPositionSectionStartCache.add(section);
			} else {
				globalPositionSectionStartCache.set(section, globalPositionSectionStart);
			}
			int globalPositionSectionEnd = globalPositionSectionStart + getCountInSection(section) + 1;

			if (globalPosition >= globalPositionSectionStart && globalPosition < globalPositionSectionEnd) {
				int positionInSection = globalPosition - globalPositionSectionStart - 1;
				if (positionInSectionForGlobalPositionCache.size() <= globalPosition) {
					positionInSectionForGlobalPositionCache.add(positionInSection);
				} else {
					positionInSectionForGlobalPositionCache.set(globalPosition, positionInSection);
				}
				return positionInSection;
			}

			globalPositionSectionStart = globalPositionSectionEnd;
		}

		return 0;
	}

	protected int calculateGlobalPositionForHeader ( int header ) {
		int globalPositionSectionStart = 0;
		for (int section = 0; section < getSectionCount(); section++) {
			if (globalPositionSectionStartCache.size() <= section) {
				globalPositionSectionStartCache.add(globalPositionSectionStart);
			} else {
				globalPositionSectionStartCache.set(section, globalPositionSectionStart);
			}
			int globalPositionSectionEnd = globalPositionSectionStart + getCountInSection(section) + 1;

			if (header == section) {
				return globalPositionSectionStart;
			}

			globalPositionSectionStart = globalPositionSectionEnd;
		}

		return -1;
	}

	protected boolean calculateIsHeader ( int globalPosition ) {
		int globalPositionSectionStart = 0;
		for (int section = 0; section < getSectionCount(); section++) {
			if (globalPositionSectionStartCache.size() <= section) {
				globalPositionSectionStartCache.add(globalPositionSectionStart);
			} else {
				globalPositionSectionStartCache.set(section, globalPositionSectionStart);
			}
			int globalPositionSectionEnd = globalPositionSectionStart + getCountInSection(section) + 1;

			if (globalPosition == globalPositionSectionStart) {
				return true;
			}

			globalPositionSectionStart = globalPositionSectionEnd;
		}

		return false;
	}

	protected int calculateSection ( int globalPosition ) {
		int globalPositionSectionStart = 0;
		for (int section = 0; section < getSectionCount(); section++) {
			if (globalPositionSectionStartCache.size() <= section) {
				globalPositionSectionStartCache.add(globalPositionSectionStart);
			} else {
				globalPositionSectionStartCache.set(section, globalPositionSectionStart);
			}
			int globalPositionSectionEnd = globalPositionSectionStart + getCountInSection(section) + 1;

			if (globalPosition >= globalPositionSectionStart && globalPosition < globalPositionSectionEnd) {
				if (sectionForGlobalPositionCache.size() <= globalPosition) {
					sectionForGlobalPositionCache.add(section);
				} else {
					sectionForGlobalPositionCache.set(globalPosition, section);
				}
				return section;
			}

			globalPositionSectionStart = globalPositionSectionEnd;
		}

		return 0;
	}

	protected void resetCaches () {
		positionInSectionForGlobalPositionCache.clear();
		countInSectionCache.clear();
		sectionForGlobalPositionCache.clear();
		globalPositionSectionStartCache.clear();
		globalCountCache = sectionCountCache = COUNT_CACHE_INVALID_TOMBSTONE;
	}
}
