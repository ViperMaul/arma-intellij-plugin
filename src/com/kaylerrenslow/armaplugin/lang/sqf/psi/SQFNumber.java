package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/15/2017
 */
public class SQFNumber extends ASTWrapperPsiElement {
	public SQFNumber(@NotNull ASTNode node) {
		super(node);
	}
}
