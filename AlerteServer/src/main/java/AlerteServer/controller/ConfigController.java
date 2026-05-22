package AlerteServer.controller;

import AlerteServer.config.AppConfig;
import AlerteServer.dto.*;
import AlerteServer.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SchedulingService schedulingService;

    @GetMapping("/alert-levels")
    public List<String> getActiveLevels() {
        return appConfig.getActiveLevels();
    }

    @PostMapping("/alert-levels")
    public LevelsResponseDTO setActiveLevels(@RequestBody List<String> levels) {
        appConfig.setActiveLevels(levels);
        return new LevelsResponseDTO("ok", appConfig.getActiveLevels());
    }

    @GetMapping("/alert-types")
    public List<String> getActiveTypes() {
        return appConfig.getActiveTypes();
    }

    @PostMapping("/alert-types")
    public TypesResponseDTO setActiveTypes(@RequestBody List<String> types) {
        appConfig.setActiveTypes(types);
        return new TypesResponseDTO("ok", appConfig.getActiveTypes());
    }

    @GetMapping("/mail-time")
    public MailCronResponseDTO getMailCron() {
        return new MailCronResponseDTO("ok", appConfig.getMailCron());
    }

    @PostMapping("/mail-time")
    public ResponseEntity<MailCronResponseDTO> setMailCron(@RequestBody Map<String, String> body) {
        String cron = body.get("cron");
        if (cron == null || !CronExpression.isValidExpression(cron)) {
            return ResponseEntity.badRequest()
                    .body(new MailCronResponseDTO("error: invalid cron", appConfig.getMailCron()));
        }
        appConfig.setMailCron(cron);
        schedulingService.rescheduleMail();
        return ResponseEntity.ok(new MailCronResponseDTO("ok", appConfig.getMailCron()));
    }

    @GetMapping("/update-time")
    public UpdateCronResponseDTO getUpdateCron() {
        return new UpdateCronResponseDTO("ok", appConfig.getUpdateCron());
    }

    @PostMapping("/update-time")
    public ResponseEntity<UpdateCronResponseDTO> setUpdateCron(@RequestBody Map<String, String> body) {
        String cron = body.get("cron");
        if (cron == null || !CronExpression.isValidExpression(cron)) {
            return ResponseEntity.badRequest()
                    .body(new UpdateCronResponseDTO("error: invalid cron", appConfig.getUpdateCron()));
        }
        appConfig.setUpdateCron(cron);
        schedulingService.rescheduleDataUpdate();
        return ResponseEntity.ok(new UpdateCronResponseDTO("ok", appConfig.getUpdateCron()));
    }

    @GetMapping
    public FullConfigDTO getFullConfig() {
        return new FullConfigDTO(
                appConfig.getActiveLevels(),
                appConfig.getActiveTypes(),
                appConfig.getMailCron(),
                appConfig.getUpdateCron());
    }
}