package io.github.plotnik.obsidian_web_editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope // Creates a new instance of this bean for each user session
public class AppState {

    private static final Logger log = LoggerFactory.getLogger(AppState.class);

    private List<String> obsidianFolders;
    private List<String> obsidianNames;
    private String selectedObsidianName;
    private List<String> subfolders;
    private String selectedSubfolder;
    private List<String> newestFiles;
    private String noteName;
    private File noteFolder;
    private String noteHome;
    String noteText;
    private String status;


    // The init() method will be called by the controller when the session starts
    public void init() {
        if (status == null) {
            status = "Obsidian Web Editor";
        }
        if (selectedObsidianName == null) {
            initObsidianNames();
        }
    }

    // Copy ALL other methods from your jbang script here, exactly as they are.
    // Examples: initObsidianNames(), initFolders(), initNewestFiles(), savePage(...), etc.
    // You only need to change methods that took a Spark `Request` object.

    // Modified methods (no longer need `spark.Request`)
    void savePage(String newNoteText) throws IOException {
        noteText = newNoteText;
        File filePath = new File(noteFolder, noteName);
        saveStr(filePath.getPath(), noteText);
        status = "Saved: " + noteName;
        log.info(status);
    }

    void restorePage() {
        initTextArea();
        status = "Restored: " + noteName;
        log.info(status);
    }

    void updatePage(String widget, String selectedValue) {
        log.info(String.format("[updatePage] widget: %s, value: %s", widget, selectedValue));
        try {
            switch (widget) {
                case "obsidianDropdown":
                    setSelectedObsidianName(selectedValue);
                    break;
                case "folderDropdown":
                    setSelectedSubfolder(selectedValue);
                    break;
                case "pageDropdown":
                    setNoteName(selectedValue);
                    break;
            }
        } catch (Exception e) {
            log.error("Error updating page state", e);
        }
    }

    // ... copy all other helper methods like initObsidianNames, initFolders, etc. here ...
    // The following are just a few for illustration.

    void initObsidianNames() {
        try {
            String homeFolder = System.getProperty("user.home");
            String os = System.getProperty("os.name").toLowerCase();
            String obsidianJsonPath = os.contains("win") ?
                    homeFolder + "\\AppData\\Roaming\\obsidian\\obsidian.json" :
                    homeFolder + "/Library/Application Support/obsidian/obsidian.json";

            String jsonContent = Files.readString(Paths.get(obsidianJsonPath));
            JSONObject obsidianJson = new JSONObject(jsonContent);
            JSONObject vaultsObject = obsidianJson.getJSONObject("vaults");

            List<JSONObject> sortedVaults = vaultsObject.keySet().stream()
                    .map(vaultsObject::getJSONObject)
                    .sorted((v1, v2) -> Long.compare(v2.getLong("ts"), v1.getLong("ts")))
                    .collect(Collectors.toList());

            obsidianFolders = sortedVaults.stream().map(v -> v.getString("path")).collect(Collectors.toList());
            obsidianNames = obsidianFolders.stream().map(Paths::get).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());

            log.info("[initObsidianNames] " + obsidianNames.size());
            if (!obsidianNames.isEmpty()) {
                setSelectedObsidianName(obsidianNames.get(0));
            }
        } catch (Exception e) {
            log.error("[initObsidianNames]", e);
        }
    }

    void initFolders() throws Exception {
        int k = obsidianNames.indexOf(selectedObsidianName);
        noteHome = obsidianFolders.get(k);
        File noteHomeDir = new File(noteHome);
        subfolders = new ArrayList<>();
        subfolders.add(0, ".");

        String[] allDirItems = noteHomeDir.list();
        if (allDirItems != null) {
            for (String item : allDirItems) {
                File itemFile = new File(noteHome, item);
                if (itemFile.isDirectory() && !item.equals(".obsidian")) {
                    subfolders.add(item);
                }
            }
        }
        log.info("[initFolders] " + subfolders.size());
        setSelectedSubfolder(subfolders.get(0));
    }

    void initNewestFiles() throws Exception {
        noteFolder = new File(noteHome, selectedSubfolder);
        newestFiles = getNewestFiles(noteFolder, 5);
        log.info("[initNewestFiles] " + newestFiles.size());
        setNoteName(!newestFiles.isEmpty() ? newestFiles.get(0) : null);
    }

    void initTextArea() {
        try {
            if (noteName != null) {
                File filePath = new File(noteFolder, noteName);
                noteText = Files.readString(Paths.get(filePath.getPath()));
            } else {
                noteText = "";
            }
            log.info("[initTextArea] " + noteText.length());
        } catch (IOException e) {
            log.error("[initTextArea]", e);
        }
    }

    List<String> getNewestFiles(File dir, int numFiles) {
        try {
            if (!dir.exists() || !dir.isDirectory()) return new ArrayList<>();
            return Files.walk(dir.toPath(), 1)
                    .filter(path -> path.toFile().isFile() && path.toString().endsWith(".md"))
                    .sorted((p1, p2) -> {
                        try {
                            return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .limit(numFiles)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error getting newest files", e);
            return new ArrayList<>();
        }
    }

    void setSelectedObsidianName(String selectedValue) throws Exception {
        if (selectedValue == null || selectedValue.equals(selectedObsidianName)) return;
        if (!selectedValue.isEmpty()) {
            selectedObsidianName = selectedValue;
        }
        status = "Obsidian selected: " + selectedObsidianName;
        log.info(status);
        initFolders();
    }

    void setSelectedSubfolder(String selectedValue) throws Exception {
        selectedSubfolder = selectedValue;
        status = "Folder selected: " + selectedSubfolder;
        log.info(status);
        initNewestFiles();
    }

    void setNoteName(String selectedValue) {
        noteName = selectedValue;
        status = noteName != null ? "Page selected: " + noteName : "Empty folder";
        log.info(status);
        initTextArea();
    }

    void saveStr(String fname, String text) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(fname))) {
            out.print(text);
        }
    }

    String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Getters for Thymeleaf

    public List<String> getObsidianNames() {
        return obsidianNames;
    }
    
    public String getSelectedObsidianName() {
        return selectedObsidianName;
    }
    
    public List<String> getSubfolders() {
        return subfolders;
    }

    public String getSelectedSubfolder() {
        return selectedSubfolder;
    }
    
    public List<String> getNewestFiles() {
        return newestFiles;
    }

    public String getNoteName() {
        return noteName;
    }

    public String getStatus() {
        return status;
    }
}