package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.util.FileReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         Provides documentation for SQF PsiElements
 *         Created on 01/03/2016.
 */
public class SQFDocumentationProvider extends DocumentationProviderEx{
	private static final String BIS_WIKI_URL_PREFIX = Plugin.resources.getString("plugin.doc.sqf.wiki_URL_prefix");
	private static final String EXTERNAL_LINK_NOTIFICATION = Plugin.resources.getString("plugin.doc.sqf.wiki_doc_external_link_notification_string_format");
	private static final String LINK_PREFIX_COMMAND = "command:";
	private static final String LINK_PREFIX_BIS_FUNCTION = "bis-function:";

	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return generateDoc(element, originalElement);
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		List<String> lst = new ArrayList<>();
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND) || SQFPsiUtil.isBisFunction(element)){
			lst.add(getWikiUrl(element.getText()));
			return lst;
		}
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return generateCommandDoc(element.getText());
		}
		if(SQFPsiUtil.isBisFunction(element)){
			return generateFunctionDoc(element.getText());
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(element, SQFTypes.BLOCK_COMMENT)){
			PsiComment comment = (PsiComment) element;
			return SQFPsiUtil.getCommentContent(comment).replaceAll("\n", "<br>");
		}
		if (element instanceof PsiFile){
			ASTNode potentialDocNode = element.getNode().getFirstChildNode();
			if (!PsiUtil.isOfElementType(potentialDocNode, SQFTypes.INLINE_COMMENT) && !PsiUtil.isOfElementType(potentialDocNode, SQFTypes.BLOCK_COMMENT)){
				potentialDocNode = potentialDocNode.getTreeNext();
			}
			if (PsiUtil.isOfElementType(potentialDocNode, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(potentialDocNode, SQFTypes.BLOCK_COMMENT)){
				PsiComment comment = (PsiComment) potentialDocNode.getPsi();
				return SQFPsiUtil.getCommentContent(comment).replaceAll("\n", "<br>");
			}
			return null;
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return element;
		}
		if (element instanceof PsiFile){
			return element;
		}

		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		if (link.startsWith(LINK_PREFIX_COMMAND)){
			return SQFPsiUtil.createElement(context.getProject(), link.substring(LINK_PREFIX_COMMAND.length()), SQFTypes.COMMAND);
		}
		if(link.startsWith(LINK_PREFIX_BIS_FUNCTION)){
			return SQFPsiUtil.createElement(context.getProject(), link.substring(LINK_PREFIX_BIS_FUNCTION.length()), SQFTypes.GLOBAL_VAR);
		}
		return null;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if (PsiUtil.isOfElementType(contextElement, SQFTypes.COMMAND)){
			return contextElement;
		}

		if (PsiUtil.isOfElementType(contextElement, SQFTypes.LOCAL_VAR)){ //this code works, but only when the selected statement has the comment
			SQFVariable var = (SQFVariable) (contextElement.getParent());
			PsiReference[] references = var.getReferences();
			for (PsiReference reference : references){
				if (reference.getElement().getParent() instanceof SQFAssignment){
					SQFStatement statement = (SQFStatement) reference.getElement().getParent().getParent();
					PsiElement comment = getInlineComment(editor, statement.getNode());
					if (comment != null){
						return comment;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private PsiElement getInlineComment(@NotNull Editor editor, ASTNode statementNode) {
		ASTNode commentNode = PsiUtil.getNextSiblingNotWhitespace(statementNode);
		if (PsiUtil.isOfElementType(commentNode, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(commentNode, SQFTypes.BLOCK_COMMENT)){
			if (editor.getDocument().getLineNumber(statementNode.getStartOffset()) != editor.getDocument().getLineNumber(commentNode.getStartOffset())){ //comment not on same line
				return null;
			}
			return commentNode.getPsi();
		}
		return null;
	}

	private static String generateCommandDoc(String commandName) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(commandName));
		try{
			doc += FileReader.getText(SQFStatic.COMMANDS_DOC_FILE_DIR + commandName);
		}catch (Exception e){
			e.printStackTrace();
		}
		return doc;
	}

	private static String generateFunctionDoc(String functionName) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(functionName));
		try{
			doc += FileReader.getText(SQFStatic.BIS_FUNCTIONS_DOC_FILE_DIR + functionName);
		}catch (Exception e){
			e.printStackTrace();
		}
		return doc;
	}

	private static String getWikiUrl(String wikiLinkName) {
		return BIS_WIKI_URL_PREFIX + wikiLinkName;
	}

}