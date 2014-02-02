package com.iainconnor.sectionedlistview;

import android.view.View;
import android.widget.AdapterView;

abstract public class SectionedListViewOnItemClickListener implements AdapterView.OnItemClickListener {
	@Override
	public void onItemClick ( AdapterView<?> parent, View view, int globalPosition, long id ) {
		SectionedAdapter sectionedAdapter = (SectionedAdapter) parent.getAdapter();
		int section = sectionedAdapter.getSection(globalPosition);
		int position = sectionedAdapter.getPositionInSection(globalPosition);

		if (position == -1) {
			onHeaderClick(parent, view, section, id);
		} else {
			onItemClick(parent, view, section, position, id);
		}
	}

	abstract public void onItemClick ( AdapterView<?> adapterView, View view, int section, int position, long id );

	abstract public void onHeaderClick ( AdapterView<?> adapterView, View view, int section, long id );
}
