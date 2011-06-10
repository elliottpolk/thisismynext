/**
 * 
 */
package com.p2s.android.apps.thisismynext.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author elliott.polk
 *
 */
public class Content {
	private String id;
	
	private String title;
	private String meta;
	private String meta_URI;
	private String content_URI;
	private String author;
	private String postedAt;
	private String post;
	private boolean read;
	
	private List gallery;
	
	private Context context;
	
	public Content() {
	}
	
	public Content(Context context, long cId) {
		this.context = context;
		ContentDBAdaptor dba = new ContentDBAdaptor(context);
		Cursor c = dba.open().fetchContent(cId);
		if(c != null) {
			id = Long.toString(cId);
			title = c.getString(1);
			meta = c.getString(2);
			meta_URI = c.getString(3);
			content_URI = c.getString(4);
			author = c.getString(5);
			postedAt = c.getString(6);
			post = c.getString(7);
		}
		c.close();
		c = null;
		
		c = dba.fetchGalleryURIs(cId);
		if(c != null) {
			gallery = new ArrayList();
			while(!c.isAfterLast()) {
				gallery.add(c.getString(2));
				c.moveToNext();
			}
		}
		c.close();
		c = null;
		
		dba.close();
	}
	
	public Content(Context context) {
		this.context = context;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta_URI(String meta_URI) {
		this.meta_URI = meta_URI;
	}

	public String getMeta_URI() {
		return meta_URI;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setContent_URI(String content_URI) {
		this.content_URI = content_URI;
	}

	public String getContent_URI() {
		return content_URI;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getPost() {
		return post;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public String getPostedAt() {
		return postedAt;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}
	
	public void saveContent() {
		ContentDBAdaptor cdba = new ContentDBAdaptor(context);
		cdba.open().createContent(title, meta, meta_URI, content_URI, author, postedAt, post, read);
		
		Cursor c = cdba.db.query("timn_content", new String[] {"_id"}, "title='"+title+"'", null, null, null, null);
		if(c != null) c.moveToFirst();
		String cId = c.getString(0);
		
		if(gallery != null) {
			for(int x = 0; x < gallery.size(); x++)
				cdba.addGalleryURI(Long.parseLong(cId), (String)gallery.get(x));
		}
		
		cdba.close();
	}
	
	public ContentDBAdaptor getContentDBAdaptor() {
		return new ContentDBAdaptor(context);
	}
	
	public void clearDB() {
		ContentDBAdaptor dba = new ContentDBAdaptor(context);
		dba.open();
		dba.dropTables();
		dba.close();
	}
	
	public void setGallery(List gallery) {
		this.gallery = gallery;
	}

	public List getGallery() {
		return gallery;
	}

	public class ContentDBAdaptor {
		public static final String KEY_ROWID = "_id";
		public static final String KEY_TITLE = "title";
		public static final String KEY_META = "meta";
		public static final String KEY_META_URI = "metaURI";
		public static final String KEY_CONTENT_URI = "contentURI";
		public static final String KEY_AUTHOR = "author";
		public static final String KEY_POSTED_AT = "postedAt";
		public static final String KEY_POST = "post";
		public static final String KEY_READ = "isRead";
		public static final String KEY_GALLERY_CONTENT_ID = "contentId";
		public static final String KEY_IMG_URI = "imageURI";
		public static final String DB_CONTENT_TABLE = "timn_content";
		public static final String DB_GALLERIES_TABLE = "timn_galleries";
		
		private Context context;
		private SQLiteDatabase db;
		private ContentDBHelper dbHelper;
		
		public ContentDBAdaptor(Context context) {
			this.context = context;
		}
		
		public ContentDBAdaptor open() throws SQLException {
			dbHelper = new ContentDBHelper(context);
			db = dbHelper.getWritableDatabase();
			return this;
		}
		
		public void close() {
			dbHelper.close();
		}
		
		public long createContent(String title, String meta, String metaURI, String contentURI, String author, String postedAt, String post, boolean isRead) {
			return db.insert(DB_CONTENT_TABLE, null, createContentValues(title, meta, metaURI, contentURI, author, postedAt, post, isRead));
		}
		
		public long addGalleryURI(long cId, String uri) {
			ContentValues v = new ContentValues();
			v.put(KEY_GALLERY_CONTENT_ID, cId);
			v.put(KEY_IMG_URI, uri);
			return db.insert(DB_GALLERIES_TABLE, null, v);
		}
		
		public boolean updateContent(long rowId, String title, String meta, String metaURI, String contentURI, String author, String postedAt, String post, boolean isRead) {
			return db.update(DB_CONTENT_TABLE, createContentValues(title, meta, metaURI, contentURI, author, postedAt, post, isRead), KEY_ROWID + "=" + rowId, null) > 0;
		}
		
		public boolean removeContent(long rowId) {
			return db.delete(DB_CONTENT_TABLE, KEY_ROWID+"="+rowId, null) > 0;
		}
		
		public boolean removeGallery(long contentId) {
			return db.delete(DB_GALLERIES_TABLE, KEY_GALLERY_CONTENT_ID+"="+contentId, null) > 0;
		}
		
		public Cursor fetchAllContent() {
			Cursor c = db.query(DB_CONTENT_TABLE, 
					new String[] {KEY_ROWID, KEY_TITLE, KEY_META, KEY_META_URI, KEY_CONTENT_URI, KEY_AUTHOR, KEY_POSTED_AT, KEY_POST, KEY_READ}, 
					null, null, null, null, null);
			if(c != null) c.moveToFirst();
			return c;
		}
		
		public Cursor fetchGalleryURIs(long cId) {
			Cursor c = db.query(DB_GALLERIES_TABLE, 
					new String[] {KEY_ROWID, KEY_GALLERY_CONTENT_ID, KEY_IMG_URI},
					KEY_GALLERY_CONTENT_ID+"="+cId, null, null, null, null, null);
			if(c != null) c.moveToFirst();
			return c;
		}
		
		public Cursor fetchContent(long rowId) {
			Cursor c = db.query(true, DB_CONTENT_TABLE, 
					new String[] {KEY_ROWID, KEY_TITLE, KEY_META, KEY_META_URI, KEY_CONTENT_URI, KEY_AUTHOR, KEY_POSTED_AT, KEY_POST, KEY_READ}, 
					KEY_ROWID+"="+rowId, null, null, null, null, null);
			if(c != null) c.moveToFirst();
			return c;
		}
		
		public void dropTables() {
			db.execSQL("DROP TABLE IF EXISTS timn_content");
			db.execSQL("DROP TABLE IF EXISTS timn_galleries");
			db.execSQL(ContentDBHelper.DATABASE_CREATE_CONTENT_TBL);
			db.execSQL(ContentDBHelper.DATABASE_CREATE_GALLERY_TBL);
		}
		
		private ContentValues createContentValues(String title, String meta, String metaURI, String contentURI, String author, String postedAt, String post, boolean isRead) {
			ContentValues v = new ContentValues();
			v.put(KEY_TITLE, title);
			v.put(KEY_META, meta);
			v.put(KEY_META_URI, metaURI);
			v.put(KEY_CONTENT_URI, contentURI);
			v.put(KEY_AUTHOR, author);
			v.put(KEY_POSTED_AT, postedAt);
			v.put(KEY_POST, post);
			v.put(KEY_READ, isRead);
			
			return v;
		}
	}
	
	public class ContentDBHelper extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 1;

		private static final String DATABASE_NAME = "thisismynext";
		public static final String DATABASE_CREATE_CONTENT_TBL = "create table timn_content (_id integer primary key autoincrement,"
			+ "title text not null, meta text not null, metaURI text not null, contentURI text not null, author text not null,"
			+ "postedAt text not null, post text not null, isRead boolean not null);";
		public static final String DATABASE_CREATE_GALLERY_TBL = "create table timn_galleries (_id integer primary key autoincrement,"
			+ "contentId integer not null, imageURI text not null);";

		public ContentDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_CONTENT_TBL);
			db.execSQL(DATABASE_CREATE_GALLERY_TBL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(ContentDBHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to " 
					+ newVersion + ", which will destroy all old data");
			dropTables(db);
			onCreate(db);
		}
		
		public void dropTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS timn_content");
			db.execSQL("DROP TABLE IF EXISTS timn_galleries");
		}
	}
}
