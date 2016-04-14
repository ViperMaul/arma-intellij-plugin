package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFFunctionItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFVariableItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarInStringReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFVariableReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author Kayler
 *         PsiElement mixin for SQF grammar file. This mixin is meant for SQFVariables
 *         Created on 03/19/2016.
 */
public class SQFVariableNamedElementMixin extends ASTWrapperPsiElement implements SQFVariableNamedElement {
	private final IElementType myVariableElementType;

	public SQFVariableNamedElementMixin(@NotNull ASTNode node) {
		super(node);
		this.myVariableElementType = this.getNode().getFirstChildNode().getElementType();
	}

	@Override
	public IElementType getVariableType() {
		return this.myVariableElementType;
	}

	@Override
	public ItemPresentation getPresentation() {
		if(this.myVariableElementType == SQFTypes.GLOBAL_VAR){
			if(SQFPsiUtil.followsSQFFunctionNameRules(this.getVarName())){
				try{
					HeaderConfigFunction function = HeaderPsiUtil.getFunctionFromCfgFunctions(this.getContainingFile(), this.getVarName());
					if(function != null){
						return new SQFFunctionItemPresentation(function);
					}
				}catch (GenericConfigException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		return new SQFVariableItemPresentation((SQFVariable) this);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		if (myVariableElementType == SQFTypes.GLOBAL_VAR) {
			return ReferenceProvidersRegistry.getReferencesFromProviders(this);
		}

		SQFVariable me = ((SQFVariable) this);
		SQFScope myVarScope = me.getDeclarationScope();

		ArrayList<PsiReference> refs = new ArrayList<>();
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(myVarScope, SQFTypes.VARIABLE, null, me.getVarName());
		SQFVariable other;
		for (int i = 0; i < nodes.size(); i++) {
			other = ((SQFVariable) nodes.get(i).getPsi());
			if (myVarScope == other.getDeclarationScope()) {
				refs.add(new SQFVariableReference(me, other));
			}
		}

		return refs.toArray(new PsiReference[refs.size()]);
	}

	@Override
	public PsiReference getReference() {
		return getReferences()[0];
	}

	@Override
	public String toString() {
		return "SQFVariableNamedElementMixin{" + this.getName() + "}";
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this.getNode().getPsi();
	}

	@Override
	public String getName() {
		return getNode().getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		if (myVariableElementType == SQFTypes.LANG_VAR) {
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		SQFVariable newVar = SQFPsiUtil.createVariable(this.getProject(), name);
		this.getParent().getNode().replaceChild(this.getNode(), newVar.getNode());
		return newVar;
	}

	@Override
	public String getVarName() {
		return this.getName();
	}
}
