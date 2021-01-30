package com.insulinpump;

import com.insulinpump.component.Display;
import com.insulinpump.entity.Misura;
import com.insulinpump.repository.IniezioneRepository;
import com.insulinpump.repository.MisuraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Controller
public class WebController {

    private MisuraRepository misuraRepository;
    private IniezioneRepository iniezioneRepository;
    private com.insulinpump.Controller controller;

    @Autowired
    public WebController(MisuraRepository misuraRepository, IniezioneRepository iniezioneRepository, com.insulinpump.Controller controller) {
        this.misuraRepository = misuraRepository;
        this.iniezioneRepository = iniezioneRepository;
        this.controller = controller;
    }

    @RequestMapping("/")
    public String index(){
       return "redirect:/dashboard";
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

    @RequestMapping("/dashboard")
    public String dashboard(Model model){
        Display d = controller.getDisplay_one();
        model.addAttribute("display", d);
        LocalDateTime dateTime = controller.getClock().getTime();
        DateTimeFormatter dateFormatter;
        model.addAttribute("date", dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        model.addAttribute("time", dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        return "dashboard";
    }

    @RequestMapping("/reduceInsulin")
    public String reduceInsulin(Model model){
        controller.getPump().setReservoir(controller.getPump().getReservoir()/(int)(1 + Math.random()*4));
        return "redirect:/dashboard";
    }

    @RequestMapping("/refillInsulin")
    public String refillInsulin(Model model){
        controller.getPump().refill();
        return "redirect:/dashboard";
    }

    @RequestMapping("/decreaseSensorBattery")
    public String decreaseSensorBattery(Model model) {
        controller.getSensor().getBattery().setChargeLevel(controller.getSensor().getBattery().getChargeLevel()/(int)(1 + Math.random()*4));
        return "redirect:/dashboard";
    }

    @RequestMapping("/decreasePumpBattery")
    public String decreasePumpBattery(Model model) {
        controller.getPump().getBattery().setChargeLevel(controller.getPump().getBattery().getChargeLevel()/(int)(1 + Math.random()*4));
        return "redirect:/dashboard";
    }

    @RequestMapping("rechargeBatteries")
    public  String rechargeBatteries(Model model) {
        controller.getPump().getBattery().rechargeBattery();
        controller.getSensor().getBattery().rechargeBattery();
        return "redirect:/dashboard";
    }

    @RequestMapping("/debugPump")
    public String debugPump(Model model) {
        Util.DEBUG_PUMP_CHECK = !Util.DEBUG_PUMP_CHECK;
        return "redirect:/dashboard";
    }

    @RequestMapping("/debugSensor")
    public String debugSensor(Model model) {
        Util.DEBUG_SENSOR_CHECK = !Util.DEBUG_SENSOR_CHECK;
        return "redirect:/dashboard";
    }
}