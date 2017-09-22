/*
 * 03/21/2010 Copyright (C) 2010 Robert Futrell robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea This library is distributed under a modified BSD license. See
 * the included RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.c;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.swing.text.Element;

import org.fife.rsta.ac.c.parser.CompilationUnit;
import org.fife.rsta.ac.c.parser.ParserNotice;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

/**
 * Parses Java code in an <tt>RSyntaxTextArea</tt>.
 * <p>
 * Like all RSTA <tt>Parser</tt>s, a <tt>JavaParser</tt> instance is notified when the RSTA's text
 * content changes. After a small delay, it will parse the content as Java code, building an AST and
 * looking for any errors. When parsing is complete, a property change event of type
 * {@link #PROPERTY_COMPILATION_UNIT} is fired. Listeners can check the new value of the property
 * for the {@link CompilationUnit} built that represents the source code in the text area. Note that
 * the <tt>CompilationUnit</tt> may be incomplete if there were parsing/syntax errors (it will
 * usually be complete "up to" the error in the content).
 * <p>
 * This parser cannot be shared amongst multiple instances of <tt>RSyntaxTextArea</tt>.
 * <p>
 * Please keep in mind that this class is a work-in-progress!
 *
 * @author Robert Futrell
 * @version 0.5
 */
public class CParser extends AbstractParser {
	
	/**
	 * The property change event that's fired when the document is re-parsed. Applications can
	 * listen for this property change and update themselves accordingly.
	 */
	public static final String PROPERTY_COMPILATION_UNIT = "CompilationUnit";
	
	private CompilationUnit unit;
	
	private PropertyChangeSupport support;
	
	private DefaultParseResult result;
	
	/**
	 * Constructor.
	 *
	 * @param textArea
	 */
	public CParser(RSyntaxTextArea textArea) {
		support = new PropertyChangeSupport(this);
		result = new DefaultParseResult(this);
	}
	
	/**
	 * Returns the compilation unit from the last time the text area was parsed.
	 *
	 * @return The compilation unit, or <code>null</code> if it hasn't yet been parsed or an
	 *         unexpected error occurred while parsing.
	 */
	public CompilationUnit getCompilationUnit() {
		return unit;
	}
	
	/**
	 * @param doc
	 * @param notice
	 * @return offset
	 */
	public int getOffset(RSyntaxDocument doc, ParserNotice notice) {
		Element root = doc.getDefaultRootElement();
		Element elem = root.getElement(notice.getLine());
		int offs = elem.getStartOffset() + notice.getColumn();
		return offs >= elem.getEndOffset() ? -1 : offs;
	}
	
	/**
	 * @param prop
	 * @param l
	 */
	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		support.addPropertyChangeListener(prop, l);
	}
	
	/**
	 * @param prop
	 * @param l
	 */
	public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {
		unit = null;
		result.clearNotices();
		// Always spell check all lines, for now.
		int lineCount = doc.getDefaultRootElement().getElementCount();
		result.setParsedLines(0, lineCount - 1);
		TokenScanner scanner = new TokenScanner(doc);
		long start = System.currentTimeMillis();
		try {
			unit = getCompilationUnit(new File(""), scanner);
			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
		} catch (IOException ioe) {
			result.setError(ioe);
		}
		result.clearNotices();
		int count = unit == null ? 0 : unit.getParserNoticeCount();
		for (int i = 0; i < count; i++) {
			ParserNotice notice = unit.getParserNotice(i);
			int offs = getOffset(doc, notice);
			if (offs > -1) {
				int len = notice.getLength();
				result.addNotice(new DefaultParserNotice(this, notice.getMessage(), notice.getLine(), offs, len));
			}
		}
		support.firePropertyChange(PROPERTY_COMPILATION_UNIT, null, unit);
		return result;
		
	}
	
	/**
	 * @param file
	 * @param scanner
	 * @return unit
	 * @throws IOException
	 */
	private CompilationUnit getCompilationUnit(File file, TokenScanner scanner) throws IOException {
		CompilationUnit unit = new CompilationUnit(file.getName());
		Token token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		token = scanner.next();
		return unit;
	}
	
}
