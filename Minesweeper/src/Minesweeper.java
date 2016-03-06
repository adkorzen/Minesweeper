import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.*;

public class Minesweeper {

	private boolean gameIsSet, gameEnded, highscoreListEmpty;
	private int width, height, mines, minesAtStart, minesLeft, pixelsPerButton;
	private double score, loadedTime;
	private String error, difficulty, bestRecords;
	private ArrayList<Integer> possibleMine, chosenMine, neighbours;
	private ArrayList<Double> easyHighscore, mediumHighscore, expertHighscore, highscoreInput;
	private ArrayList<NewButton> buttons;
	private NewButton newGameButton, loadGameButton, initiateButton, reGameButton;
	private JFrame frame;
	private ButtonGroup difficultyButtons;
	private JRadioButton easyButton, mediumButton, expertButton, customButton;
	private JPanel jradioPanel, inputPanel, gameNorthPanel, fieldPanel;
	private JTextField widthCustom, heightCustom, minesCustom;
	private JLabel widthLabel, heightLabel, minesLabel, timerLabel, minesLeftLAbel, showRecordsLabel;
	private GridLayout grid;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItemNewGame, menuItemHighscores, menuItemCloseGame;
	private JComboBox comboBox;
	private ImageIcon flagIcon, mineIcon, blankIcon, numIcon1, numIcon2, numIcon3, numIcon4, numIcon5, numIcon6, numIcon7, numIcon8;
	private SaveGame saveGame;
	private MouseListener buttonListener, flagListener;

	public static void main(String[] args) {
		new Minesweeper().preMainMenu();
	}

	public void preMainMenu() {
		newGameButton = new NewButton("New Game");
		loadGameButton = new NewButton("Load Game");
		newGameButton.addActionListener(new NewGameListener());
		loadGameButton.addActionListener(new LoadGameListener());
		pixelsPerButton = 25;

		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		menuItemNewGame = new JMenuItem("Start New Game");
		menuItemHighscores = new JMenuItem("Highscores");
		menuItemCloseGame = new JMenuItem("Close");
		menu.add(menuItemNewGame);
		menu.add(menuItemHighscores);
		menu.add(menuItemCloseGame);
		menuItemNewGame.addActionListener(new MenuListener());
		menuItemHighscores.addActionListener(new MenuListener());
		menuItemCloseGame.addActionListener(new MenuListener());

		flagIcon = new ImageIcon("images/flag.png");
		blankIcon = new ImageIcon("images/blank.png");
		mineIcon = new ImageIcon("images/mine.png");
		numIcon1 = new ImageIcon("images/num1.png");
		numIcon2 = new ImageIcon("images/num2.png");
		numIcon3 = new ImageIcon("images/num3.png");
		numIcon4 = new ImageIcon("images/num4.png");
		numIcon5 = new ImageIcon("images/num5.png");
		numIcon6 = new ImageIcon("images/num6.png");
		numIcon7 = new ImageIcon("images/num7.png");
		numIcon8 = new ImageIcon("images/num8.png");

		frame = new JFrame("(Almost) The Best Minesweeper Game");
		frame.add(newGameButton, BorderLayout.CENTER);
		frame.add(loadGameButton, BorderLayout.SOUTH);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setResizable(false);
		frame.setVisible(true);

		try {
			FileInputStream fileStream = new FileInputStream("data/GameSet.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			saveGame = (SaveGame) is.readObject();
			is.close();
			width = saveGame.getWidth();
			height = saveGame.getHeight();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		File hScore = new File("data/Highscores.sav");
		File folder = new File("data");
		folder.mkdir();

		if (!hScore.exists()) {
			new Highscore().go();
		}
		try {
			FileInputStream fileStream = new FileInputStream("data/Highscores.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			easyHighscore = (ArrayList<Double>) is.readObject();
			mediumHighscore = (ArrayList<Double>) is.readObject();
			expertHighscore = (ArrayList<Double>) is.readObject();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (!(width > 0)) {
			loadGameButton.setEnabled(false);
		}
	}

	public void mainMenu() {

		frame.getContentPane().removeAll();
		easyButton = new JRadioButton("Easy - 9x9 grid, 10 mines");
		mediumButton = new JRadioButton("Pro - 16x16 grid, 40 mines");
		expertButton = new JRadioButton("Expert - 30x16 grid, 99 mines");
		customButton = new JRadioButton("Custom");
		easyButton.setSelected(true);
		easyButton.addActionListener(new CustomListener());
		mediumButton.addActionListener(new CustomListener());
		expertButton.addActionListener(new CustomListener());
		customButton.addActionListener(new CustomListener());

		difficultyButtons = new ButtonGroup();
		difficultyButtons.add(easyButton);
		difficultyButtons.add(mediumButton);
		difficultyButtons.add(expertButton);
		difficultyButtons.add(customButton);

		jradioPanel = new JPanel();
		jradioPanel.setLayout(new BoxLayout(jradioPanel, BoxLayout.Y_AXIS));
		jradioPanel.add(easyButton);
		jradioPanel.add(mediumButton);
		jradioPanel.add(expertButton);
		jradioPanel.add(customButton);

		widthLabel = new JLabel("Width (9-30): ");
		heightLabel = new JLabel("Height (9-24): ");
		minesLabel = new JLabel("Mines (10-667): ");

		widthCustom = new JTextField("");
		heightCustom = new JTextField("");
		minesCustom = new JTextField("");
		widthCustom.setEditable(false);
		heightCustom.setEditable(false);
		minesCustom.setEditable(false);

		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.add(widthLabel);
		inputPanel.add(widthCustom);
		inputPanel.add(heightLabel);
		inputPanel.add(heightCustom);
		inputPanel.add(minesLabel);
		inputPanel.add(minesCustom);

		initiateButton = new NewButton("Start Game");
		initiateButton.setFont(new Font("SansSerif", Font.BOLD, 26));
		initiateButton.addActionListener(new InitiateListener());

		frame.add(initiateButton, BorderLayout.SOUTH);
		frame.add(inputPanel, BorderLayout.CENTER);
		frame.add(jradioPanel, BorderLayout.NORTH);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	public void loadGame() {
		buttons = new ArrayList<NewButton>();
		try {
			FileInputStream fileStream = new FileInputStream("data/Game.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			buttons = (ArrayList<NewButton>) is.readObject();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		File hScore = new File("data/Highscores.sav");
		if (!hScore.exists()) {
			new Highscore().go();
		}
		try {
			FileInputStream fileStream = new FileInputStream("data/Highscores.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			highscoreInput = (ArrayList<Double>) is.readObject();
			is.close();
			easyHighscore = highscoreInput;
			mediumHighscore = highscoreInput;
			expertHighscore = highscoreInput;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		pixelsPerButton = saveGame.getPixels();
		mines = saveGame.getMines();
		minesLeft = saveGame.getMinesLeft();
		minesAtStart = saveGame.getMinesAtStart();
		difficulty = saveGame.getHighscore();

		frame.getContentPane().removeAll();
		minesLeftLAbel = new JLabel("Mines left: " + mines);
		timerLabel = new JLabel("Time: 0:00     ");
		gameNorthPanel = new JPanel();
		gameNorthPanel.add(timerLabel);
		gameNorthPanel.add(minesLeftLAbel);
		grid = new GridLayout(saveGame.getHeight(), saveGame.getWidth());
		grid.setHgap(1);
		grid.setVgap(1);
		fieldPanel = new JPanel(grid);
		for (NewButton a : buttons) {
			fieldPanel.add(a);
			if (a.isFlagged()) {
				flagListener = new FlagListener();
				a.addMouseListener(flagListener);
			} else {
				buttonListener = new ButtonListener();
				a.addMouseListener(buttonListener);
			}
		}

		gameIsSet = true;
		gameEnded = false;
		frame.addWindowListener(new CloseFrameListener());
		frame.add(fieldPanel, BorderLayout.CENTER);
		frame.add(gameNorthPanel, BorderLayout.NORTH);
		frame.setSize(pixelsPerButton * width, pixelsPerButton * height + 40);
		loadedTime = saveGame.getLoadedTime();
		Time time = new Time();
		Thread timer = new Thread(time);
		timer.start();

		frame.setVisible(true);
	}

	public void saveGame() {
		try {
			FileOutputStream fileStream = new FileOutputStream("data/Game.sav");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(buttons);
			objectStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		SaveGame saveGame = new SaveGame(difficulty, width, height, mines, minesAtStart, minesLeft, pixelsPerButton, score);
		try {
			FileOutputStream fileStream = new FileOutputStream("data/GameSet.sav");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(saveGame);
			objectStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void setData() {
		if (easyButton.isSelected()) {
			width = 9;
			height = 9;
			mines = 10;
			difficulty = "easy";
			setUpGame();
		} else if (mediumButton.isSelected()) {
			width = 16;
			height = 16;
			mines = 40;
			difficulty = "medium";
			setUpGame();
		} else if (expertButton.isSelected()) {
			width = 30;
			height = 16;
			mines = 99;
			difficulty = "expert";
			setUpGame();
		} else {
			width = Integer.parseInt(widthCustom.getText());
			height = Integer.parseInt(heightCustom.getText());
			mines = Integer.parseInt(minesCustom.getText());
			difficulty = "custom";

			if ((width > 8 && width < 31) && (height > 8 && height < 25)
					&& (mines > 9 && mines < (width - 1) * (height - 1) + 1)) {
				setUpGame();
			} else {
				error = "";
				if (width < 9 || width > 30) {
					error += "Width out of bounds. Choose beetween 9 and 30\n";
					if (width < 9) {
						width = 9;
						widthCustom.setText("9");
					} else {
						width = 30;
						widthCustom.setText("30");
					}
				}
				if (height < 9 || height > 24) {
					error += "Height out of bounds. Choose beetween 9 and 24\n";
					if (height < 9) {
						height = 9;
						heightCustom.setText("9");
					} else {
						height = 24;
						heightCustom.setText("24");
					}
				}
				if (mines < 10 || mines > (width - 1) * (height - 1)) {
					error += "Mines range depends on size. For current size choose beetween 10 and "
							+ (width - 1) * (height - 1);
					if (mines < 10) {
						minesCustom.setText("10");
					} else {
						int text = (width - 1) * (height - 1);
						minesCustom.setText(Integer.toString(text));
					}
				}
				JOptionPane.showMessageDialog(null, error);
			}
		}
		minesAtStart = mines;
		minesLeft = width * height;
	}

	public void setUpGame() {
		frame.getContentPane().removeAll();
		timerLabel = new JLabel("Time: 0:00     ");
		minesLeftLAbel = new JLabel("Mines left: " + mines);
		gameNorthPanel = new JPanel();
		gameNorthPanel.add(timerLabel);
		gameNorthPanel.add(minesLeftLAbel);

		buttons = new ArrayList<NewButton>();
		grid = new GridLayout(height, width);
		grid.setHgap(1);
		grid.setVgap(1);
		fieldPanel = new JPanel(grid);
		for (int i = 0; i < width * height; i++) {
			NewButton a = new NewButton();
			buttons.add(a);
			fieldPanel.add(a);
			buttonListener = new ButtonListener();
			a.addMouseListener(buttonListener);
		}
		frame.addWindowListener(new CloseFrameListener());
		frame.add(fieldPanel, BorderLayout.CENTER);
		frame.add(gameNorthPanel, BorderLayout.NORTH);
		frame.setSize(pixelsPerButton * width, pixelsPerButton * height + 40);
		frame.setVisible(true);
	}

	public void reveal(NewButton b) {
		if (b.getMine()) {
			bust();
		} else if (b.getBombNeighbours() > 0) {
			if (b.isEnabled() && !b.isFlagged()) {
				displayNum(b);
				b.setEnabled(false);
				b.deFlag();
				minesLeft--;
				updateMines();
			}
		} else {
			if (b.isEnabled()) {
				displayNum(b);
				b.setEnabled(false);
				b.deFlag();
				minesLeft--;
				updateMines();
			}
			neighbours(buttons.indexOf(b));
			for (int a : neighbours) {
				if (buttons.get(a).isEnabled()) {
					displayNum(buttons.get(a));
					buttons.get(a).setEnabled(false);
					buttons.get(a).deFlag();
					minesLeft--;
					updateMines();
					if (buttons.get(a).getBombNeighbours() > 0) {
						displayNum(buttons.get(a));
					} else {
						reveal(buttons.get(a));
					}
				}
			}
		}
	}

	public void bust() {
		gameEnded = true;
		for (NewButton b : buttons) {
			if (b.getMine()) {
				b.setIcon(mineIcon);
				b.setDisabledIcon(mineIcon);
			} else if (b.getBombNeighbours() > 0) {
				displayNum(b);
			} else if (b.getBombNeighbours() == 0) {
				displayNum(b);
			}
			b.setEnabled(false);
			b.deFlag();
		}
		File file = new File("data/Game.sav");
		File file2 = new File("data/GameSet.sav");
		loadedTime = 0;
		try {
			Files.deleteIfExists(file.toPath());
			Files.deleteIfExists(file2.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showOptionDialog(null, "You lost, you loser!", "Game lost", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);
		setUpNextGame();
	}

	public void setUpNextGame() {
		gameIsSet = false;
		gameEnded = false;
		frame.getContentPane().removeAll();
		reGameButton = new NewButton("Play New Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(reGameButton);
		reGameButton.addActionListener(new NewGameListener());
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public NewButton getButton(int x, int y) {
		int index = x + width * (y - 1) - 1;
		return buttons.get(index);
	}

	public int getIndex(int x, int y) {
		return x + width * (y - 1) - 1;
	}

	public int getX(NewButton b) {
		int index = buttons.indexOf(b);
		return (index) % width + 1;
	}

	public int getX(int index) {
		return (index) % width + 1;
	}

	public int getY(NewButton b) {
		double index = buttons.indexOf(b);
		return (int) ((index / width + 1));
	}

	public int getY(int index) {
		return (int) (int) ((index / width + 1));
	}

	public void neighbours(int index) {
		int x = getX(index);
		int y = getY(index);
		neighbours = new ArrayList<Integer>();
		if (x > 1 && x < width && y > 1 && y < height) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (x == 1 && y == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + 1);
			neighbours.add(index);
		} else if (x == width && y == height) {
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (x == 1 && y == height) {
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
		} else if (x == width && y == 1) {
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index);
			neighbours.add(index - 1);
		} else if (x == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
		} else if (x == width) {
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (y == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
		} else if (y == height) {
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		}
	}

	public void displayNum(NewButton b) {
		if (!b.getMine()) {
			switch (b.getBombNeighbours()) {
			case 0:
				b.setIcon(blankIcon);
				b.setDisabledIcon(blankIcon);
				break;
			case 1:
				b.setIcon(numIcon1);
				b.setDisabledIcon(numIcon1);
				break;
			case 2:
				b.setIcon(numIcon2);
				b.setDisabledIcon(numIcon2);
				break;
			case 3:
				b.setIcon(numIcon3);
				b.setDisabledIcon(numIcon3);
				break;
			case 4:
				b.setIcon(numIcon4);
				b.setDisabledIcon(numIcon4);
				break;
			case 5:
				b.setIcon(numIcon5);
				b.setDisabledIcon(numIcon5);
				break;
			case 6:
				b.setIcon(numIcon6);
				b.setDisabledIcon(numIcon6);
				break;
			case 7:
				b.setIcon(numIcon7);
				b.setDisabledIcon(numIcon7);
				break;
			case 8:
				b.setIcon(numIcon8);
				b.setDisabledIcon(numIcon8);
				break;
			default:
				break;
			}
		}
	}

	public void updateMines() {
		mines = minesAtStart;
		for (NewButton a : buttons) {
			if (a.isFlagged()) {
				mines--;
			}
		}
		minesLeftLAbel.setText("Mines left:" + mines);
	}

	public class NewGameListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			mainMenu();
		}
	}

	public class LoadGameListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			loadGame();
		}
	}

	public class CustomListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			if (customButton.isSelected()) {
				widthCustom.setEditable(true);
				heightCustom.setEditable(true);
				minesCustom.setEditable(true);
			} else {
				widthCustom.setEditable(false);
				heightCustom.setEditable(false);
				minesCustom.setEditable(false);
			}
		}
	}

	public class InitiateListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {

			File file = new File("data/Game.sav");
			File file2 = new File("data/GameSet.sav");
			loadedTime = 0;
			try {
				Files.deleteIfExists(file.toPath());
				Files.deleteIfExists(file2.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			setData();
		}
	}

	public class ButtonListener implements MouseListener {

		public void mouseClicked(MouseEvent m) {
		}

		public void mouseEntered(MouseEvent m) {
		}

		public void mouseExited(MouseEvent m) {
		}

		public void mousePressed(MouseEvent m) {

			if (m.getButton() == MouseEvent.BUTTON1 && !gameIsSet) {
				gameIsSet = true;
				displayNum((NewButton) m.getComponent());
				m.getComponent().setEnabled(false);
				((NewButton) (m.getComponent())).deFlag();
				minesLeft--;
				updateMines();
				possibleMine = new ArrayList<Integer>();
				chosenMine = new ArrayList<Integer>();
				int index = buttons.indexOf(m.getComponent());
				for (int i = 0; i < width * height; i++) {
					possibleMine.add(i);
				}
				neighbours(index);
				for (int i : neighbours) {
					possibleMine.remove(i);
				}
				neighbours = null;
				for (int i = 0; i < mines; i++) {
					int inde = (int) (Math.random() * possibleMine.size());
					chosenMine.add(possibleMine.get(inde));
					buttons.get(possibleMine.get(inde)).setMine(true);
					//
					//buttons.get(possibleMine.get(inde)).setText("X");
					//
					possibleMine.remove(possibleMine.get(inde));
				}
				for (NewButton b : buttons) {
					if (!b.getMine()) {

						int inde = buttons.indexOf(b);
						int count = 0;
						neighbours(inde);
						for (int i : neighbours) {
							if (buttons.get(i).getMine()) {
								count++;
							}
							try {
								b.setBombNeighbours(count);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						neighbours = null;
					}
				}
				reveal((NewButton) (m.getComponent()));

				Time time = new Time();
				Thread timer = new Thread(time);
				timer.start();
			} else if (gameIsSet && m.getButton() == MouseEvent.BUTTON1 && m.getComponent().isEnabled()) {
				NewButton butt = (NewButton) (m.getComponent());
				if (!butt.isFlagged()) {
					reveal((NewButton) (m.getComponent()));
				}
			} else if (gameIsSet && m.getButton() == MouseEvent.BUTTON3) {
				if (m.getComponent().isEnabled()) {
					NewButton flag = (NewButton) (m.getComponent());
					if (!flag.isFlagged()) {
						flag.setFlagged();
						flag.setIcon(flagIcon);
						mines--;
						minesLeftLAbel.setText("Mines left: " + mines);
						flag.removeMouseListener(buttonListener);
						flagListener = new FlagListener();
						flag.addMouseListener(flagListener);
					}
				}
			}
			if (minesAtStart == minesLeft) {
				gameEnded = true;
				String information = "";
				File file = new File("data/Game.sav");
				File file2 = new File("data/GameSet.sav");
				loadedTime = 0;
				try {
					Files.deleteIfExists(file.toPath());
					Files.deleteIfExists(file2.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (score > 0) {
					Highscore highscoreOutput = new Highscore(easyHighscore, mediumHighscore, expertHighscore);
					if (difficulty.equals("easy")) {
						for (Double el : easyHighscore) {
							if (score < el) {
								if (easyHighscore.indexOf(el) == 0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								} else if (easyHighscore.indexOf(el) > 0) {
									String th = "th";
									if (easyHighscore.indexOf(el) == 1) {
										th = "nd";
									}
									if (easyHighscore.indexOf(el) == 2) {
										th = "rd";
									}
									information = "Contratulations, you achieved " + (easyHighscore.indexOf(el) + 1)
											+ th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								}
								easyHighscore.add(easyHighscore.indexOf(el), new Double(score));
								break;
							}
							information = "Contratulations, you won the game \nYour score is: ";
							int scoreSeconds = (int) (score % 60);
							int scoreMinutes = (int) (score / 60) % 60;
							information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
						}
						if (easyHighscore.size() > 5) {
							easyHighscore.remove(5);
						}
						highscoreOutput.setHighscoreList("easy", easyHighscore);

					} else if (difficulty.equals("medium")) {
						for (Double el : mediumHighscore) {
							if (score < el) {
								if (mediumHighscore.indexOf(el) == 0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								} else if (mediumHighscore.indexOf(el) > 0) {
									String th = "th";
									if (mediumHighscore.indexOf(el) == 1) {
										th = "nd";
									}
									if (mediumHighscore.indexOf(el) == 2) {
										th = "rd";
									}
									information = "Contratulations, you achieved " + (mediumHighscore.indexOf(el) + 1)
											+ th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								}
								mediumHighscore.add(mediumHighscore.indexOf(el), new Double(score));
								break;
							}
							information = "Contratulations, you won the game \nYour score is: ";
							int scoreSeconds = (int) (score % 60);
							int scoreMinutes = (int) (score / 60) % 60;
							information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
						}
						if (mediumHighscore.size() > 5) {
							mediumHighscore.remove(5);
						}
						highscoreOutput.setHighscoreList("medium", mediumHighscore);

					} else if (difficulty.equals("expert")) {
						for (Double el : expertHighscore) {
							if (score < el) {
								if (expertHighscore.indexOf(el) == 0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								} else if (expertHighscore.indexOf(el) > 0) {
									String th = "th";
									if (expertHighscore.indexOf(el) == 1) {
										th = "nd";
									}
									if (expertHighscore.indexOf(el) == 2) {
										th = "rd";
									}
									information = "Contratulations, you achieved " + (expertHighscore.indexOf(el) + 1)
											+ th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
								}
								expertHighscore.add(expertHighscore.indexOf(el), new Double(score));
								break;
							}
							information = "Contratulations, you won the game \nYour score is: ";
							int scoreSeconds = (int) (score % 60);
							int scoreMinutes = (int) (score / 60) % 60;
							information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
						}
						if (expertHighscore.size() > 5) {
							expertHighscore.remove(5);
						}
						highscoreOutput.setHighscoreList("expert", expertHighscore);
					} else {
						information = "Contratulations, you won the game \nYour score is: ";
						int scoreSeconds = (int) (score % 60);
						int scoreMinutes = (int) (score / 60) % 60;
						information += String.format("%d:%02d", scoreMinutes, scoreSeconds);
					}
					try {
						FileOutputStream fileStream = new FileOutputStream("data/Highscores.sav");
						ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
						try {
							objectStream.writeObject(highscoreOutput.getHighscoreList("easy"));
							objectStream.writeObject(highscoreOutput.getHighscoreList("medium"));
							objectStream.writeObject(highscoreOutput.getHighscoreList("expert"));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						objectStream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					JOptionPane.showOptionDialog(null, information, "Game won!", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, null, null);
					setUpNextGame();
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class FlagListener implements MouseListener {
		public void mouseClicked(MouseEvent m) {
		}

		public void mouseEntered(MouseEvent m) {
		}

		public void mouseExited(MouseEvent m) {
		}

		public void mousePressed(MouseEvent m) {
			if (gameIsSet && m.getButton() == MouseEvent.BUTTON3) {
				if (m.getComponent().isEnabled()) {
					NewButton flag = (NewButton) (m.getComponent());
					if (flag.isFlagged()) {
						flag.deFlag();
						flag.setIcon(null);
						mines++;
						minesLeftLAbel.setText("Mines left: " + mines);
						flag.removeMouseListener(flagListener);
						flag.addMouseListener(buttonListener);
					}
				}
			}
		}

		public void mouseReleased(MouseEvent m) {
		}
	}

	public class CloseFrameListener implements WindowListener {

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowClosing(WindowEvent arg0) {
			if (gameIsSet) {
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				int result = JOptionPane.showConfirmDialog(null, "Save game?", "Do you want to save game?",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (result == JOptionPane.YES_OPTION) {
					saveGame();
					System.exit(0);
				} else if (result == JOptionPane.NO_OPTION) {
					File file = new File("data/Game.sav");
					File file2 = new File("data/GameSet.sav");
					try {
						Files.deleteIfExists(file.toPath());
						Files.deleteIfExists(file2.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				} else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
				}
			}

		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowOpened(WindowEvent arg0) {
		}
	}

	public class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			if (a.getSource() == menuItemNewGame) {
				if (gameIsSet) {
					int result = JOptionPane.showConfirmDialog(null, "Start new game?", "Do you want to start new game?",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						File file = new File("data/Game.sav");
						File file2 = new File("data/GameSet.sav");
						try {
							Files.deleteIfExists(file.toPath());
							Files.deleteIfExists(file2.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						gameEnded = true;

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						setUpNextGame();
						mainMenu();
					} else if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
					}
				} else {
					File file = new File("data/Game.sav");
					File file2 = new File("data/GameSet.sav");
					try {
						Files.deleteIfExists(file.toPath());
						Files.deleteIfExists(file2.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					gameEnded = true;

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setUpNextGame();
					mainMenu();
				}

			} else if (a.getSource() == menuItemHighscores) {
				String[] string = { "Easy", "Pro", "Expert" };

				comboBox = new JComboBox(string);
				comboBox.addActionListener(new ComboboxListener());
				highscoreListEmpty = true;
				String hint = "Choose difficulty level";

				bestRecords = "<html>Best results for Easy level: <br>";
				for (Double el : easyHighscore) {
					if (el < 99999) {
						highscoreListEmpty = false;
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = easyHighscore.indexOf(el) + 1;
						bestRecords += place + "<html>:      " + String.format("%d:%02d", scoreMinutes, scoreSeconds)
								+ "<br>";
					} else {
						break;
					}
				}
				bestRecords += "</html>";
				if (highscoreListEmpty)
					bestRecords = "None";
				showRecordsLabel = new JLabel(bestRecords);
				highscoreListEmpty = true;

				Object[] things = new Object[3];
				things[0] = hint;
				things[1] = comboBox;
				things[2] = showRecordsLabel;

				JOptionPane dialog = new JOptionPane();
				dialog.showMessageDialog(null, things);

			} else if (a.getSource() == menuItemCloseGame) {
				if (gameIsSet) {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					int result = JOptionPane.showConfirmDialog(null, "Save game?", "Do you want to save game?",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						saveGame();
						System.exit(0);
					} else if (result == JOptionPane.NO_OPTION) {
						File file = new File("data/Game.sav");
						File file2 = new File("data/GameSet.sav");
						try {
							Files.deleteIfExists(file.toPath());
							Files.deleteIfExists(file2.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					} else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
					}
				} else {
					System.exit(0);
				}
			}
		}

	}

	public class ComboboxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (comboBox.getSelectedIndex() == 0) {
				bestRecords = "<html>Best results for Easy level: <br>";
				for (Double el : easyHighscore) {
					if (el < 99999) {
						highscoreListEmpty = false;
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = easyHighscore.indexOf(el) + 1;
						bestRecords += place + "<html>:      " + String.format("%d:%02d", scoreMinutes, scoreSeconds)
								+ "<br>";
					} else {
						break;
					}
				}
				bestRecords += "</html>";
				if (highscoreListEmpty)
					bestRecords = "None";
				showRecordsLabel.setText(bestRecords);
				highscoreListEmpty = true;
			} else if (comboBox.getSelectedIndex() == 1) {
				bestRecords = "<html>Best results for Pro level: <br>";
				for (Double el : mediumHighscore) {
					if (el < 99999) {
						highscoreListEmpty = false;
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = mediumHighscore.indexOf(el) + 1;
						bestRecords += place + "<html>:      " + String.format("%d:%02d", scoreMinutes, scoreSeconds)
								+ "<br>";
					} else {
						break;
					}
				}
				bestRecords += "</html>";
				if (highscoreListEmpty)
					bestRecords = "None";
				showRecordsLabel.setText(bestRecords);
				highscoreListEmpty = true;
			} else if (comboBox.getSelectedIndex() == 2) {
				bestRecords = "<html>Best results for Expert level: <br>";
				for (Double el : expertHighscore) {
					if (el < 99999) {
						highscoreListEmpty = false;
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = expertHighscore.indexOf(el) + 1;
						bestRecords += place + "<html>:      " + String.format("%d:%02d", scoreMinutes, scoreSeconds)
								+ "<br>";
					} else {
						break;
					}
				}
				bestRecords += "</html>";
				if (highscoreListEmpty)
					bestRecords = "None";
				showRecordsLabel.setText(bestRecords);
				highscoreListEmpty = true;
			}
		}
	}

	public class Time implements Runnable {
		long startTime, currentTime;
		double currentTimer;
		int timerSeconds, timerMinutes, timerHours;

		public void run() {
			go();
		}

		public void go() {
			startTime = System.currentTimeMillis();

			while (!gameEnded) {
				currentTime = System.currentTimeMillis();
				timerLabel.setText(String.format("Time:  %d:%02d     ", timerMinutes, timerSeconds));
				currentTimer = (currentTime - startTime) / 1000.0 + loadedTime;
				timerSeconds = (int) (currentTimer % 60);
				timerMinutes = (int) (currentTimer / 60) % 60;
				score = currentTimer;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
