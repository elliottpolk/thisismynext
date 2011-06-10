/**
 * 
 */
package com.p2s.android.apps.thisismynext.util;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import android.content.Context;
import android.util.Log;

/**
 * @author elliott.polk
 *
 */
public class ThisIsMyNextScrapper {
	
	public static List scrape(Context context, String URI) {
		List<Content> contentItems = new ArrayList<Content>();
		try {
			Parser p = new Parser();
			p.setURL(URI);
			NodeList inside_divs = p.parse(new TagNameFilter("div")).extractAllNodesThatMatch(new HasAttributeFilter("class","inside"));
			
			for(int i = 0; i < inside_divs.size(); i++) {
				Content content = new Content(context);
				NodeList elems = inside_divs.elementAt(i).getChildren();
				
				// get meta category and meta category url
				Node nMetacat = elems.elementAt(1);
				String aTag = nMetacat.getChildren().elementAt(1).getText();
				content.setMeta_URI(aTag.substring(aTag.indexOf("http"), (aTag.indexOf("title") - 2)));
				content.setMeta(nMetacat.getFirstChild().getText() + nMetacat.getChildren().elementAt(1).getFirstChild().getText()); 
				//
				
				// get title and url
				Node h1 = elems.elementAt(3);
				aTag = h1.getChildren().elementAt(0).getText();
				content.setTitle(strScrubber(h1.getChildren().elementAt(0).getFirstChild().getText()));
				content.setContent_URI(aTag.substring(aTag.indexOf("http"), (aTag.indexOf("rel=") -2)));
				//
				
				// get post date/time and author
				Node authNode = elems.elementAt(5);
				aTag = authNode.getChildren().elementAt(2).getText();
				content.setPostedAt(aTag.substring(0,aTag.length() - 3));
				content.setAuthor(authNode.getChildren().elementAt(1).getChildren().elementAt(0).getText());
				//
				
				contentItems.add(content);
			}
		}catch(Exception e) {
			Log.e("TIMN", e.getMessage().toString());
		}
		
		return contentItems;
	}
	
	private static NodeList getPostNodeList(String url) {
		NodeList nl = null;
		try {
			Parser p = new Parser();
			p.setURL(url);
			nl = p.parse(new TagNameFilter("div")).extractAllNodesThatMatch(new HasAttributeFilter("class", "post"));
		}catch(Exception e) {
			Log.e("TIMN", e.getMessage().toString());
		}
		
		return nl;
	}
	
	public static Content getPost(String contentURI) {
		Content c = new Content();
		int count = 0;
		
		StringBuffer sb = new StringBuffer();
		NodeList post = getPostNodeList(contentURI);
		while(count < 3) {
			count++;
			if(post != null && post.elementAt(0) != null) {
				NodeList paragraphs = post.elementAt(0).getChildren().extractAllNodesThatMatch(new TagNameFilter("p"));
				
				for(int j = 1; j < paragraphs.size(); j++)
					if(!paragraphs.elementAt(j).toHtml().contains("see gallery_shortcode()"))
						sb.append(paragraphs.elementAt(j).toHtml());
				
				String aTag;
				NodeList galleryNodes = null;
				List galleryItems = new ArrayList();
				
				for(int x = 0; x < post.elementAt(0).getChildren().size(); x++) {
					if(post.elementAt(0).getChildren().elementAt(x).toHtml().contains("see gallery_shortcode() in wp-includes/media.php")) {
						if(post.elementAt(0) != null) {
							if(post.elementAt(0).getChildren().elementAt(x + 1).getChildren() != null)
								galleryNodes = post.elementAt(0).getChildren().elementAt(x + 1).getChildren().extractAllNodesThatMatch(new TagNameFilter("dl"));
							else
								galleryNodes = post.elementAt(0).getChildren().elementAt(x + 2).getChildren().extractAllNodesThatMatch(new TagNameFilter("dl"));
							for(int y = 0; y < galleryNodes.size(); y++) {
								if(galleryNodes.elementAt(y) != null &&
										galleryNodes.elementAt(y).getChildren().elementAt(1) != null &&
										galleryNodes.elementAt(y).getChildren().elementAt(1).getChildren().elementAt(1) != null) {
									aTag = galleryNodes.elementAt(y).getChildren().elementAt(1).getChildren().elementAt(1).getText();
									aTag = aTag.substring(aTag.indexOf("http"), aTag.indexOf("' title="));
									if(!galleryItems.contains(aTag)) galleryItems.add(aTag);
								}
							}
						}
					}
				}
				
				c.setPost(sb.toString());
				c.setGallery(galleryItems);
				break;
			}else 
				post = getPostNodeList(contentURI);
		}
		return c;
	}
	
	public static String[] getNavURIs(String currURI) {
		String[] navURIs = new String[2];
		try {
			Parser p = new Parser();
			p.setURL(currURI);
			NodeList nl = p.parse(new TagNameFilter("div")).extractAllNodesThatMatch(new HasAttributeFilter("class","pagination clearfix"));
			NodeList pagination = nl.elementAt(0).getChildren().extractAllNodesThatMatch(new TagNameFilter("p"));
			
			NodeList next = pagination.elementAt(0).getChildren();
			NodeList prev = pagination.elementAt(1).getChildren();
			
			String aTag = next.elementAt(1).getText();
			aTag = aTag.substring(aTag.indexOf("http"), (aTag.length() - 2));
			navURIs[0] = aTag;
			
			if(prev.elementAt(1) != null) {
				aTag = prev.elementAt(1).getText();
				aTag = aTag.substring(aTag.indexOf("http"), (aTag.length() - 2));
				navURIs[1] = aTag;
			}
		}catch(Exception e) {
			Log.e("TIMN", e.getMessage().toString());
		}
		
		return navURIs;
	}
	
	private static String strScrubber(String str) {
		String s = str;
		if(str.contains("&#")) {
			int cnt = s.split("&#").length - 1;
			
			for(int i = 0; i < cnt; i++) {
				int dec = Integer.parseInt(s.substring((s.indexOf("&#") + 2),(s.indexOf(';'))));
				switch(dec) {
				case 38:
					s = s.replaceFirst("&#038;", ("&"));
					break;
				case 8216:
					s = s.replaceFirst("&#8216;", ("‘"));
					break;
				case 8217:
					s = s.replaceFirst("&#8217;", ("’"));
					break;
				default:
					s = s.replaceFirst("&#"+dec+";", Character.toString((char)dec));
					break;
				}
			}
		}	
		return s;
	}
}
