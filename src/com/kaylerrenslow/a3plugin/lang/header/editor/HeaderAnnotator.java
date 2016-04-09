package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 04/02/2016.
 */
public class HeaderAnnotator implements Annotator {

	private final HeaderAnnotatorVisitor visitor = new HeaderAnnotatorVisitor();

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		visitor.setAnnotationHolder(holder);
		element.accept(visitor);
	}
}
