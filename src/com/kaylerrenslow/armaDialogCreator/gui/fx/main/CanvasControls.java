package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.IPositionCalculator;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 Created by Kayler on 05/15/2016.
 */
class CanvasControls extends VBox implements IPositionCalculator{

	MenuItem mi = new MenuItem("New Folder");
	ContextMenu cm = new ContextMenu(mi);
	private final EditableTreeView treeView = new EditableTreeView("Test", new ITreeCellSelectionUpdate() {
		@Override
		public void selectionUpdate(CellType cellType) {

		}
	});

	CanvasControls() {
		super(5);
		getChildren().addAll(treeView);

		treeView.setContextMenu(cm);
		TreeViewMenuItemBuilder.setNewFolderAction(treeView, mi, "New Folder");
		treeView.addChildToRoot("test", 0);
		treeView.addChildToRoot("test1", 0);
		MoveableTreeItem item = new MoveableTreeItem("item");
		item.setGraphic(new TextField("tst"));
		treeView.addChildToRoot(item, 0);

		VBox.setVgrow(treeView, Priority.ALWAYS);
	}

	@Override
	public double getGridScale() {
		return 1;
	}

	@Override
	public int smallestSnapPercentage() {
		return 1;
	}

	@Override
	public int snapPercentage() {
		return 5;
	}
}
