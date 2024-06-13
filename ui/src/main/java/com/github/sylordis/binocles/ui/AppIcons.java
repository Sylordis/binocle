package com.github.sylordis.binocles.ui;

import java.util.HashMap;
import java.util.Map;

import com.github.sylordis.binocles.model.review.CommentType;
import com.github.sylordis.binocles.model.review.DefaultCommentType;
import com.github.sylordis.binocles.model.review.DefaultNomenclature;
import com.github.sylordis.binocles.model.review.Nomenclature;
import com.github.sylordis.binocles.model.text.Book;
import com.github.sylordis.binocles.model.text.Chapter;
import com.github.sylordis.binocles.ui.settings.BinoclesUIConfiguration;
import com.github.sylordis.binocles.utils.MapUtils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Holds the icons configuration.
 * 
 * @author sylordis
 *
 */
public final class AppIcons {

	private static final Map<Class<?>, Image> ICON_DICTIONARY = new HashMap<>();
	private static final String BASE_ICON_PATH = "img/";

	/**
	 * Icon for the software.
	 */
	public static final Image ICON_SOFTWARE = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "binocles.png"));

	/**
	 * Icon for {@link Book}.
	 */
	public static final Image ICON_BOOK = new Image(AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "book.png"));

	/**
	 * Icon for {@link Chapter}.
	 */
	public static final Image ICON_CHAPTER = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "chapter.png"));
	/**
	 * Icon for {@link Chapter} creation.
	 */
	public static final Image ICON_CHAPTER_CREATE = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "chapter_create.png"));

	/**
	 * Icon for {@link CommentType}.
	 */
	public static final Image ICON_COMMENT_TYPE = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "comment_type.png"));

	/**
	 * Icon for comments.
	 */
	public static final Image ICON_COMMENT = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "comment.png"));

	/**
	 * Icon for help.
	 */
	public static final Image ICON_HELP = new Image(AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "help.png"));

	/**
	 * Icon for {@link Nomenclature}/Nomenclature.
	 */
	public static final Image ICON_NOMENCLATURE = new Image(
	        AppIcons.class.getResourceAsStream(BASE_ICON_PATH + "nomenclature.png"));

	// Initialise icon map for Cell renderers (Tree & List)
	static {
		MapUtils.create(ICON_DICTIONARY, new Class<?>[] { Book.class, //
		        Chapter.class, //
		        Nomenclature.class, //
		        DefaultNomenclature.class, //
		        CommentType.class, //
		        DefaultCommentType.class },
		        new Image[] { ICON_BOOK, //
		                ICON_CHAPTER, //
		                ICON_NOMENCLATURE, //
		                ICON_NOMENCLATURE, //
		                ICON_COMMENT_TYPE, //
		                ICON_COMMENT_TYPE });
	}

	/**
	 * Creates an image view based on the current display size of the {@link BinoclesUIConfiguration}.
	 * 
	 * @param image Image to wrap into a view
	 * @return
	 * @see BinoclesUIConfiguration#getDisplaySize
	 */
	public static final ImageView createImageViewFromConfig(Image image) {
		return createImageView(image, BinoclesUIConfiguration.getInstance().getDisplaySize().getIconMaxSize(),
		        BinoclesUIConfiguration.getInstance().getDisplaySize().getIconMaxSize());
	}

	/**
	 * Creates an image view with provided dimensions.
	 * 
	 * @param image  Image to wrap into a view
	 * @param width  Maximum width of the image
	 * @param height Maximum height of the image
	 * @return
	 */
	public static final ImageView createImageView(Image image, int width, int height) {
		ImageView view = new ImageView(image);
		view.setPreserveRatio(true);
		view.setFitWidth(width);
		view.setFitHeight(height);
		return view;
	}

	/**
	 * Gets the typical representative image for a given type.
	 * 
	 * @param type
	 * @return the image, or null if there's no image associated to that type
	 */
	public static final Image getImageForType(Class<?> type) {
		return ICON_DICTIONARY.get(type);
	}

	/**
	 * Private constructor.
	 */
	private AppIcons() {
		// Nothing to do here
	}
}
