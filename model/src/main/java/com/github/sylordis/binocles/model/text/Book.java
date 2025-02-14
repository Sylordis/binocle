package com.github.sylordis.binocles.model.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.sylordis.binocles.model.review.Nomenclature;
import com.github.sylordis.binocles.utils.contracts.Identifiable;
import com.github.sylordis.binocles.utils.contracts.SelfCopying;
import com.github.sylordis.binocles.utils.exceptions.UniqueIDException;
import com.google.common.base.Preconditions;

/**
 * Represents a book that can contain several chapters. Books will be the base entity that is
 * managed by Binocle. Each Chapter will have their own comments but their configuration is managed
 * at the Book level.
 * 
 * @author sylordis
 *
 */
public class Book extends ReviewableContent implements SelfCopying<Book> {

	private static final long serialVersionUID = 8322075498271400519L;

	/**
	 * Default title for chapters.
	 */
	public static final String CHAPTER_DEFAULT_TITLE = "Chapter ";

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
	 * Book description.
	 */
	private String description;
	/**
	 * Any other metadata concerning the book (author, source, publication year, ...). This map should
	 * never be null.
	 */
	private final Map<String, String> metadata;
	/**
	 * Current nomenclature for the review.
	 */
	private Nomenclature nomenclature;

	/**
	 * Creates a new Book with a title.
	 * 
	 * @param title Title of the book, cannot be null or blank
	 * @see #Book(String, String, String)
	 * @throws NullPointerException     when title is null
	 * @throws IllegalArgumentException when title is blank
	 */
	public Book(String title) {
		this(title, null, null);
	}

	/**
	 * Creates a new Book with a title and a synopsis.
	 * 
	 * @param title    Title of the book, cannot be null or blank
	 * @param synopsis Synopsis of the book, empty string if null is provided
	 * @see #Book(String, String, String)
	 * @throws NullPointerException     when title is null
	 * @throws IllegalArgumentException when title is blank
	 */
	public Book(String title, String synopsis) {
		this(title, synopsis, null);
	}

	/**
	 * Creates a new book with a title, synopsis and description.
	 * 
	 * @param title    Title of the book, cannot be null or blank
	 * @param synopsis Synopsis of the book, empty string if null is provided
	 * @throws NullPointerException     when title is null
	 * @throws IllegalArgumentException when title is blank
	 */
	public Book(String title, String synopsis, String description) {
		super();
		Preconditions.checkNotNull(title, "Book title should not be null");
		Preconditions.checkArgument(!title.isBlank(), "Book title should not be blank");
		this.title = title;
		this.chapters = new ArrayList<>();
		this.synopsis = null == synopsis ? "" : synopsis;
		this.setDescription(null == description ? "" : description);
		this.metadata = new TreeMap<>();
	}

	@Override
	public String toString() {
		return "Book [title=" + title + ", #chapters=" + chapters.size() + ", nomenclature=" + nomenclature + "]";
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
	 * @throws NullPointerException     if the title is null
	 * @throws IllegalArgumentException if the title is blank
	 */
	public void setTitle(String title) {
		Preconditions.checkNotNull(title, "Book title should not be null");
		Preconditions.checkArgument(!title.isBlank(), "Book title should not be blank");
		this.title = title;
	}

	/**
	 * Creates a new empty chapter. The title will be created automatically based on the amount of
	 * already existing chapters.
	 * 
	 * @see #CHAPTER_DEFAULT_TITLE
	 */
	public void createChapter() {
		Chapter chapter = new Chapter(CHAPTER_DEFAULT_TITLE + (this.chapters.size() + 1));
		this.chapters.add(chapter);
	}

	/**
	 * Adds a new chapter to the list of chapters.
	 * 
	 * @param chapter
	 * @throws UniqueIDException
	 * @see List#add(Object)
	 */
	public void addChapter(Chapter chapter) throws UniqueIDException {
		Identifiable.checkIfUnique(chapter, this.chapters);
		this.chapters.add(chapter);
	}

	/**
	 * @return the chapters
	 */
	public List<Chapter> getChapters() {
		return chapters;
	}

	/**
	 * Gets a chapter of the given ID.
	 * 
	 * @param id
	 * @return the chapter corresponding to the id or null if it doesn't exist.
	 * @see Identifiable#formatId(String)
	 */
	public Chapter getChapter(String id) {
		Chapter result = null;
		for (Chapter chapter : chapters) {
			if (chapter.is(id)) {
				result = chapter;
				break;
			}
		}
		return result;
	}

	/**
	 * Checks if this books has chapters.
	 * 
	 * @return true if this book has at least one chapter.
	 */
	public boolean hasChapters() {
		return !this.chapters.isEmpty();
	}

	/**
	 * Replaces all current chapters with provided ones. Empties the list if provided null.
	 * 
	 * @param chapters the chapters to set
	 */
	public void setChapters(List<Chapter> chapters) {
		this.chapters.clear();
		if (chapters != null)
			this.chapters.addAll(chapters);
	}

	/**
	 * Replaces all chapters with provided one, checking for unique id.
	 * 
	 * @param chapters
	 * @throws UniqueIDException
	 */
	public void setChaptersUnique(Collection<Chapter> chapters) throws UniqueIDException {
		this.chapters.clear();
		for (Chapter chapter : chapters) {
			this.addChapter(chapter);
		}
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
		if (synopsis == null)
			this.synopsis = "";
		else
			this.synopsis = synopsis;
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
		if (description == null)
			this.description = "";
		else
			this.description = description;
	}

	@Override
	public List<? extends ReviewableContent> getChildren() {
		return getChapters();
	}

	public int getCommentsCount() {
		return getChapters().stream().mapToInt(c -> c.getCommentsCount()).sum()
		        + (getGlobalComment().isBlank() ? 0 : 1);
	}

	@Override
	public void copy(Book item) {
//		this.description = item.description;
		this.nomenclature = item.nomenclature;
//		this.synopsis = item.synopsis;
		this.title = item.title;
//		this.metadata.clear();
//		this.metadata.putAll(item.metadata);
	}

}
