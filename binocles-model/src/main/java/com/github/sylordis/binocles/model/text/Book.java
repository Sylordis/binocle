package com.github.sylordis.binocles.model.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.sylordis.binocles.model.review.Nomenclature;
import com.github.sylordis.binocles.model.review.ReviewableContent;
import com.github.sylordis.binocles.utils.Identifiable;

/**
 * Represents a book that can contain several chapters. Books will be the base entity that is
 * managed by Binocle. Each Chapter will have their own comments but their configuration is managed
 * at the Book level.
 * 
 * @author sylordis
 *
 */
public class Book extends ReviewableContent {

	/**
	 * Title of the book.
	 */
	private String title;
	/**
	 * List of chapters structuring the books. This list cannot be null.
	 */
	private List<Chapter> chapters;
	/**
	 * Book synopsis.
	 */
	private String synopsis;
	/**
	 * Book description, whatever the author wants to put there.
	 */
	private String description;
	/**
	 * Any other metadata concerning the book (author, source, publication year, ...). This map is never
	 * null.
	 */
	private final Map<String, String> metadata;
	/**
	 * Current nomenclature for the review.
	 */
	private Nomenclature nomenclature;

	/**
	 * Creates a new Book with a title.
	 * 
	 * @param title
	 */
	public Book(String title) {
		this(title, "", "");
	}

	/**
	 * @param title
	 * @param chapters
	 * @param synopsis
	 * @param description
	 */
	public Book(String title, String synopsis, String description) {
		super();
		this.title = title;
		this.chapters = new ArrayList<>();
		this.synopsis = synopsis;
		this.description = description;
		this.metadata = new TreeMap<>();
	}

	@Override
	public String toString() {
		StringBuilder rame = new StringBuilder();
		rame.append(title);
		if (hasChapters()) {
			rame.append(" [").append(this.chapters.size()).append("]");
		}
		rame.append(" <").append(null != nomenclature ? nomenclature : "none").append(">");
		return rame.toString();
	}

	@Override
	public String getId() {
		return Identifiable.formatId(title);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Creates a new empty chapter. The title will be created automatically based on the amount of
	 * already existing chapters.
	 */
	public void createChapter() {
		Chapter chapter = new Chapter("Chapter " + this.chapters.size());
		this.chapters.add(chapter);
	}

	/**
	 * 
	 * @param chapter
	 */
	public void addChapter(Chapter chapter) {
		this.chapters.add(chapter);
	}

	/**
	 * @return the chapters
	 */
	public List<Chapter> getChapters() {
		return chapters;
	}

	/**
	 * Checks if this books has chapters.
	 * @return true if this book has at least one chapter.
	 */
	public boolean hasChapters() {
		return !this.chapters.isEmpty();
	}
	
	/**
	 * @param chapters the chapters to set
	 */
	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	/**
	 * @return the synopsis
	 */
	public String getSynopsis() {
		return synopsis;
	}

	/**
	 * @param synopsis the synopsis to set
	 */
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the nomenclature.
	 * 
	 * @return
	 */
	public Nomenclature getNomenclature() {
		return nomenclature;
	}

	/**
	 * Replaces the nomenclature the hard way. All existing comments on this entity and its chapters
	 * will be assigned the Orphan type.
	 * 
	 * @param nomenclature
	 */
	public void setNomenclature(Nomenclature nomenclature) {
		this.nomenclature = nomenclature;
	}

	/**
	 * Checks that the book has a given chapter, matching on ID.
	 * 
	 * @param n
	 * @see Identifiable#is(String)
	 * @return
	 */
	public boolean hasChapter(String n) {
		return this.chapters.stream().anyMatch(c -> c.is(n));
	}

	/**
	 * Replaces the metadata. Null key will not be added and null values will be inserted as empty
	 * strings.
	 * 
	 * @param metadatas
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata.clear();
		if (metadata != null) {
			for (Map.Entry<String, String> data : metadata.entrySet()) {
				if (null != data.getKey())
					this.metadata.put(data.getKey(), null != data.getValue() ? data.getValue() : "");
			}
		}
	}

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

}
