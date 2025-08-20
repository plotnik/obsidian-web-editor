package io.github.plotnik.obsidian_web_editor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class WebController {

    // Spring will inject the session-scoped bean for us
    @Autowired
    private AppState appState;

    @GetMapping("/")
    public String homePage(Model model) {
        appState.init(); // Initialize state for a new session

        // Add all state variables to the model for Thymeleaf to use
        model.addAttribute("obsidianNames", appState.getObsidianNames());
        model.addAttribute("selectedObsidianName", appState.getSelectedObsidianName());
        model.addAttribute("subfolders", appState.getSubfolders());
        model.addAttribute("selectedSubfolder", appState.getSelectedSubfolder());
        model.addAttribute("newestFiles", appState.getNewestFiles());
        model.addAttribute("noteName", appState.getNoteName());
        model.addAttribute("noteText", appState.noteText);
        model.addAttribute("status", "[" + appState.currentTime() + "] " + appState.getStatus());

        return "page"; // This tells Thymeleaf to render "page.html"
    }

    @PostMapping("/post")
    public String handlePost(@RequestParam String action, @RequestParam String noteText) throws IOException {
        switch (action) {
            case "save":
                appState.savePage(noteText);
                break;
            case "restore":
                appState.restorePage();
                break;
        }
        return "redirect:/"; // Redirect back to the home page to refresh
    }

    @PutMapping("/update")
    @ResponseBody // Indicates the return value is the response body, not a template name
    public String handleUpdate(@RequestBody UpdateRequest updateRequest) {
        appState.updatePage(updateRequest.getWidget(), updateRequest.getValue());
        return "{}"; // Return an empty JSON object, same as before
    }
}