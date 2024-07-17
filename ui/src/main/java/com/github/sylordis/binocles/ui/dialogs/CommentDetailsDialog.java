package com.github.sylordis.binocles.ui.dialogs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.sylordis.binocles.model.BinoclesModel;
import com.github.sylordis.binocles.model.review.Comment;
import com.github.sylordis.binocles.model.review.CommentType;
import com.github.sylordis.binocles.model.review.CommentTypeField;
import com.github.sylordis.binocles.model.review.Nomenclature;
import com.github.sylordis.binocles.model.text.Book;
import com.github.sylordis.binocles.model.text.Chapter;
import com.github.sylordis.binocles.ui.AppIcons;
import com.github.sylordis.binocles.ui.components.CustomListCell;
import com.github.sylordis.binocles.ui.functional.ListenerValidator;
import com.github.sylordis.binocles.ui.functional.TextAreaResizeUpToListener;
import com.github.sylordis.binocles.ui.javafxutils.StyleUtils;
import com.github.sylordis.binocles.ui.javafxutils.StyleUtils.CSSType;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Dialog to create or edit a comment. When editing, the nomenclature should be fixed.
 */
public class CommentDetailsDialog extends AbstractAnswerDialog<Comment> {

	/**
	 * Size of the text excerpt. Labels will usually be about 100px long.
	 */
	private static final int FIELDS_SIZES = 500;

	/**
	 * Index where the comment type specific fields starts.
	 */
	private static final int ROW_FIELDS_START = 7;

	/**
	 * Maximum number of rows for a text area before having to scroll.
	 */
	private static final int COMMENT_TEXTAREA_MAX_LINES = 10;

	private Book book;
	private Chapter chapter;
	private int start;
	private int end;

	private ComboBox<CommentType> fieldCommentTypeChoice;
	private TextFlow fieldTextExcerpt;
	private Map<String, TextInputControl> fieldsFields;

	private boolean commentTypeValidity = false;
	private String commentTypeFeedback;

	/**
	 * Creates a new details dialog.
	 * 
	 * @param model
	 * @param book
	 * @param chapter
	 * @param start
	 * @param end
	 */
	public CommentDetailsDialog(BinoclesModel model, Book book, Chapter chapter, int start, int end) {
		super("Comment details", model);
		setIcon(AppIcons.ICON_COMMENT);
		this.book = book;
		this.chapter = chapter;
		this.start = start;
		this.end = end;
		fieldsFields = new LinkedHashMap<>();
	}

	@Override
	protected void build() {
		// Create components
		Label labelChapter = new Label("Chapter");
		Text fieldChapter = new Text(this.chapter.getTitle());
		Label labelNomenclature = new Label("Nomenclature");
		Nomenclature nomenclature = book.getNomenclature() == null ? getModel().getDefaultNomenclature()
		        : book.getNomenclature();
		Text fieldNomenclature = new Text(nomenclature.getName());
		Label labelCommentType = new Label("Comment type");
		fieldCommentTypeChoice = new ComboBox<CommentType>(FXCollections.observableList(nomenclature.getTypes()));
		Label labelTextExcerpt = new Label("Text excerpt:");
		fieldTextExcerpt = new TextFlow();
		fieldTextExcerpt.getChildren().add(new Text(chapter.getText().substring(start, end)));
		// TODO All fields according to comment type
		// Set dialog content
		addFormFeedback();
		getGridPane().addRow(1, labelChapter, fieldChapter);
		getGridPane().addRow(2, labelNomenclature, fieldNomenclature);
		getGridPane().addRow(3, labelCommentType, fieldCommentTypeChoice);
		getGridPane().addRow(4, labelTextExcerpt);
		GridPane.setColumnSpan(labelTextExcerpt, GridPane.REMAINING);
		getGridPane().addRow(5, fieldTextExcerpt);
		GridPane.setColumnSpan(fieldTextExcerpt, GridPane.REMAINING);
		Separator separator = new Separator();
//		separator.setPrefHeight(10);
		getGridPane().addRow(6, separator);
		GridPane.setColumnSpan(separator, GridPane.REMAINING);
		GridPane.setValignment(separator, VPos.CENTER);
		// Setup listeners
		ChangeListener<? super CommentType> fieldCommentTypeChoiceListener = (c, o, n) -> {
			updateTextPreview(n);
			removeCommentTypeFields();
			recreateCommentTypeFields(n);
		};
		fieldCommentTypeChoice.valueProperty().addListener(fieldCommentTypeChoiceListener);
		// TODO Set feedback collectors & form validators
		ListenerValidator<CommentType> commentTypeValidator = new ListenerValidator<CommentType>()
		        .validIf("You have to pick a comment type.", (o, n) -> null != n).feed(this::setCommentTypeFeedback)
		        .onEither(b -> this.commentTypeValidity = b).andThen(this::updateFormStatus);
		fieldCommentTypeChoice.valueProperty().addListener(commentTypeValidator);
		// Styling and component status
		fieldCommentTypeChoice.setButtonCell(new CustomListCell<CommentType>(b -> b.getName()));
		fieldCommentTypeChoice.setCellFactory(p -> {
			return new CustomListCell<>(b -> b.getName());
		});
		// Feedback setup
		addFeedbackCollector(() -> commentTypeFeedback);
		addFormValidator(() -> commentTypeValidity);
		// Components set up
		fieldTextExcerpt.setMaxSize(FIELDS_SIZES, 200);
		fieldTextExcerpt.setOpaqueInsets(new Insets(10, 10, 10, 10));
		BackgroundFill whiteFill = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(0, 0, 0, 0));
		fieldTextExcerpt.setBackground(new Background(whiteFill));
		// Actions and runnables
		fieldCommentTypeChoiceListener.changed(null, null,
		        fieldCommentTypeChoice.getSelectionModel().getSelectedItem());
		commentTypeValidator.changed(null, null, fieldCommentTypeChoice.getValue());
	}

	private void updateTextPreview(CommentType type) {
		fieldTextExcerpt.getChildren().clear();
		// TODO Provide proper preview:
		// if it's a full paragraph, provide small excerpts of previous last and beginning of next
		// paragraphs.
		// If it's a part, provide the full paragraph, up to a point?
		Text contextStartEtc = new Text("[..]");
		Text contextStart = new Text(chapter.getText().substring(Math.max(0, start - 50), start));
		Text startDelim = new Text("[");
		Font currentFont = startDelim.getFont();
		Font fontBold = Font.font(currentFont.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, currentFont.getSize());
		Text commentText = new Text(chapter.getText().substring(start, end));
		Text endDelim = new Text("]");
		Text contextEnd = new Text(chapter.getText().substring(end, Math.min(end + 50, chapter.getText().length())));
		Text contextEndEtc = new Text("[..]");
		fieldTextExcerpt.getChildren().addAll(contextStartEtc, contextStart, startDelim, commentText, endDelim,
		        contextEnd, contextEndEtc);
		// Styling
		startDelim.setFont(fontBold);
		endDelim.setFont(fontBold);
		contextStartEtc.getStyleClass().add("text-muted");
		contextEndEtc.getStyleClass().add("text-muted");
		if (type == null || type.getStyles().isEmpty()) {
			contextStart.getStyleClass().add("text-muted");
			contextEnd.getStyleClass().add("text-muted");
		} else
			applyCommentTypeStyleTo(type, commentText);
	}

	/**
	 * Apply comment type styles to the current comment text's field.
	 * 
	 * @param type
	 * @param commentText
	 */
	private void applyCommentTypeStyleTo(CommentType type, Text commentText) {
		String style = String.join(" ",
		        type.getStyles().entrySet().stream()
		                .map(e -> StyleUtils.convertCSStoTypeStyle(e.getKey(), e.getValue(), CSSType.JavaFX))
		                .toArray(String[]::new));
		commentText.setStyle(style);
	}

	/**
	 * Remove all comment type specific fields.
	 */
	private void removeCommentTypeFields() {
		getGridPane().getChildren().removeIf(node -> GridPane.getRowIndex(node) >= ROW_FIELDS_START);
		fieldsFields.clear();
	}

	/**
	 * Recreates all text input controls according to current comment type.
	 * 
	 * @param type
	 */
	private void recreateCommentTypeFields(CommentType type) {
		if (type != null) {
			int row = ROW_FIELDS_START;
			for (Entry<String, CommentTypeField> entry : type.getFields().entrySet()) {
				TextInputControl field;
				Label label = new Label(StringUtils.capitalize(entry.getKey()));
				if (entry.getValue().getIsLongText()) {
					TextArea textField = new TextArea();
					textField.setWrapText(true);
					textField.setPrefRowCount(5);
					textField.setPrefWidth(FIELDS_SIZES - 100);
					textField.textProperty().addListener(new TextAreaResizeUpToListener(textField));
					fieldsFields.put(entry.getKey(), textField);
					getGridPane().addRow(row, label, textField);
					GridPane.setValignment(label, VPos.TOP);
					field = textField;
				} else {
					TextField textField = new TextField();
					fieldsFields.put(entry.getKey(), textField);
					getGridPane().addRow(row, label, textField);
					field = textField;
				}
				field.promptTextProperty().set(entry.getValue().getDescription());
				row++;
			}
		}
		sizeToScene();
	}

	@Override
	public Comment convertResult(ButtonType button) {
		Comment result = new Comment(fieldCommentTypeChoice.getValue(), start, end);
		result.setFields(fieldsFields.entrySet().stream()
		        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getText())));
		return result;
	}

	/**
	 * Sets the feedback message for the comment type feedback.
	 * 
	 * @param msg
	 */
	private void setCommentTypeFeedback(String msg) {
		commentTypeFeedback = msg;
	}

}
