package com.kaylerrenslow.a3plugin.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;

/**
 * Created on 01/02/2016.
 */
public class PluginUtil{

	public static File convertURLToFile(URL url){
		File f;
		try{
			f = new File(url.toURI());
			f = new File(f.getPath());
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
		return f;
	}

	@NotNull
	public static Module getModuleForPsiFile(PsiFile file){
		final ProjectFileIndex index = ProjectRootManager.getInstance(file.getProject()).getFileIndex();

		if(file.getVirtualFile() == null){
			file = file.getOriginalFile();
//			return null;
		}
		final Module module = index.getModuleForFile(file.getVirtualFile());
		return module;
	}

}
