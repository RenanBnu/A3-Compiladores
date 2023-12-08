package br.trabalhocompiladores.frontend;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import br.trabalhocompiladores.backend.LexicalAnalyzer;
import br.trabalhocompiladores.backend.SyntacticAnalyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserInterface extends JFrame {

    private static final long serialVersionUID = 1943732405160942512L;
    private File openedFile;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField statusBar;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UserInterface frame = new UserInterface();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private UserInterface() {
        setTitle("Trabalho Compiladores - 2023");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setMinimumSize(new Dimension(900, 600));
        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollEditor = new JScrollPane();
        scrollEditor.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollEditor.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        inputTextArea = new JTextArea();
        inputTextArea.setBorder(new NumeroBorda());
        scrollEditor.setViewportView(inputTextArea);
        contentPane.add(scrollEditor, BorderLayout.CENTER);

        JPanel panelStatus = new JPanel();
        contentPane.add(panelStatus, BorderLayout.SOUTH);
        GridBagLayout gbl_panelStatus = new GridBagLayout();
        gbl_panelStatus.columnWidths = new int[]{900};
        gbl_panelStatus.rowHeights = new int[]{130};
        gbl_panelStatus.columnWeights = new double[]{1.0};
        gbl_panelStatus.rowWeights = new double[]{0.0, 0.0};
        panelStatus.setLayout(gbl_panelStatus);

        JScrollPane scrollOutput = new JScrollPane();
        scrollOutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollOutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setFocusable(false);
        scrollOutput.setViewportView(outputTextArea);
        GridBagConstraints gbc_scrollOutput = new GridBagConstraints();
        gbc_scrollOutput.fill = GridBagConstraints.BOTH;
        gbc_scrollOutput.gridx = 0;
        gbc_scrollOutput.gridy = 0;
        panelStatus.add(scrollOutput, gbc_scrollOutput);

        statusBar = new JTextField();
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);
        statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.setEditable(false);
        statusBar.setFocusable(false);
        statusBar.setColumns(10);
        GridBagConstraints gbc_status = new GridBagConstraints();
        gbc_status.anchor = GridBagConstraints.SOUTH;
        gbc_status.fill = GridBagConstraints.BOTH;
        gbc_status.gridx = 0;
        gbc_status.gridy = 1;
        panelStatus.add(statusBar, gbc_status);

        JPanel panelTop = new JPanel();
        contentPane.add(panelTop, BorderLayout.NORTH);
        panelTop.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        panelTop.add(buttonPanel, BorderLayout.WEST);
        GridBagLayout gbl_buttonPanel = new GridBagLayout();
        gbl_buttonPanel.columnWidths = new int[]{100, 100, 100, 100, 100, 100, 100, 100};
        gbl_buttonPanel.rowHeights = new int[]{70};
        buttonPanel.setLayout(gbl_buttonPanel);

        JButton btnNew = new JButton("Novo [ctrl-n]");
        ImageIcon novo = new ImageIcon(getClass().getResource("icons/new-file.png"));
        btnNew.setIcon(novo);
		btnNew.setHorizontalTextPosition(SwingConstants.CENTER);
		btnNew.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        btnNew.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        GridBagConstraints gbc_btnNew = new GridBagConstraints();
        gbc_btnNew.insets = new Insets(0, 5, 0, 1);
        gbc_btnNew.fill = GridBagConstraints.BOTH;
        gbc_btnNew.gridx = 0;
        gbc_btnNew.gridy = 0;
        buttonPanel.add(btnNew, gbc_btnNew);
        btnNew.addActionListener(arg0 -> newFile());
        btnNew.setFocusable(false);
        //btnNew.setIcon(new ImageIcon(getClass().getResource("../../../icons/new-file.png")));
        btnNew.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnNew.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnOpenFile = new JButton("Abrir [ctrl-o]");
        ImageIcon abrir = new ImageIcon(getClass().getResource("icons/open-file.png"));
        btnOpenFile.setIcon(abrir);
        btnOpenFile.setHorizontalTextPosition(SwingConstants.CENTER);
        btnOpenFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnOpen = new GridBagConstraints();
        gbc_btnOpen.insets = new Insets(0, 0, 0, 1);
        gbc_btnOpen.fill = GridBagConstraints.BOTH;
        gbc_btnOpen.gridx = 1;
        gbc_btnOpen.gridy = 0;
        buttonPanel.add(btnOpenFile, gbc_btnOpen);
        btnOpenFile.addActionListener(e -> openFile());

        btnOpenFile.setFocusable(false);
        //btnOpenFile.setIcon(new ImageIcon(getClass().getResource("../../../icons/open-file.png")));
        btnOpenFile.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnOpenFile.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnSave = new JButton("Salvar [ctrl-s]");
        ImageIcon salvar = new ImageIcon(getClass().getResource("icons/save.png"));
        btnSave.setIcon(salvar);
		btnSave.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSave.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnSave = new GridBagConstraints();
        gbc_btnSave.insets = new Insets(0, 0, 0, 1);
        gbc_btnSave.fill = GridBagConstraints.BOTH;
        gbc_btnSave.gridx = 2;
        gbc_btnSave.gridy = 0;
        buttonPanel.add(btnSave, gbc_btnSave);
        btnSave.addActionListener(e -> saveFile());
        btnSave.setFocusable(false);
//        btnSave.setIcon(new ImageIcon(getClass().getResource("../../../icons/save.png")));
        btnSave.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnSave.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnCopy = new JButton("Copiar [ctrl-c]");
        ImageIcon copiar = new ImageIcon(getClass().getResource("icons/copy.png"));
        btnCopy.setIcon(copiar);
		btnCopy.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCopy.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnCopy = new GridBagConstraints();
        gbc_btnCopy.insets = new Insets(0, 0, 0, 1);
        gbc_btnCopy.fill = GridBagConstraints.BOTH;
        gbc_btnCopy.gridx = 3;
        gbc_btnCopy.gridy = 0;
        buttonPanel.add(btnCopy, gbc_btnCopy);
        btnCopy.addActionListener(e -> copy());
        btnCopy.setFocusable(false);
//        btnCopy.setIcon(new ImageIcon(getClass().getResource("../../../icons/copy.png")));
        btnCopy.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCopy.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnPaste = new JButton("Colar [ctrl-v]");
        ImageIcon colar = new ImageIcon(getClass().getResource("icons/paste.png"));
        btnPaste.setIcon(colar);
		btnPaste.setHorizontalTextPosition(SwingConstants.CENTER);
		btnPaste.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnPaste = new GridBagConstraints();
        gbc_btnPaste.insets = new Insets(0, 0, 0, 1);
        gbc_btnPaste.fill = GridBagConstraints.BOTH;
        gbc_btnPaste.gridx = 4;
        gbc_btnPaste.gridy = 0;
        buttonPanel.add(btnPaste, gbc_btnPaste);
        btnPaste.addActionListener(e -> paste());
        btnPaste.setFocusable(false);
//        btnPaste.setIcon(new ImageIcon(getClass().getResource("../../../icons/paste.png")));
        btnPaste.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnPaste.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnCut = new JButton("Recortar [ctrl-x]");
        ImageIcon recortar = new ImageIcon(getClass().getResource("icons/cut.png"));
        btnCut.setIcon(recortar);
		btnCut.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCut.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnCut = new GridBagConstraints();
        gbc_btnCut.insets = new Insets(0, 0, 0, 1);
        gbc_btnCut.fill = GridBagConstraints.BOTH;
        gbc_btnCut.gridx = 5;
        gbc_btnCut.gridy = 0;
        buttonPanel.add(btnCut, gbc_btnCut);
        btnCut.addActionListener(e -> cut());
        btnCut.setFocusable(false);
//        btnCut.setIcon(new ImageIcon(getClass().getResource("../../../icons/cut.png")));
        btnCut.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCut.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnCompile = new JButton("Compilar [F7]");
        ImageIcon compilar = new ImageIcon(getClass().getResource("icons/play.png"));
        btnCompile.setIcon(compilar);
		btnCompile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCompile.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnCompile = new GridBagConstraints();
        gbc_btnCompile.insets = new Insets(0, 0, 0, 1);
        gbc_btnCompile.fill = GridBagConstraints.BOTH;
        gbc_btnCompile.gridx = 6;
        gbc_btnCompile.gridy = 0;
        buttonPanel.add(btnCompile, gbc_btnCompile);
        btnCompile.addActionListener(e -> compile());
        btnCompile.setFocusable(false);
//        btnCompile.setIcon(new ImageIcon(getClass().getResource("../../../icons/play.png")));
        btnCompile.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCompile.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton btnTeam = new JButton("Equipe [F1]");
        ImageIcon equipe = new ImageIcon(getClass().getResource("icons/team.png"));
        btnTeam.setIcon(equipe);
		btnTeam.setHorizontalTextPosition(SwingConstants.CENTER);
		btnTeam.setVerticalTextPosition(SwingConstants.BOTTOM);
		
        GridBagConstraints gbc_btnTeam = new GridBagConstraints();
        gbc_btnTeam.insets = new Insets(0, 0, 0, 1);
        gbc_btnTeam.fill = GridBagConstraints.BOTH;
        gbc_btnTeam.gridx = 7;
        gbc_btnTeam.gridy = 0;
        buttonPanel.add(btnTeam, gbc_btnTeam);
        btnTeam.addActionListener(e -> printTeamName());
        btnTeam.setFocusable(false);
//        btnTeam.setIcon(new ImageIcon(getClass().getResource("../../../icons/team.png")));
        btnTeam.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnTeam.setHorizontalTextPosition(SwingConstants.CENTER);

        /*
         *  Key listners.
         */
        inputTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @SuppressWarnings("deprecation")
			public void keyPressed(java.awt.event.KeyEvent evt) {
                if ((evt.getKeyCode() == KeyEvent.VK_N) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    newFile();
                }
                if ((evt.getKeyCode() == KeyEvent.VK_O) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    openFile();
                }
                if ((evt.getKeyCode() == KeyEvent.VK_S) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    saveFile();
                }
                if ((evt.getKeyCode() == KeyEvent.VK_C) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    copy();
                }
                if ((evt.getKeyCode() == KeyEvent.VK_V) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    paste();
                }
                if ((evt.getKeyCode() == KeyEvent.VK_X) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    cut();
                }
                if (evt.getKeyCode() == KeyEvent.VK_F7) {
                    compile();
                }
                if (evt.getKeyCode() == KeyEvent.VK_F1) {
                    printTeamName();
                }
            }
        });
    }

    /*
     * Operações dos botões.
     */

    private void newFile() {
        cleanFields();
        openedFile = null;
    }

    private void openFile() {
        String error = "";
        try {
            error = "abrir a seleção de arquivos";
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Escolha o arquivo...");
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setApproveButtonText("OK");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(fileChooser);

            if (result == JFileChooser.APPROVE_OPTION) {
                error = "limpar os campos";
                cleanFields();
                File file = fileChooser.getSelectedFile();
                FileReader fileReader = new FileReader(file);
                BufferedReader buffer = new BufferedReader(fileReader);

                String line = buffer.readLine();
                StringBuilder text = new StringBuilder();

                while (line != null) {
                    text.append(line);
                    line = buffer.readLine();

                    if (inputTextArea.getText().isEmpty()) {
                        text.append("\n");
                    }
                }

                statusBar.setText(file.getAbsolutePath());
                inputTextArea.setText(text.toString());
                openedFile = file;
                fileReader.close();
            }

        } catch (Exception erro) {
            System.out.println("Ocorreu o seguinte erro ao " + error + ". Mensagem de erro: " + erro.getMessage());
        }
    }

    private void saveFile() {
        String errorMessage = "";

        try {
        	 errorMessage = "abrir a seleção de pastas";
             JFileChooser fileChooser = new JFileChooser();
             fileChooser.setDialogTitle("Salvando arquivo...");
             fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
             fileChooser.setApproveButtonText("OK");
             fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
             int result = fileChooser.showOpenDialog(fileChooser);
             if (result == JFileChooser.APPROVE_OPTION) {
                 errorMessage = "criar um arquivo";
                 File createdFile = new File(fileChooser.getSelectedFile().getPath());

                 errorMessage = "adicionar as informações no arquivo";
                 final String createdFileAbsolutePath = createdFile.getAbsolutePath();
                 final String createdFileName = createdFileAbsolutePath.contains(".txt") ? createdFileAbsolutePath : createdFileAbsolutePath + ".txt";
                 FileWriter fileWriter = new FileWriter(createdFileName);
                 fileWriter.write(inputTextArea.getText());
                 fileWriter.close();

                 statusBar.setText(createdFileName);
                 openedFile = new File(createdFileName);
             }
        } catch (Exception ex) {
            System.out.println("Ocorreu o seguinte erro ao " + errorMessage + ". Mensagem de erro: " + ex.getMessage());
        }
    }

    private void copy() {
        StringSelection stringSelection = new StringSelection(inputTextArea.getSelectedText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    private void paste() {
        try {
            String copiedContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            inputTextArea.append(copiedContent);
        } catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private void cut() {
        StringSelection stringSelection = new StringSelection(inputTextArea.getSelectedText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        String texto = inputTextArea.getText().substring(0, inputTextArea.getSelectionStart());
        texto += inputTextArea.getText().substring(inputTextArea.getSelectionEnd(), inputTextArea.getText().length());
        inputTextArea.setText(texto);
    }

    private void compile() {
    	try {    		
    		outputTextArea.setText("");
    		
    		boolean lexicalValid = false;
    		boolean syntaticValid = false;

    		List<Map<Integer, Map<String, String>>> lines = LexicalAnalyzer.compileLexical(inputTextArea.getText()); 
    		String tokensText = "";
    		String fixValue = "";
    		
    		for (Map<Integer, Map<String, String>> line : lines) {
    			
    			for(Map.Entry<Integer, Map<String, String>> token : line.entrySet()) {

        			String[] parts = token.toString().split("=");
        			
        			if (parts.length == 4)
        				fixValue = "="; 
        			else if (parts.length == 5)
        				fixValue = "=="; 
        			else
        				fixValue = parts[2].contains("}}") ? "}" : parts[2].replaceAll("}", "");
        			
        			tokensText += parts[1].replace("{", "") + " : " + fixValue + "\n";
    			}
    		}
    		    		
    		syntaticValid = SyntacticAnalyzer.compileSyntatic(inputTextArea.getText(), lines);

    		if (!lines.isEmpty() && syntaticValid)
    			outputTextArea.setText(tokensText + "\nPrograma compilado com sucesso!!");

    	} catch (Exception e) {
    		outputTextArea.setText(e.getMessage());
    	}
    }

    private void printTeamName() {
        outputTextArea.setText("");
        outputTextArea.setText("Desenvolvedores do compilador\n - Renan e Gabriel.");
    }

    private void cleanFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        statusBar.setText("");
    }
}
