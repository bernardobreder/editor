/*
 * 03/21/2010 Copyright (C) 2010 Robert Futrell robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea This library is distributed under a modified BSD license. See
 * the included RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.c;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

/**
 * Language support for C.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CLanguageSupport extends AbstractLanguageSupport {
	
	/**
	 * The completion provider, shared amongst all text areas editing C.
	 */
	private CCompletionProvider provider;
	
	/**
	 * Constructor.
	 */
	public CLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new CCellRenderer();
	}
	
	private CCompletionProvider getProvider() {
		if (provider == null) {
			provider = new CCompletionProvider();
		}
		return provider;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {
		// Provider
		CCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		// Tooltip
		textArea.setToolTipSupplier(provider);
		// Parser
		Parser parser = new CParser(textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}
	
}
