package com.iainconnor.sectionedlistview.example;

public class Movie {
	protected int id;
	protected String title;
	protected String director;
	protected int year;

	public Movie ( int id, String title, String director, int year ) {
		this.id = id;
		this.title = title;
		this.director = director;
		this.year = year;
	}

	public String getTitle () {
		return title;
	}

	public String getDirector () {
		return director;
	}

	public int getYear () {
		return year;
	}

	public int getId () {
		return id;
	}
}
