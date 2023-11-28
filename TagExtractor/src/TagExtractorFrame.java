
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;


public class TagExtractorFrame extends JFrame {

    JPanel displayPanel;
    JTextArea displayArea;
    JPanel selectPanel;
    JButton selectFileButton;
    JButton writeButton;
    ArrayList words = new ArrayList<>();

    Set<String> filterSet = new HashSet<>();
    Map<String, Integer> wordMap = new HashMap<>();
    public TagExtractorFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        initDisplayPanel();
        initSelectPanel();

        add(displayPanel, BorderLayout.NORTH);
        add(selectPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initDisplayPanel() {
        displayPanel = new JPanel();
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayPanel.add(new JScrollPane(displayArea));
    }

    private void initSelectPanel() {
        selectPanel = new JPanel();
        selectFileButton = new JButton("Select File");
        writeButton = new JButton("Write File");

        selectFileButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectAndDisplayFile();
            }
        });
        selectPanel.add(selectFileButton);

        writeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeNewFile();
            }
        });
        selectPanel.add(writeButton);
    }

    private void selectAndDisplayFile() {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        try {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                displayArea.append("Chosen File:   " + selectedFile.getAbsoluteFile() + "\n");
                getFilterFile();

                Scanner inFile = new Scanner(selectedFile);
                String word = "";
                while (inFile.hasNext()) {
                    word = inFile.next().replaceAll("[^a-zA-Z]", "").trim().toLowerCase();
                    if (!word.isEmpty() && !filterSet.contains(word) ) {
                        words.add(word);
                    }
                }

                wordMap = (Map<String, Integer>) words.parallelStream().collect(Collectors.groupingByConcurrent(w -> w, Collectors.summingInt(w -> 1)));
                wordMap.forEach((key, value) -> displayArea.append("Word:\t" + key + "\t\tFrequency:\t" + value + "\n"));

                reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File Not Found Error");
            e.printStackTrace();
        }
        catch (IOException e) // code to handle this exception
        {
            System.out.println("IOException Error");
            e.printStackTrace();
        }


    }

    private void getFilterFile(){
        JOptionPane.showMessageDialog(selectPanel, "Choose a Stop Word Filter File", "Choose a File", JOptionPane.INFORMATION_MESSAGE);
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        try
        {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                Scanner inFile = new Scanner(selectedFile);
                String word = "";

                while(inFile.hasNext()){
                    word = inFile.next();
                    filterSet.add(word.toLowerCase());

                }
                reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File Not Found Error");
            e.printStackTrace();
        }
        catch (IOException e) // code to handle this exception
        {
            System.out.println("IOException Error");
            e.printStackTrace();
        }
    }

    private void writeNewFile(){
        File workingDirectory = new File(System.getProperty("user.dir"));
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\filteredlist.txt");
        try
        {
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, CREATE));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            writer.write(displayArea.getText());
            JOptionPane.showMessageDialog(displayPanel,"File has been written!", "File", JOptionPane.INFORMATION_MESSAGE);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
