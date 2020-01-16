package view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controller.DirectoryWatchService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.DirectoryHandler;

public class MusicBorderPain extends BorderPane {

	private DirectoryWatchService directoryWatchService = new DirectoryWatchService();
	private ExecutorService exService;

	public MusicBorderPain() {
		super();

		MenuBar menuBar = new MusicMenuBar();
		this.setTop(menuBar);

		this.setLeft(createTreeView());

		System.out.println("Starting watchservice");

		Task<Void> watchService = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				directoryWatchService.run();
				return null;
			}

		};

		this.exService = Executors.newSingleThreadExecutor();
		this.exService.submit(watchService);
	}

	private Node createTreeView() {
		GridPane grid = new GridPane();

		TreeView<String> playlistView = new TreeView<String>();
		playlistView.getStyleClass().add("margin-8");

		TreeView<String> directoryView = new TreeView<String>();
		directoryView.getStyleClass().add("margin-8");

		TreeItem<String> playlistViewRoot = new TreeItem<String>("Playlists");

		TreeItem<String> directoryViewRoot = new TreeItem<String>("Directories");
		
		DirectoryHandler dl = new DirectoryHandler(this.directoryWatchService);
		dl.load(directoryViewRoot);

		playlistView.setRoot(playlistViewRoot);

		directoryView.setRoot(directoryViewRoot);
		directoryViewRoot.setExpanded(true);
		// Make root always expanded => bad hack
		directoryViewRoot.addEventHandler(TreeItem.branchCollapsedEvent(),
				new EventHandler<TreeModificationEvent<String>>() {

					@Override
					public void handle(TreeModificationEvent<String> e) {
						if (e.getTreeItem().getValue().equals("Directories"))
							e.getTreeItem().setExpanded(true);
					}

				});

		directoryView.setContextMenu(new DirectoryContextMenu());

		grid.add(playlistView, 1, 1);
		grid.add(directoryView, 1, 2);

		return grid;
	}

	public ExecutorService getExecutorService() {
		return this.exService;
	}

}
