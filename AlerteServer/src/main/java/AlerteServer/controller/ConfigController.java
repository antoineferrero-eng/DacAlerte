package AlerteServer.controller;

import AlerteServer.service.ConfigService;
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
    private ConfigService configService;

    @Autowired
    private SchedulingService schedulingService;

    @GetMapping("/alert-levels")
    public List<String> getActiveLevels() {
        return configService.getActiveLevels();
    }

    @PostMapping("/alert-levels")
    public LevelsResponseDTO setActiveLevels(@RequestBody List<String> levels) {
        configService.setActiveLevels(levels);
        return new LevelsResponseDTO("ok", configService.getActiveLevels());
    }

    @GetMapping("/alert-types")
    public List<String> getActiveTypes() {
        return configService.getActiveTypes();
    }

    @PostMapping("/alert-types")
    public TypesResponseDTO setActiveTypes(@RequestBody List<String> types) {
        configService.setActiveTypes(types);
        return new TypesResponseDTO("ok", configService.getActiveTypes());
    }

    @GetMapping("/mail-time")
    public MailCronResponseDTO getMailCron() {
        return new MailCronResponseDTO("ok", configService.getMailCron());
    }

    @PostMapping("/mail-time")
    public ResponseEntity<MailCronResponseDTO> setMailCron(@RequestBody Map<String, String> body) {
        String cron = body.get("cron");
        if (cron == null || !CronExpression.isValidExpression(cron)) {
            return ResponseEntity.badRequest()
                    .body(new MailCronResponseDTO("error: invalid cron", configService.getMailCron()));
        }
        configService.setMailCron(cron);
        schedulingService.rescheduleMail();
        return ResponseEntity.ok(new MailCronResponseDTO("ok", configService.getMailCron()));
    }

    @GetMapping("/update-time")
    public UpdateCronResponseDTO getUpdateCron() {
        return new UpdateCronResponseDTO("ok", configService.getUpdateCron());
    }

    @PostMapping("/update-time")
    public ResponseEntity<UpdateCronResponseDTO> setUpdateCron(@RequestBody Map<String, String> body) {
        String cron = body.get("cron");
        if (cron == null || !CronExpression.isValidExpression(cron)) {
            return ResponseEntity.badRequest()
                    .body(new UpdateCronResponseDTO("error: invalid cron", configService.getUpdateCron()));
        }
        configService.setUpdateCron(cron);
        schedulingService.rescheduleDataUpdate();
        return ResponseEntity.ok(new UpdateCronResponseDTO("ok", configService.getUpdateCron()));
    }

    @GetMapping
    public FullConfigDTO getFullConfig() {
        return new FullConfigDTO(
                configService.getActiveLevels(),
                configService.getActiveTypes(),
                configService.getMailCron(),
                configService.getUpdateCron());
    }
}