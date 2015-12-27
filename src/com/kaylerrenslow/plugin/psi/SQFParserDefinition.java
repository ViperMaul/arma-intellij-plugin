package com.kaylerrenslow.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.plugin.SQFLanguage;
import com.kaylerrenslow.plugin.SQFLexerAdapter;
import com.kaylerrenslow.plugin.Static;
import com.kaylerrenslow.plugin.parser.SQFParser;
import com.kaylerrenslow.plugin.parser.SQFParser_NoSyntax;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFParserDefinition implements ParserDefinition{
	public static final TokenSet WHITE_SPACES = TokenSet.create(SQFTypes.WHITE_SPACE);
	public static final TokenSet COMMENTS = TokenSet.create(SQFTypes.COMMENT);
	public static final IFileElementType FILE = new IFileElementType(Language.<SQFLanguage>findInstance(SQFLanguage.class));

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		return new SQFLexerAdapter();
	}

	@Override
	public PsiParser createParser(Project project) {
		String syntax = Static.plugin.getPluginProperty(Static.PluginPropertiesKey.PARSER);
		if(syntax.equalsIgnoreCase("false")){
			return new SQFParser_NoSyntax();
		}
		return new SQFParser();
	}

	@Override
	public IFileElementType getFileNodeType() {
		return FILE;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens() {
		return WHITE_SPACES;
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens() {
		return COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements() {
		return TokenSet.EMPTY;
	}

	@NotNull
	@Override
	public PsiElement createElement(ASTNode node) {
		return SQFTypes.Factory.createElement(node);
	}

	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new SQFFile(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}
