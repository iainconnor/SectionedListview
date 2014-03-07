package com.iainconnor.sectionedlistview.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Toast;
import com.iainconnor.sectionedlistview.SectionedListView;
import com.iainconnor.sectionedlistview.SectionedListViewOnItemClickListener;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		SectionedListView sectionedListView;

		public PlaceholderFragment () {
		}

		@Override
		public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			sectionedListView = (SectionedListView) rootView.findViewById(R.id.list_view);

			sectionedListView.addHeaderView(inflater.inflate(R.layout.header, sectionedListView, false));
			sectionedListView.addHeaderView(inflater.inflate(R.layout.header_two, sectionedListView, false));
			sectionedListView.addHeaderView(inflater.inflate(R.layout.header_three, sectionedListView, false));

			sectionedListView.addFooterView(inflater.inflate(R.layout.header_three, sectionedListView, false));
			sectionedListView.addFooterView(inflater.inflate(R.layout.header_two, sectionedListView, false));

			ArrayList<Movie> movies = new ArrayList<Movie>();
			movies.add(new Movie(1, "Brief Encounter", "David Lean", 1945));
			movies.add(new Movie(2, "Casablanca", "Michael Curtiz", 1942));
			movies.add(new Movie(3, "Before Sunrise", "Richard Linklater", 1995));
			movies.add(new Movie(4, "Before Sunset", "Richard Linklater", 2004));
			movies.add(new Movie(5, "Breathless", "Jean-Luc Godard", 1960));
			/*
		    movies.add(new Movie(6, "In the Mood for Love", "Kar Wai Wong", 2000));
	        movies.add(new Movie(7, "The Apartment", "Billy Wilder", 1960));
	        movies.add(new Movie(8, "Hannah & Her Sisters", "Woody Allen", 1986));
	        movies.add(new Movie(9, "Eternal Sunshine of the Spotless Mind", "Michel Gondry", 2004));
	        movies.add(new Movie(10, "Room With a View", "James Ivory", 1985));
	        movies.add(new Movie(11, "All That Heaven Allows", "Douglas Sirk", 1955));
	        movies.add(new Movie(12, "Gone with the Wind", "Victor Fleming", 1939));
	        movies.add(new Movie(13, "An Affair to Remember", "Leo McCarey", 1957));
	        movies.add(new Movie(14, "Umbrellas of Cherbourg", "Jaques Demy", 1964));
	        movies.add(new Movie(15, "Lost in Translation", "Sofia Coppola", 2003));
			movies.add(new Movie(16, "Roman Holiday", "William Wyler", 1953));
			movies.add(new Movie(17, "Wall-E", "Andrew Stanton", 2008));
			movies.add(new Movie(18, "My Night With Maud", "Eric Rohmer", 1969));
	        movies.add(new Movie(19, "Voyage to Italy", "Roberto Rossellini", 1954));
	        movies.add(new Movie(20, "Dr Zhivago", "David Lean", 1965));
			movies.add(new Movie(1, "Brief Encounter", "David Lean", 1945));
			movies.add(new Movie(2, "Casablanca", "Michael Curtiz", 1942));
			movies.add(new Movie(3, "Before Sunrise", "Richard Linklater", 1995));
			movies.add(new Movie(4, "Before Sunset", "Richard Linklater", 2004));
			movies.add(new Movie(5, "Breathless", "Jean-Luc Godard", 1960));
			movies.add(new Movie(6, "In the Mood for Love", "Kar Wai Wong", 2000));
			movies.add(new Movie(7, "The Apartment", "Billy Wilder", 1960));
			movies.add(new Movie(8, "Hannah & Her Sisters", "Woody Allen", 1986));
			movies.add(new Movie(9, "Eternal Sunshine of the Spotless Mind", "Michel Gondry", 2004));
			movies.add(new Movie(10, "Room With a View", "James Ivory", 1985));
			movies.add(new Movie(11, "All That Heaven Allows", "Douglas Sirk", 1955));
			movies.add(new Movie(12, "Gone with the Wind", "Victor Fleming", 1939));
			movies.add(new Movie(13, "An Affair to Remember", "Leo McCarey", 1957));
			movies.add(new Movie(14, "Umbrellas of Cherbourg", "Jaques Demy", 1964));
			movies.add(new Movie(15, "Lost in Translation", "Sofia Coppola", 2003));
			movies.add(new Movie(16, "Roman Holiday", "William Wyler", 1953));
			movies.add(new Movie(17, "Wall-E", "Andrew Stanton", 2008));
			movies.add(new Movie(18, "My Night With Maud", "Eric Rohmer", 1969));
			movies.add(new Movie(19, "Voyage to Italy", "Roberto Rossellini", 1954));
			movies.add(new Movie(20, "Dr Zhivago", "David Lean", 1965));
			movies.add(new Movie(1, "Brief Encounter", "David Lean", 1945));
			movies.add(new Movie(2, "Casablanca", "Michael Curtiz", 1942));
			movies.add(new Movie(3, "Before Sunrise", "Richard Linklater", 1995));
			movies.add(new Movie(4, "Before Sunset", "Richard Linklater", 2004));
			movies.add(new Movie(5, "Breathless", "Jean-Luc Godard", 1960));
			movies.add(new Movie(6, "In the Mood for Love", "Kar Wai Wong", 2000));
			movies.add(new Movie(7, "The Apartment", "Billy Wilder", 1960));
			movies.add(new Movie(8, "Hannah & Her Sisters", "Woody Allen", 1986));
			movies.add(new Movie(9, "Eternal Sunshine of the Spotless Mind", "Michel Gondry", 2004));
			movies.add(new Movie(10, "Room With a View", "James Ivory", 1985));
			movies.add(new Movie(11, "All That Heaven Allows", "Douglas Sirk", 1955));
			movies.add(new Movie(12, "Gone with the Wind", "Victor Fleming", 1939));
			movies.add(new Movie(13, "An Affair to Remember", "Leo McCarey", 1957));
			movies.add(new Movie(14, "Umbrellas of Cherbourg", "Jaques Demy", 1964));
			movies.add(new Movie(15, "Lost in Translation", "Sofia Coppola", 2003));
			movies.add(new Movie(16, "Roman Holiday", "William Wyler", 1953));
			movies.add(new Movie(17, "Wall-E", "Andrew Stanton", 2008));
			movies.add(new Movie(18, "My Night With Maud", "Eric Rohmer", 1969));
			movies.add(new Movie(19, "Voyage to Italy", "Roberto Rossellini", 1954));
			movies.add(new Movie(20, "Dr Zhivago", "David Lean", 1965));
			*/

			ArrayList<Book> books = new ArrayList<Book>();
			books.add(new Book(1, "A Tale of Two Cities", "Charles Dickens", 1859));
			books.add(new Book(2, "The Lord of the Rings", "J. R. R. Tolkien", 1954));
			books.add(new Book(3, "And Then There Were None", "Agatha Christie", 1939));
			books.add(new Book(4, "The Hobbit", "J. R. R. Tolkien", 1937));
			books.add(new Book(5, "Dream of the Red Chamber", "Cao Xueqin", 1800));
			/*
	        books.add(new Book(6, "She", "H. Rider Haggard", 1887));
	        books.add(new Book(7, "The Little Prince", "Antoine de Saint-Exupéry", 1943));
	        books.add(new Book(8, "The Da Vinci Code", "Dan Brown", 2003));
	        books.add(new Book(9, "The Catcher in the Rye", "J. D. Salinger", 1951));
	        books.add(new Book(10, "The Alchemist", "Paulo Coelho", 1988));
	        books.add(new Book(11, "Heidi", "Johanna Spyri", 1880));
	        books.add(new Book(12, "Anne of Green Gables", "Lucy Maud Montgomery", 1908));
	        books.add(new Book(13, "Black Beauty", "Anna Sewell", 1877));
	        books.add(new Book(14, "The Name of the Rose", "Umberto Eco", 1980));
	        books.add(new Book(15, "Charlotte's Web", "E.B. White", 1952));
	        books.add(new Book(16, "Harry Potter and the Deathly Hallows", "J.K. Rowling", 2007));
	        books.add(new Book(17, "Jonathan Livingston Seagull", "Richard Bach", 1970));
	        books.add(new Book(18, "How the Steel Was Tempered", "Nikolai Ostrovsky", 1932));
	        books.add(new Book(19, "War and Peace", "Leo Tolstoy", 1869));
	        books.add(new Book(20, "Kane and Abel", "Jeffrey Archer", 1979));
	        */

			SectionedAdapter sectionedAdapter = new SectionedAdapter(getActivity().getApplicationContext(), books, movies);
			sectionedListView.setAdapter(sectionedAdapter);

			sectionedListView.setOnItemClickListener(new SectionedListViewOnItemClickListener() {
				@Override
				public void onItemClick ( AdapterView<?> adapterView, View view, int section, int position, long id ) {
					Toast toast = Toast.makeText(getActivity(), "Clicked item at " + position + " in " + section + ".", Toast.LENGTH_LONG);
					toast.show();
				}

				@Override
				public void onSectionHeaderClick ( AdapterView<?> adapterView, View view, int section, long id ) {
					Toast toast = Toast.makeText(getActivity(), "Clicked header for " + section + ".", Toast.LENGTH_LONG);
					toast.show();
				}

				@Override
				public void onListHeaderClick ( AdapterView<?> adapterView, View view, int headerNumber, long id ) {
					Toast toast = Toast.makeText(getActivity(), "Clicked list header for " + headerNumber + ".", Toast.LENGTH_LONG);
					toast.show();
				}

				@Override
				public void onListFooterClick ( AdapterView<?> adapterView, View view, int footerNumber, long id ) {
					Toast toast = Toast.makeText(getActivity(), "Clicked list footer for " + footerNumber + ".", Toast.LENGTH_LONG);
					toast.show();
				}
			});

			return rootView;
		}
	}
}
