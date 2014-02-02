package com.iainconnor.sectionedlistview.example;

public class Book {
	protected int id;
	protected String title;
	protected String author;
	protected int year;

	public Book ( int id, String title, String author, int year ) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.year = year;
	}

	public String getTitle () {
		return title;
	}

	public String getAuthor () {
		return author;
	}

	public int getYear () {
		return year;
	}

	public int getId () {
		return id;
	}
}
