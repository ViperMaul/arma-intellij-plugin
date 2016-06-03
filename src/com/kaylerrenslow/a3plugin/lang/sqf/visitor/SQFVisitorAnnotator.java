package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFAssignment;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCaseStatement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor;
import org.jetbrains.annotations.NotNull;

/**
 @author Kayler
 AST visistor implementation for annotating with SQFAnnotator
 Created on 03/16/2016. */
public class SQFVisitorAnnotator extends SQFVisitor {

	private AnnotationHolder annotator;

	private Annotation expect(String expected, PsiElement got) {
		return annotator.createErrorAnnotation(got, String.format(Plugin.resources.getString("lang.sqf.annotator.error.expected_f"), expected, got.getText()));
	}

	private Annotation createDeleteTokenAnotation(PsiElement element) {
		return annotator.createErrorAnnotation(element, String.format(Plugin.resources.getString("lang.sqf.annotator.error.unexpected_f"), element.getText()));
	}


	public SQFVisitorAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}

	@Override
	public void visitComment(PsiComment comment) {
		super.visitComment(comment);
		String commentContent = SQFPsiUtil.getCommentContent(comment);
		if (commentContent.toLowerCase().startsWith("note")) {
			Annotation a = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.from(comment.getTextOffset() + 2, commentContent.length()), commentContent);
			a.setTextAttributes(SQFSyntaxHighlighter.COMMENT_NOTE);

		}
	}

	@Override
	public void visitAssignment(@NotNull SQFAssignment o) {
		if (o.getCommand() != null) {
			if (!o.getCommand().getText().equals("private")) {
				expect("private", o.getCommand());
			}
		}
		super.visitAssignment(o);
	}

	@Override
	public void visitCaseStatement(@NotNull SQFCaseStatement o) {
		super.visitCaseStatement(o);
		if (!o.getCommand().getText().equals("case")) {
			expect("case", o.getCommand());
		}
	}


}

