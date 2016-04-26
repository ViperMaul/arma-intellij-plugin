package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 *         This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
 *         Created on 03/30/2016.
 */
public class HeaderConfigFunction {
	private final HeaderClassDeclaration classDeclaration;
	private final String tagName;
	private final String filePath;
	private final String functionFileExtension;
	private final boolean appendFn_;

	/**
	 * This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
	 *
	 * @param classDeclaration        the HeaderClassDeclaration PsiElement that links to the definition
	 * @param containingDirectoryPath file path to the function that is defined in the config (defined from file="exampleFileDir")
	 * @param tagName                 the prefix tag for the function. This is defined in the config with tag="something" (or if not defined, it is the first child class of CfgFunctions)
	 * @param functionFileExtension   file extension (.sqf, .fsm)
	 */
	public HeaderConfigFunction(HeaderClassDeclaration classDeclaration, String containingDirectoryPath, String tagName, @Nullable String functionFileExtension, boolean appendFn_) {
		this.classDeclaration = classDeclaration;
		this.filePath = containingDirectoryPath;
		this.tagName = tagName;
		if (functionFileExtension == null) {
			this.functionFileExtension = ".sqf";
		} else {
			this.functionFileExtension = functionFileExtension;
		}
		this.appendFn_ = appendFn_;
	}

	public HeaderClassDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}


	public String getFunctionClassName(){
		return this.classDeclaration.getClassName();
	}

	public String getTagName() {
		return this.tagName;
	}

	public String getCallableName() {
		return tagName + "_fnc_" + getFunctionClassName();
	}

	public String getContainingDirectoryPath() {
		return this.filePath;
	}

	public String getFunctionFileExtension() {
		return this.functionFileExtension;
	}

	/**
	 * Get the full path to this function (\ will be converted to / and fn_ will be appended if required)
	 */
	public String getFullRelativePath() {
		return getFullRelativePath(getFunctionClassName(), this.filePath, this.functionFileExtension, this.appendFn_);
	}

	public static String getFullRelativePath(String functionClassName, String functionFilePath, String functionFileExtension, boolean appendFn_){
		return (functionFilePath + (functionFileExtension.length() > 0 ? "\\" : "") + (appendFn_ ? "fn_" : "") + functionClassName + functionFileExtension).replaceAll("\\\\", "/");
	}

	public static Icon getIcon() {
		return PluginIcons.ICON_SQF_FUNCTION;
	}
}
