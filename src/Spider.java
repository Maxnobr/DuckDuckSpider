//This program takes a URl and finds all URLs in <> tags.

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;


public class Spider extends JFrame implements ActionListener {

    private static final String DEFAULT_URL = "https://www.w3schools.com/";
    private JTextField url;         //textfield to enter the remote machine
    private JTextField maxSites;    //textfield to enter the remote file name
    private JEditorPane result;        //textarea to display everything the server returns
    private Index ind;
    private JTextField textField = new JTextField(1000);

    public static void main(String[] cmd) {
        new Spider();
    } //main

    private Spider() {

        this.add(textField, BorderLayout.SOUTH);
        String placeHolderText = "Type your search";
        textField.setText(placeHolderText);
        textField.setColumns(5);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeHolderText)) {
                    textField.setText("");
                }
            }

            // This focusLost code seems to be a bit buggy for some reason.
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().equals("")) {
                    textField.setText(placeHolderText);
                    // Without setting draw circles to true, program will
                    // begin connecting dots after focus is lost.
                }
            }
        });

        // Listen for changes in the text
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            void warn() {
                if (ind != null) {
                    result.setText("");
                    String search = textField.getText();
                    String[] query = search.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+");
                    ArrayList<String> parsed = new ArrayList<>();
                    for (String s : query) {
                        s = s.replaceAll(" ", "");
                        if (s.length() > 1 && (!Index.crapWords.contains(s) || s.equals("or")))
                            parsed.add(s);
                    }
                    if (parsed.size() > 0) {
                        System.out.println("Searching...");
                        query = new String[parsed.size()];
                        parsed.toArray(query);
                        List<Index.Page> pages = ind.getPages(!parsed.contains("or"), query);
                        pages.sort(Comparator.comparingInt(p -> p.searchValue));
                        if (pages.size() > 0) {
                            final int[] i = {1};
                            StringBuilder sb = new StringBuilder();
                            pages.iterator().forEachRemaining(s ->
                                    sb.append("<p>").append(i[0]++).append(". ").append("<a href=\"").append(s.url)
                                            .append("\">").append(s.title).append("</a></p>"));
                            result.setText(sb.toString());
                            result.setCaretPosition(0);
                        } else
                            result.setText("No results found.");
                    }
                }
            }
        });

        Container cp = getContentPane();//Frame's content pane
        JPanel url_pane = new JPanel();    //panel for content pane's North area
        url = new JTextField(35);
        url.setText(DEFAULT_URL);
        url_pane.add(new JLabel("Site:"));
        url_pane.add(url);
        maxSites = new JTextField("50", 24);
        url_pane.add(new JLabel("Max Sites:"));
        url_pane.add(maxSites);
        JButton go = new JButton("Go");    //button to activate the retrieval
        go.addActionListener(this);
        go.setBackground(Color.red);
        url_pane.add(go);
        cp.add(url_pane, BorderLayout.NORTH);

        setBounds(50, 150, 1400, 700);

        result = new JEditorPane();
        result.setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        result.setEditorKit(editorKit);
        Dimension size = new Dimension(getWidth()-70,getHeight()-150);
        result.setSize(size);
        result.setMinimumSize(size);
        result.setMaximumSize(size);
        result.setOpaque(true);
        result.setText("<div height=\""+size.height+"\" width=\""+size.width+"\"></div>");
        JScrollPane jsp = new JScrollPane(result);    //place editorPane in a scrollpane
        JPanel pane = new JPanel();   //panel to place in content pane's center
        pane.add(jsp);
        cp.add(pane, BorderLayout.CENTER);
        result.setBackground(Color.cyan);
        result.addHyperlinkListener(e -> {
            if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    result.setPage(String.valueOf(e.getURL().toURI()));
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setVisible(true);
    }//constructor

    public void actionPerformed(ActionEvent e) {
        System.out.println("Spider crawling...");
        result.setText("Spider crawling...");
        ind = new Index(url.getText(),Integer.parseInt(maxSites.getText()),result);
        result.setText("Done! Ready for Searching!");
    }//actionPerformed
}//class