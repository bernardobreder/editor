/*
 * 03/21/2010 Copyright (C) 2010 Robert Futrell robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea This library is distributed under a modified BSD license. See
 * the included RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.c.parser;

import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.c.ast.AbstractASTNode;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Token;

/**
 * A <code>CompilationUnit</code> is the root node of an AST for a Java source file.
 *
 * <pre>
 * CompilationUnit:
 *    [[Annotations] 'package' QualifiedIdentifier ';' ] {ImportDeclaration} {TypeDeclaration}
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CompilationUnit extends AbstractASTNode {
	
	private List<ParserNotice> notices;
	
	private static final Offset ZERO_OFFSET = new ZeroOffset();
	
	/**
	 * @param name
	 */
	public CompilationUnit(String name) {
		super(name, ZERO_OFFSET);
	}
	
	/**
	 * Shorthand for "<tt>addParserNotice(new ParserNotice(t, msg))</tt>".
	 *
	 * @param t
	 * @param msg
	 */
	public void addParserNotice(Token t, String msg) {
		addParserNotice(new ParserNotice(t, msg));
	}
	
	/**
	 * @param notice
	 */
	public void addParserNotice(ParserNotice notice) {
		if (notices == null) {
			notices = new ArrayList<ParserNotice>();
			notices.add(notice);
		}
	}
	
	public ParserNotice getParserNotice(int index) {
		if (notices == null) {
			throw new IndexOutOfBoundsException("No parser notices available");
		}
		return notices.get(index);
	}
	
	public int getParserNoticeCount() {
		return notices == null ? 0 : notices.size();
	}
	
	/**
	 * An offset that always returns 0.
	 */
	private static class ZeroOffset implements Offset {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getOffset() {
			return 0;
		}
		
	}
	
}
