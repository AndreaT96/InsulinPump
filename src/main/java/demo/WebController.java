package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    private MisuraRepository misuraRepository;
    @Autowired
    private IniezioneRepository iniezioneRepository;
    private demo.Controller controller;
    @RequestMapping("/")
    public String index(){
        if(controller == null) {
            controller = new demo.Controller(misuraRepository, iniezioneRepository);
        }
        return "list";
    }

    @RequestMapping("/list")
    public String list(Model model){
        List<Misura> data = new LinkedList<>();
        for (Misura m: misuraRepository.findAll()){
            data.add(m);
        }
        model.addAttribute("misure", data);
        return "list";
    }
}