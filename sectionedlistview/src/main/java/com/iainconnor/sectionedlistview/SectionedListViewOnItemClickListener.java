package com.iainconnor.sectionedlistview;

import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;

abstract public class SectionedListViewOnItemClickListener implements AdapterView.OnItemClickListener {
	@Override
	public void onItemClick ( AdapterView<?> parent, View view, int globalPosition, long id ) {
		SectionedAdapter sectionedAdapter;
		int trueGlobalPostion = globalPosition;

		if (parent.getAdapter() instanceof HeaderViewListAdapter) {
			sectionedAdapter = (SectionedAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter();
			globalPosition -= ((HeaderViewListAdapter) parent.getAdapter()).getHeadersCount();
		} else {
			sectionedAdapter = (SectionedAdapter) parent.getAdapter();
		}

		if (globalPosition >= 0) {
			int section = sectionedAdapter.getSection(globalPosition);
			int position = sectionedAdapter.getPositionInSection(globalPosition);

			if (sectionedAdapter.isHeader(globalPosition)) {
				onSectionHeaderClick(parent, view, section, id);
			} else {
				onItemClick(parent, view, section, position, id);
			}
		} else {
			onListHeaderClick(parent, view, trueGlobalPostion, id);
		}
	}

	abstract public void onItemClick ( AdapterView<?> adapterView, View view, int section, int position, long id );

	abstract public void onSectionHeaderClick ( AdapterView<?> adapterView, View view, int section, long id );

	abstract public void onListHeaderClick ( AdapterView<?> adapterView, View view, int headerNumber, long id );
}
