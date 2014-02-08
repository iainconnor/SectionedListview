package com.iainconnor.sectionedlistview.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.iainconnor.sectionedlistview.BaseSectionedAdapter;

import java.util.ArrayList;

public class SectionedAdapter extends BaseSectionedAdapter {
	private final static int BOOK_SECTION = 0;
	private final static int MOVIE_SECTION = 1;

	protected Context context;
	protected ArrayList<Book> books;
	protected ArrayList<Movie> movies;

	public SectionedAdapter ( Context context, ArrayList<Book> books, ArrayList<Movie> movies ) {
		this.context = context;
		this.books = books;
		this.movies = movies;
	}

	@Override
	public int getItemViewType ( int section, int position ) {
		// In our example, each section has a different type of view for its items
		return section;
	}

	@Override
	public int getHeaderItemViewType ( int section ) {
		// In our example, each section header has the same view type
		// NOTE: This tripped me up for quite a while, the item view types are 0-indexed
		return 0;
	}

	@Override
	public int getItemViewTypeCount () {
		// In our example, each section has a different type of view for its items
		return getSectionCount();
	}

	@Override
	public int getHeaderViewTypeCount () {
		// In our example, each section header has the same view type
		return 1;
	}

	@Override
	public Object getItem ( int section, int position ) {
		if (section == BOOK_SECTION) {
			return books.get(position);
		} else if (section == MOVIE_SECTION) {
			return movies.get(position);
		}

		return null;
	}

	@Override
	public long getItemId ( int section, int position ) {
		if (position != -1) {
			if (section == BOOK_SECTION) {
				return books.get(position).getId();
			} else if (section == MOVIE_SECTION) {
				return movies.get(position).getId();
			}
		}

		return -1;
	}

	@Override
	public View getView ( int section, int position, View convertView, ViewGroup parent ) {
		View view;

		if (convertView != null && convertView.getTag().equals(Integer.toString(section))) {
			view = convertView;
		} else {
			int layoutToInflate;
			if (section == BOOK_SECTION) {
				layoutToInflate = R.layout.book_row;
			} else {
				layoutToInflate = R.layout.movie_row;
			}

			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(layoutToInflate, null);
			if (view != null) {
				view.setTag(Integer.toString(section));
			}
		}

		if (view != null) {
			if (section == BOOK_SECTION) {
				Book book = books.get(position);
				((TextView) view.findViewById(R.id.mainText)).setText(book.getTitle());
				((TextView) view.findViewById(R.id.dateText)).setText("" + book.getYear());
				((TextView) view.findViewById(R.id.subText)).setText(book.getAuthor());
			} else {
				Movie movie = movies.get(position);
				((TextView) view.findViewById(R.id.mainText)).setText(movie.getTitle());
				((TextView) view.findViewById(R.id.dateText)).setText("" + movie.getYear());
				((TextView) view.findViewById(R.id.subText)).setText(movie.getDirector());
			}
		}

		return view;
	}

	@Override
	public View getHeaderView ( int section, View convertView, ViewGroup parent ) {
		View view;

		if (convertView != null && convertView.getTag().equals("header-" + Integer.toString(section))) {
			view = convertView;
		} else {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.header_row, null);
			if (view != null) {
				view.setTag("header-" + Integer.toString(section));
			}
		}

		if (view != null) {
			if (section == BOOK_SECTION) {
				((TextView) view.findViewById(R.id.headerText)).setText("Books");
			} else {
				((TextView) view.findViewById(R.id.headerText)).setText("Movies");
			}

			Button moviesButton = (Button) view.findViewById(R.id.moviesButton);
			moviesButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick ( View v ) {
					Toast toast = Toast.makeText(context, "I love movies!", Toast.LENGTH_LONG);
					toast.show();
				}
			});

			Button booksButton = (Button) view.findViewById(R.id.booksButton);
			booksButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick ( View v ) {
					Toast toast = Toast.makeText(context, "I love books too!", Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}

		return view;
	}

	@Override
	public int getSectionCount () {
		return 2;
	}

	@Override
	public int getCountInSection ( int section ) {

		if (section == BOOK_SECTION) {
			return books.size();
		} else if (section == MOVIE_SECTION) {
			return movies.size();
		}

		return 0;
	}
}
